package org.doframework.sample.component;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.doframework.sample.global.Configuration;
import org.doframework.sample.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({AllTestSuiteInProcessDb.class, AllTestSuiteInProcessDb.class,
        AllTestSuiteInProcessDb.class})
public class AllTestSuiteInProcessDbThreeTimes
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
        System.out.println("Elapsed time of " + AllTestSuiteInProcessDbThreeTimes.class.getName() + " is " + (System.currentTimeMillis() - startTime) + " ms. (3 times the test suite).");
    }

}