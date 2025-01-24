package net.sharksystem.contracts;

/**
 * Listener that receives incoming contracts and signatures as long as it's registered in SharkContracts
 * @see SharkContracts
 */
public interface ContractsListener {

    /**
     * Called when a new contract has been received
     * @param contract Contract
     */
    void onContractReceived(Contract contract);

    /**
     * Called when a new signature has been received
     * @param signature Signature
     */
    void onSignatureReceived(ContractSignature signature);

}
