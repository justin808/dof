package org.doframework.sample.persistence;

import static org.junit.Assert.*;
import org.junit.*;
import static org.doframework.sample.component.ComponentFactory.*;
import org.doframework.sample.component.*;
import org.doframework.sample.global.*;
import org.doframework.sample.persistence.jdbc_persistence.*;

public class JdbcCustomerPersistenceTest
{

    @Before
    public void beforeTests()
    {
        GlobalContext.setPersistanceFactory(new JdbcPersistenceFactory());
    }


    @Test
    public void testCustomerInsertAndGet()
    {
        Customer c1 = getNewCustomerWithUniqueName();
        Customer c2 = getNewCustomerWithUniqueName();

        assertEquals(c1.getId() + 1, c2.getId());

        Customer c1FromDb = getCustomerComponent().getById(c1.getId());
        assertEquals(c1.getName(), c1FromDb.getName());
    }


    private Customer getNewCustomerWithUniqueName()
    {
        Customer c1 = getCustomerComponent().createNew();
        c1.setName("A" + System.currentTimeMillis());
        c1.setPhoneNumber("808-555-1212");
        getCustomerComponent().persist(c1);
        return c1;
    }


    @Test
    public void testCustomerUpdateAndGet()
    {
        Customer c1 = getNewCustomerWithUniqueName();
        Customer c1FromDb = getCustomerComponent().getById(c1.getId());
        String NEW_PHONE = "415-555-1212";
        c1FromDb.setPhoneNumber(NEW_PHONE);
        getCustomerComponent().persist(c1FromDb);
        Customer c1FromDbAgain = getCustomerComponent().getById(c1.getId());
        assertEquals(NEW_PHONE, c1FromDbAgain.getPhoneNumber());
    }


    @Test
    public void testDeleteCustomer()
    {
        Customer c1 = getNewCustomerWithUniqueName();
        Customer c1FromDb = getCustomerComponent().getById(c1.getId());
        getCustomerComponent().delete(c1FromDb);
        Customer c1FromDbAgain = getCustomerComponent().getById(c1.getId());
        assertNull(c1FromDbAgain);
    }


}

