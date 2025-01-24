package net.sharksystem.contracts;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Represents a contract signature
 */
public class ContractSignature {

    private final String contractHash;
    private final String author;
    private final byte[] signature;

    /**
     * Creates a contract signature
     * @param contractHash Hash of the contract that this signature signs
     * @param author ASAP identifier of the author that created this signature
     * @param signature Signed contractHash and author using hashSignedData to validate this signature
     */
    public ContractSignature(String contractHash, String author, byte[] signature) {
        this.contractHash = contractHash;
        this.author = author;
        this.signature = signature;
    }

    /**
     *
     * @return Hash of the contract that this signature signs
     */
    public String getContractHash() {
        return contractHash;
    }

    /**
     *
     * @return ASAP identifier of the author that created this signature
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @return Signed contractHash and author using hashSignedData to validate this signature
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * Hashes contractHash and author together. This is used for signing the signature
     * @param contractHash Hash of the contract that this signature signs
     * @param author ASAP identifier of the author that creates the signature
     * @return Hash of the given data
     * @throws NoSuchAlgorithmException if the hash algorithm is not available on this system
     */
    public static String hashSignedData(String contractHash, String author) throws NoSuchAlgorithmException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(out);

            dos.writeUTF(contractHash);
            dos.writeUTF(author);
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
