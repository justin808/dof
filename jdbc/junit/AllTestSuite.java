import dof_xml_handler.*;
import entity.*;
import junit.framework.*;

public class AllTestSuite extends TestCase
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new TestSuite(MockAccountingTest.class));
        suite.addTest(new TestSuite(JdbcAccountingTest.class));
        suite.addTest(new TestSuite(InvoiceXmlFactoryTest.class));
        suite.addTest(new TestSuite(CustomerXmlFactoryTest.class));
        return suite;
    }


}
