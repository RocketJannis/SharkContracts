package net.sharksystem.contracts;

import java.util.List;

public interface SharkContracts {

    List<Contract> listContracts();

    Contract createContract(byte[] content);

    ContractSignature signContract(Contract contract);

    // ...

}
