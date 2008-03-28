package org.doframework;

import java.io.*;

public class TypePkExtensionFileNamePartsProcessor implements FileNamePartsProcessor
{

    public FileNameParts getFileNameParts(String fileToLoad)
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

        FileNameParts fileNameParts = new FileNameParts();
        final String period = ".";
        int firstPeriodIndex = fileToLoad.indexOf(period);
        int lastPeriodIndex = fileToLoad.lastIndexOf(period);

        fileNameParts.objectType = fileToLoad.substring(0, firstPeriodIndex);
        fileNameParts.pk = fileToLoad.substring(firstPeriodIndex + 1, lastPeriodIndex);
        fileNameParts.fileType = fileToLoad.substring(lastPeriodIndex + 1);

        return fileNameParts;
    }
}
