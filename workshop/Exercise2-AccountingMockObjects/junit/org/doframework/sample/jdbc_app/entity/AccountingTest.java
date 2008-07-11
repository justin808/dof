package org.doframework.sample.jdbc_app.entity;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.factory.CustomerFactory;
import org.doframework.sample.jdbc_app.factory.InvoiceFactory;
import org.doframework.sample.jdbc_app.factory.MockFactoryLookupService;
import org.doframework.sample.jdbc_app.factory.SampleApp_FactoryLookupService;
import org.junit.BeforeClass;
import org.junit.Test;

public class AccountingTest
{
	private SampleApp_FactoryLookupService factoryLookupService = GlobalContext.getFactoryLookupService();
    private CustomerFactory customerComponent = factoryLookupService.getCustomerFactory();
    private InvoiceFactory invoiceComponent = factoryLookupService.getInvoiceFactory();


    @BeforeClass
    public static void beforeTests()
    {
    	// Put the Mock Factory Lookup Service in place so our
    	// tests work.
    	MockFactoryLookupService mockFactoryLookupService = new MockFactoryLookupService();
        GlobalContext.setFactoryLookupService(mockFactoryLookupService);
        //GlobalContext.setAccountingConstants(new MockAccountingConstants());
    }


    @Test
    public void testCustomerWithNoInvoicesHasZeroBalance()
    {
        Customer customer = getNewUniqueCustomer();
        Customer customerFromPersisted = customerComponent.getById(customer.getId());
        BigDecimal balance = customerFromPersisted.getBalance();
        assertEquals(0, balance.doubleValue());
    }


    private Customer getNewUniqueCustomer()
    {
    	int newCustomerId = customerComponent.getNextId();
        Customer customer = new Customer();
        // Example of pattern to create unique PKs
        customer.setName(System.currentTimeMillis() + "");
        customerComponent.insert(customer);
        return customer;
    }


    @Test
    public void testCustomerWithOneInvoiceHasInvoiceAmountAsCustomerBalance()
    {
        Customer customer = getNewUniqueCustomer();

        Invoice invoice = new Invoice();
//        int INVOICE_AMOUNT = 99;
//        invoice.setAmount(INVOICE_AMOUNT);
//        invoice.setCustomer(customer);
        invoiceComponent.insert(invoice);
//
//        Customer customerFromPersisted = customerComponent.getById(customer.getId());
//        int balance = customerFromPersisted.getBalance();
//        assertEquals(INVOICE_AMOUNT, balance);
    }


//    @Test(expected = InvoiceLimitExceededException.class)
    public void testInvoiceExceedsLimitThrowsInvoiceLimitException()
    {
//        Customer customer = getNewUniqueCustomer();
//
//        Invoice invoice = invoiceComponent.createNew();
//        int INVOICE_AMOUNT = GlobalContext.getAccountingConstants().getMaximumInvoiceAmount() + 1;
//        invoice.setAmount(INVOICE_AMOUNT);
//        invoice.setCustomer(customer);
//        invoiceComponent.persist(invoice);
    }

// BUG Reported:
// If invoice is at limit, an exception is thrown. Fix this using TDD!

//	@Test
//	public void testAddInvoiceToCustomerIncreasesCustomersBalance()

// @Test
// public void testAddPaymentToCustomerDecreasesCustomersBalance()


}
