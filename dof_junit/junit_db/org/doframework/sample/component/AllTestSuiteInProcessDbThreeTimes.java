package org.doframework.sample.component;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.doframework.sample.global.Configuration;
import org.doframework.sample.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({AllTestSuiteInProcessDb.class, AllTestSuiteInProcessDb.class,
        AllTestSuiteInProcessDb.class})
public class AllTestSuiteInProcessDbThreeTimes
{


}