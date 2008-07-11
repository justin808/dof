package org.doframework.sample.jdbc_app.factory;

import java.util.Collection;
import java.util.List;

import org.doframework.sample.jdbc_app.entity.Customer;


public interface CustomerFactory extends Factory
{

    Customer getById(int customerId);

    void insert(Customer customer) throws DuplicateRecordException;

    void update(Customer customer) throws DuplicateRecordException;

    boolean delete(Customer customer);

    Collection<Customer> getAllCustomers();

    List<Customer> getCustomersWithOutstandingBalances();

    /**
     * Put in the where clause for getting customers
     * @param query
     * @return
     */
    List<Customer> query(String query);

}
