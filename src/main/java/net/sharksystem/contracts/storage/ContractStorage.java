package net.sharksystem.contracts.storage;

import net.sharksystem.contracts.Contract;
import net.sharksystem.contracts.ContractSignature;

import java.util.List;

/**
 * Data persistence interface needed for SharkContracts
 *
 * @see net.sharksystem.contracts.SharkContracts
 */
public interface ContractStorage {

    /**
     * Stores a new contract
     * @param contract Contract to store
     */
    void insertContract(Contract contract);

    /**
     * Stored a new signature
     * @param signature Signature to store
     */
    void insertSignature(ContractSignature signature);

    /**
     * Returns a list of all known contracts
     * @return list of all known contracts
     */
    List<Contract> loadAllContracts();

    /**
     * Searches for a specific contract using its hash ID
     * @param hash Hash of the contract
     * @return Found contract or null if none was found
     */
    Contract findContract(String hash);

    /**
     * Finds all known signatures for a specific contract
     * @param contract Contract
     * @return list of all known signatures for a specific contract
     */
    default List<ContractSignature> findSignatures(Contract contract){
        return findSignatures(contract.getHash());
    }

    /**
     * Finds all known signatures for a specific contract hash
     * @param contractHash Hash of the contract
     * @return list of all known signatures for the contract hash
     */
    List<ContractSignature> findSignatures(String contractHash);
    
}
