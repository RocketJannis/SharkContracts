package net.sharksystem.contracts;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a contract party
 */
public class ContractParty {

    private final String id;
    private final byte[] encryptedKey;

    /**
     * Represents a contract party
     * @param id ASAP identifier of this party
     * @param encryptedKey the symmetric key of the contract contents encrypted using the public key of this party. should be an empty array if the contract is not encrypted
     */
    public ContractParty(String id, byte[] encryptedKey) {
        this.id = id;
        this.encryptedKey = encryptedKey;
    }

    /**
     *
     * @return ASAP identifier of this party
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return the symmetric key of the contract contents encrypted using the public key of this party. should be an empty array if the contract is not encrypted
     */
    public byte[] getEncryptedKey() {
        return encryptedKey;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ContractParty party = (ContractParty) o;
        return Objects.equals(id, party.id) && Arrays.equals(encryptedKey, party.encryptedKey);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Arrays.hashCode(encryptedKey);
        return result;
    }
}
