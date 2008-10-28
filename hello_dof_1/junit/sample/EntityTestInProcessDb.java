package sample;

import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.*;
import org.hibernate.cfg.*;
import org.h2.tools.Server;

import java.sql.*;
import java.util.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({EntityTest.class})
public class EntityTestInProcessDb
{
    static boolean runOnce = false;

    @BeforeClass
    public static void beforeClass()
    {
        if (!runOnce)
        {
            System.out.println("Using in-process, in-memory DB");
            Map configOverrides = new HashMap();
            configOverrides.put("hibernate.connection.url", "jdbc:h2:mem:");
            JpaUtility.setConfigOverrides(configOverrides);
            runOnce = true;
        }
    }

}
