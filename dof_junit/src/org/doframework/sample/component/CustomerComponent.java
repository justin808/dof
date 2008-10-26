package org.doframework.sample.component;

import static org.doframework.sample.global.GlobalContext.*;
import org.doframework.sample.global.*;

/**
 * This class orchestrates actions around the Customer entity Using class rather than interface because we want only one
 * copy of the business logic.
 *
 * @see Customer
 */
public class CustomerComponent
{

    /**
     * Save the customer
     *
     * @param customer
     */
    public void persist(Customer customer)
    {
        if (customer.isNew())
        {
            getPersistanceFactory().getCustomerPersistence().insert(customer);
            customer.setNew(false);
        }
        else
        {
            getPersistanceFactory().getCustomerPersistence().update(customer);
        }
    }


    /**
     * Creates an empty customer with a unique ID
     *
     * @return the new customer
     */
    public Customer createNew()
    {
        int id = getPersistanceFactory().getCustomerPersistence().getNextId();
        return new Customer(id);
    }


    /**
     * Get the customer by ID
     *
     * @param id
     *
     * @return the customer or null if the customer does not exist with that ID
     */
    public Customer getById(int id)
    {
        return getPersistanceFactory().getCustomerPersistence().getById(id);
    }


    public boolean delete(Customer customer)
    {
        return getPersistanceFactory().getCustomerPersistence().delete(customer);
    }


    public Customer getByName(String name)
    {
        return getPersistanceFactory().getCustomerPersistence().getByName(name);
    }


    public boolean hasInvoices(Customer customer)
    {
        int numInvoices = GlobalContext.getPersistanceFactory().getCustomerPersistence()
                .countInvoicesWithCustomerId(customer.getId());
        return (numInvoices > 0);
    }
}
