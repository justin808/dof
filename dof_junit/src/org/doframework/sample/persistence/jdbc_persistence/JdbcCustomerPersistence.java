package org.doframework.sample.persistence.jdbc_persistence;

import org.doframework.sample.component.*;
import org.doframework.sample.persistence.*;

import java.util.*;
import java.sql.*;


public class JdbcCustomerPersistence extends JdbcBasePersistence implements CustomerPersistence
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
        Customer customer =
                new Customer(Integer.parseInt(id), name, phoneNumber, new Integer(balance), isOverdue.equals(YES));
        return customer;
    }


    public void insert(Customer customer)
    {
        //checkForCustomerMatch(customer);

        String sql = "insert into customer (id, name, phone_number, balance, is_overdue) values (" + customer.getId() +
                     ", '" + customer.getName() + "', '" + customer.getPhoneNumber() + "', '" + customer.getBalance() +
                     "', '" + (customer.isOverdue() ? YES : NO) + "')";
        JdbcDbUtil.update(sql);
    }


    /**
     * Saves the customer record
     *
     * @param customer
     */
    public void update(Customer customer)
    {
        String sql = "update customer " + "set name = '" + customer.getName() + "', phone_number = '" +
                     customer.getPhoneNumber() + "', balance = '" + customer.getBalance() + "', is_overdue = '" +
                     (customer.isOverdue() ? YES : NO) + "' where id = " + customer.getId();
        JdbcDbUtil.update(sql);
    }


    public boolean delete(Customer customer)
    {
        String sql = "delete from customer where id = " + customer.getId();
        try
        {
            int rows = JdbcDbUtil.update(sql);
            return (rows > 0);
        }
        catch (Exception e)
        {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException)
            {
                // assumption that this
                return false;
            }
            else
            {
                throw new RuntimeException(e);
            }
        }

    }


    public Customer getByName(String name)
    {
        String sql = "select * from customer where name = '" + name + "'";
        String[][] data = JdbcDbUtil.executeMultiColumnQuery(sql);
        for (int row = 0; row < data.length; row++)
        {
            Customer customer = getCustomerForRow(data[row]);
            return customer;
        }
        return null; // if none found
    }


    public int countInvoicesWithCustomerId(int customerId)
    {
        String sql =
                "select count(*) from invoice inv where inv.customer_id = " + customerId;
        return JdbcDbUtil.executeSingleIntQuery(sql);
    }

    //public Collection<Customer> getAllCustomers()
    //{
    //    return null;
    //}

    //public List<Customer> getCustomersWithOutstandingBalances()
    //{
    //    return null;
    //}


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

    //public void checkForCustomerMatch(Customer customer)
    //{
    //    String query = "name = '" + customer.getName() + "' and phone_number = '" + customer.getPhoneNumber() +
    //                   "' and id <> " + customer.getId();
    //    List<Customer> customers = query(query);
    //    if (customers.size() > 0)
    //    {
    //        throw new DuplicateRecordException("Found duplicate customers " + customers + "\nof " + customer);
    //    }
    //}


    public String getTableName()
    {
        return "customer";
    }
}
