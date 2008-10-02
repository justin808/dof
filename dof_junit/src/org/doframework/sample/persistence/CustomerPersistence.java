package org.doframework.sample.persistence;

import org.doframework.sample.component.*;

public interface CustomerPersistence extends BasePersistence
{
    /**
     * Save the new customer record to the database
     *
     * @param customer
     */
    void insert(Customer customer);


    /**
     * Update the existing customer record in the database
     *
     * @param customer
     */
    void update(Customer customer);


    /**
     * Get the customer with a given id
     *
     * @param id
     *
     * @return
     */
    Customer getById(int id);


    /**
     * Delete the customer
     * @param customer to delete
     * @return true if the customer was deleted
     */
    boolean delete(Customer customer);


    Customer getByName(String name);
}
