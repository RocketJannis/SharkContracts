package net.sharksystem.contracts;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ContractSignature {

    private final String contractHash;
    private final String author;
    private final byte[] signature;

    public ContractSignature(String contractHash, String author, byte[] signature) {
        this.contractHash = contractHash;
        this.author = author;
        this.signature = signature;
    }

    public String getContractHash() {
        return contractHash;
    }

    public String getAuthor() {
        return author;
    }

    public byte[] getSignature() {
        return signature;
    }

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
