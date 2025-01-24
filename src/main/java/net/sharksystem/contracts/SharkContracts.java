package net.sharksystem.contracts;

import net.sharksystem.ASAPFormats;
import net.sharksystem.SharkComponent;
import net.sharksystem.SharkException;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPSecurityException;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * SharkContracts is a SharkComponent that implements digital contracts and signatures to be used with ASAP.
 */
@ASAPFormats(formats = { SharkContracts.APP_NAME })
public interface SharkContracts extends SharkComponent {

    String APP_NAME = "application/x-contracts";

    String URI_CONTRACT = "contracts://contract";
    String URI_SIGNATURE = "contracts://signature";

    /**
     * Returns a list of known peers that can be used to create contracts.
     * @return list of known peers
     * @throws SharkException if the shark peer has not been started
     */
    List<String> getKnownPeers() throws SharkException;

    /**
     * Returns a list of all known contracts
     * @return list of all known contracts
     */
    List<Contract> listContracts();

    /**
     * Returns a list of signatures for a specific contract
     * @param contract Contract
     * @return list of signatures
     */
    List<ContractSignature> listSignatures(Contract contract);

    /**
     * Creates a new contract, stores it and sends it via ASAP to other peers
     * @param content Contract content
     * @param otherParties List of the ASAP identifiers of other parties that should sign the contract
     * @param encrypted if the contract should be encrypted. If encrypted, the content will only be visible to the chosen parties
     * @return new instance of the contract
     * @throws SharkException if signing or sending failed
     * @throws NoSuchAlgorithmException if needed algorithms are not present on this system
     */
    Contract createContract(byte[] content, List<String> otherParties, boolean encrypted) throws SharkException, NoSuchAlgorithmException;

    /**
     * Creates a new contract signature, stores it and sends it via ASAP to other peers
     * @param contract Contract to sign
     * @return new instance of the signature
     * @throws NoSuchAlgorithmException if needed algorithms are not present on this system
     * @throws ASAPException if signing or sending failed
     */
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

    /**
     * Checks if all parties that are in the contract's otherParties list have signed the contract.
     * @param contract Contract
     * @return true if all parties signed, false otherwise
     */
    boolean isSignedByAllParties(Contract contract);

    /**
     * Registers a new ContractsListener that is informed about new contracts and signatures
     * @param listener Listener
     * @see ContractsListener
     */
    void registerListener(ContractsListener listener);

    /**
     * Unregisters a ContractsListener
     * @param listener Listener
     * @see ContractsListener
     */
    void unregisterListener(ContractsListener listener);

}
