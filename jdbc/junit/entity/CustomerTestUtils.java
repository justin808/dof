package entity;

import global.*;
import junit.framework.*;

import java.math.*;
import java.util.*;

public class CustomerTestUtils
{


    public static Customer createCustomerRecordWithNamePhoneNumber(String name, String phoneNumber)
    {
        Customer customer = Customer.createNewCustomer(name, phoneNumber);
        customer.persist();
        return customer;
    }

    public static Customer createInvoiceForNewCustomer(String name,
                                                       Date invoiceDate,
                                                       BigDecimal amount)
    {
        Customer customer = createCustomerRecordWithNamePhoneNumber(name, "8085551212");
        Assert.assertEquals(new BigDecimal(0.0), customer.getBalance());
        createInvoiceWithAmountForCustomer(customer, amount, invoiceDate);
        Assert.assertEquals(amount, customer.getBalance());
        return customer;
    }

    public static void createInvoiceWithAmountForCustomer(Customer customer,
                                                          BigDecimal amount,
                                                          Date invoiceDate)
    {
        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setSubTotal(amount);
        invoice.setInvoiceDate(invoiceDate);
        invoice.persist(); // also updates and saves the customer
    }

    public static void initMockDependencies()
    {
        if (!(GlobalContext.getComponentFactory() instanceof MockComponentFactory))
        {
            GlobalContext.setComponentFactory(new MockComponentFactory());
        }
    }

    public static void initDbDependencies()
    {
        if (!(GlobalContext.getComponentFactory() instanceof JdbcComponentFactory))
        {
            GlobalContext.setComponentFactory(new JdbcComponentFactory());
        }
    }
}
