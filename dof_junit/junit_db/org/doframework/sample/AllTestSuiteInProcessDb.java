package org.doframework.sample;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.doframework.sample.global.Configuration;

@RunWith(Suite.class)
@Suite.SuiteClasses({AllTestSuite.class})
public class AllTestSuiteInProcessDb
{
    @BeforeClass
    public static void beforeClass()
    {
        System.out.println("Using in-process, in-memory DB");
        // Configuration.setDbUrl("jdbc:hsqldb:mem:memdbid");
        Configuration.setDbUrl("jdbc:h2:mem:memdbid");

        //System.out.println("Creating Schema in memory");
        //CreateSchema.main(null);
        //System.out.println("Finished Creating Schema in memory");
    }


}
