package jpa_component;

import jdbc_component.*;
import component.*;
import entity.*;

import java.math.*;
import java.util.*;

public class JpaCustomerComponent extends JdbcBaseComponent implements CustomerComponent
{
    private static final String YES = "Y";
    private static final String NO = "N";

    public Customer getById(int customerId)
    {
        String sql = "select * from customer where id = " + customerId;
        String[][] data = JdbcDbUtil.executeMultiColumnQuery(sql);
        for (int row = 0; row < data.length; row++)
        {
            Customer customer = getCustomerForRow(data[row]);
            return customer;
        }
        return null; // if none found
    }

    private Customer getCustomerForRow(String[] columns)
    {
        String id = columns[0];
        String name = columns[1];
        String phoneNumber = columns[2];
        String balance = columns[3];
        String isOverdue = columns[4];
        Customer customer = new Customer(Integer.parseInt(id));
        customer.setName(name);
        customer.setPhoneNumber(phoneNumber);
        customer.setBalance(new BigDecimal(balance));
        customer.setOverdue(isOverdue.equals(YES));
        return customer;
    }

    public void insert(Customer customer) throws DuplicateRecordException
    {
        checkForCustomerMatch(customer);

        String sql =
                "insert into customer (id, name, phone_number, balance, is_overdue) values (" +
                customer.getId() +
                ", '" +
                customer.getName() +
                "', '" +
                customer.getPhoneNumber() +
                "', '" +
                customer.getBalance().toString() +
                "', '" +
                (customer.isOverDue() ? YES : NO) +
                "')";
        JdbcDbUtil.update(sql);
    }

    /**
     * Saves the customer record
     *
     * @param customer
     *
     * @throws component.DuplicateRecordException
     *
     */
    public void update(Customer customer) throws DuplicateRecordException
    {
        String sql =
                "update customer " +
                "set name = '" +
                customer.getName() +
                "', phone_number = '" +
                customer.getPhoneNumber() +
                "', balance = '" +
                customer.getBalance() +
                "', is_overdue = '" +
                (customer.isOverDue() ? YES : NO) +
                "' where id = " +
                customer.getId();
        JdbcDbUtil.update(sql);
    }


    public boolean delete(Customer customer)
    {
        String sql = "delete from customer where id = " + customer.getId();

        int rows = JdbcDbUtil.update(sql);
        return (rows > 0);
    }

    public Collection<Customer> getAllCustomers()
    {
        return null;
    }

    public List<Customer> getCustomersWithOutstandingBalances()
    {
        return null;
    }

    public List<Customer> query(String query)
    {
        String sql = "select * from customer where " + query;
        String[][] data = JdbcDbUtil.executeMultiColumnQuery(sql);
        List<Customer> customers = new ArrayList<Customer>();
        for (int row = 0; row < data.length; row++)
        {
            Customer customer = getCustomerForRow(data[row]);
            customers.add(customer);
        }
        return customers; // if none found
    }

    public void checkForCustomerMatch(Customer customer)
    {
        String query = "name = '" + customer.getName()
                       + "' and phone_number = '" + customer.getPhoneNumber() +
                       "' and id <> " + customer.getId();
        List<Customer> customers = query(query);
        if (customers.size() > 0)
        {
            throw new DuplicateRecordException("Found duplicate customers " + customers + "\nof " + customer);
        }
    }


    public String getTableName()
    {
        return "customer";
    }
}
