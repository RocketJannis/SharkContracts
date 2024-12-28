package net.sharksystem.contracts.content;

public class TextContent extends ContractContent {

    public final String text;

    public TextContent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
