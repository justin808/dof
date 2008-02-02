package jdbc_component;


import global.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.*;

public class JdbcDbUtil
{
    static
    {
        try
        {
            //Class.forName(Configuration.getDbClassName());
            Class.forName("org.hsqldb.jdbcDriver");
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd"); // WARNING -- not for production code -- not threadsafe!

    private static Connection connection; // would use connection pool in real life

    static Connection getConnection() throws SQLException
    {
        if (connection == null)
        {
            connection = DriverManager.getConnection(Configuration.getDbUrl(),
                                               Configuration.getDbUser(),
                                               Configuration.getDbPassword());
        }
        return connection;
    }


    static public void shutdown()
    {
        try
        {
            Statement st = getConnection().createStatement();

            // db writes out to files and performs clean shuts down
            // otherwise there will be an unclean shutdown
            // when program ends
            st.execute("SHUTDOWN");
            getConnection().close();    // if there are no other open connection
            setConnection(null);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Use for SQL command SELECT
     * @param expression Select blah, blah from table blah where blah, blah
     * @param resultSetProcessor Anonymous class to handle the results
     */
    public static synchronized void query(String expression, ResultSetProcessor resultSetProcessor)
            throws SQLException
    {

        Statement st = null;
        try
        {
            ResultSet rs = null;

            st = getConnection().createStatement();         // statement objects can be reused with

            // repeated calls to execute but we
            // choose to make a new one each time
            rs = st.executeQuery(expression);    // run the query

            // do something with the result set.
            resultSetProcessor.process(rs);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {

            try
            {
                if (st != null) st.close();
                // NOTE!! if you close a statement the associated ResultSet is
                // closed too
                // so you should copy the contents to some other object.
                // the result set is invalidated also  if you recycle an Statement
                // and try to execute some other query before the result set has been
                // completely examined.
            }
            catch (SQLException e)
            {
                abort(e);
            }
        }
    }

    private static void abort(SQLException e)
    {
        throw new RuntimeException(e);
    }


    /**
     * @param expression -- a non query, such as a create, insert, update, delete
     *
     * @return the number of rows inserted or updated or deleted
     */
    public static synchronized int update(String expression)
    {
        Statement st = null;
        try
        {
            st = getConnection().createStatement();    // statements

            int i = st.executeUpdate(expression);    // run the query

            if (i == -1)
            {
                System.out.println("db error : " + expression);
            }

            return st.getUpdateCount();
        }
        catch (SQLException e)
        {
            abort(e);
        }
        finally
        {
            try
            {
                if (st != null)
                    st.close();
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
        return -1;
    }    // void update()




    /**
     * @param sql, like select count(*)
     *
     * @return the int value from the query
     */
    public static int executeSingleIntQuery(String sql)
    {
        Connection c = null;
        Statement s = null;
        try
        {
            c = getConnection();
            s = c.createStatement();
            ResultSet rs = s.executeQuery(sql);
            rs.next();
            int result = rs.getInt(1);
            return result;
        }
        catch (SQLException e)
        {
            abort(e);
        }
        finally
        {
            if (s != null)
            {
                try
                {
                    s.close();
                }
                catch (SQLException e)
                {
                    abort(e);
                }
            }
        }
        return -1;
    }


    /**
     * @param sql, like select count(*)
     *
     * @return the int value from the query
     */
    public static String[][] executeMultiColumnQuery(String sql)
    {
        Connection c = null;
        Statement s = null;
        try
        {
            c = getConnection();
            s = c.createStatement();
            ResultSet rs = s.executeQuery(sql);
            ResultSetMetaData setMetaData = rs.getMetaData();
            int columnCount = setMetaData.getColumnCount();
            ArrayList<String[]> rows = new ArrayList<String[]>();

            while (rs.next())
            {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++)
                {
                    row[i - 1] = rs.getString(i);
                }
                rows.add(row);
            }

            return (String[][]) rows.toArray(new String[][]{});
        }
        catch (SQLException e)
        {
            abort(e);
        }
        finally
        {
            if (s != null)
            {
                try
                {
                    s.close();
                }
                catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    private static void setConnection(Connection connection)
    {
        JdbcDbUtil.connection = connection;
    }

    /**
     * WARNING -- not for production code -- not threadsafe!
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date)
    {
        return dateFormat.format(date);
    }

    public static Date parseDate(String sDate)
    {
        try
        {
            return dateFormat.parse(sDate);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out.println("Usage: java jdbc_component.JdbcDbUtil [shutdown]");
            System.exit(0);
        }
        if (args[0].equals("shutdown"))
        {
            shutdown();
        }
    }




}

