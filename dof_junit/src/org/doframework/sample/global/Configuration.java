package org.doframework.sample.global;

import java.util.*;
import java.io.*;

public class Configuration
{

    static Properties m_properties;
    //private static final String DB_TYPE = "db_type";
    private static final String DB_USER = "db_user";
    private static final String DB_PASSWORD = "db_password";
    private static final String DB_URL = "db_url";
    private static final String DB_CLASS_NAME = "db_class_name";


    static
    {
        m_properties = new Properties();
        InputStream propertiesInputStream = ClassLoader.getSystemResourceAsStream("configuration.properties");
        try
        {
            m_properties.load(propertiesInputStream);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }


    /**
     * Package leveall
     *
     * @return
     */
    //static String getDbTypeString()
    //{
    //    return m_properties.getProperty(DB_TYPE);
    //}


    public static String getDbUser()
    {
        return m_properties.getProperty(DB_USER);
    }




    public static String getDbPassword()
    {
        return m_properties.getProperty(DB_PASSWORD);
    }


    public static String getDbUrl()
    {
        return m_properties.getProperty(DB_URL);
    }


    /**
     * Override the one in the configuration file for a in-process, in-memory DB
     * @param dbUrl
     * @return
     */
    public static Object setDbUrl(String dbUrl)
    {
        return m_properties.setProperty(DB_URL, dbUrl);
    }


    public static String getDbClassName()
    {
        return m_properties.getProperty(DB_CLASS_NAME);
    }


}
