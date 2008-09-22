package org.doframework.sample.persistence;

import org.doframework.sample.component.*;

import java.util.*;


public class MockCustomerPersistence implements CustomerPersistence
{
    Map<Integer, Customer> customerIdToCustomer = new HashMap<Integer, Customer>();
    static int nextId = 0;


    public Customer getById(int id)
    {
        return customerIdToCustomer.get(id);
    }


    /**
     * Delete the customer
     *
     * @param customer to delete
     *
     * @return true if the customer was deleted
     */
    public boolean delete(Customer customer)
    {
        return (customerIdToCustomer.remove(customer.getId()) != null);
    }


    public int getNextId()
    {
        return nextId++;
    }


    public void insert(Customer customer)
    {
        customerIdToCustomer.put(customer.getId(), customer);
    }


    public void update(Customer customer)
    {
        customerIdToCustomer.put(customer.getId(), customer);
    }

}
