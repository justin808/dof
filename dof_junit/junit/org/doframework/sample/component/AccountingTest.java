package org.doframework.sample.component;

import junit.framework.*;
import org.junit.Test;
import org.doframework.sample.global.*;
import org.doframework.sample.persistence.*;
import org.doframework.sample.persistence.jdbc_persistence.JdbcPersistenceFactory;
import org.doframework.*;

import java.util.*;

/**
 * This class uses the mock objects unless overriden
 */
public class AccountingTest extends TestCase
{
    private CustomerComponent customerComponent = ComponentFactory.getCustomerComponent();
    private InvoiceComponent invoiceComponent = ComponentFactory.getInvoiceComponent();

    public void setUp()
    {
        GlobalContext.setPersistanceFactory(new JdbcPersistenceFactory());
    }



    public void testCustomerWithNoInvoicesHasZeroBalance()
    {
        Customer customer = getNewUniqueCustomer();
        Customer customerFromPersisted = customerComponent.getById(customer.getId());
        int balance = customerFromPersisted.getBalance();
        assertEquals(0, balance);
    }


    private Customer getNewUniqueCustomer()
    {
        Customer customer = customerComponent.createNew();
        // Example of pattern to create unique PKs
        customer.setName(System.currentTimeMillis() + ":" +customer.getId());
        customerComponent.persist(customer);
        return customer;
    }



    public void testCustomerWithOneInvoiceHasInvoiceAmountAsCustomerBalance()
    {
        Customer customer = getNewUniqueCustomer();

        Invoice invoice = invoiceComponent.createNew();
        invoice.setInvoiceNumber(invoiceComponent.getNextInvoiceNumber());

        int INVOICE_TOTAL = 99;
        invoice.setTotal(INVOICE_TOTAL);
        invoice.setCustomer(customer);
        invoiceComponent.persist(invoice);

        Customer customerFromPersisted = customerComponent.getById(customer.getId());
        int balance = customerFromPersisted.getBalance();
        assertEquals(INVOICE_TOTAL, balance);
    }


    // Changed to 3.8.1 for setup method
    // @Test(expected = InvoiceLimitExceededException.class)
    public void testInvoiceExceedsLimitThrowsInvoiceLimitException()
    {
        try
        {
            Customer customer = getNewUniqueCustomer();

            Invoice invoice = invoiceComponent.createNew();
            int INVOICE_TOTAL = GlobalContext.getAccountingConstants().getMaximumInvoiceTotal() + 1;
            invoice.setTotal(INVOICE_TOTAL);
            invoice.setCustomer(customer);
            invoiceComponent.persist(invoice);
            fail("Expected InvoiceLimitExceeded exception");
        }
        catch (InvoiceLimitExceededException e)
        {
            // Expected
        }
    }

    // BUG Reported:
    // If invoice is at limit, an exception is thrown. Fix this using TDD!
    public void testInvoiceAtLimitDoesNotThrowInvoiceLimitException()
    {
        try
        {
            Customer customer = getNewUniqueCustomer();
            Invoice invoice = invoiceComponent.createNew();
            int INVOICE_TOTAL = GlobalContext.getAccountingConstants().getMaximumInvoiceTotal();
            invoice.setInvoiceNumber(invoiceComponent.getNextInvoiceNumber());
            invoice.setTotal(INVOICE_TOTAL);
            invoice.setCustomer(customer);
            invoiceComponent.persist(invoice);
        }
        catch (InvoiceLimitExceededException e)
        {
            fail("Not expecting exception: " + e);
        }
    }


    //@Test
    public void testAddInvoiceToCustomerIncreasesCustomersBalanceWithScratchCustomer()
    {
        Customer customer = (Customer) DOF.createScratchObject("customer.scratch.xml");

        Invoice invoice = invoiceComponent.createNew();
        invoice.setInvoiceNumber(invoiceComponent.getNextInvoiceNumber());
        int INVOICE_TOTAL = 99;
        invoice.setTotal(INVOICE_TOTAL);
        invoice.setCustomer(customer);
        invoiceComponent.persist(invoice);

        Invoice invoice2 = invoiceComponent.createNew();
        int INVOICE_TOTAL_2 = 101;
        invoice2.setTotal(INVOICE_TOTAL_2);
        invoice2.setCustomer(customer);
        invoice2.setInvoiceNumber(invoiceComponent.getNextInvoiceNumber());
        invoiceComponent.persist(invoice2);


        Customer customerFromPersisted = customerComponent.getById(customer.getId());
        int balance = customerFromPersisted.getBalance();
        assertEquals(INVOICE_TOTAL + INVOICE_TOTAL_2, balance);

    }


    /**
     * Next test demonstrates creating 2 scratch Invoices that depend on the same scratch customer.
     */
    @Test
    public void testAddInvoiceToCustomerDoublesCustomersBalanceWithScratchInvoice()
    {

        Invoice invoice = (Invoice) DOF.createScratchObject("invoice.scratchWithScratchCustomer.xml");
        Customer customer = invoice.getCustomer();
        assertEquals(customer.getBalance(), invoice.getTotal());
        assertEquals(customer.getBalance(), invoice.getPendingBalance());

        Map scratchReferenceToPk = new HashMap();
        scratchReferenceToPk.put("scratchCustomerPk", customer.getName() + "");
        Invoice invoice2 = (Invoice) DOF.createScratchObject("invoice.scratchWithScratchCustomer.xml", scratchReferenceToPk);
        assertEquals(invoice.getCustomer().getId(), invoice2.getCustomer().getId());
        assertEquals(invoice2.getTotal().intValue() * 2 , invoice2.getCustomer().getBalance().intValue());
    }


    @Test
    public void testCreate2ScratchInvoicesCreates2ScratchInvoicesWithUniqueCustomers()
    {

        Invoice invoice = (Invoice) DOF.createScratchObject("invoice.scratchWithScratchCustomer.xml");
        Customer customer = invoice.getCustomer();
        assertEquals(customer.getBalance(), invoice.getTotal());
        assertEquals(customer.getBalance(), invoice.getPendingBalance());

        Invoice invoice2 = (Invoice) DOF.createScratchObject("invoice.scratchWithScratchCustomer.xml");
        assertTrue(invoice2.getCustomer().getId() > invoice.getCustomer().getId());
    }

// @Test
// public void testAddPaymentToCustomerDecreasesCustomersBalance()


}
