package net.sharksystem.contracts.storage;

import net.sharksystem.contracts.Contract;
import net.sharksystem.contracts.ContractSignature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TemporaryStorageTest {

    @Test
    public void testTemporaryStorage(){
        ContractStorage storage = new TemporaryInMemoryStorage();

        Contract contract1 = new Contract("Test", new byte[]{1, 2, 3}, new ArrayList<>(), false, "abc", new byte[]{ 4, 5, 6 });
        Contract contract2 = new Contract("Test", new byte[]{1, 2, 3}, new ArrayList<>(), false, "def", new byte[]{ 4, 5, 6 });
        ContractSignature signature = new ContractSignature(contract1.getHash(), "test2", new byte[]{7, 5, 3});
        storage.insertContract(contract1);
        storage.insertContract(contract2);
        storage.insertSignature(signature);

        Assertions.assertEquals(2, storage.loadAllContracts().size());
        Assertions.assertEquals(contract2, storage.findContract(contract2.getHash()));

        List<ContractSignature> foundSignatures = storage.findSignatures(contract1);
        Assertions.assertEquals(1, foundSignatures.size());
        Assertions.assertEquals(signature, foundSignatures.get(0));

        Assertions.assertEquals(0, storage.findSignatures(contract2).size());
    }

}
