package net.sharksystem.contracts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContractSerializerTest {

    @Test
    public void testSerialization() throws NoSuchAlgorithmException {
        String authorId = "test_sender";
        byte[] content = "abcdefg".getBytes(StandardCharsets.UTF_8);
        List<ContractParty> otherParties = Arrays.asList(new ContractParty("Alice", new byte[]{ 1, 2 }), new ContractParty("Bob", new byte[]{ 3, 4 }));
        String hash = Contract.hashSignedData(authorId, content, otherParties.stream().map(ContractParty::getId).collect(Collectors.toList()));
        byte[] signature = "sig".getBytes(StandardCharsets.UTF_8);

        Contract contract = new Contract(authorId, content, otherParties, false, hash, signature);

        byte[] serialized = ContractSerializer.serialize(contract);
        Contract deserialized = ContractSerializer.deserialize(serialized);

        Assertions.assertEquals(authorId, deserialized.getAuthorId());
        Assertions.assertArrayEquals(content, deserialized.getContent());
        Assertions.assertEquals(otherParties, deserialized.getOtherParties());
        Assertions.assertEquals(hash, deserialized.getHash());
        Assertions.assertArrayEquals(signature, deserialized.getSignature());
    }

}
