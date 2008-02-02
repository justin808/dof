package com.ibm.dof;

//import junit.framework.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class DOF
{

    private static Map<String, Object> m_pathToLoadedObject = new HashMap<String, Object>();


    static Pattern repIncludeDependency;

    static void checkRepIncludeDependencyInitialized()
    {
        repIncludeDependency = Pattern.compile("\\$require\\s*\\(\\s*\"(.*)\"\\s*\\);");

    }

    /**
     * The principal entry point for this class. If the object exists in the database, then it is
     * simply returned. If the object does not exist, then it is created. During the creation
     * process, the object definition file may specify other objects that are "required" and those
     * will get loaded recursively depth first.
     *
     * The file processor looks in the definition files for this pattern:
     * $require("{file_name}");
     * where {file_name} might be something like "manufacturer.35.xml"

     * Thus, in an XML file, one would use the commented form:
     * <!-- $require("manufacturer.35.xml"); -->
     *
     * @param fileToLoad File name in form: {objectType}.{objectPk}.{fileType}
     *
     * @return The Object requested
     */
    public static Object require(String fileToLoad)
    {
        return requireWorker(fileToLoad);
    }


    /**
     * @param fileToLoad File name in form: {objectType}.{objectPk}.{fileType}
     */

    public static Object requireWorker(String fileToLoad)
    {
        // First check local cache of loaded files
        Object resultObject = m_pathToLoadedObject.get(fileToLoad);
        if (resultObject == null)
        {
            // Get handler class for object
            String[] fileNameParts = fileToLoad.split("\\.");
            String objectType = fileNameParts[0];
            String pk = fileNameParts[1];
            String fileType = fileNameParts[2].toLowerCase();
            DependentObjectHandler dbJUnitHandler =
                    getHandlerForObject(objectType, fileType);

            loadDependencies(fileToLoad);

            // Now check if object exists in DB
            resultObject = dbJUnitHandler.get(pk);
            if (resultObject == null)
            {
                resultObject = dbJUnitHandler.create(fileToLoad);
            }
            if (resultObject == null)
            {
                throw new RuntimeException("DbJUnitHandler failed to create object with pk " + pk);
            }

            m_pathToLoadedObject.put(fileToLoad, resultObject);
        }
        return resultObject;
    }

    /**
     * Use this method to delete an object. This method will opportunistically try to delete all of
     * the parent dependencies. Note, the deletion is greedy. Even if the object requested to be deleted
     * does not exist, then it's parent objects will still try to be deleted. This method is useful
     * when setting up tests, as the object definition files often need frequent tweaking.
     *
     * @param fileToLoad -- Note the handler class may use the file to load, or just the encoded
     *                   primary key for the deletion.
     *
     * @return true if requested object deleted successfully, false if object could not be deleted,
     *         maybe because another object depends upon it. Note, the return value from deleting
     *         dependency objectsfor the requested object is discarded. For example, if you request
     *         an invoice to be deleted, the return value only reflects if that requested invoice
     *         was deleted, and not if the parent customer record of that invoice is deleted.
     */
    public static boolean delete(String fileToLoad)
    {
        Set<String> processedDeletions = new HashSet<String>();
        return deleteObjectWorker(fileToLoad, processedDeletions);
    }


    /**
     * @param fileToLoad
     * @param processedDeletions
     *
     * @return true if requested object is deleted. Note, the return value from deleting dependency
     *         objects for the requested object is discarded. For example, if you request an invoice
     *         to be deleted, the return value only reflects if that requested invoice was deleted,
     *         and not if the parent customer record of that invoice is deleted.
     */
    static boolean deleteObjectWorker(String fileToLoad, Set<String> processedDeletions)
    {
        processedDeletions.add(fileToLoad);
        String[] fileNameParts = fileToLoad.split("\\.");
        String objectType = fileNameParts[0];
        String pk = fileNameParts[1];
        String fileType = fileNameParts[2].toLowerCase();
        DependentObjectHandler dbJUnitHandler =
                getHandlerForObject(objectType, fileType);

        // delete parent object first
        boolean deletedParent = dbJUnitHandler.delete(pk);

        m_pathToLoadedObject.remove(fileToLoad);

        // then delete the dependencies
        deleteDependencies(fileToLoad, processedDeletions);
        return deletedParent;
    }


    private static void deleteDependencies(String fileToLoad, Set<String> processedDeletions)
    {
        String textForFile = getResourceAsString(fileToLoad);

        ArrayList<String> dependencies = getRequiredDependecies(textForFile);
        for (int i = dependencies.size() - 1; i >= 0; i--)
        {
            String requiredPath = dependencies.get(i);
            try
            {
                if (!processedDeletions.contains(requiredPath))
                {
                    deleteObjectWorker(requiredPath, processedDeletions);
                }
                //else
                //{
                //    System.out
                //            .println("Duplicate dependency caught on deleting: " +
                //                     requiredPath +
                //                     ", already processed = " +
                //                     processedDeletions);
                //}
            }
            catch (Exception e)
            {
                System.out
                        .println("Could not delete path = " +
                                 requiredPath +
                                 ", Possibly other objects depend on this object. " +
                                 e);
            }
        }
    }


    static private void loadDependencies(String fileName)
    {
        String textForFile = getResourceAsString(fileName);

        ArrayList<String> dependencies = getRequiredDependecies(textForFile);
        for (Iterator<String> iterator = dependencies.iterator(); iterator.hasNext();)
        {
            String requiredPath = iterator.next();
            if (!m_pathToLoadedObject.containsKey(requiredPath))
            {
                requireWorker(requiredPath);
            }
            //else
            //{
            //    System.out
            //            .println("Duplicate dependency caught on loading: " +
            //                     requiredPath +
            //                     ", already processed");
            //}
        }

    }


    public static ArrayList<String> getRequiredDependecies(String requireText)
    {
        checkRepIncludeDependencyInitialized();
        Matcher matcher = repIncludeDependency.matcher(requireText);
        int pos = 0;
        ArrayList<String> matches = new ArrayList<String>();
        while (pos < requireText.length() && matcher.find(pos))
        {
            String requireMatch = matcher.group(1);
            pos = matcher.end(0);
            matches.add(requireMatch);
        }
        return matches;
    }


    /**
     * Used to test the test suite
     */
    public static void clearFileCache()
    {
        m_pathToLoadedObject.clear();
    }

    public static String getResourceAsString(String resourceName)
    {
        InputStream sqlInputStream = ClassLoader.getSystemResourceAsStream(resourceName);
        InputStreamReader isr = new InputStreamReader(sqlInputStream);
        BufferedReader br = new BufferedReader(isr);
        String line;
        StringBuffer sb = new StringBuffer();

        try
        {
            while ((line = br.readLine()) != null)
            {
                sb.append(line + "\n");
            }

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static DependentObjectHandler getHandlerForObject(String objectType, String fileType)
    {
        String className = HandlerMappings.getHandlerClassNameForObject(objectType, fileType);
        // todo -- store instance in a map
        try
        {
            Class handlerClass = Class.forName(className);
            return (DependentObjectHandler) handlerClass.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
    }
}
