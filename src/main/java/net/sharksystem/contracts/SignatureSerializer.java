package net.sharksystem.contracts;

import java.io.*;

public class SignatureSerializer {

    public static byte[] serialize(ContractSignature signature){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(out);

            dos.writeUTF(signature.getContractHash());
            dos.writeUTF(signature.getAuthor());
            dos.writeInt(signature.getSignature().length);
            dos.write(signature.getSignature());
            dos.close();

            return out.toByteArray();
        }catch (IOException e){
            // mask IOException signature because it does not happen in ByteArrayOutputStream
            throw new RuntimeException(e);
        }
    }

    public static ContractSignature deserialize(byte[] data){
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bis);

            String contractHash = dis.readUTF();
            String author = dis.readUTF();
            int signatureLength = dis.readShort();
            byte[] signature = dis.readNBytes(signatureLength);
            dis.close();

            return new ContractSignature(contractHash, author, signature);
        }catch (IOException e){
            // mask IOException signature because it does not happen in ByteArrayInputStream
            throw new RuntimeException(e);
        }
    }

}
