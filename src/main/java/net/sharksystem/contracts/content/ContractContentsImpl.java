package net.sharksystem.contracts.content;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.sharksystem.SharkException;
import net.sharksystem.asap.ASAPPeer;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContractContentsImpl implements ContractContents {

    private final Map<String, Class<? extends ContractContent>> types = new HashMap<>();
    private final Map<Class<? extends ContractContent>, String> typesReversed = new HashMap<>();
    private final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();

    @Override
    public void onStart(ASAPPeer asapPeer) { }

    @Override
    public ContentPackage extract(byte[] data) throws MalformedContentData, UnknownContentTypeException {
        try {
            String contentString = new String(data, StandardCharsets.UTF_8);
            InternalContentPackage contentPackage = gson.fromJson(contentString, InternalContentPackage.class);
            Class<? extends ContractContent> type = types.get(contentPackage.getType());
            if(type == null) throw new UnknownContentTypeException("Unknown content type: " + contentPackage.getType());
            ContractContent content = gson.fromJson(contentPackage.getContent(), type);
            return new ContentPackage(contentPackage.getType(), contentPackage.getDate(), content);
        } catch (JsonSyntaxException e) {
            throw new MalformedContentData("Could not decrypt JSON content", e);
        }
    }

    @Override
    public byte[] pack(ContractContent content) throws UnknownContentTypeException {
        String type = typesReversed.get(content.getClass());
        if(type == null) throw new UnknownContentTypeException("Unknown content class: " + content.getClass().getName());
        String contentJSON = gson.toJson(content);
        InternalContentPackage contentPackage = new InternalContentPackage(type, new Date(), contentJSON);
        String packageJSON = gson.toJson(contentPackage);
        return packageJSON.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void registerType(String key, Class<? extends ContractContent> type) {
        types.put(key, type);
        typesReversed.put(type, key);
    }
}
