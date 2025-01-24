package net.sharksystem.contracts.content;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ContentTest {

    @Test
    public void testTextContentSerialization() throws UnknownContentTypeException, MalformedContentData {
        ContractContents contents = new ContractContentsImpl();
        contents.registerType("text", TextContent.class);

        TextContent content = new TextContent("Title", "Test");
        byte[] data = contents.pack(content);
        ContentPackage extracted = contents.extract(data);

        System.out.println(new String(data));

        Assertions.assertInstanceOf(TextContent.class, extracted.getContent());
        Assertions.assertEquals(((TextContent) extracted.getContent()).getText(), content.getText());
        Assertions.assertEquals(((TextContent) extracted.getContent()).getTitle(), content.getTitle());
    }

}
