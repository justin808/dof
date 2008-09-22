package org.doframework;

public class ObjectInfo
{
    /**
     * The primary key of the object to create.
     * Note, in the case of a request to create a "scratch" object, the pk will not be representative of what
     * is in the file. Instead, you need to substitute the provided PK with the one you've provided in the file.
     */
    protected String pk;
    /**
     * Like "xml"
     */
    protected String fileType;


    public ObjectInfo(String fileType, String pk)
    {
        this.fileType = fileType;
        this.pk = pk;
    }


    public String getPk()
    {
        return pk;
    }


    void setPk(String pk)
    {
        this.pk = pk;
    }


    public String getFileType()
    {
        return fileType;
    }
}
