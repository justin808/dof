package org.doframework.sample.component.reference;

import org.doframework.*;
import org.doframework.sample.component.*;

public class Customer_35 extends CustomerReferenceBuilder implements ReferenceBuilder
{
    private static final int PRIMARY_KEY = 35;


    /**
     * @return the primary key of this reference dependent object
     */
    public Object getPrimaryKey()
    {
        return new Integer(PRIMARY_KEY);
    }


    /**
     * The implementation of this method must insert the defined object into the DB.
     * <p/>
     *
     * @return An object that was created and saved in the DB
     */
    public Object create()
    {
        Customer customer = new Customer(PRIMARY_KEY);
        customer.setName("John Doe").setBalance(120).setOverdue(false).setPhoneNumber("415-555-1212").setNew(true);
        customerComponent.persist(customer);
        return customer;
    }


}
