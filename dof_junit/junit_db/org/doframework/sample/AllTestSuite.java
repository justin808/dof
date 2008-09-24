package org.doframework.sample;

import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.*;
import org.doframework.sample.component.*;
import org.doframework.sample.xml_handler.*;
import org.doframework.sample.global.*;
import org.doframework.sample.persistence.jdbc_persistence.*;


@RunWith(Suite.class)
@Suite.SuiteClasses({AccountingTest.class, ManufacturerXmlFactoryTest.class, ProductXmlFactoryTest.class, CustomerXmlFactoryTest.class, InvoiceXmlFactoryTest.class, JdbcAccountingTest.class, InvoiceComponentTest.class,
        ManufacturerJavaTest.class, InvoiceJavaTest.class, ShoppingListXmlFactoryTest.class})

public class AllTestSuite
{
    static long startTime;


    @BeforeClass
    public static void beforeClass()
    {
        startTime = System.currentTimeMillis();
        System.out.println("Using connection:" + Configuration.getDbUrl());

        try
        {
            JdbcDbUtil.executeSingleIntQuery("SELECT COUNT(*) FROM CUSTOMER");
        }
        catch (Exception e)
        {
            if (e.getMessage().indexOf("Table not found") >= 0)
            {
                System.out.println("Creating Schema in memory");
                CreateSchema.main(null);
                System.out.println("Finished Creating Schema in memory");
            }
        }
    }

    @AfterClass
    public static void afterClass()
    {
        System.out.println("Elapsed time is " + (System.currentTimeMillis() - startTime) + " ms.");
    }

    // JUnit 3.8.1 way
    //    public class AllTestSuite extends TestCase
    //public static Test suite()
    //{
    //    TestSuite suite = new TestSuite();
    //    suite.addTest(new TestSuite(JdbcAccountingTest.class));
    //    suite.addTest(new TestSuite(CustomerXmlFactoryTest.class));
    //    return suite;
    //}
}
