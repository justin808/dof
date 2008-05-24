package org.doframework;

// Released under the Eclipse Public License-v1.0


import java.io.*;
import java.util.*;
import java.util.regex.*;


/**
 * This class is the engine of the Dependent Object Framework.<p>
 * <p/>
 * The general problem this framework solves is how does a JUnit test ensure that a persistent object needed for a test
 * exists in the database (or any persistent store). Alternative solutions to this problem include running SQL scripts
 * to populate the database and restoring database backups. Both methods are inconvenient.
 * <p/>
 * There are two basic categorizations of objects when running JUnits against a DB.<pre>
 * 1. Reference Objects: Some objects are the same for repeated runs of a single test. These objects can be cached.
 * 2. Scratch Objects: These are objects that you want unique primary keys so that modification to the objects do not
 * affect other tests.
 *
 * The Dependent Object Framework allows the test writer to specify what objects are required for a test. The test
 * writer provides:
 * <p/>
 * 1. An appropriate handler class for each object type. The handler class implements the interface
 * DepedendentObjectHandler and needs to be specfied in a file called handler_mappings.properties that exists somewhere
 * in the classpath or at directory specified as system property <b>DOF_DIR</b>.
 *
 * This class knows how to create, get, and delete objects of a given type and given a format for the
 * description files. Note, the description files can be of any form because the test writer is responsible for writing
 * the code that processes the description files.<p>
 *
 * 2. For each object, a data file containing information to create each object, including the specification of any
 * object dependencies. For example a "product" record might specify what manufacturer record is required. In order to
 * be located by the framework, all DOF data files must exist in the classpath, or they must be under a directory passed
 * as a system parameter "DOF_DIR".
 * <p/>
 * There are only 3 methods that JUnit tests will call: <b>require</b>(fileToLoad), <b>createScratchObject</b> and
 * <b>delete</b>(fileToLoad).<p>
 * <p/>
 * The fileToLoad encodes the object type, the object primary key, and the file type, and it may specify a subdirectory.
 * <p/>
 * Example:
 * <pre>
 * public void testNewInvoiceSubtotal()
 * {
 * // Get objects needed for test
 * Customer johnSmith = (Customer) DOF.require("customer.25.xml");
 * Product coffee = (Product) DOF.require("product.13.xml");
 * Product tea = (Product) DOF.require("product.14.xml");
 * .... rest of the test
 * }
 * </pre>
 * The product file may specify the manufacturer required, note in the XML comment using the form
 * <b>@require("fileToLoad")</b> that indicates the dependency on manufacturer 35.
 * <p/>
 * <pre>
 * product.13.xml
 * &lt;!-- @require("manufacturer.35.xml") --&gt;
 * &lt;product&gt;
 * &lt;id&gt;13&lt;/id&gt;
 * &lt;name&gt;coffee&lt;/name&gt;
 * &lt;price&gt;8.99&lt;/price&gt;
 * &lt;manufacturer_id&gt;35&lt;/manufacturer_id&gt;
 * &lt;/product&gt;
 * </pre>
 * <b>Configuration</b><p/>
 * The user of the DOF is responsible for having a file named "handler_mappings.properties" located in the classpath or
 * at directory specified as VM system property DOF_DIR. Please consult the sample file for more documentation on
 * configurable parameters. The key aspect of the properties file is the mapping of object types to handlers:
 * <p/>
 * <code>objectType.fileSuffix=DependentObjectHandlerImplementationClassName</code>
 * <p/>
 * This is an example of a line in the mappings file:
 * <p/>
 * <code>customer.xml=dof_xml_handler.CustomerXmlFactory</code>
 * <p/>
 * It states that a customer.PK.xml file maps to the handler class dof_xml_handler.CustomerXmlFactory Note, the
 * CustomerXmlFactory class must implement interface <b>DependentObjectHandler</b>. Even though fileToLoad uses the
 * period as the delimiter, the primary key may contain periods because the first and last periods are used to find the
 * object type and the file suffix. This also means that object types may NOT contain a period.
 * <p/>
 * You may specify regexp matches like this. Regexps to be recognized as such will contain at least one '*', '?', or '+'.
 * <pre>
 * {regular expression}=class
 * \\w+\\.xml=dof_xml_handler.GenericXmlFactory
 * # NOTE: you must use double backslashes to mean a backslash
 * </pre>
 * For regexp documentation, consult the javadoc for java.util.regexp.Pattern<p/>

 * In case you do not like the form of "objectType.PK.fileType", you may specify a custom ObjectFileInfoProcessor class
 * by putting in this property in file handler_mappings.properties
 * <pre>
 * ObjectFileInfoProcessor=FullClassName
 * </pre>
 *
 *
 *
 *
 *
 *
 * </p>
 * Note: For verbose output of when objects are created and deleted, set system property <b>-DDOF_DEBUG=TRUE</b><p/>
 * Note: For a listing of how file contents with scratch keys get replaced, set system property <b>-DDOF_PRINT_FILES=TRUE</b>
 *
 * @author Justin Gordon
 * @date January, 2008
 * @see DependentObjectHandler
 * @see ObjectFileInfoProcessor
 */
public class DOF
{
    //////////////////////////////////////////////////////////////////////////////////////////////
    // public API ////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * The principal entry point for this class. If the object was previously requested, it is returned from map of the
     * file name to the object. Otherwise, the database is then checked. If the object exists in the database (or
     * whatever persistent store used by the handler classes), then it is simply returned. If the object does not exist,
     * then it is created. During the creation process, the object definition file may specify other objects that are
     * "required" and those will get loaded recursively depth first. Thus, if object A depends on object B that depends
     * on object C, then a request for object A invokes the request for object B which invokes the request for object
     * C.
     * <p/>
     * Note that the fileToLoad uses a specific format to define the file to load which encodes the:
     * <pre>
     * 1. Object Type
     * 2. Object PK
     * 3. Object File Type (like xml)
     * </pre>
     * <p/>
     * The file processor looks for dependencies in the definition files using this pattern:<p>
     * <b>@require("{object_type.object_pk.extension}")</b>
     * <p/>
     * where {object_type.object_pk.extension} might be something like "manufacturer.35.xml".
     * <p/>
     * Thus, in an XML file, one would use the commented form:<p> <code> &lt;!-- @require("manufacturer.35.xml") --&gt;
     * </code><p> Even though fileToLoad uses the period as the delimiter, the primary key may contain periods because
     * the first and last periods are used to find the object type and the file suffix. This also means that object
     * types may NOT contain a period.
     *
     * @param fileToLoad File name in form: {objectType}.{objectPk}.{fileType}
     *
     * @param scratchReferenceToPk
     * @return The Object requested
     */
    public static Object require(String fileToLoad, Map<String, String> scratchReferenceToPk)
    {
        FileToLoadAndScratchTag so = new FileToLoadAndScratchTag();
        so.fileToLoad = fileToLoad;
        so.scratchTag = null;
        return requireWorker(so, false, scratchReferenceToPk);
    }

    public static Object require(String fileToLoad)
    {
        return require(fileToLoad, new HashMap<String, String>());
    }


    /**
     *
     * @param fileToLoad
     * @param scratchReferenceToPk
     * @return
     */
    public static Object createScratchObject(String fileToLoad, Map<String, String> scratchReferenceToPk)
    {
        FileToLoadAndScratchTag so = new FileToLoadAndScratchTag();
        so.fileToLoad = fileToLoad;
        so.scratchTag = null;
        return requireWorker(so, true, scratchReferenceToPk);
    }

    public static Object createScratchObject(String fileToLoad)
    {
        return createScratchObject(fileToLoad, new HashMap<String, String>());
    }


    /**
     * Use this method to delete an object. This method will opportunistically try to delete all of the parent
     * dependencies. Note, the deletion is greedy. Even if the object requested to be deleted does not exist, then it's
     * parent objects will still try to be deleted. This method is useful when setting up tests, as the object
     * definition files often need frequent tweaking. Note, it is critical that objects created with the require method
     * be deleted using this method because the require method caches the created objects by the file name.
     * <p/>
     * Note, this method takes the same parameter as the require method to facilitate copying and pasting the require
     * line.
     *
     * @param fileToLoad File name in form: {objectType}.{objectPk}.{fileType}
     *
     * @return true if requested object deleted successfully, false if object could not be deleted, maybe because
     *         another object depends upon it. Note, the return value from deleting dependency objects for the requested
     *         object is discarded. For example, if you request an invoice to be deleted, the return value only reflects
     *         if that requested invoice was deleted, and not if the parent customer record of that invoice is deleted.
     *         If the object does not exist, then theis method returns true, indicating that object was previously deleted.
     */
    public static boolean delete(String fileToLoad)
    {
        Set<String> processedDeletions = new HashSet<String>();
        return deleteObjectWorker(fileToLoad, processedDeletions);
    }


    /**
     * Clears the cache (map) of {file loaded} to {object returned}.
     * <p/>
     * The below example demonstrates how the objects returned on successive calls to <b>require</b> with the same
     * fileToLoad return the same object, unless the object is deleted, or the file cache is cleared. A reason this
     * might be done is that code changes the object returned and persists it, and you want to fetch the object cleanly
     * from the database again using a DOF.require call. Typically, you won't need to use this method, as the file cache
     * speeds up performance of your tests.
     * <pre>
     * public void testDeleteManufacturer()
     * {
     * Manufacturer m1 = (Manufacturer) DOF.require("manufacturer.20.xml");
     * assertTrue(DOF.delete("manufacturer.20.xml"));
     * Manufacturer m2 = (Manufacturer) DOF.require("manufacturer.20.xml");
     * assertNotSame(m1, m2);
     * Manufacturer m3 = (Manufacturer) DOF.require("manufacturer.20.xml");
     * assertSame(m2, m3);
     * //DOF.clearFileCache();
     * Manufacturer m4 = (Manufacturer) DOF.require("manufacturer.20.xml");
     * assertNotSame(m3, m4);
     * }
     * </pre>
     */
    public static void clearFileCache()
    {
        objectTypePkToLoadedObject.clear();
    }


    /**
     * Used to take the fileToLoad and to convert it into the ObjectFileInfo
     *
     * @param fileToLoad, which may contain a leading path
     *
     * @return The ObjectFileInfo containing the object type, pk, and file type
     */
    public static ObjectFileInfo getObjectFileInfo(String fileToLoad)
    {
        ObjectFileInfo ofi = HandlerMappings.getObjectFileInfoProcessor().getObjectFileInfo(fileToLoad);
        ofi.setFileToLoad(fileToLoad);
        return ofi;
    }


    /**
     * If property DOF_DEFS_DIR is defined, then the files are retrieved from that directory, or else the files are
     * retrieved using ClassLoader.getSystemResourceAsStream
     *
     * @param resourceName
     *
     * @return
     */
    static String getResourceAsString(String resourceName)
    {
        if (DOF_DIR.length() == 0)
        {
            return getResourceAsStringFromClassLoader(resourceName);
        }
        else
        {
            return getResourceAsStringFromDofDefsDir(resourceName);
        }
    }


    /**
     * Used to get a fileToLoad's absolute path if you are setting system property DOF_DIR
     * to place the object files
     * @param fileToLoad
     * @return The absolute path to the resource with which is DOF_DIR plus File.separator + resourceName
     */
    public static String getAbsolutePath(String fileToLoad)
    {
        return DOF_DIR + File.separator + fileToLoad;
    }

    /**
     * Set this to true to print messages to System.out when objects are loaded or deleted
     * from the database. This property can also be set by setting VM System Property:<p/>
     * -DDOF_DEBUG=TRUE
     *
     * @param b
     */
    public static void setDofDebug(boolean b)
    {
        dofDebug = b;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // private members and methods ///////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    private static final Map<String, DependentObjectHandler> classNameToInstance = new HashMap<String, DependentObjectHandler>();

    /**
     * we keep a cache of the loaded objects to avoid searching the DB every time.
     */
    private static final Map<String, Object> objectTypePkToLoadedObject = new HashMap<String, Object>();

    private static Pattern repIncludeDependency;
    private static Pattern repIncludeScratch;

    static final String DOF_DIR = System.getProperty("DOF_DIR", "");

    static boolean dofDebug = System.getProperty("DOF_DEBUG", "").equalsIgnoreCase("TRUE");

    static boolean dofPrintDescriptionFiles = System.getProperty("DOF_PRINT_FILES", "").equalsIgnoreCase("TRUE");

    private static void checkRepIncludeDependencyInitialized()
    {
        // Original pattern -- use the @require("type.pk.ext")
        //repIncludeDependency = Pattern.compile("\\$require\\s*\\(\\s*\"(.*)\"\\s*\\);");

        // Changed 3/20/2008 to use annotation format:
        // Leading @ and removed the trailing semicolon
        if (repIncludeDependency == null)
        {
            repIncludeDependency = Pattern.compile("@require\\s*\\(\\s*\"(.*)\"\\s*\\)");
            repIncludeScratch = Pattern.compile("@createScratchObject\\s*\\(\\s*\"(.*)\"\\s*,\\s*\"(.*)\\s*\"\\)"); 
        }
    }


    /**
     * No need for constructor.
     */
    private DOF()
    {
    }


    /**
     * @param fileToLoadAndScratchTag
     * @param isScratchObject
     * @param scratchReferenceToPk
     */

    private static Object requireWorker(FileToLoadAndScratchTag fileToLoadAndScratchTag, boolean isScratchObject,
                                        Map<String, String> scratchReferenceToPk)
    {
        // First check local cache of loaded files
        //isScratchObject = true;
        Object resultObject = null;
        ObjectFileInfo objectFileInfo = getObjectFileInfo(fileToLoadAndScratchTag.fileToLoad);
        final boolean hasScratchTag = fileToLoadAndScratchTag.scratchTag != null && fileToLoadAndScratchTag.scratchTag.length() > 0;
        String scratchPk = scratchReferenceToPk.get(fileToLoadAndScratchTag.scratchTag);
        if (scratchPk != null)
        {
            // Check if object exists in DB
            objectFileInfo.setPk(scratchPk);
        }

        resultObject = objectTypePkToLoadedObject.get(objectFileInfo.getKeyForHashing());
        if (resultObject == null)
        {
            // Get handler class for object
            String objectType = objectFileInfo.getObjectType();
            String pk = objectFileInfo.getPk();
            String fileType = objectFileInfo.getFileType().toLowerCase();

            DependentObjectHandler dependentObjectHandler = getHandlerForObject(objectType, fileType);

            if (!isScratchObject)
            {
                // Now check if object exists in DB
                resultObject = dependentObjectHandler.get(objectFileInfo);
            }

            if (resultObject == null)
            {
                loadDependencies(fileToLoadAndScratchTag.fileToLoad, scratchReferenceToPk);

                loadDependencyScratchObjects(fileToLoadAndScratchTag.fileToLoad, scratchReferenceToPk);

                if (dofDebug)
                {
                    System.out.println("DOF: Loading: ObjectFileInfo = " + objectFileInfo);
                }

                if (isScratchObject)
                {
                    objectFileInfo.setScratchMode(true);
                    // else make a new scratch PK if not tag 
                    if (scratchPk == null)
                    {
                        scratchPk = getScratchPk(dependentObjectHandler);
                        if (hasScratchTag)
                        {
                            scratchReferenceToPk.put(fileToLoadAndScratchTag.scratchTag,scratchPk);
                        }
                        objectFileInfo.setPk(scratchPk);
                    }
                }
                objectFileInfo.setScratchReferenceToPk(scratchReferenceToPk);
                resultObject = dependentObjectHandler.create(objectFileInfo);
                // There may be a problem with this in case we want to use the same scratch file twice
                // So long as the reference is different, we should be OK
                objectTypePkToLoadedObject.put(objectFileInfo.getKeyForHashing(), resultObject);
            }
            if (resultObject == null)
            {
                throw new RuntimeException("DbJUnitHandler failed to create object with pk " + pk);
            }

        }
        return resultObject;
    }

    private static String getScratchPk(DependentObjectHandler dofHandler)
    {
        String scratchPk;
        if (dofHandler instanceof ScratchPkProvider)
        {
            scratchPk = ((ScratchPkProvider) dofHandler).getScratchPk();
        }
        else if (defaultScratchPrimaryKeyProvider != null)
        {
            scratchPk = defaultScratchPrimaryKeyProvider.getScratchPk();
        }
        else
        {
            scratchPk = System.currentTimeMillis() + "";
        }
        return scratchPk;
    }


    /**
     * @param fileToLoad
     * @param processedDeletions
     *
     * @return true if requested object is deleted. Note, the return value from deleting dependency objects for the
     *         requested object is discarded. For example, if you request an invoice to be deleted, the return value
     *         only reflects if that requested invoice was deleted, and not if the parent customer record of that
     *         invoice is deleted.
     */
    private static boolean deleteObjectWorker(String fileToLoad, Set<String> processedDeletions)
    {
        ObjectFileInfo objectFileInfo = getObjectFileInfo(fileToLoad);
        String objectType = objectFileInfo.getObjectType();
        String fileType = objectFileInfo.getFileType().toLowerCase();

        DependentObjectHandler doh = getHandlerForObject(objectType, fileType);

        // delete parent object first
        boolean deletedParent;
        Object objectToDelete = doh.get(objectFileInfo);
        if (objectToDelete != null)
        {
            deletedParent = doh.delete(objectFileInfo, objectToDelete);
        }
        else
        {
            deletedParent = true; // b/c already deleted
        }
        if (dofDebug)
        {
            System.out.println("DOF: Deleting: ObjectFileInfo = " + objectFileInfo + ", deleted = " + deletedParent);
        }

        if (deletedParent)
        {
            processedDeletions.add(fileToLoad);
        }

        objectTypePkToLoadedObject.remove(fileToLoad);

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
                        .println("Could not delete path = " + requiredPath +
                                 ", Possibly other objects depend on this object. " + e);
            }
        }
    }


    private static void loadDependencies(String fileName, Map<String, String> scratchReferenceToPk)
    {
        String textForFile = getResourceAsString(fileName);

        ArrayList<String> dependencies = getRequiredDependecies(textForFile);
        for (Iterator<String> iterator = dependencies.iterator(); iterator.hasNext();)
        {
            String requiredPath = iterator.next();
            if (requiredPath.equals(fileName))
            {
                System.out.println("WARNING: You listed a file a file as a dependency of itself. Please see: " + fileName);
                continue;
            }

            if (!objectTypePkToLoadedObject.containsKey(requiredPath))
            {
                FileToLoadAndScratchTag so = new FileToLoadAndScratchTag();
                so.fileToLoad = requiredPath;
                so.scratchTag = null;
                requireWorker(so, false, scratchReferenceToPk);
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

    private static void loadDependencyScratchObjects(String fileName, Map<String, String> scratchReferenceToPk)
    {

        String textForFile = getResourceAsString(fileName);

        ArrayList<FileToLoadAndScratchTag> dependencies = getRequiredScratchObjects(textForFile);
        for (Iterator<FileToLoadAndScratchTag> iterator = dependencies.iterator(); iterator.hasNext();)
        {
            FileToLoadAndScratchTag fileToLoadAndScratchTag = iterator.next();
            String requiredPath = fileToLoadAndScratchTag.fileToLoad;

            if (requiredPath.equals(fileName))
            {
                System.out.println("WARNING: You listed a file a file as a dependency of itself. Please see: " + fileName);
                continue;
            }

            if (!objectTypePkToLoadedObject.containsKey(requiredPath))
            {
                requireWorker(fileToLoadAndScratchTag, true, scratchReferenceToPk);
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


    private static ArrayList<String> getRequiredDependecies(String requireText)
    {
        checkRepIncludeDependencyInitialized();
        Matcher matcher = repIncludeDependency.matcher(requireText);
        int pos = 0;
        ArrayList<String> matches = new ArrayList<String>();
        while (pos < requireText.length() && matcher.find(pos))
        {
            String requireMatch = matcher.group(1);
            pos = matcher.end();
            matches.add(requireMatch);
        }
        return matches;
    }

    static class FileToLoadAndScratchTag
    {
        String fileToLoad;
        String scratchTag;

        public int hashCode()
        {
            return (getComparisionKey()).hashCode();//To change body of overridden methods use File | Settings | File Templates.
        }

        public boolean equals(Object obj)
        {
            if (obj instanceof FileToLoadAndScratchTag)
            {
                FileToLoadAndScratchTag so = (FileToLoadAndScratchTag) obj;
                return (getComparisionKey()).equals(so.getComparisionKey());
            }
            else
            {
                return false;
            }

        }

        private String getComparisionKey()
        {
            return fileToLoad + ":" + scratchTag;
        }

        public String toString()
        {
            return getComparisionKey();
        }
    }

    private static ArrayList<FileToLoadAndScratchTag> getRequiredScratchObjects(String requireText)
    {
        checkRepIncludeDependencyInitialized();
        Matcher matcher = repIncludeScratch.matcher(requireText);
        int pos = 0;
        ArrayList<FileToLoadAndScratchTag> matches = new ArrayList<FileToLoadAndScratchTag>();
        while (pos < requireText.length() && matcher.find(pos))
        {
            FileToLoadAndScratchTag fileToLoadAndScratchTag = new FileToLoadAndScratchTag();
            fileToLoadAndScratchTag.fileToLoad = matcher.group(1);
            fileToLoadAndScratchTag.scratchTag = matcher.group(2);
            pos = matcher.end();
            matches.add(fileToLoadAndScratchTag);
        }
        return matches;
    }


    private static String getResourceAsStringFromDofDefsDir(String resourceName)
    {
        final String resourceAbsolutePath = getAbsolutePath(resourceName);
        InputStreamReader isr = getInputStreamReaderForPath(resourceAbsolutePath);

        StringBuffer sb = readContentsOfInputStream(isr);
        return sb.toString();
    }


    private static InputStreamReader getInputStreamReaderForPath(String resourceAbsolutePath)
    {
        File file = new File(resourceAbsolutePath);
        if (!file.exists())
        {
            throw new RuntimeException("File not found: " + file.getAbsolutePath());
        }
        BufferedInputStream bis;
        try
        {
            bis = new BufferedInputStream(new FileInputStream(file));
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        return new InputStreamReader(bis);
    }


    private static StringBuffer readContentsOfInputStream(InputStreamReader isr)
    {
        StringBuffer sb;
        BufferedReader br = new BufferedReader(isr);
        String line;
        sb = new StringBuffer();
        try
        {
            while ((line = br.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return sb;
    }


    private static String getResourceAsStringFromClassLoader(String resourceName)
    {
        try
        {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
            InputStreamReader isr = new InputStreamReader(inputStream);
            StringBuffer sb = readContentsOfInputStream(isr);
            return sb.toString();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Caught error loading resource from classpath: '"
                                       + resourceName
                                       + "'. Possibly resource does not exist.", e);
        }
    }


    static DependentObjectHandler getHandlerForObject(String objectType, String fileType)
    {
        String className = HandlerMappings.getHandlerClassNameForObject(objectType, fileType);
        if (className == null)
        {
            throw new RuntimeException("Could not find class name for objectType: " + objectType + ", fileType = " + fileType);
        }
        try
        {
            DependentObjectHandler doh = classNameToInstance.get(className);
            if (doh == null)
            {
                Class<? extends DependentObjectHandler> handlerClass =
                        (Class<? extends DependentObjectHandler>) Class.forName(className);
                doh = handlerClass.newInstance();
                classNameToInstance.put(className, doh);
            }
            return doh;
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

    private static ScratchPkProvider defaultScratchPrimaryKeyProvider;

    static
    {
        String defaultScratchPrimaryKeyProviderClassName = HandlerMappings.getDefaultScratchPrimaryKeyProviderClassName();
        if (defaultScratchPrimaryKeyProviderClassName != null && defaultScratchPrimaryKeyProviderClassName.length() > 0)
        {
            try
            {
                Class<? extends ScratchPkProvider> scratchClass =
                        (Class<? extends ScratchPkProvider>) Class.forName(defaultScratchPrimaryKeyProviderClassName);
                defaultScratchPrimaryKeyProvider = scratchClass.newInstance();
            }
            catch (Throwable e)
            {
                throw new RuntimeException(e);
            }
        }
    }


}
