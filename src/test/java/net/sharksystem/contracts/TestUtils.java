package net.sharksystem.contracts;

import java.io.*;
import java.util.List;

public class TestUtils {
    public static final String ALICE = "alice";
    public static final String BOB = "bob";
    public static final String APP_NAME = "contracts";
    public static final String APP_URI = "contract://data";
    public static final List<CharSequence> supportedFormats = List.of(APP_NAME);

    /**
     * a serialization example
     * @param exampleLong
     * @param exampleString
     * @param exampleBoolean
     * @return
     */
    public static byte[] serializeExample(long exampleLong, String exampleString, boolean exampleBoolean) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(baos);

        // serialize
        daos.writeLong(exampleLong);
        daos.writeUTF(exampleString);
        daos.writeBoolean(exampleBoolean);

        return baos.toByteArray();
    }

    /**
     * a deserialization example
     */
    public static void deserializeExample(byte[] serializedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
        DataInputStream dais = new DataInputStream(bais);

        // deserialize
        long exampleLong = dais.readLong();
        String exampleString = dais.readUTF();
        boolean exampleBoolean = dais.readBoolean();

        // call a methode in your app

        // here: just print
        System.out.println("received: " + exampleLong + " | " + exampleString + " | " + exampleBoolean);
    }
}