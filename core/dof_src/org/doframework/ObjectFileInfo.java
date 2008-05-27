package org.doframework;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;

/**
 * This class offers information so that a DependentObjectHandler knows how to create an object, either a
 * "reference" object or a "scratch" object.<p/>
 *
 * 
 *
 */
public class ObjectFileInfo
{
    /**
     * Like "Customer" or "Invoice" or whatever type of object you are saving
     */
    private String objectType;

    /**
     * The primary key of the object to create.
     * Note, in the case of a request to create a "scratch" object, the pk will not be representative of what
     * is in the file. Instead, you need to substitute the provided PK with the one you've provided in the file.
     *
     * If you use the utilitye method DOF.
     */
    private String pk;

    /**
     * Like "xml"
     */
    private String fileType;

    /**
     * Like "customers/Customer.15.xml". Note, the fileToLoad indicates the PK for reference objects. For
     * scratch objects, the name part is simply used to distinguish flavors of scratch objects.
     */
    private String fileToLoad;

    private boolean scratchMode;


    /**
     * This is the original PK -- which differs from the "PK" in scratch mode
     */
    private String originalPk;

    private String fileContents;
    private Map<String, String> scratchReferenceToPk;

    public ObjectFileInfo(String objectType, String pk, String fileType)
    {
        this.objectType = objectType;
        this.originalPk = pk;
        this.pk = pk; // this will change if a scratch object
        this.fileType = fileType;
    }

    public String toString()
    {
        return '{' + getFileToLoad() + ", " + getObjectType() + ", " + getPk() + ", " + getFileType() + '}';
    }

    String getKeyForHashing()
    {
        return getObjectType() + ":" + getPk();
    }

    /**
     * This returns the contents of the file to create the object. For creation of a "reference" object, this
     * string is the exact same as the file contents. In the case of creating a scratch object, scratch object
     * primary keys are replaced
     *
     * @return The contents of the file to create object.
     */
    public String getFileContentsAsString()
    {
        if (fileContents == null)
        {
            String originalFileContents = DOF.getResourceAsString(getFileToLoad());
            fileContents = swapOutPksWithScratchValues(originalFileContents);
        }
        if (DOF.dofPrintDescriptionFiles)
        {
            System.out.println("\n\n\nfileContents = " + fileContents);
        }
        return fileContents;
    }

    /**
     * This method is called when the file contents need to processed for "scratch" mode.
     * @param originalFileContents
     * @return The original file contents with the scratch PK
     */
    private String swapOutPksWithScratchValues(String originalFileContents)
    {
        Pattern patternForScratchPK = HandlerMappings.getRegexpPatternForScratchPK();
        Matcher matcher = patternForScratchPK.matcher(originalFileContents);
        StringBuffer replacedText = new StringBuffer(originalFileContents.length());
        int pos = 0;
        while (pos < originalFileContents.length() && matcher.find(pos))
        {
            // First copy the text between matches
            replacedText.append(originalFileContents.substring(pos, matcher.start()));

            // Now find the right scratch pk, could be either the main pk or a mapped one
            String scratchPk;

            // Default way to get a new scratch pk
            if (matcher.groupCount() == 0) // no tag specified
            {
                scratchPk = pk;
            }
            else if (matcher.groupCount() == 1) // invalid pattern
            {
                throw new RuntimeException("Replacing scratch tags with values: Illegal group count of 1. "
                                           + "You must have a group count of 2. Found: " + matcher.group());
            }
            else // else we have a scratch tag reference
            {
                if (matcher.group(1) == null)
                {
                    scratchPk = pk;
                }
                else
                {
                    String scratchTag = matcher.group(2);
                    scratchPk = scratchReferenceToPk.get(scratchTag);
                    if (scratchPk == null) // there was no reference, so quit
                    {
                        if (scratchTag.equals(originalPk))
                        {
                            scratchPk = pk;
                        }
                        else
                        {
                            throw new RuntimeException("Replacing scratch tags with values: Could not find a value for " +
                                                       "scratchTag = " + scratchTag + ". This value must either be set using " +
                                                       "@createScratchObject(file, scratchTag) or can be passed into the " +
                                                       "DOF.createScratchObject() command.");
                        }
                    }
                }
                replacedText.append(scratchPk);
                pos = matcher.end();
            }
        }
        replacedText.append(originalFileContents.substring(pos));

        return replacedText.toString();
    }


    public InputStream getFileContentsAsInputStream()
    {
        return new ByteArrayInputStream(getFileContentsAsString().getBytes());
    }


    public boolean isScratchMode()
    {
        return scratchMode;
    }

    public void setScratchMode(boolean scratchMode)
    {
        this.scratchMode = scratchMode;
    }

    public String getOriginalPk()
    {
        return originalPk;
    }

    public void setOriginalPk(String originalPk)
    {
        this.originalPk = originalPk;
    }

    public String getObjectType()
    {
        return objectType;
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

    /**
     * This is the passed in path of the file to load. Note, you MUST NOT
     * use this to retrieve the file data yourself, as for scratch objects
     * the PK needs to get swapped. So call getFileContentsAsInputStream()
     * or getFileContentsAsString() for the contents of the file.
     * @return path of the file to load, which may be inside the classpath or
     * relative to the DOF_DIR environment variable.
     */
    public String getFileToLoad()
    {
        return fileToLoad;
    }

    public void setFileToLoad(String fileToLoad)
    {
        this.fileToLoad = fileToLoad;
    }

    Map<String, String> getScratchReferenceToPk()
    {
        return scratchReferenceToPk;
    }

    void setScratchReferenceToPk(Map<String, String> scratchReferenceToPk)
    {
        this.scratchReferenceToPk = scratchReferenceToPk;
    }
}
