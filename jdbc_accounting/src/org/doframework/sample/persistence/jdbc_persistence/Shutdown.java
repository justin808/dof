package org.doframework.sample.persistence.jdbc_persistence;

import java.sql.*;


public class Shutdown
{
    public static void main(String[] args)
    {
        Statement st = null;
        try
        {
            st = JdbcDbUtil.getConnection().createStatement();    // statements
            st.executeUpdate("SHUTDOWN");    // run the query
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            try
            {
                if (st != null)
                {
                    st.close();
                }
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
        System.out.println("DB Shutdown completion successful");
    }
}