package net.sharksystem.contracts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class SignatureSerializerTest {

    @Test
    public void testSerialization() {
        String contractHash = "sdfjhasldfsdljkhf";
        String author = "test_author";
        byte[] signature = new byte[]{1, 2, 3, 4, 5};

        ContractSignature contractSignature = new ContractSignature(contractHash, author, signature);

        byte[] serialized = SignatureSerializer.serialize(contractSignature);
        ContractSignature deserialized = SignatureSerializer.deserialize(serialized);

        Assertions.assertEquals(contractSignature.getContractHash(), deserialized.getContractHash());
        Assertions.assertEquals(contractSignature.getAuthor(), deserialized.getAuthor());
        Assertions.assertArrayEquals(signature, deserialized.getSignature());
    }

}
