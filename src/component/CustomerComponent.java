package component;

import entity.*;

import java.util.*;


public interface CustomerComponent extends Component
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
