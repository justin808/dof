package org.doframework.sample.jdbc_app.factory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.doframework.sample.jdbc_app.entity.Customer;


public class MockCustomerFactory implements CustomerFactory
{
    Map<Integer, Customer> customerIdToCustomer = new HashMap<Integer, Customer>();
    static int nextId = 0;


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


	public boolean delete(Customer customer) {
		// TODO Auto-generated method stub
		return false;
	}


	public Collection<Customer> getAllCustomers() {
		// TODO Auto-generated method stub
		return null;
	}


	public Customer getById(int customerId) {
        return customerIdToCustomer.get(customerId);
	}


	public List<Customer> getCustomersWithOutstandingBalances() {
		// TODO Auto-generated method stub
		return null;
	}


	public List<Customer> query(String query) {
		// TODO Auto-generated method stub
		return null;
	}

}
