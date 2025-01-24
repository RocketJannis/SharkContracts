package net.sharksystem.contracts;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a contract between an author and optionally other parties.
 * This class is immutable.
 */
public class Contract {

    private final String authorId;
    private final byte[] content;
    private final List<ContractParty> otherParties;
    private final boolean encrypted;

    private final String hash;
    private final byte[] signature;

    /**
     * Creates a new contract
     * @param authorId ASAP identifier of the author
     * @param content contract content
     * @param otherParties other parties, can be empty
     * @param encrypted if the contract content is currently encrypted
     * @param hash hash of the contract using hashSignedData()
     * @param signature Signature of the hash using the author's private key
     */
    public Contract(String authorId, byte[] content, List<ContractParty> otherParties, boolean encrypted, String hash, byte[] signature) {
        this.authorId = authorId;
        this.content = content;
        this.otherParties = otherParties;
        this.encrypted = encrypted;
        this.hash = hash;
        this.signature = signature;
    }

    /**
     * ASAP identifier of the author
     */
    public String getAuthorId() {
        return authorId;
    }

    /**
     * contract content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @return other parties, can be empty
     */
    public List<ContractParty> getOtherParties() {
        return otherParties;
    }

    /**
     * @return IDs of the other parties, can be empty
     */
    public List<String> getOtherPartyIds(){
        return otherParties.stream().map(ContractParty::getId).collect(Collectors.toList());
    }

    /**
     * @return Hash of the contract using the authorId, unencrypted content and parties
     * @see #hashSignedData(String, byte[], List)
     */
    public String getHash() {
        return hash;
    }

    /**
     * @return Signature of the hash using the author's private key
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * @return true if the contract content is currently encrypted, false otherwise
     */
    public boolean isEncrypted() {
        return encrypted;
    }

    /**
     * Creates the hash of a contract from its data
     * @param authorId ASAP identifier of the author
     * @param content contract content
     * @param otherParties other parties, can be empty
     * @return Hash
     * @throws NoSuchAlgorithmException if the used hash algorithm is not available on this system
     */
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
