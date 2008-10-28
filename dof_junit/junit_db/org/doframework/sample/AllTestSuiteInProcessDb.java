package org.doframework.sample;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.doframework.sample.global.Configuration;

@RunWith(Suite.class)
@Suite.SuiteClasses({AllTestSuite.class})
public class AllTestSuiteInProcessDb
{
    static boolean runOnce = false;

    @BeforeClass
    public static void beforeClass()
    {
        if (!runOnce)
        {
            System.out.println("Using in-process, in-memory DB");
            // Configuration.setDbUrl("jdbc:hsqldb:mem:memdbid");
            Configuration.setDbUrl("jdbc:h2:mem:memdbid");
            runOnce = true;
        }
    }


}
