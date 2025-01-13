package net.sharksystem.contracts;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class Contract {

    private final String authorId;
    private final byte[] content;
    private final List<ContractParty> otherParties;
    private final boolean encrypted;

    private final String hash;
    private final byte[] signature;

    public Contract(String authorId, byte[] content, List<ContractParty> otherParties, boolean encrypted, String hash, byte[] signature) {
        this.authorId = authorId;
        this.content = content;
        this.otherParties = otherParties;
        this.encrypted = encrypted;
        this.hash = hash;
        this.signature = signature;
    }

    public String getAuthorId() {
        return authorId;
    }

    public byte[] getContent() {
        return content;
    }

    public List<ContractParty> getOtherParties() {
        return otherParties;
    }

    public List<String> getOtherPartyIds(){
        return otherParties.stream().map(ContractParty::getId).collect(Collectors.toList());
    }

    /**
     * Hash of the contract using the authorId, unencrypted content and parties
     * @return
     */
    public String getHash() {
        return hash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public static String hashSignedData(String authorId, byte[] content, List<String> otherParties) throws NoSuchAlgorithmException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(out);

            dos.writeUTF(authorId);
            dos.write(content);
            for(String party : otherParties){
                dos.writeUTF(party);
            }
            dos.close();

            byte[] packedData = out.toByteArray();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(packedData);
            return Base64.getEncoder().encodeToString(hash);
        }catch (IOException e){
            // mask IOException signature because it does not happen in ByteArrayOutputStream
            throw new RuntimeException(e);
        }
    }

}
