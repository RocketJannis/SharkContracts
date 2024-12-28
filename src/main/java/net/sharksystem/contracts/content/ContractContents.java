package net.sharksystem.contracts.content;

import net.sharksystem.ASAPFormats;
import net.sharksystem.contracts.Contract;

@ASAPFormats(formats = { })
public interface ContractContents {

    default ContentPackage extract(Contract contract) throws UnknownContentTypeException, MalformedContentData {
        if(contract.isEncrypted()) throw new IllegalArgumentException("Given contract is encrypted");
        return extract(contract.getContent());
    }

    ContentPackage extract(byte[] data) throws MalformedContentData, UnknownContentTypeException;

    byte[] pack(ContractContent content) throws UnknownContentTypeException;

    void registerType(String key, Class<? extends ContractContent> type);

}
