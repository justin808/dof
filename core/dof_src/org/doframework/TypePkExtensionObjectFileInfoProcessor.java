package org.doframework;

import java.io.*;

/**
 * This classs is the default ObjectFileInfoProcessor which takes files named like:
 * objectType.pk.fileExtension, like Customer.25.xml
 */
public class TypePkExtensionObjectFileInfoProcessor implements ObjectFileInfoProcessor
{

    public ObjectFileInfo getObjectFileInfo(String fileToLoad)
    {
        // Assume unix style separator
        int lastPathSeparator = fileToLoad.lastIndexOf('/');
        if (lastPathSeparator >= 0)
        {
            fileToLoad = fileToLoad.substring(lastPathSeparator + 1);
        } // else there was no path separator
        else if (File.separatorChar != '/')
        {
            lastPathSeparator = fileToLoad.lastIndexOf(File.separatorChar);
            if (lastPathSeparator >= 0)
            {
                fileToLoad = fileToLoad.substring(lastPathSeparator + 1);
            } // else there was no path separator
        }

        ObjectFileInfo objectFileInfo = new ObjectFileInfo();
        final String period = ".";
        int firstPeriodIndex = fileToLoad.indexOf(period);
        int lastPeriodIndex = fileToLoad.lastIndexOf(period);

        objectFileInfo.objectType = fileToLoad.substring(0, firstPeriodIndex);
        objectFileInfo.pk = fileToLoad.substring(firstPeriodIndex + 1, lastPeriodIndex);
        objectFileInfo.fileType = fileToLoad.substring(lastPeriodIndex + 1);

        return objectFileInfo;
    }
}
