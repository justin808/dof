package component;

import entity.*;
import global.*;
import junit.framework.*;

/**
 * User: gordonju Date: Jan 13, 2008 Time: 4:57:20 PM
 */
public class JdbcCustomerComponentTest extends TestCase
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
                GlobalContext.getComponentFactory().getCustomerComponent().getById(c1.getId());
        assertEquals(c1.getName(), c1FromDb.getName());
    }

    public void testCustomerUpdateAndGet()
    {
        Customer c1 = new Customer(); // will assign new id
        c1.setName("A" + System.currentTimeMillis());
        c1.setPhoneNumber("808-555-1212");
        c1.persist();

        Customer c1FromDb =
                GlobalContext.getComponentFactory().getCustomerComponent().getById(c1.getId());
        String NEW_PHONE = "415-555-1212";
        c1FromDb.setPhoneNumber(NEW_PHONE);
        c1FromDb.persist();

        Customer c1FromDbAgain =
                GlobalContext.getComponentFactory().getCustomerComponent().getById(c1.getId());

        assertEquals(NEW_PHONE, c1FromDbAgain.getPhoneNumber());
    }

    public void testDeleteCustomer()
    {
        Customer c1 = new Customer(); // will assign new id
        c1.setName("A" + System.currentTimeMillis());
        c1.setPhoneNumber("808-555-1212");
        c1.persist();

        Customer c1FromDb =
                GlobalContext.getComponentFactory().getCustomerComponent().getById(c1.getId());
        c1FromDb.delete();

        Customer c1FromDbAgain =
                GlobalContext.getComponentFactory().getCustomerComponent().getById(c1.getId());
        assertNull(c1FromDbAgain);


    }


}
