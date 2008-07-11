package org.justingordon;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.justingordon.ExceptionTest;
import org.justingordon.VendingMachineTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({VendingMachineTest.class, ExceptionTest.class})
public class VendingMachineTestSuite
{
}
