package net.sharksystem.contracts.content;

import java.util.Date;

public class ContentPackage {

    private final String type;
    private final Date date;
    private final ContractContent content;

    public ContentPackage(String type, Date date, ContractContent content) {
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

    public ContractContent getContent() {
        return content;
    }
}
