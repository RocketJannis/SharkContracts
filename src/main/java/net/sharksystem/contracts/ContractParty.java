package net.sharksystem.contracts;

import java.util.Arrays;
import java.util.Objects;

public class ContractParty {

    private final String id;
    private final byte[] encryptedKey;

    public ContractParty(String id, byte[] encryptedKey) {
        this.id = id;
        this.encryptedKey = encryptedKey;
    }

    public String getId() {
        return id;
    }

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
