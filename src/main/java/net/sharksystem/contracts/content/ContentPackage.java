package net.sharksystem.contracts.content;

import java.util.Date;

/**
 * A package containing content metadata and the actual content
 */
public class ContentPackage {

    private final String type;
    private final Date date;
    private final ContractContent content;

    public ContentPackage(String type, Date date, ContractContent content) {
        this.type = type;
        this.date = date;
        this.content = content;
    }

    /**
     * returns the content type. This is the registered key via ContractContents.registerType
     * @return content type key
     */
    public String getType() {
        return type;
    }

    /**
     * returns the creation date
     * @return creation date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Returns the actual content
     * @return actual content
     */
    public ContractContent getContent() {
        return content;
    }
}
