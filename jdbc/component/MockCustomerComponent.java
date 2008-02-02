package component;

import entity.*;

import java.util.*;
import java.math.*;

public class MockCustomerComponent implements CustomerComponent
{
    private static Map<Integer, Customer> customerIdToCustomer = new HashMap<Integer,Customer>();
    private static int lastCustomerId = 1000;


    public Customer getById(int customerId)
    {
        return customerIdToCustomer.get(customerId);
    }

    public void insert(Customer customer) throws DuplicateRecordException
    {
        checkForCustomerMatch(customer);
        persist(customer);
    }

    /**
     * Saves the customer record
     *
     * @param customer
     *
     * @throws DuplicateRecordException
     *
     */
    public void update(Customer customer) throws DuplicateRecordException
    {
        persist(customer);
    }

    public void persist(Customer customer)
    {
        if (customer.getId() == Component.ID_NEW)
        {
            customer.setId(getNextId());
        }
        customerIdToCustomer.put(customer.getId(), customer);
    }

    private void checkForCustomerMatch(Customer customer)
    {
        Set<Map.Entry<Integer, Customer>> entries = customerIdToCustomer.entrySet();
        for (Iterator<Map.Entry<Integer, Customer>> iterator = entries.iterator(); iterator.hasNext();)
        {
            Map.Entry<Integer, Customer> entry = iterator.next();
            Customer customerToTest = entry.getValue();
            if (customerToTest.getName().equals(customer.getName())
                && customerToTest.getId() != customer.getId())
            {
                throw new DuplicateRecordException("Found customer duplicate customer: " + customerToTest);
            }
        }
   }

    public boolean delete(Customer customer)
    {
        Customer customer1 = customerIdToCustomer.remove(customer.getId());
        return customer1 != null;
    }

    public Collection<Customer> getAllCustomers()
    {
        return customerIdToCustomer.values();
    }

    public List<Customer> getCustomersWithOutstandingBalances()
    {
        Collection<Customer> all = getAllCustomers();
        List<Customer> result = new ArrayList<Customer>();
        for (Iterator<Customer> iterator = all.iterator(); iterator.hasNext();)
        {
            Customer customer = (Customer) iterator.next();
            if (customer.getBalance().compareTo(new BigDecimal(0)) > 0)
            {
                result.add(customer);
            }
        }
        return result;
    }

    public List<Customer> query(String query)
    {
        throw new RuntimeException("Not implementing query for MockCustomerComponent");
    }

    public int getNextId()
    {
        return lastCustomerId++;
    }
}
