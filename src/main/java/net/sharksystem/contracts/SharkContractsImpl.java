package net.sharksystem.contracts;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.asap.*;
import net.sharksystem.asap.crypto.ASAPCryptoAlgorithms;
import net.sharksystem.contracts.storage.ContractStorage;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;


public class SharkContractsImpl implements SharkContracts, ASAPMessageReceivedListener {

    private ASAPPeer asapPeer;
    private final SharkPeer me;
    private final SharkPKIComponent pki;
    private final ContractStorage storage;

    public SharkContractsImpl(SharkPeer me, ContractStorage storage, SharkPKIComponent pki) {
        this.me = me;
        this.storage = storage;
        this.pki = pki;
    }

    @Override
    public void onStart(ASAPPeer asapPeer) {
        this.asapPeer = asapPeer;
        asapPeer.addASAPMessageReceivedListener(APP_NAME, this);
    }

    @Override
    public List<String> getKnownPeers() {
        return pki.getCertificates()
                .stream()
                .map((certificate) -> certificate.getSubjectID().toString())
                .toList();
    }

    @Override
    public List<Contract> listContracts() {
        return storage.loadAllContracts();
    }

    @Override
    public Contract createContract(byte[] content, List<String> otherParties) throws SharkException, NoSuchAlgorithmException {
        String authorId = pki.getOwnerID().toString();
        String hash = Contract.hashSignedData(authorId, content, otherParties);
        byte[] signature = ASAPCryptoAlgorithms.sign(hash.getBytes(StandardCharsets.UTF_8), pki.getASAPKeyStore());
        Contract contract = new Contract(authorId, content, otherParties, hash, signature);

        // insert into local storage
        storage.insertContract(contract);

        // publish contract
        byte[] data = ContractSerializer.serialize(contract);
        asapPeer.sendASAPMessage(APP_NAME, URI_CONTRACT, data);

        Log.writeLog(this, "Created contract " + hash);

        return contract;
    }

    @Override
    public ContractSignature signContract(Contract contract) throws ASAPException, NoSuchAlgorithmException {
        String contractHash = contract.getHash();
        String author = pki.getOwnerID().toString();
        String hash = ContractSignature.hashSignedData(contractHash, author);
        byte[] signatureBytes = ASAPCryptoAlgorithms.sign(hash.getBytes(StandardCharsets.UTF_8), pki.getASAPKeyStore());
        ContractSignature signature = new ContractSignature(contractHash, author, signatureBytes);

        // insert into local storage
        storage.insertSignature(signature);

        // publish signature
        byte[] data = SignatureSerializer.serialize(signature);
        asapPeer.sendASAPMessage(APP_NAME, URI_SIGNATURE, data);

        Log.writeLog(this, "Signed contract " + contract.getHash());

        return signature;
    }

    @Override
    public boolean verifyContract(Contract contract) throws NoSuchAlgorithmException, ASAPSecurityException {
        String calculatedHash = Contract.hashSignedData(contract.getAuthorId(), contract.getContent(), contract.getOtherParties());
        if(!contract.getHash().equals(calculatedHash)) return false; // hash does not match

        // verify signature
        return ASAPCryptoAlgorithms.verify(calculatedHash.getBytes(StandardCharsets.UTF_8), contract.getSignature(), contract.getAuthorId(), pki.getASAPKeyStore());
    }

    @Override
    public boolean verifySignature(ContractSignature signature) throws ASAPSecurityException, NoSuchAlgorithmException {
        String calculatedHash = ContractSignature.hashSignedData(signature.getContractHash(), signature.getAuthor());

        return ASAPCryptoAlgorithms.verify(calculatedHash.getBytes(StandardCharsets.UTF_8), signature.getSignature(), signature.getAuthor(), pki.getASAPKeyStore());
    }

    @Override
    public boolean isSignedByAllParties(Contract contract) {
        List<ContractSignature> signatures = storage.findSignatures(contract);
        for(String party : contract.getOtherParties()){
            boolean signatureIsPresent = signatures.stream().anyMatch(s -> s.getAuthor().equals(party));
            if(!signatureIsPresent){
                return false;
            }
        }

        return true;
    }

    @Override
    public void asapMessagesReceived(ASAPMessages messages, String senderE2E, List<ASAPHop> hops) throws IOException {
        Log.writeLog(this, "Received messages format=" + messages.getFormat() + ", uri=" + messages.getURI() + ", size=" + messages.size());
        Iterator<byte[]> iterator = messages.getMessages();
        while (iterator.hasNext()){
            byte[] data = iterator.next();
            switch (messages.getURI().toString()){
                case URI_CONTRACT: onContractReceived(data); break;
                case URI_SIGNATURE: onSignatureReceived(data); break;
            }
        }
    }

    private void onContractReceived(byte[] data){
        try {
            Contract contract = ContractSerializer.deserialize(data);
            Log.writeLog(this, "Received contract " + contract.getHash());
            if(verifyContract(contract)){
                Log.writeLog(this, "Verification successful.");
                storage.insertContract(contract);
            }else{
                Log.writeLog(this, "Verification failed.");
            }
        }catch (Exception e){
            Log.writeLogErr(this, "Could not process contract: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void onSignatureReceived(byte[] data){
        try {
            ContractSignature signature = SignatureSerializer.deserialize(data);
            Log.writeLog(this, "Received signature for " + signature.getContractHash() + " by " + signature.getAuthor());
            if(verifySignature(signature)){
                Log.writeLog(this, "Verification successful.");
                storage.insertSignature(signature);
            }else{
                Log.writeLog(this, "Verification failed.");
            }
        }catch (Exception e){
            Log.writeLogErr(this, "Could not process contract signature: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
