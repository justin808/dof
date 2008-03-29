package org.doframework;

// Released under the Eclipse Public License-v1.0

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 This is helper class for that handles the mappings of the Handler classes to the object types. The user of the DOF is
 responsible for having a file "handler_mappings.properties" located in the classpath.
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
 (if you are using the default org.doframework.TypePkExtensionFileNamePartsProcessor)
  <p/>
  You may specify a custom FileNamePartsProcessor class in case you do not like the form of objectType.PK.fileType.
  Do this by putting in a property in file handler_mappings.properties
  <pre>
  FileNamePartsProcessor=FullClassName
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


class HandlerMappings
{
    static Properties handlerMappings;
    private static final String DEFAULT_DOF_FILE_NAME_PARTS_PROCESSOR = "org.doframework.TypePkExtensionFileNamePartsProcessor";
    private static FileNamePartsProcessor fileNamePartsProcessor;

    /**
     * This is the property to specify a custom FileNamePartsProcessor
     */
    private static final String FILE_NAME_PARTS_PROCESSOR_PROPERTY = "FileNamePartsProcessor";
    private static Map<Pattern, String> compiledPatterns;


    static
    {
        handlerMappings = new Properties();
        final String PROPERTIES_FILE_NAME = "handler_mappings.properties";
        InputStream handlerMappingsInputStream = ClassLoader.getSystemResourceAsStream(PROPERTIES_FILE_NAME);
        if (handlerMappingsInputStream == null)
        {
            if (DOF.DOF_DEFS_DIR.length() > 0)
            {
                String resourceAbsolutePath = DOF.getResourceAbsolutePath(PROPERTIES_FILE_NAME);
                File file = new File(resourceAbsolutePath);
                try
                {
                    handlerMappingsInputStream = new BufferedInputStream(new FileInputStream(file));
                }
                catch (FileNotFoundException e)
                {
                    throw new RuntimeException("You must put '" + PROPERTIES_FILE_NAME + "'" +
                                               " in the classpath or under directory defined by system property " +
                                               "DOF_DIR: " + file.getAbsolutePath());
                }
            }
            else
            {
                throw new RuntimeException("You must put '" + PROPERTIES_FILE_NAME + "'" +
                                           " in the classpath or under directory defined by system property " +
                                           "DOF_DIR.");

            }
        }
        try
        {
            handlerMappings.load(handlerMappingsInputStream);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get the file name parts processor class
     * @return The fully qualified class name that implements the interface org.doframework.FileNamePartsProcessor
     */
    static FileNamePartsProcessor getFileNamePartsProcessor()
    {
        if (fileNamePartsProcessor == null)
        {
            String c = (String) handlerMappings.get(FILE_NAME_PARTS_PROCESSOR_PROPERTY);
            if (c == null || c.length() == 0)
            {
                c = DEFAULT_DOF_FILE_NAME_PARTS_PROCESSOR;
            }
            try
            {
                Class fileNamePartsProcessorClass = Class.forName(c);
                fileNamePartsProcessor = (FileNamePartsProcessor) fileNamePartsProcessorClass.newInstance();
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
        return fileNamePartsProcessor;
    }


    static String getHandlerClassNameForObject(String objectType, String fileType)
    {
        String key = objectType + "." + fileType;
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

    private static void initCompiledPatterns()
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

    /**
     No need for public constructor
     */
    private HandlerMappings()
    {
    }


}
