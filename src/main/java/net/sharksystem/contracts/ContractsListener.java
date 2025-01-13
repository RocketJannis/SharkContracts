package net.sharksystem.contracts;

public interface ContractsListener {

    void onContractReceived(Contract contract);

    void onSignatureReceived(ContractSignature signature);

}
