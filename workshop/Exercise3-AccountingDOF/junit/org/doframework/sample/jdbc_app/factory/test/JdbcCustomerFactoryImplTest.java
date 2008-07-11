package org.doframework.sample.jdbc_app.factory.test;

import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.entity.Customer;

/**
 * These are some junit tests - these tests do NOT use DOF.
 * 
 * User: gordonju Date: Jan 13, 2008 Time: 4:57:20 PM
 */
public class JdbcCustomerFactoryImplTest extends JdbcFactoryImplTest
{    
    public void testCustomerInsertAndGet()
    {
        Customer c1 = new Customer(); // will assign new id
        c1.setName("A" + System.currentTimeMillis());
        c1.setPhoneNumber("808-555-1212");
        c1.persist();


        Customer c2 = new Customer(); // will assign new id
        c2.setName("B" + System.currentTimeMillis());
        c2.setPhoneNumber("808-555-1212");
        c2.persist();

        assertEquals(c1.getId() + 1, c2.getId());

        Customer c1FromDb =
                GlobalContext.getFactoryLookupService().getCustomerFactory().getById(c1.getId());
        assertEquals(c1.getName(), c1FromDb.getName());
    }

    public void testCustomerUpdateAndGet()
    {
        Customer c1 = new Customer(); // will assign new id
        c1.setName("A" + System.currentTimeMillis());
        c1.setPhoneNumber("808-555-1212");
        c1.persist();

        Customer c1FromDb =
                GlobalContext.getFactoryLookupService().getCustomerFactory().getById(c1.getId());
        String NEW_PHONE = "415-555-1212";
        c1FromDb.setPhoneNumber(NEW_PHONE);
        c1FromDb.persist();

        Customer c1FromDbAgain =
                GlobalContext.getFactoryLookupService().getCustomerFactory().getById(c1.getId());

        assertEquals(NEW_PHONE, c1FromDbAgain.getPhoneNumber());
    }
}
