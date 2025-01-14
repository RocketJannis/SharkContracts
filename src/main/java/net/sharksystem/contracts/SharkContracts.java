package net.sharksystem.contracts;

import net.sharksystem.ASAPFormats;
import net.sharksystem.SharkComponent;
import net.sharksystem.SharkException;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPSecurityException;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@ASAPFormats(formats = { SharkContracts.APP_NAME })
public interface SharkContracts extends SharkComponent {

    String APP_NAME = "application/x-contracts";

    String URI_CONTRACT = "contracts://contract";
    String URI_SIGNATURE = "contracts://signature";

    List<String> getKnownPeers() throws SharkException;

    List<Contract> listContracts();

    List<ContractSignature> listSignatures(Contract contract);

    /**
     *
     * @param content
     * @param otherParties
     * @param encrypted
     * @return unencrypted contract
     * @throws SharkException
     * @throws NoSuchAlgorithmException
     */
    Contract createContract(byte[] content, List<String> otherParties, boolean encrypted) throws SharkException, NoSuchAlgorithmException;

    ContractSignature signContract(Contract contract) throws NoSuchAlgorithmException, ASAPException;

    /**
     * Checks if the hash and signature are valid
     * @param contract Contract to verify
     * @return true if verified, false otherwise
     * @throws NoSuchAlgorithmException if hashing failed
     * @throws ASAPSecurityException if verification could not be completed, e.g. when a key is not present
     */
    boolean verifyContract(Contract contract) throws ASAPSecurityException, NoSuchAlgorithmException;

    /**
     * Verifies the signature
     * @param signature Signature to verify
     * @return true if verified, false otherwise
     * @throws NoSuchAlgorithmException if hashing failed
     * @throws ASAPSecurityException if verification could not be completed, e.g. when a key is not present
     */
    boolean verifySignature(ContractSignature signature) throws ASAPSecurityException, NoSuchAlgorithmException;

    boolean isSignedByAllParties(Contract contract);

    void registerListener(ContractsListener listener);

    void unregisterListener(ContractsListener listener);

}
