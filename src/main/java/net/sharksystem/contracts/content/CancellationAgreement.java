package net.sharksystem.contracts.content;

/**
 * Parties can sign a cancellation agreement to clarify that another contract should be viewed as invalid.
 */
public class CancellationAgreement extends ContractContent {

    private final String contractHash;

    public CancellationAgreement(String contractHash) {
        this.contractHash = contractHash;
    }

    public String getContractHash() {
        return contractHash;
    }
}
