package net.sharksystem.contracts;

import java.util.Date;

public class Contract {

    private final String author;
    private final Date timestamp;
    private final byte[] content;

    private final String hash;
    private final String signature;

    public Contract(String author, Date timestamp, byte[] content, String hash, String signature) {
        this.author = author;
        this.timestamp = timestamp;
        this.content = content;
        this.hash = hash;
        this.signature = signature;
    }

    public String getAuthor() {
        return author;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public byte[] getContent() {
        return content;
    }

    public String getHash() {
        return hash;
    }

    public String getSignature() {
        return signature;
    }
}
