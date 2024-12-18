package net.sharksystem.contracts.storage;

import net.sharksystem.contracts.Contract;
import net.sharksystem.contracts.ContractSignature;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements Contract Storage for testing purposes.
 */
public class TemporaryInMemoryStorage implements ContractStorage {

    private final List<Contract> contracts = new ArrayList<>();
    private final List<ContractSignature> signatures = new ArrayList<>();

    @Override
    public void insertContract(Contract contract) {
        contracts.add(contract);
    }

    @Override
    public void insertSignature(ContractSignature signature) {
        signatures.add(signature);
    }

    @Override
    public List<Contract> loadAllContracts() {
        return new ArrayList<>(contracts);
    }

    @Override
    public Contract findContract(String hash) {
        if(hash == null) throw new NullPointerException("Hash must not be null");
        return contracts.stream()
                .filter(c -> c.getHash().equals(hash))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ContractSignature> findSignatures(String contractHash) {
        if(contractHash == null) throw new NullPointerException("contractHash must not be null");
        return signatures.stream()
                .filter(s -> s.getContractHash().equals(contractHash))
                .toList();
    }
}
