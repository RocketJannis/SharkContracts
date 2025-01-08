package net.sharksystem.contracts.content;

public class TextContent extends ContractContent {

    public final String title;
    public final String text;

    public TextContent(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
