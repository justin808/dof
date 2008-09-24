package org.doframework.sample.component;

import junit.framework.*;

import java.util.*;


public class CustomerTestUtils
{

    public static Customer createCustomerRecordWithNamePhoneNumber(String name, String phoneNumber)
    {
        final CustomerComponent customerComponent = ComponentFactory.getCustomerComponent();
        Customer customer = customerComponent.createNew();
        customer.setName(name);
        customer.setPhoneNumber(phoneNumber);
        customerComponent.persist(customer);
        return customer;
    }


    public static Customer createInvoiceForNewCustomer(String name, Date invoiceDate, Integer amount)
    {
        Customer customer = createCustomerRecordWithNamePhoneNumber(name, "8085551212");
        Assert.assertEquals(new Integer(0), customer.getBalance());
        createInvoiceWithAmountForCustomer(customer, amount, invoiceDate);
        Assert.assertEquals(amount, customer.getBalance());
        return customer;
    }


    public static void createInvoiceWithAmountForCustomer(Customer customer, Integer total, Date invoiceDate)
    {
        InvoiceComponent invoiceComponent = ComponentFactory.getInvoiceComponent();
        Invoice invoice = invoiceComponent.createNew();
        invoice.setCustomer(customer);
        invoice.setTotal(total);
        invoice.setInvoiceDate(invoiceDate);
        invoiceComponent.persist(invoice); // also updates and saves the customer
    }

}


