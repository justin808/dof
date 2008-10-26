package org.doframework.sample.persistence.jdbc_persistence;

import org.doframework.sample.global.*;

import java.io.*;

public class CreateSchema
{
    public static void main(String[] args)
    {
        System.out.println("Dropping Schema");
        String dbUrl = Configuration.getDbUrl();
        if (dbUrl.indexOf("h2") > 0)
        {
            JdbcDbUtil.update("DROP ALL OBJECTS; SET MODE ORACLE");
        }
        else if (dbUrl.indexOf("hsqldb") > 0)
        {
            JdbcDbUtil.update("DROP SCHEMA PUBLIC CASCADE");
        }
        else
        {
            throw new RuntimeException("Unsupported DB: " + dbUrl);
        }


        System.out.println("Creating Schema");
        InputStream is = ClassLoader.getSystemResourceAsStream("accounting.sql");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String s;
        StringBuffer sql = new StringBuffer();
        try
        {
            while ((s = br.readLine()) != null)
            {
                sql.append(s);
                if (s.length() > 0 && s.charAt(s.length() - 1) == ';')
                {
                    JdbcDbUtil.update(sql.toString());
                    sql.delete(0, sql.length());
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    	System.out.println("Schema completion successful");
    }
}


