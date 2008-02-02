package global;

import jdbc_component.*;

import java.io.*;

public class DBTestSetup extends DBTestCase
{
    public void testRecreateSchema()
    {
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
    }
}
