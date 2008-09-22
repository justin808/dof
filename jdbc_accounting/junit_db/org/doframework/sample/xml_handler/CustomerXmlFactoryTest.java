package org.doframework.sample.xml_handler;

import static org.junit.Assert.*;
import org.junit.*;
import org.doframework.sample.component.*;
import org.doframework.sample.global.*;
import org.doframework.sample.persistence.jdbc_persistence.*;
import org.doframework.*;

public class CustomerXmlFactoryTest
{

    @BeforeClass
    static public void beforeTests()
    {
        GlobalContext.setPersistanceFactory(new JdbcPersistenceFactory());
    }


    /**
     * This will test that we have our files set up so they are accessible. In this example, we are using the method
     * InputStream is = ClassLoader.getSystemResourceAsStream(xmlDescriptionFile); Thus, we need to add the test_data
     * directory to the classpath. In IntelliJ, this is done by adding the directory as a "project library"
     */
    @Test
    public void testParsing()
    {
        CustomerXmlFactory mxf = new CustomerXmlFactory();
        ObjectFileInfo ofi = new ObjectFileInfo("customer", "25", "xml");
        ofi.setFileToLoad("customer.25.xml");
        Customer customer = mxf.createCustomer(ofi);
        assertEquals(25, customer.getId());
        assertEquals("John Smith", customer.getName());
    }


}
