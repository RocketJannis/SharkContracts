package net.sharksystem.contracts.content;

public class MalformedContentData extends Exception {
    public MalformedContentData(String message) {
        super(message);
    }

    public MalformedContentData(String message, Throwable cause) {
        super(message, cause);
    }
}
