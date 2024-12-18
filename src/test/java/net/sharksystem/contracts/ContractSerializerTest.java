package net.sharksystem.contracts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class ContractSerializerTest {

    @Test
    public void testSerialization() throws NoSuchAlgorithmException {
        String authorId = "test_sender";
        byte[] content = "abcdefg".getBytes(StandardCharsets.UTF_8);
        List<String> otherParties = Arrays.asList("Alice", "Bob");
        String hash = Contract.hashSignedData(authorId, content, otherParties);
        byte[] signature = "sig".getBytes(StandardCharsets.UTF_8);

        Contract contract = new Contract(authorId, content, otherParties, hash, signature);

        byte[] serialized = ContractSerializer.serialize(contract);
        Contract deserialized = ContractSerializer.deserialize(serialized);

        Assertions.assertEquals(authorId, deserialized.getAuthorId());
        Assertions.assertArrayEquals(content, deserialized.getContent());
        Assertions.assertEquals(otherParties, deserialized.getOtherParties());
        Assertions.assertEquals(hash, deserialized.getHash());
        Assertions.assertArrayEquals(signature, deserialized.getSignature());
    }

}
