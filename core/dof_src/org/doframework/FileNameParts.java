package org.doframework;

/**
 * This class is a struct to represent the different key parts of the file name
 */
public class FileNameParts
{

    /**
     * Like "Customer" or "Invoice" or whatever type of object you are saving
     */
    public String objectType;

    /**
     * The primary key
     */
    public String pk;

    /**
     * Like "xml"
     */
    public String fileType;
}
