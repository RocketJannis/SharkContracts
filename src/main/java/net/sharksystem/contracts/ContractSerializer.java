package net.sharksystem.contracts;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ContractSerializer {

    public static byte[] serialize(Contract contract){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(out);

            dos.writeUTF(contract.getAuthorId());
            dos.writeInt(contract.getOtherParties().size());
            for(ContractParty party : contract.getOtherParties()){
                dos.writeUTF(party.getId());
                dos.writeInt(party.getEncryptedKey().length);
                dos.write(party.getEncryptedKey());
            }
            dos.writeInt(contract.getContent().length);
            dos.write(contract.getContent());
            dos.writeBoolean(contract.isEncrypted());
            dos.writeUTF(contract.getHash());
            dos.writeShort(contract.getSignature().length);
            dos.write(contract.getSignature());
            dos.close();

            return out.toByteArray();
        }catch (IOException e){
            // mask IOException signature because it does not happen in ByteArrayOutputStream
            throw new RuntimeException(e);
        }
    }

    public static Contract deserialize(byte[] data){
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bis);

            String authorId = dis.readUTF();
            int otherPartiesSize = dis.readInt();
            List<ContractParty> otherParties = new ArrayList<>();
            for(int i = 0; i < otherPartiesSize; i++){
                String id = dis.readUTF();
                int keyLength = dis.readInt();
                byte[] key = dis.readNBytes(keyLength);
                otherParties.add(new ContractParty(id, key));
            }
            int contentLength = dis.readInt();
            byte[] content = dis.readNBytes(contentLength);
            boolean encrypted = dis.readBoolean();
            String hash = dis.readUTF();
            int signatureLength = dis.readShort();
            byte[] signature = dis.readNBytes(signatureLength);
            dis.close();

            return new Contract(authorId, content, otherParties, encrypted, hash, signature);
        }catch (IOException e){
            // mask IOException signature because it does not happen in ByteArrayInputStream
            throw new RuntimeException(e);
        }
    }

}
