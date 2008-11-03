package org.doframework;

import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.*;


@RunWith(Suite.class)
@Suite.SuiteClasses({CalcDeletionOrderTest.class, DOFTest.class, HandlerMappingsTest.class})

public class AllTestSuiteCore
{
    static long startTime;


    @BeforeClass
    public static void beforeClass()
    {
        startTime = System.currentTimeMillis();
    }

    @AfterClass
    public static void afterClass()
    {
        System.out.println("Elapsed time is " + (System.currentTimeMillis() - startTime) + " ms.");
    }

}