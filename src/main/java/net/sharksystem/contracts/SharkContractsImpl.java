package net.sharksystem.contracts;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.asap.*;
import net.sharksystem.asap.crypto.ASAPCryptoAlgorithms;
import net.sharksystem.contracts.storage.ContractStorage;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.Log;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of SharkContracts
 * @see SharkContracts
 */
public class SharkContractsImpl implements SharkContracts, ASAPMessageReceivedListener {

    private static final String SYMMETRIC_ALGORITHM = "AES";

    private ASAPPeer asapPeer;
    private final SharkPeer me;
    private final SharkPKIComponent pki;
    private final ContractStorage storage;
    private final List<ContractsListener> listeners = new ArrayList<>();

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
    public List<String> getKnownPeers() throws SharkException {
        String myId = me.getPeerID().toString();
        return pki.getCertificates()
                .stream()
                .map((certificate) -> certificate.getSubjectID().toString())
                .filter((peer) -> !peer.equals(myId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Contract> listContracts() {
        return storage.loadAllContracts();
    }

    @Override
    public List<ContractSignature> listSignatures(Contract contract) {
        return storage.findSignatures(contract.getHash());
    }

    @Override
    public Contract createContract(byte[] content, List<String> otherPartyIds, boolean encrypted) throws SharkException, NoSuchAlgorithmException {
        String authorId = pki.getOwnerID().toString();
        String hash = Contract.hashSignedData(authorId, content, otherPartyIds);
        byte[] signature = ASAPCryptoAlgorithms.sign(hash.getBytes(StandardCharsets.UTF_8), pki.getASAPKeyStore());
        List<ContractParty> otherParties = otherPartyIds.stream()
                .map((id) -> new ContractParty(id, new byte[0]))
                .collect(Collectors.toList());

        Contract unencryptedContract = new Contract(authorId, content, otherParties, false, hash, signature);
        Contract encryptedContract;
        if(encrypted){
            encryptedContract = encryptContract(unencryptedContract);
        } else {
            encryptedContract = unencryptedContract;
        }

        // insert into local storage
        storage.insertContract(unencryptedContract);

        // publish contract
        byte[] data = ContractSerializer.serialize(encryptedContract);
        asapPeer.sendASAPMessage(APP_NAME, URI_CONTRACT, data);

        Log.writeLog(this, "Created contract " + hash);

        return unencryptedContract;
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
        String calculatedHash = Contract.hashSignedData(contract.getAuthorId(), contract.getContent(), contract.getOtherPartyIds());
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
        for(ContractParty party : contract.getOtherParties()){
            boolean signatureIsPresent = signatures.stream().anyMatch(s -> s.getAuthor().equals(party.getId()));
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
            Contract encryptedContract = ContractSerializer.deserialize(data);
            Log.writeLog(this, "Received contract " + encryptedContract.getHash());
            if(storage.findContract(encryptedContract.getHash()) != null){
                Log.writeLog(this, "Contract with same hash id already exists, discarding");
                return;
            }
            Contract contract = decryptContract(encryptedContract);
            if(verifyContract(contract)){
                Log.writeLog(this, "Verification successful.");
                storage.insertContract(contract);
                for(ContractsListener listener : listeners){
                    listener.onContractReceived(contract);
                }
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
                for(ContractsListener listener : listeners){
                    listener.onSignatureReceived(signature);
                }
            }else{
                Log.writeLog(this, "Verification failed.");
            }
        }catch (Exception e){
            Log.writeLogErr(this, "Could not process contract signature: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Contract encryptContract(Contract contract) throws ASAPSecurityException, NoSuchAlgorithmException {
        if(contract.isEncrypted()) return contract; // is already encrypted
        Log.writeLog(this, "Encrypting contract " + contract.getHash());

        // Create symmetric key and encrypt content
        SecretKey secretKey = KeyGenerator.getInstance(SYMMETRIC_ALGORITHM).generateKey();
        byte[] keyBytes = secretKey.getEncoded();
        byte[] encryptedContent = ASAPCryptoAlgorithms.encryptSymmetric(contract.getContent(), secretKey, pki.getASAPKeyStore());

        // Encrypt key for all parties
        List<ContractParty> parties = new ArrayList<>();
        for(ContractParty party : contract.getOtherParties()){
            byte[] encryptedKey = ASAPCryptoUtilsExtension.encryptAsymmetric(keyBytes, party.getId(), pki.getASAPKeyStore());
            parties.add(new ContractParty(party.getId(), encryptedKey));
        }

        return new Contract(contract.getAuthorId(), encryptedContent, parties, true, contract.getHash(), contract.getSignature());
    }

    private Contract decryptContract(Contract contract) throws ASAPSecurityException {
        if(!contract.isEncrypted()) return contract; // is already decrypted
        Log.writeLog(this, "Decrypting contract " + contract.getHash());

        // Retrieve ContractParty directing to the current user
        String myId = pki.getOwnerID().toString();
        Optional<ContractParty> myPartyOptional = contract.getOtherParties()
                .stream()
                .filter((party) -> myId.equals(party.getId())).findFirst();
        if(!myPartyOptional.isPresent()) throw new ASAPSecurityException("Cannot find encryption key for " + myId);
        ContractParty myParty = myPartyOptional.get();

        // Decrypt key
        byte[] symmetricKeyBytes = ASAPCryptoAlgorithms.decryptAsymmetric(myParty.getEncryptedKey(), pki.getASAPKeyStore());
        SecretKey symmetricKey = new SecretKeySpec(symmetricKeyBytes, SYMMETRIC_ALGORITHM);

        // Decrypt content
        byte[] unencryptedContent = ASAPCryptoAlgorithms.decryptSymmetric(contract.getContent(), symmetricKey, pki.getASAPKeyStore());

        // Return copy with unencrypted data
        return new Contract(contract.getAuthorId(), unencryptedContent, contract.getOtherParties(), false, contract.getHash(), contract.getSignature());
    }

    @Override
    public void registerListener(ContractsListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterListener(ContractsListener listener) {
        listeners.remove(listener);
    }
}
