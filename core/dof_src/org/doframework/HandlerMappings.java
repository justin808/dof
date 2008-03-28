package org.doframework;

// Released under the Eclipse Public License-v1.0

import java.io.*;
import java.util.*;


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

 @author Justin Gordon
 @date January, 2008


 */


class HandlerMappings
{
    static Properties m_handlerMappings;
    private static final String DEFAULT_DOF_FILE_NAME_PARTS_PROCESSOR = "org.doframework.TypePkExtensionFileNamePartsProcessor";
    private static FileNamePartsProcessor fileNamePartsProcessor;

    /**
     * This is the property to specify a custom FileNamePartsProcessor
     */
    private static final String FILE_NAME_PARTS_PROCESSOR_PROPERTY = "FileNamePartsProcessor";


    static
    {
        m_handlerMappings = new Properties();
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
            m_handlerMappings.load(handlerMappingsInputStream);
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
            String c = (String) m_handlerMappings.get(FILE_NAME_PARTS_PROCESSOR_PROPERTY);
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
        return (String) m_handlerMappings.get(objectType + "." + fileType);
    }

    /**
     No need for public constructor
     */
    private HandlerMappings()
    {
    }


}
