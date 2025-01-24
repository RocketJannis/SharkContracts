package net.sharksystem.contracts.content;

import net.sharksystem.ASAPFormats;
import net.sharksystem.SharkComponent;
import net.sharksystem.contracts.Contract;

/**
 * A component as a utility to serialize and deserialize contract contents.
 */
@ASAPFormats(formats = { ContractContents.CONTENTS_FORMAT })
public interface ContractContents extends SharkComponent {

    String CONTENTS_FORMAT = "application/x-contracts-content"; // this is not used at all but there needs to be at least one element in ASAPFormats.formats

    /**
     * Extracts the content from a contract
     * @param contract Contract from which to extract the content
     * @return Extracted content
     * @throws UnknownContentTypeException if the content type is not registered
     * @throws MalformedContentData if the contract is not in a valid format
     * @throws IllegalArgumentException if the contract is encrypted and therefore cannot be deserialized
     */
    default ContentPackage extract(Contract contract) throws UnknownContentTypeException, MalformedContentData {
        if(contract.isEncrypted()) throw new IllegalArgumentException("Given contract is encrypted");
        return extract(contract.getContent());
    }

    /**
     * Extracts the content from a byte array
     * @param data Data from which to extract the content
     * @return Extracted content
     * @throws UnknownContentTypeException if the content type is not registered
     * @throws MalformedContentData if the data is not in a valid format
     */
    ContentPackage extract(byte[] data) throws MalformedContentData, UnknownContentTypeException;

    /**
     * Serializes contract content to a byte array. Can later be deserialized back to a content package.
     * @param content Content
     * @return serialized byte array
     * @throws UnknownContentTypeException if the content type is not registered via registerType()
     */
    byte[] pack(ContractContent content) throws UnknownContentTypeException;

    /**
     * Registers a new type
     * @param key identifier for this type
     * @param type type class, must be serializable for serializers like Gson (DTO/beans like class having a public constructor and getter)
     */
    void registerType(String key, Class<? extends ContractContent> type);

}
