package dof_xml_handler;

import entity.*;
import junit.framework.*;
import org.doframework.DOF;

/**
 * User: gordonju Date: Jan 13, 2008 Time: 4:17:22 PM
 */
public class CustomerXmlFactoryTest extends TestCase
{

    {
        CustomerTestUtils.initDbDependencies();
    }


    /**
     * This will test that we have our files set up so they are accessible. In this example, we are
     * using the method InputStream is = ClassLoader.getSystemResourceAsStream(xmlDescriptionFile);
     * Thus, we need to add the test_data directory to the classpath. In IntelliJ, this is done by
     * adding the directory as a "project library"
     */
    public void testParsing()
    {
        String testFile = "customer.25.xml";
        Customer customer = (Customer) DOF.require(testFile);
        assertEquals(25, customer.getId());
        assertEquals("John Smith", customer.getName());
    }


}
