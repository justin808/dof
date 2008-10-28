package org.doframework.sample.component.reference;

import org.doframework.*;
import org.doframework.annotation.*;
import org.doframework.sample.component.*;

@TargetClass(Customer.class)
public abstract class CustomerReferenceBuilder implements ReferenceBuilder
{
    CustomerComponent customerComponent = ComponentFactory.getCustomerComponent();


    /**
     * This method defines what other objects need to be created (persisted) before this object is created. Typically,
     * this method should be defined in the superclass for the object defined, and the implementation should use the
     * known foreign keys and the naming pattern to generate the right class objects.
     * <p/>
     * Note, a StaticDependentObject can only depend on other other StaticDependentObjects
     *
     * @return Array of static dependent objects that this object directly depends on.
     */
    public ReferenceBuilder[] getReferenceJavaDependencies()
    {
        return null;
    }


    /**
     * Fetches the object, if it exists, with the given PK. Otherwise null is returned. Typically, this method should be
     * defined in the superclass for the object defined.
     *
     * @return The object created from the db if it existed, or else null */
    public Object fetch()
    {
        return customerComponent.getByName((String) getPrimaryKey());
    }


    /**
     * Delete the object passed. Note that the framework will automatically try to delete the object's dependencies as
     * well in a breadth first manner. It is CRITICAL that this method not delete the requested object and return false
     * if there are any existing dependencies upon this object. For example, if this is a request to delete a customer
     * record and invoices depend upon this customer record, it must simply return false.<p/> Typically, this method
     * should be defined in the superclass for the object defined.
     * <p/>
     * This method is passed the objectToDelete as a convenience as many systems will use that object as part of the
     * deletion code.
     *
     * @param objectToDelete Object requested for deletion, never null
     *
     * @return true if requested object is deleted.
     */
    public boolean delete(Object objectToDelete)
    {
        return customerComponent.delete((Customer) objectToDelete);
    }




    /**
     * The implementation of this method must insert the defined object into the DB.
     * <p/>
     *
     * @return An object that was created and saved in the DB
     */
    public Object create()
    {
        Customer customer = customerComponent.createNew();
        customer.setName((String) getPrimaryKey()).setBalance(0).setOverdue(false).setPhoneNumber("415-555-1212");
        customerComponent.persist(customer);
        return customer;
    }
}
