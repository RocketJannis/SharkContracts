package net.sharksystem.contracts.content;

import net.sharksystem.ASAPFormats;
import net.sharksystem.SharkComponent;
import net.sharksystem.contracts.Contract;

@ASAPFormats(formats = { ContractContents.CONTENTS_FORMAT })
public interface ContractContents extends SharkComponent {

    String CONTENTS_FORMAT = "application/x-contracts-content"; // this is not used at all but there needs to be at least one element in ASAPFormats.formats

    default ContentPackage extract(Contract contract) throws UnknownContentTypeException, MalformedContentData {
        if(contract.isEncrypted()) throw new IllegalArgumentException("Given contract is encrypted");
        return extract(contract.getContent());
    }

    ContentPackage extract(byte[] data) throws MalformedContentData, UnknownContentTypeException;

    byte[] pack(ContractContent content) throws UnknownContentTypeException;

    void registerType(String key, Class<? extends ContractContent> type);

}
