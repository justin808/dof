package org.doframework.sample.jdbc_app.test;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.Assert;

import org.doframework.sample.jdbc_app.CustomerManager;
import org.doframework.sample.jdbc_app.entity.Customer;
import org.doframework.sample.jdbc_app.entity.Invoice;

public class CustomerTestUtils
{


    public static Customer createCustomerRecordWithNamePhoneNumber(String name, String phoneNumber)
    {
        Customer customer = CustomerManager.createNewCustomer(name, phoneNumber);
        customer.persist();
        return customer;
    }

    public static Customer createInvoiceForNewCustomer(String name,
                                                       Date invoiceDate,
                                                       BigDecimal amount)
    {
        Customer customer = CustomerManager.createNewCustomer(name, "8085551212");
        Assert.assertEquals(new BigDecimal(0.0), customer.getBalance());
        createInvoiceWithAmountForCustomer(customer, invoiceDate, amount);
        Assert.assertEquals(amount, customer.getBalance());
        return customer;
    }

    public static void createInvoiceWithAmountForCustomer(Customer customer,
                                                          Date invoiceDate,
                                                          BigDecimal amount)
    {
        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setSubTotal(amount);
        invoice.setInvoiceDate(invoiceDate);
        invoice.persist(); // also updates and saves the customer
    }
}
