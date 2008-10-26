package org.doframework.sample;

import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.*;
import org.doframework.sample.component.*;
import org.doframework.sample.xml_handler.*;
import org.doframework.sample.global.*;
import org.doframework.sample.persistence.jdbc_persistence.*;
import org.doframework.*;


@RunWith(Suite.class)
@Suite.SuiteClasses({AccountingTest.class, ManufacturerXmlFactoryTest.class, ProductXmlFactoryTest.class, FrameworkTest.class, InvoiceXmlHandlerTest.class, JdbcAccountingTest.class, InvoiceComponentTest.class,
        ManufacturerJavaTest.class, InvoiceJavaTest.class})

public class AllTestSuite
{
    static long startTime;


    @BeforeClass
    public static void beforeClass()
    {
        startTime = System.currentTimeMillis();
        System.out.println("Using connection:" + Configuration.getDbUrl());

        if (DOFGlobalSettings.getDofDir().trim().length() == 0)
        {
            DOFGlobalSettings.setDofDir("dof_junit/dof_data");
        }

        try
        {
            JdbcDbUtil.executeSingleIntQuery("SELECT COUNT(*) FROM CUSTOMER");
        }
        catch (Exception e)
        {
            System.out.println("Creating Schema in memory");
            CreateSchema.main(null);
            System.out.println("Finished Creating Schema in memory");
        }
    }

    @AfterClass
    public static void afterClass()
    {
        System.out.println("Elapsed time is " + (System.currentTimeMillis() - startTime) + " ms.");
    }

}
