package org.doframework;

// Released under the Eclipse Public License-v1.0

import org.doframework.annotation.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.lang.annotation.*;


/**
 * This is helper class for that handles the mappings of the Handler classes to the object types.
 * The user of the DOF is responsible for having a file "handler_mappings.properties" located in the
 * classpath
 * <p/>
 * The format of the properties file is:
 * <p/>
 * <code>objectType.fileSuffix=DependentObjectHandlerImplementationClassName</code>
 * <p/>
 * This is an example of a line in the mappings file:
 * <p/>
h * <code>customer.xml=dof_xml_handler.CustomerXmlFactory</code>
 * <p/>
 * <p/>
 * It states that a customer.PK.xml file maps to the handler class dof_xml_handler.CustomerXmlFactory
 * Note, the CustomerXmlFactory class must implement interface <b>DependentObjectHandler</b>
 * <p/>
 * Even though fileToLoad uses the period as the delimiter, the primary key may contain periods
 * because the first and last periods are used to find the object type and the file suffix. This
 * also means that object types may NOT contain a period (if you are using the default
 * org.doframework.TypePkExtensionObjectFileInfoProcessor)
 * <p/>
 * You may specify a custom ObjectFileInfoProcessor class in case you do not like the form of
 * objectType.PK.fileType. Do this by putting in a property in file handler_mappings.properties
 * <pre>
 * ObjectFileInfoProcessor=FullClassName
 * </pre>
 * <p/>
 * Specify regexp matches like this (note the "RE:" signifies regexp, and it is removed from the
 * expression
 * <pre>
 * RE:{regular expression}=class
 * RE:\w+\.xml=dof_xml_handler.GenericXmlFactory
 * </pre>
 * For regexp documentation, consult http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html<p/>
 *
 * @author Justin Gordon
 * @date January, 2008
 */


public class DOFGlobalSettings
{
    static final String DOF_PREFERENCES = "dof_preferences.properties";
    static final String HANDLER_MAPPINGS_PROPERTIES_NAME = "handler_mappings.properties";
    static final String OBJECT_DELETION_HELPER_MAPPINGS_NAME = "deletion_helpers.properties";


    public static final String MAX_CACHED_SCRATCH_OBJECTS = "MaxCachedScratchObjects";
    public static final String MAX_CACHED_REFERENCE_OBJECTS = "MaxCachedReferenceObjects";
    public static final String DEFAULT_SCRATCH_PRIMARY_KEY_PROVIDER =
            "DefaultScratchPrimaryKeyProvider";
    private static final String DEFAULT_SCRATCH_PK_PROVIDER =
            "org.doframework.IntScratchPkProvider";

    public static final String OBJECT_FILE_INFO_PROCESSOR_PROPERTY = "ObjectFileInfoProcessor";
    public static final String DEFAULT_OBJECT_FILE_INFO_PROCESSOR =
            "org.doframework.TypePkExtensionObjectFileInfoProcessor";

    private static String dofDir = System.getProperty("DOF_DIR", "");
    public static boolean dofDebug = System.getProperty("DOF_DEBUG", "").equalsIgnoreCase("TRUE");
    static DOFGlobalSettings instance;


    Properties handlerMappings;
    Properties dofPreferences;
    Properties scratchDeletionHelpers;

    Map<String, String> propertyFileToResource = new HashMap<String, String>();
    /** This is the property to specify a custom ObjectFileInfoProcessor */
    ObjectFileInfoProcessor objectFileInfoProcessor;

    Map<Pattern, String> compiledPatterns;


    Pattern SCRATCH_PK_PATTERN;


    Map<Class, DeletionHelper> classToObjectDeletionHelper =
            new HashMap<Class, DeletionHelper>();

    /** Mapping of the entries in the handler_mappings.properties to the appropriate classes */
    Map<String, Class> objectTypeFileTypeToDOFHandlerClass = new HashMap<String, Class>();

    Map<String, DependentObjectHandler> dofHandlerClassNameToInstance =
            new HashMap<String, DependentObjectHandler>();

    /**
     * This is the optimal order to try deleting objects, based on which objects depend on other
     * objects.
     */
    List<Class> objectDeletionOrder;
    private ScratchPkProvider defaultScratchPrimaryKeyProvider;
    /**
     * we keep a cache of the loaded objects to avoid searching the DB every time.
     */
    //static
    //{   // Make sure we init first
    //    DOFGlobalSettings.getInstance();
    //}
    //
    DOFObjectCache dofObjectCache;
    private HashSet<String> m_propertyFilesNotFound = new HashSet<String>();


    /**
     * @param clazz
     *
     * @return
     */
    DeletionHelper getDeletionHelperForClass(Class clazz)
    {
        return classToObjectDeletionHelper.get(clazz);
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
            if (c == null || c.trim().length() == 0)
            {
                c = DEFAULT_OBJECT_FILE_INFO_PROCESSOR;
            }
            try
            {
                Class objectFileInfoProcessorClass = Class.forName(c);
                objectFileInfoProcessor =
                        (ObjectFileInfoProcessor) objectFileInfoProcessorClass.newInstance();
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


    DependentObjectHandler getDependentObjectHandlerForObjectTypeFileType(String objectType,
                                                                          String fileType)
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
     * other objects dependendent upon them. For example, if invoices depend on products that depend
     * on manufacturers, return { Invoice.class, Product.class, Manufacturer.class}
     *
     * @return List of classes in the optimal order to delete objects
     */
    public List<Class> getObjectDeletionOrder()
    {
        if (objectDeletionOrder == null)
        {
            objectDeletionOrder = calcObjectDeletionOrder(classToObjectDeletionHelper);
        }
        return objectDeletionOrder;
    }


    public Class getClassForObjectTypeFileType(String objectType, String fileType)
    {
        final DependentObjectHandler doh =
                getDependentObjectHandlerForObjectTypeFileType(objectType, fileType);
        return getTargetClassFromAnnotatedClass(doh);
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
            String s = dofPreferences
                    .getProperty("ScratchPrimaryKeyPattern", "\\{\\{pk(\\:(\\w+))?\\}\\}");
            if (dofDebug)
            {
                DOF.logMessage("Parsed preference: ScratchPrimaryKeyPattern=" + s);
            }
            SCRATCH_PK_PATTERN = Pattern.compile(s);
        }
        return SCRATCH_PK_PATTERN;
    }


    int getMaxCachedScratchObjects()
    {
        String sMaxCachedScratchObjects = dofPreferences.getProperty(MAX_CACHED_SCRATCH_OBJECTS);
        if (sMaxCachedScratchObjects == null || sMaxCachedScratchObjects.trim().length() == 0)
        {
            return 0;
        }
        else
        {
            return Integer.parseInt(sMaxCachedScratchObjects);
        }
    }


    int getMaxCachedReferenceObjects()
    {

        String sMaxCachedReferenceObjects =
                dofPreferences.getProperty(MAX_CACHED_REFERENCE_OBJECTS);
        if (sMaxCachedReferenceObjects == null || sMaxCachedReferenceObjects.trim().length() == 0)
        {
            return 0;
        }
        else
        {
            return Integer.parseInt(sMaxCachedReferenceObjects);
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////  PRIVATE METHODS  /////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////


    void initialize()
    {
        dofPreferences = getPropertiesFromFileName(DOF_PREFERENCES);
        if (dofPreferences == null)
        {
            if (dofDebug)
            {
                logMissingFileMessage("Using default DOF preferences", DOF_PREFERENCES);
            }
            dofPreferences = new Properties();

            dofPreferences.put(DEFAULT_SCRATCH_PRIMARY_KEY_PROVIDER, DEFAULT_SCRATCH_PK_PROVIDER);
            dofPreferences.put(MAX_CACHED_SCRATCH_OBJECTS, "0");
            dofPreferences.put(MAX_CACHED_REFERENCE_OBJECTS, "0");
        }
        parseObjectDeletionHelperMappings();
        int referenceObjectsSize = getMaxCachedReferenceObjects();
        int scratchObjectsSize = getMaxCachedScratchObjects();
        dofObjectCache = new DOFObjectCache(referenceObjectsSize, scratchObjectsSize);

        String defaultScratchPrimaryKeyProviderClassName =
                getDefaultScratchPrimaryKeyProviderClassName();
        if (defaultScratchPrimaryKeyProviderClassName != null &&
            defaultScratchPrimaryKeyProviderClassName.trim().length() > 0)
        {
            try
            {
                Class<? extends ScratchPkProvider> scratchClass =
                        (Class<? extends ScratchPkProvider>) Class
                                .forName(defaultScratchPrimaryKeyProviderClassName);
                defaultScratchPrimaryKeyProvider = scratchClass.newInstance();
            }
            catch (Throwable e)
            {
                throw new RuntimeException(e);
            }
        }

    }


    void parseObjectDeletionHelperMappings()
    {
        Properties objectDeletionHelperProperties =
                getPropertiesFromFileName(OBJECT_DELETION_HELPER_MAPPINGS_NAME);
        if (objectDeletionHelperProperties == null)
        {
            if (dofDebug)
            {
                logMissingFileMessage("No deletion helper mappings file found",
                                      OBJECT_DELETION_HELPER_MAPPINGS_NAME);
            }
        }
        else
        {
            Set<Object> objectDeletionHelpers = objectDeletionHelperProperties.keySet();
            for (Iterator classNameIterator = objectDeletionHelpers.iterator();
                 classNameIterator.hasNext();)
            {
                String objectDeletionHelperClassName = (String) classNameIterator.next();
                try
                {
                    DeletionHelper deletionHelper = (DeletionHelper) Class
                            .forName(objectDeletionHelperClassName).newInstance();
                    Class deletionTarget = getTargetClassFromAnnotatedClass(deletionHelper);
                    classToObjectDeletionHelper
                            .put(deletionTarget, deletionHelper);
                }
                catch (ClassNotFoundException e)
                {
                    throw new RuntimeException("Class name not found: " +
                                               objectDeletionHelperClassName +
                                               "\nPlease examine contents of file: " +
                                               OBJECT_DELETION_HELPER_MAPPINGS_NAME);
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


    /**
     * Used to consistently get the annotation value
     * @param dofHelperInstance Either a "builder" class or a DependentObjectHandler that has annotation TargetClass
     * @return The class that is produced by the helper class
     */
    static Class getTargetClassFromAnnotatedClass(Object dofHelperInstance)
    {
        TargetClass annotation = (TargetClass) getAnnotationFromObjectOrSuper(dofHelperInstance,
                                                                              TargetClass.class);
        if (annotation == null)
        {
            throw new RuntimeException(dofHelperInstance.getClass() + " class or superclass " +
                                       "must declare annotation: " + TargetClass.class.getName() +
                                       ", specifying the created " +
                                       "class from the helper class.");
        }

        return annotation.value();
    }


    /**
     * Gets the annotation from the class passed in or a parent class
     * @param dofHelperInstance The DOFBuilder or DependentObjectHandler
     * @param annotationClass The annotation sought
     * @return The Annotation found or null if not defined on this the dofHelperInstance or a parent
     */
    private static Annotation getAnnotationFromObjectOrSuper(Object dofHelperInstance,
                                                             Class <? extends Annotation> annotationClass )
    {
        Class<? extends Object> classToCheck = dofHelperInstance.getClass();
        Annotation annotation = null;
        while (classToCheck != null && annotation == null)
        {
            annotation = classToCheck.getAnnotation(annotationClass);
            if (annotation == null)
            {
                classToCheck = classToCheck.getSuperclass();
            }
        }
        return annotation;
    }


    private void logMissingFileMessage(String message, String file)
    {
        String getDofDirLogMessage = getDofDirLogMessage();
        DOF.logMessage(message + ". File '" + file + "' can either be in " +
                       "the classpath or inside directory indicated by JVM " +
                       "property DOF_DIR: " + getDofDirLogMessage);
    }


    private String getDofDirLogMessage()
    {
        return DOFGlobalSettings.getDofDir().length() > 0 ? DOFGlobalSettings.getDofDir() : "<NOT DEFINED>";
    }


    private Properties parseHandlerMappings()
    {
        Properties handlerMappings = getPropertiesFromFileName(HANDLER_MAPPINGS_PROPERTIES_NAME);
        if (handlerMappings == null)
        {
            logMissingFileMessage(
                    "ERROR: If using text definitions and handlers, you need to specify file : " +
                    HANDLER_MAPPINGS_PROPERTIES_NAME, HANDLER_MAPPINGS_PROPERTIES_NAME);

            //System.out.println("Create an empty version of handler_mappings.properties " +
            //                   "and dof_preferences.properties to avoid this message and to use defaults.");
            System.exit(1);
        }
        // Process the optional mapping to class names
        Set<Map.Entry<Object, Object>> entries = handlerMappings.entrySet();
        for (Iterator entryIterator = entries.iterator(); entryIterator.hasNext();)
        {
            Map.Entry<Object, Object> objectObjectEntry =
                    (Map.Entry<Object, Object>) entryIterator.next();
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
                objectTypeFileTypeToDOFHandlerClass
                        .put(objectTypeFileType, Class.forName(handlerClass));
                DependentObjectHandler doh = getDofHandlerInstanceForClassName(handlerClass);
                if (doh instanceof DeletionHelper)
                {
                    Class targetClass = getTargetClassFromAnnotatedClass(doh);
                    classToObjectDeletionHelper
                            .put(targetClass, (DeletionHelper) doh);
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
        return handlerMappings;
    }


    private Properties getPropertiesFromFileName(String propertyFileName)
    {
        InputStream inputStream = getInputStreamForFile(propertyFileName);

        Properties properties = new Properties();
        if(inputStream==null)
        {
            return properties;
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


    private InputStream getInputStreamForFile(String propertyFileName)
    {
        InputStream inputStream = null;
        if (getDofDir().length() > 0)
        {
            String resourceAbsolutePath = getAbsolutePath(propertyFileName);
            File file = new File(resourceAbsolutePath);
            String filePath = file.getAbsolutePath();
            try
            {
                inputStream = new BufferedInputStream(new FileInputStream(file));
                DOF.logMessage("Loaded file " + filePath);
                propertyFileToResource.put(propertyFileName, filePath + "");

            }
            catch (FileNotFoundException e)
            {
                if (dofDebug && !m_propertyFilesNotFound.contains(resourceAbsolutePath))
                {
                    m_propertyFilesNotFound.add(resourceAbsolutePath);
                    System.out.println("WARNING: File " + resourceAbsolutePath + " was not found. " +
                                       "Checking classpath.");
                }
                inputStream = null;
            }
        }

        if (inputStream == null)
        {
            inputStream = ClassLoader.getSystemResourceAsStream(propertyFileName);
            if (inputStream != null)
            {
                URL resource = ClassLoader.getSystemResource(HANDLER_MAPPINGS_PROPERTIES_NAME);
                String msg = "Loaded " + propertyFileName + " from " + resource;
                DOF.logMessage(msg);
                propertyFileToResource.put(propertyFileName, resource + "");
            }
            else
            {
                if (dofDebug && !m_propertyFilesNotFound.contains(propertyFileName))
                {
                    m_propertyFilesNotFound.add(propertyFileName);
                    System.out.println("WARNING: File " + propertyFileName + " was not found in the classpath. Using default values.");
                }

            }
        }
        return inputStream;
    }


    String getDOFHandlerClassName(String objectType, String fileType)
    {
        Properties handlerMappings = getHandlerMappings();
        String key = objectType + '.' + fileType;
        String exactMatch = (String) handlerMappings.get(key);
        if (exactMatch != null)
        {
            return exactMatch;
        }

        initCompiledPatterns(handlerMappings);
        for (Map.Entry<Pattern, String> entry : compiledPatterns.entrySet())
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


    void initCompiledPatterns(Properties handlerMappings)
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
                        DOF.logMessage("Caught exception in compiling regexp patter " + sKey +
                                       ", error was " + e);
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


    /** No need for public constructor */
    DOFGlobalSettings()
    {
        initialize();
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


    static List<Class> calcObjectDeletionOrder(Map<Class, DeletionHelper> classToScratchObjectDeletionHelperMap)
    {
        List<Class> sortedClasses = new ArrayList<Class>();
        try
        {
            Set<Map.Entry<Class, DeletionHelper>> entries =
                    classToScratchObjectDeletionHelperMap.entrySet();
            for (Iterator entryIterator = entries.iterator(); entryIterator.hasNext();)
            {
                Map.Entry<Class, DeletionHelper> classScratchObjectDeletionHelperEntry =
                        (Map.Entry<Class, DeletionHelper>) entryIterator.next();
                Class clazz = classScratchObjectDeletionHelperEntry.getKey();
                DeletionHelper scratchDeletionHelper =
                        classScratchObjectDeletionHelperEntry.getValue();
                calcObjectDeletionHelperWorker(sortedClasses, clazz, scratchDeletionHelper,
                                               classToScratchObjectDeletionHelperMap);
            }
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);

        }
        return sortedClasses;
    }


    private static void calcObjectDeletionHelperWorker(List<Class> sortedClasses,
                                                       Class currentClass,
                                                       DeletionHelper scratchDeletionHelper,
                                                       Map<Class, DeletionHelper> classToScratchObjectDeletionHelperMap)
            throws ClassNotFoundException
    {
        TargetReferencedClasses targetReferencedAnnotation = (TargetReferencedClasses)
                getAnnotationFromObjectOrSuper(scratchDeletionHelper,
                                               TargetReferencedClasses.class);


        Class[] parentDependencyClasses = targetReferencedAnnotation != null ?
                                          targetReferencedAnnotation.value() : null;

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
                                                          classToScratchObjectDeletionHelperMap,
                                                          parentDependencyClasses,
                                                          Integer.MAX_VALUE);
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
                DeletionHelper deletionHelper = classToScratchObjectDeletionHelperMap
                        .get(parentDependencyClass);
                if (deletionHelper == null)
                {
                    throw new RuntimeException(
                            "Could not find ScratchObjectDeletionHelper for class: " +
                            parentDependencyClass);
                }
                calcObjectDeletionHelperWorker(sortedClasses,
                                               parentDependencyClass,
                                               deletionHelper,
                                               classToScratchObjectDeletionHelperMap);

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
                                                         Map<Class, DeletionHelper> classToScratchObjectDeletionHelperMap,
                                                         Class[] parentDependencyClasses,
                                                         int highestOrderDependencyPosition)
    {
        if (parentDependencyClasses != null)
        {
            for (int i = 0; i < parentDependencyClasses.length; i++)
            {
                Class parentDependencyClass = parentDependencyClasses[i];

                int positionParentDependency = sortedClasses.indexOf(parentDependencyClass);
                if (positionParentDependency != -1 &&
                    positionParentDependency < highestOrderDependencyPosition)
                {
                    highestOrderDependencyPosition = positionParentDependency;
                }

                DeletionHelper parentDependencyDeletionHelper =
                        classToScratchObjectDeletionHelperMap
                                .get(parentDependencyClass);
                TargetReferencedClasses annotation = parentDependencyDeletionHelper.getClass()
                        .getAnnotation(TargetReferencedClasses.class);
                Class[] parentParentDependencyClasses = null;
                if (annotation != null)
                {
                    parentParentDependencyClasses = annotation.value();
                }
                highestOrderDependencyPosition = getHighestOrderDependencyPosition(sortedClasses,
                                                                                   classToScratchObjectDeletionHelperMap,
                                                                                   parentParentDependencyClasses,
                                                                                   highestOrderDependencyPosition);
            }
        }
        return highestOrderDependencyPosition;
    }


    public synchronized static DOFGlobalSettings getInstance()
    {
        if (instance == null)
        {
            instance = new DOFGlobalSettings();
        }
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
        return getDofDir() + File.separator + fileToLoad;
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
        if (getDofDir().length() == 0)
        {
            return getResourceAsStringFromClassLoader(resourceName);
        }
        else
        {
            return getResourceAsStringFromDofDefsDir(resourceName);
        }
    }


    /**
     * Utility method to get a resource from the DOF_DIR. Note, this method should NEVER be used
     * to create scratch objects, as scratch key substitution will not take place.
     * @param resourceName File name relative to DOF_DIR
     * @return The resource as a string from the DOF_DIR
     */
    public static String getResourceAsStringFromDofDefsDir(String resourceName)
    {
        final String resourceAbsolutePath = getAbsolutePath(resourceName);
        InputStreamReader isr = getInputStreamReaderForPath(resourceAbsolutePath);

        StringBuffer sb = readContentsOfInputStream(isr);
        return sb.toString();
    }


    /**
     * Utility method to get a stream reader from a path
     * @param resourceAbsolutePath
     * @return
     */
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


    /**
     * Will throw if this is called and handler mappings file does not exist.
     *
     * @return properties if handler_mappings.properties was found
     */
    Properties getHandlerMappings()
    {
        if (handlerMappings == null)
        {
            // Next line will exit if this is called and handler mappings does not exist
            handlerMappings = parseHandlerMappings();
        }
        return handlerMappings;
    }


    /**
     * This is an alternate to having the JVM property of DOF_DIR set. This is where properties files
     * will first be checked, as well as where definitions of objects will be searched.
     * @param path Path absolute path or relative path to where the JVM is started.
     */
    public static void setDofDir(String path)
    {
        File file = new File(path);
        String newAbsPath = file.getAbsolutePath();
        if (!newAbsPath.equals(dofDir))
        {
            dofDir = newAbsPath;
            instance = new DOFGlobalSettings();
        }
    }


    public ScratchPkProvider getDefaultScratchPrimaryKeyProvider()
    {
        return defaultScratchPrimaryKeyProvider;
    }


    public static String getDofDir()
    {
        return dofDir;
    }


}
