package org.doframework.sample.component.reference;

import org.doframework.sample.component.*;

public class Customer_36 extends CustomerReferenceBuilder
{
    private static final int PRIMARY_KEY = 36;


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
        customer.setName("Jane Doe").setBalance(120).setOverdue(false).setPhoneNumber("415-555-1212").setNew(true);
        customerComponent.persist(customer);
        return customer;
    }

    
}