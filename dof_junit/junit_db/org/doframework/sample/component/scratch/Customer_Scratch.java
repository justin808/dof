package org.doframework.sample.component.scratch;

import org.doframework.*;
import org.doframework.sample.component.*;

import java.util.*;

public class Customer_Scratch implements ScratchBuilder
{

    /**
     * The implementation of this method must insert the defined object into the DB.
     * <p/>
     *
     * @return An object that was created and saved in the DB
     * @param scratchReferenceToPrimaryKey
     */
    public Object create(Map scratchReferenceToPrimaryKey)
    {
        CustomerComponent customerComponent = ComponentFactory.getCustomerComponent();
        Customer customer = customerComponent.createNew();
        customer.setName(customer.getId() + "__Jane Doe").setBalance(0).setOverdue(false).setPhoneNumber("415-555-1212").setNew(true);
        customer.setNew(true);
        customerComponent.persist(customer);
        return customer;
    }

    /**
     * Fetches the object, if it exists, with the given PK. Otherwise null is returned.
     * <p/>
     * This method is only called if ScratchBuilder.PK was defined in the map passed into this
     * object. Typically, this method should be defined in the superclass for the object defined and
     * the superclass should call getPrimaryKey to see what object should be retrieved. Note, this
     * method <b>MUST</b> go to the persistent store. It it returns null, then the create method is
     * called.
     *
     * @return The object created from the db if it existed, or else null @param pk
     */
    public Object fetch(Object pk)
    {
        int id = !(pk instanceof Integer) ? Integer.parseInt(pk + "") : (int)(Integer)pk;
        return ComponentFactory.getCustomerComponent().getById(id);
    }


    /**
     * Return the primary key for the scratch object
     */
    public Object extractPrimaryKey(Object scratchObject)
    {
        return ((Customer)scratchObject).getName();
    }


    public Class getCreatedClass()
    {
        return Customer.class;
    }


}