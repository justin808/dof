package org.doframework;

// Released under the Eclipse Public License-v1.0

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;


/**
 This is helper class for that handles the mappings of the Handler classes to the object types. The user of the DOF is
 responsible for having a file "handler_mappings.properties" located in the classpath
 <p/>
 The format of the properties file is:
 <p/>
 <code>objectType.fileSuffix=DependentObjectHandlerImplementationClassName</code>
 <p/>
 This is an example of a line in the mappings file:
 <p/>
 <code>customer.xml=dof_xml_handler.CustomerXmlFactory</code>
 <p/>

 It states that a customer.PK.xml file maps to the handler class dof_xml_handler.CustomerXmlFactory Note, the
 CustomerXmlFactory class must implement interface <b>DependentObjectHandler</b>

 Even though fileToLoad uses the period as the delimiter, the primary key may contain periods because the first and last periods are used to find the
 object type and the file suffix. This also means that object types may NOT contain a period
 (if you are using the default org.doframework.TypePkExtensionObjectFileInfoProcessor)
  <p/>
  You may specify a custom ObjectFileInfoProcessor class in case you do not like the form of objectType.PK.fileType.
  Do this by putting in a property in file handler_mappings.properties
  <pre>
  ObjectFileInfoProcessor=FullClassName
  </pre>
  <p/>
  Specify regexp matches like this (note the "RE:" signifies regexp, and it is removed from the expression
  <pre>
  RE:{regular expression}=class
  RE:\w+\.xml=dof_xml_handler.GenericXmlFactory
  </pre>
  For regexp documentation, consult http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html<p/>

 @author Justin Gordon
 @date January, 2008


 */


class DOFGlobalSettings
{
    static final String DOF_PREFERENCES = "dof_preferences.properties";
    static final String HANDLER_MAPPINGS_PROPERTIES_NAME = "handler_mappings.properties";
    static final String SCRATCH_OBJECT_DELETION_HELPER_PROPERTIES_NAME = "deletion_mappings.properties";


    public static final String MAX_CACHED_SCRATCH_OBJECTS = "MaxCachedScratchObjects";
    public static final String MAX_CACHED_REFERENCE_OBJECTS = "MaxCachedReferenceObjects";
    public static final String DEFAULT_SCRATCH_PRIMARY_KEY_PROVIDER =
            "DefaultScratchPrimaryKeyProvider";
    public static final String OBJECT_FILE_INFO_PROCESSOR_PROPERTY = "ObjectFileInfoProcessor";
    public static final String DEFAULT_OBJECT_FILE_INFO_PROCESSOR = "org.doframework.TypePkExtensionObjectFileInfoProcessor";
    static final String DOF_DIR = System.getProperty("DOF_DIR", "");
    static DOFGlobalSettings instance;

    static
    {
        instance = new DOFGlobalSettings();
        instance.initialize();
    }



    Properties handlerMappings;
    Properties dofPreferences;
    Properties scratchDeletionHelpers;

    Map<String,String> propertyFileToResource = new HashMap<String, String>();
    /**
     * This is the property to specify a custom ObjectFileInfoProcessor
     */
    ObjectFileInfoProcessor objectFileInfoProcessor;

    Map<Pattern, String> compiledPatterns;


    Pattern SCRATCH_PK_PATTERN;


    Map<String, ObjectDeletionHelper> classNameToScratchObjectDeletionHelper =
            new HashMap<String, ObjectDeletionHelper>();

    /**
     *  Mapping of the entries in the handler_mappings.properties to the appropriate classes
     */
    Map<String, Class> objectTypeFileTypeToDOFHandlerClass = new HashMap<String, Class>();

    Map<String, DependentObjectHandler> dofHandlerClassNameToInstance =
            new HashMap<String, DependentObjectHandler>();

    /**
     * This is the optimal order to try deleting objects, based on which objects depend on other
     * objects.
     */
    List<Class> objectDeletionOrder;




    /**
     * @param objectClassName
     *
     * @return
     */
    ObjectDeletionHelper getScratchDeletionHelperForClass(String objectClassName)
    {
        return classNameToScratchObjectDeletionHelper.get(objectClassName);
    }


    /**
     * Get the file name parts processor class
     *
     * @return The fully qualified class name that implements the interface
     *         org.doframework.ObjectFileInfoProcessor
     */
    ObjectFileInfoProcessor getObjectFileInfoProcessor()
    {
        if (objectFileInfoProcessor == null)
        {
            String c = dofPreferences.getProperty(OBJECT_FILE_INFO_PROCESSOR_PROPERTY,
                                                  DEFAULT_OBJECT_FILE_INFO_PROCESSOR);
            try
            {
                Class objectFileInfoProcessorClass = Class.forName(c);
                objectFileInfoProcessor = (ObjectFileInfoProcessor) objectFileInfoProcessorClass.newInstance();
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
        return objectFileInfoProcessor;
    }


    DependentObjectHandler getDependentObjectHandlerForObjectTypeFileType(String objectType, String fileType)
    {
        String className = getDOFHandlerClassName(objectType, fileType);
        if (className == null)
        {
            throw new RuntimeException("Could not find class name for objectType: " + objectType +
                                       ", fileType = " + fileType);
        }
        return getDofHandlerInstanceForClassName(className);
    }


    /**
     * Determine the most efficient order for deletion. Delete those objects that have the least
     * other objects dependendent upon them. For example, if invoices depend on products that
     * depend on manufacturers, return { Invoice.class, Product.class, Manufacturer.class}
     * @return List of classes in the optimal order to delete objects
     */
    public List<Class> getObjectDeletionOrder()
    {
        if (objectDeletionOrder == null)
        {
            objectDeletionOrder = calcObjectDeletionOrder(classNameToScratchObjectDeletionHelper);
        }
        return objectDeletionOrder;
    }


    public Class getClassForObjectTypeFileType(String objectType, String fileType)
    {
        return getDependentObjectHandlerForObjectTypeFileType(objectType, fileType)
                .getCreatedClass();
    }


    /**
     * Return the pattern for matching the scratch PK. This is a regexp and must contain one
     * grouping.
     *
     * @return the regexp for matching the scratch pk
     */
    Pattern getRegexpPatternForScratchPK()
    {
        if (SCRATCH_PK_PATTERN == null)
        {
            String s = handlerMappings
                    .getProperty("ScratchPrimaryKeyPattern", "\\{\\{pk(\\:(\\w+))?\\}\\}");
            if (DOF.dofDebug)
            {
                System.out.println("ScratchPrimaryKeyPattern=" + s);
            }
            SCRATCH_PK_PATTERN = Pattern.compile(s);
        }
        return SCRATCH_PK_PATTERN;
    }


    int getMaxCachedScratchObjects()
    {
        return Integer.parseInt(dofPreferences.getProperty(MAX_CACHED_SCRATCH_OBJECTS));
    }


    int getMaxCachedReferenceObjects()
    {
        return Integer.parseInt(dofPreferences.getProperty(MAX_CACHED_REFERENCE_OBJECTS));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////  PRIVATE METHODS  /////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    void initialize()
    {
        parseHandlerMappings();

        dofPreferences = getPropertiesFromFileName(DOF_PREFERENCES, false);

        parseScratchObjectDeletionHelperMappings();
    }


    void parseScratchObjectDeletionHelperMappings()
    {
        Properties scratchObjectDeletionHelperProperties =
                getPropertiesFromFileName(SCRATCH_OBJECT_DELETION_HELPER_PROPERTIES_NAME, true);
        if (scratchObjectDeletionHelperProperties != null)
        {
            Set<Map.Entry<Object, Object>> entries = scratchObjectDeletionHelperProperties.entrySet();
            for (Iterator entryIterator = entries.iterator(); entryIterator.hasNext();)
            {
                Map.Entry<Object, Object> objectObjectEntry = (Map.Entry<Object, Object>) entryIterator.next();
                String scratchObjectDeletionHelperClassName = (String) objectObjectEntry.getValue();
                String scratchObjectClassName = (String) objectObjectEntry.getKey();
                try
                {
                    ObjectDeletionHelper scratchObjectDeletionHelper =
                            (ObjectDeletionHelper) Class
                                    .forName(scratchObjectDeletionHelperClassName).newInstance();
                    classNameToScratchObjectDeletionHelper
                            .put(scratchObjectClassName, scratchObjectDeletionHelper);
                }
                catch (ClassNotFoundException e)
                {
                    throw new RuntimeException("Class name not found: " + scratchObjectDeletionHelperClassName +
                                               "\nPlease examine contents of file: " +
                                               propertyFileToResource
                                                       .get(HANDLER_MAPPINGS_PROPERTIES_NAME));
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
    }


    private void parseHandlerMappings()
    {
        handlerMappings = getPropertiesFromFileName(HANDLER_MAPPINGS_PROPERTIES_NAME, false);
        // Process the optional mapping to class names
        Set<Map.Entry<Object, Object>> entries = handlerMappings.entrySet();
        for (Iterator entryIterator = entries.iterator(); entryIterator.hasNext();)
        {
            Map.Entry<Object, Object> objectObjectEntry = (Map.Entry<Object, Object>) entryIterator.next();
            String handlerClass = (String) objectObjectEntry.getValue();
            String objectTypeFileType = (String) objectObjectEntry.getKey();

            int locPeriod = objectTypeFileType.lastIndexOf('.');
            if (locPeriod == -1)
            {
                throw new RuntimeException("Object type for: " + objectTypeFileType +
                                           " improperly defined. It should like " +
                                           "{objectType}.{fileExtension}, like \"Customer.xml\"" +
                                           "\nPlease examine contents of file: " +
                                           propertyFileToResource
                                                   .get(HANDLER_MAPPINGS_PROPERTIES_NAME));

            }
            try
            {
                objectTypeFileTypeToDOFHandlerClass.put(objectTypeFileType, Class.forName(handlerClass));
                DependentObjectHandler doh = getDofHandlerInstanceForClassName(handlerClass);
                if (doh instanceof ObjectDeletionHelper)
                {
                    classNameToScratchObjectDeletionHelper
                            .put(doh.getCreatedClass().getName(), (ObjectDeletionHelper) doh);
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Class name not found: " + handlerClass +
                                           "\nPlease examine contents of file: " +
                                           propertyFileToResource
                                                   .get(HANDLER_MAPPINGS_PROPERTIES_NAME));
            }
        }
    }


    private Properties getPropertiesFromFileName(String propertyFileName, boolean okIfNoFile)
    {
        Properties properties = new Properties();
        InputStream inputStream =
                ClassLoader.getSystemResourceAsStream(propertyFileName);
        if (inputStream == null)
        {
            if (DOF_DIR.length() > 0)
            {
                String resourceAbsolutePath = getAbsolutePath(propertyFileName);
                File file = new File(resourceAbsolutePath);
                String filePath = file.getAbsolutePath();
                try
                {
                    inputStream = new BufferedInputStream(new FileInputStream(file));
                    System.out.println("Loaded file " + filePath);
                    propertyFileToResource.put(propertyFileName, filePath + "");

                }
                catch (FileNotFoundException e)
                {
                    if(!okIfNoFile)
                    {
                        throw new RuntimeException("You must put '" + propertyFileName + '\'' +
                                                   " in the classpath or under directory defined by system property " +
                                                   "DOF_DIR: '" + DOF_DIR + "': " + filePath);
                    }
                    else
                    {
                        return null;
                    }

                }
            }
            else
            {
                if (!okIfNoFile)
                {
                    throw new RuntimeException("You must put '" + propertyFileName + '\'' +
                                           " in the classpath or under directory defined by system property " +
                                           "DOF_DIR '");
                }
                else
                {
                    return null;
                }
            }
        }
        else
        {
            URL resource = ClassLoader.getSystemResource(HANDLER_MAPPINGS_PROPERTIES_NAME);
            System.out.println("Loaded " + propertyFileName + " from " + resource);
            propertyFileToResource.put(propertyFileName, resource + "");
        }
        try
        {
            properties.load(inputStream);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            // Make sure the input stream is always closed.
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }




    String getDOFHandlerClassName(String objectType, String fileType)
    {
        String key = objectType + '.' + fileType;
        String exactMatch = (String) handlerMappings.get(key);
        if (exactMatch != null)
        {
            return exactMatch;
        }

        initCompiledPatterns();
        for (Map.Entry<Pattern, String> entry : compiledPatterns.entrySet() )
        {
            Pattern p = entry.getKey();
            Matcher m = p.matcher(key);
            if (m.matches())
            {
                return entry.getValue();
            }
        }
        return null;
    }

    void initCompiledPatterns()
    {
        if (compiledPatterns == null)
        {
            Pattern isRegexp = Pattern.compile("[?+*]");
            compiledPatterns = new HashMap(handlerMappings.size());
            // try a regexp match
            for (Map.Entry entry : handlerMappings.entrySet())
            {
                String sKey = (String) entry.getKey();
                Matcher m = isRegexp.matcher(sKey);
                if (m.find())
                {
                    Pattern p = null;
                    try
                    {
                        p = Pattern.compile(sKey);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Caught exception in compiling regexp patter " + sKey + ", error was " + e);
                    }
                    compiledPatterns.put(p, (String) entry.getValue());
                }
            }
        }
    }






    String getDefaultScratchPrimaryKeyProviderClassName()
    {
        return dofPreferences.getProperty(DEFAULT_SCRATCH_PRIMARY_KEY_PROVIDER);
    }


    /**
     No need for public constructor
     */
    DOFGlobalSettings()
    {
    }



    DependentObjectHandler getDofHandlerInstanceForClassName(String className)
    {
        try
        {
            DependentObjectHandler doh = dofHandlerClassNameToInstance.get(className);
            if (doh == null)
            {
                Class<? extends DependentObjectHandler> handlerClass =
                        (Class<? extends DependentObjectHandler>) Class.forName(className);
                doh = handlerClass.newInstance();
                dofHandlerClassNameToInstance.put(className, doh);
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





    static List<Class> calcObjectDeletionOrder(
            Map<String, ObjectDeletionHelper> classNameToScratchObjectDeletionHelperMap)
    {
        List<Class> sortedClasses = new ArrayList<Class>();
        try
        {
            Set<Map.Entry<String, ObjectDeletionHelper>> entries =
                    classNameToScratchObjectDeletionHelperMap.entrySet();
            for (Iterator entryIterator = entries.iterator(); entryIterator.hasNext();)
            {
                Map.Entry<String, ObjectDeletionHelper> stringScratchObjectDeletionHelperEntry =
                        (Map.Entry<String, ObjectDeletionHelper>) entryIterator.next();
                String className = stringScratchObjectDeletionHelperEntry.getKey();
                ObjectDeletionHelper scratchObjectDeletionHelper = stringScratchObjectDeletionHelperEntry.getValue();
                Class currentClass = Class.forName(className);
                calcObjectDeletionHelperWorker(sortedClasses,
                                               currentClass,
                                               scratchObjectDeletionHelper,
                                               classNameToScratchObjectDeletionHelperMap);
            }
        }
        catch(ClassNotFoundException e)
        {
            throw new RuntimeException(e);

        }
        return sortedClasses;
    }


    private static void calcObjectDeletionHelperWorker(List<Class> sortedClasses,
                                                       Class currentClass,
                                                       ObjectDeletionHelper scratchObjectDeletionHelper,
                                                       Map<String, ObjectDeletionHelper> classNameToScratchObjectDeletionHelperMap)
            throws ClassNotFoundException
    {
        Class[] parentDependencyClasses = scratchObjectDeletionHelper.getParentDependencyClasses();

        int position = sortedClasses.indexOf(currentClass);
        if (position < 0)
        {
            if (parentDependencyClasses == null || parentDependencyClasses.length == 0)
            {
                sortedClasses.add(currentClass);
            }
            else
            {
                // check if any dependencies exist in list and insert before highest order one
                int highestOrderDependencyPosition =
                        getHighestOrderDependencyPosition(sortedClasses,
                                                          classNameToScratchObjectDeletionHelperMap,
                                                          parentDependencyClasses, Integer.MAX_VALUE);
                if (highestOrderDependencyPosition == Integer.MAX_VALUE)
                {
                    sortedClasses.add(currentClass);
                }
                else
                {
                    sortedClasses.add(highestOrderDependencyPosition, currentClass);
                }
            }
        }
        // Or else it's already there. Now take care of parent dependencies recursively
        if (parentDependencyClasses != null)
        {
            for (int i = 0; i < parentDependencyClasses.length; i++)
            {
                Class parentDependencyClass = parentDependencyClasses[i];
                ObjectDeletionHelper deletionHelper =
                        classNameToScratchObjectDeletionHelperMap.get(parentDependencyClass.getName());
                if (deletionHelper == null)
                {
                    throw new RuntimeException(
                            "Could not find ScratchObjectDeletionHelper for class: " +
                            parentDependencyClass);
                }
                calcObjectDeletionHelperWorker(sortedClasses, parentDependencyClass,
                                               deletionHelper, classNameToScratchObjectDeletionHelperMap);


                //int positionParentDependency = sortedClasses.indexOf(parentDependencyClass);
                //if (positionParentDependency < 0)
                //{
                //
                //    sortedClasses.add(parentDependencyClass);
                //
                //}
                //// else already there, so leave it
            }
        }
    }


    private static int getHighestOrderDependencyPosition(List<Class> sortedClasses,
                                                         Map<String, ObjectDeletionHelper> classNameToScratchObjectDeletionHelperMap,
                                                         Class[] parentDependencyClasses,
                                                         int highestOrderDependencyPosition)
    {
        if (parentDependencyClasses != null)
        {
            for (int i = 0; i < parentDependencyClasses.length; i++)
            {
                Class parentDependencyClass = parentDependencyClasses[i];

                int positionParentDependency = sortedClasses.indexOf(parentDependencyClass);
                if (positionParentDependency != -1 && positionParentDependency <
                                                      highestOrderDependencyPosition)
                {
                    highestOrderDependencyPosition = positionParentDependency;
                }

                ObjectDeletionHelper parentDependencyDeletionHelper =
                        classNameToScratchObjectDeletionHelperMap
                                .get(parentDependencyClass.getName());
                Class[] parentParentDependencyClasses = parentDependencyDeletionHelper.getParentDependencyClasses();
                highestOrderDependencyPosition = getHighestOrderDependencyPosition(sortedClasses,
                                                                                   classNameToScratchObjectDeletionHelperMap,
                                                                                   parentParentDependencyClasses,
                                                                                   highestOrderDependencyPosition);
            }
        }
        return highestOrderDependencyPosition;
    }


    public static DOFGlobalSettings getInstance()
    {
        //if (instance == null)
        //{
        //    instance = new DOFGlobalSettings();
        //}
        return instance;
    }


    /**
     * Used to get a fileToLoad's absolute path if you are setting system property DOF_DIR to place
     * the object files
     *
     * @param fileToLoad
     *
     * @return The absolute path to the resource with which is DOF_DIR plus File.separator +
     *         resourceName
     */
    public static String getAbsolutePath(String fileToLoad)
    {
        return DOF_DIR + File.separator + fileToLoad;
    }


    /**
     * If property DOF_DEFS_DIR is defined, then the files are retrieved from that directory, or
     * else the files are retrieved using ClassLoader.getSystemResourceAsStream
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


    static String getResourceAsStringFromDofDefsDir(String resourceName)
    {
        final String resourceAbsolutePath = getAbsolutePath(resourceName);
        InputStreamReader isr = getInputStreamReaderForPath(resourceAbsolutePath);

        StringBuffer sb = readContentsOfInputStream(isr);
        return sb.toString();
    }


    static InputStreamReader getInputStreamReaderForPath(String resourceAbsolutePath)
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


    static StringBuffer readContentsOfInputStream(InputStreamReader isr)
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


    static String getResourceAsStringFromClassLoader(String resourceName)
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
            throw new RuntimeException("Caught error loading resource from classpath: '" +
                                       resourceName + "'. Possibly resource does not exist.", e);
        }
    }
}
