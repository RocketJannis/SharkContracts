package net.sharksystem.contracts;

public class ContractSignature {

    private final String contractHash;
    private final String author;
    private final String signature;

    public ContractSignature(String contractHash, String author, String signature) {
        this.contractHash = contractHash;
        this.author = author;
        this.signature = signature;
    }

    public String getContractHash() {
        return contractHash;
    }

    public String getAuthor() {
        return author;
    }

    public String getSignature() {
        return signature;
    }

}
