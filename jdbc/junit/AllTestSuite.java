import junit.framework.*;
import entity.*;
import dof_xml_handler.*;

public class AllTestSuite extends TestSuite
{
    public void testAll()
    {
        addTest(new TestSuite(MockAccountingTest.class));
        addTest(new TestSuite(JdbcAccountingTest.class));
        addTest(new TestSuite(InvoiceXmlFactoryTest.class));
        addTest(new TestSuite(CustomerXmlFactoryTest.class));
        addTest(new TestSuite(HbnAccountingTest.class));
    }

}
