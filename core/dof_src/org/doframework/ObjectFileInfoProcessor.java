package org.doframework;

/**
 * Strategy class to convert break file name into org.doframework.ObjectFileInfo,
 * which has the object type, the pk, and the extension
 */
public interface ObjectFileInfoProcessor
{
    /**
     * Take the file name (will include any relative path information) and return the ObjectFileInfo.
     * Note, you may want to strip out all the characters up to File.separator with code like
     * <pre>
     int lastPathSeparator = fileToLoad.lastIndexOf(File.pathSeparatorChar);
     if (lastPathSeparator >= 0)
     {
       fileToLoad = fileToLoad.substring(lastPathSeparator + 1);
     } // else there was no path separator
     *
     * </pre>
     * @param fileToLoad The file name to parse
     * @return org.doframework.ObjectFileInfo given the file to Load
     */
    ObjectFileInfo getObjectFileInfo(String fileToLoad);
}
