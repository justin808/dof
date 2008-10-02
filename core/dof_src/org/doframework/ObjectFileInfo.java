package org.doframework;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * This class offers information so that a DependentObjectHandler knows how to create an object, either a
 * "reference" object or a "scratch" object from a file.<p/>
 */
public class ObjectFileInfo
{
    /**
     * Like "Customer" or "Invoice" or whatever type of object you are saving
     */
    private String objectType;

    /**
     * Like "customers/Customer.15.xml". Note, the fileToLoad indicates the PK for reference objects. For
     * scratch objects, the name part is simply used to distinguish flavors of scratch objects.
     */
    private String fileToLoad;

    private boolean scratchMode;


    /**
     * This is the original PK -- which differs from the "PK" in scratch mode. I.e., this value reflects the file name,
     * and the "pk" part of that file name
     */
    private String originalPk;

    private String fileContents;
    private Map<String, String> scratchReferenceToPk;
    /**
     * The primary key of the object to create.
     * Note, in the case of a request to create a "scratch" object, the pk will not be representative of what
     * is in the file. Instead, you need to substitute the provided PK with the one you've provided in the file.
     */
    private String pk;
    /**
     * Like "xml"
     */
    private String fileType;


    ObjectFileInfo(String objectType, String pk, String fileType)
    {
        this.fileType = fileType;
        this.pk = pk;
        this.objectType = objectType;

        this.originalPk = pk;
    }

    public String toString()
    {
        return '{' + getFileToLoad() + ", " + getObjectType() + ", " + getPk() + ", " + getFileType() + '}';
    }


    /**
     * @return objectType + ":" + pk
     */
    String getKeyForHashing()
    {
        return DOFObjectCache.getKeyForHashing(DOFGlobalSettings.getInstance().getClassForObjectTypeFileType(getObjectType(), getFileType()) , getPk());
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
            String originalFileContents = DOFGlobalSettings.getResourceAsString(getFileToLoad());
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
        Pattern patternForScratchPK = DOFGlobalSettings.getInstance().getRegexpPatternForScratchPK();
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
                    String scratchReference = matcher.group(2);
                    scratchPk = scratchReferenceToPk.get(scratchReference);
                    if (scratchPk == null) // there was no reference, so quit
                    {
                        if (scratchReference.equals(originalPk))
                        {
                            scratchPk = pk;
                        }
                        else
                        {
                            throw new RuntimeException("Replacing scratch tags with values: Could not find a value for " +
                                                       "scratchReference = " + scratchReference + ". This value must either be set using " +
                                                       "@createScratchObject(file, scratchReference) or can be passed into the " +
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


    /**
     * This is one of two methods for getting the file contents to create a reference or scratch
     * object. In the case of scratch objects, substitutions are made to put in scratch primary keys.
     * @return InputStream with file contents to parse
     */
    public InputStream getFileContentsAsInputStream()
    {
        return new ByteArrayInputStream(getFileContentsAsString().getBytes());
    }


    /**
     * DependentObjectHandler.create() method can use this call to see if the object created
     * is a scratch object.
     * @return
     */
    public boolean isScratchMode()
    {
        return scratchMode;
    }

    void setScratchMode(boolean scratchMode)
    {
        this.scratchMode = scratchMode;
    }


    /**
     * This is the primary key that comes from parsing the file name. For scratch objects, this is
     * simply an identifier on the file.
     * @return primary key that comes from parsing the file name
     */
    public String getFileNamePk()
    {
        return originalPk;
    }

    void setOriginalPk(String originalPk)
    {
        this.originalPk = originalPk;
    }


    /**
     * This is the object type, parsed out of the file name which should correspond to an object
     * types defined in handler_mappings.properties. This does not included the file type.
     * @return The object type, which is parsed out of the file name
     */
    public String getObjectType()
    {
        return objectType;
    }


    /**
     * This is the passed in path of the file to load. Note, you MUST NOT
     * use this to retrieve the file data yourself, as for scratch objects
     * the PK needs to get swapped. So call getFileContentsAsInputStream()
     * or getFileContentsAsString() for the contents of the file.
     * @return path of the file to load, which may be inside the classpath or
     * relative to the DOF_DIR environment variable.
     */
    String getFileToLoad()
    {
        return fileToLoad;
    }

    void setFileToLoad(String fileToLoad)
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


    /**
     * This is the actual logical primary key of the object. For reference objects, this will be equal
     * to calling getFileNamePk(). For scratch objects, this value will be based on the value for
     * use in the scratch object from the designated scratch key provider class.
     * @return logical primary key of the object to be created
     */
    public String getPk()
    {
        return pk;
    }


    void setPk(String pk)
    {
        this.pk = pk;
    }


    /**
     * This is the suffix of the file that is being parsed.
     * @return suffix of the file that is being parsed, like "xml"
     */
    public String getFileType()
    {
        return fileType;
    }
}
