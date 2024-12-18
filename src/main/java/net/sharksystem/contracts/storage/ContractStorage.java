package net.sharksystem.contracts.storage;

import net.sharksystem.contracts.Contract;
import net.sharksystem.contracts.ContractSignature;

import java.util.List;

public interface ContractStorage {

    void insertContract(Contract contract);

    void insertSignature(ContractSignature signature);

    List<Contract> loadAllContracts();

    Contract findContract(String hash);

    default List<ContractSignature> findSignatures(Contract contract){
        return findSignatures(contract.getHash());
    }

    List<ContractSignature> findSignatures(String contractHash);
    
}
