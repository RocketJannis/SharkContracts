package net.sharksystem.contracts.content;

import java.util.Date;

public class InternalContentPackage {

    private final String type;
    private final Date date;
    private final String content;

    public InternalContentPackage(String type, Date date, String content) {
        this.type = type;
        this.date = date;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

}
