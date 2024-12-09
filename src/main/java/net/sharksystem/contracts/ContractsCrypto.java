package net.sharksystem.contracts;

public interface ContractsCrypto {

    boolean verifySignature(String data, String publicKey, String signature);

    String createSignature(String data, String privateKey);

}
