package com.ibm.dof;

import java.util.*;
import java.io.*;

public class HandlerMappings
{
    static Properties m_handlerMappings;

    static
    {
        m_handlerMappings = new Properties();
        InputStream handlerMappingsInputStream = ClassLoader.getSystemResourceAsStream("handler_mappings.properties");
        try
        {
            m_handlerMappings.load(handlerMappingsInputStream);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    public static String getHandlerClassNameForObject(String objectType, String fileType)
    {
        return (String) m_handlerMappings.get(objectType + "." + fileType);
    }

}
