package entity;

import junit.framework.*;

import java.util.*;
import java.math.*;

import global.*;
import component.*;

/**
 * User: gordonju Date: Jan 11, 2008 Time: 8:04:30 PM
 */
public abstract class AccountingTest extends TestCase
{


    /**
     * Sets up the fixture, for example, open a network connection. This method is called before a test
     * is executed.
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }


    public void testAddCustomerRecord()
    {
        final String lastName = "Gordon" + System.currentTimeMillis();
        Customer createdCustomer = CustomerTestUtils.createCustomerRecordWithNamePhoneNumber(lastName, "8085551212");
        Customer fetchedCustomer = getCustomerComponent().getById(createdCustomer.getId());
        assertNotNull(fetchedCustomer);
        assertEquals(lastName, fetchedCustomer.getName());
        assertEquals(createdCustomer.getId(), fetchedCustomer.getId());
    }


    public void testDeleteCustomerRecord()
    {
        final String lastName = "Smith";
        Customer customer = CustomerTestUtils.createCustomerRecordWithNamePhoneNumber(lastName, "8085551212");
        assertNotNull(getCustomerComponent().getById(customer.getId()));
        customer.delete();
        assertNull(getCustomerComponent().getById(customer.getId()));
    }


    public void testCreateCustomerRecordThrowsExceptionWhenDuplicateCustomerCreated()
    {
        String name = "Higgins" + System.currentTimeMillis();
        CustomerTestUtils.createCustomerRecordWithNamePhoneNumber(name, "8085551212");
        try
        {
            CustomerTestUtils.createCustomerRecordWithNamePhoneNumber(name, "8085551212");
            fail("Expected duplicate customer exception");
        }
        catch (Exception e)
        {
        }
    }


    public void testAddChargeToCustomerIncreasesCustomersBalance()
    {
        String name = "Slater" + System.currentTimeMillis();
        CustomerTestUtils.createInvoiceForNewCustomer(name, new Date(), new BigDecimal("12.34"));
    }

    public void testGetInvoicesReturnsInvoicesForCustomerInChronologicalOrder()
    {
        BigDecimal invoiceAmount1 = new BigDecimal("11");
        BigDecimal invoiceAmount2 = new BigDecimal("12");
        BigDecimal invoiceAmount3 = new BigDecimal("13");
        Date today = new Date();

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, -1);
        Date oneMonthsAgo = calendar.getTime();
        calendar.add(Calendar.MONTH, -1);
        Date twoMonthsAgo = calendar.getTime();

        Customer lopez = CustomerTestUtils.
                createInvoiceForNewCustomer("Lopez" + System.currentTimeMillis(), today,
                                            invoiceAmount1);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(lopez,invoiceAmount2, twoMonthsAgo);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(lopez, invoiceAmount3, oneMonthsAgo);

        List<Invoice> list = lopez.getInvoices();
        assertEquals(invoiceAmount2, list.get(0).getSubTotal());
        assertEquals(invoiceAmount3, list.get(1).getSubTotal());
        assertEquals(invoiceAmount1, list.get(2).getSubTotal());
    }


    public void testRecordPaymentDecreasesCustomerBalanceAndLastInvoicePendingAmount()
    {
        BigDecimal invoiceAmount = new BigDecimal("12.34");
        Customer irons = CustomerTestUtils.createInvoiceForNewCustomer("Irons" + System.currentTimeMillis(),
                                                                       new Date(),
                                                                       invoiceAmount);
        BigDecimal originalBalance = irons.getBalance();
        Payment newPayment = new Payment();
        final BigDecimal paymentAmount = new BigDecimal("11.00");
        newPayment.setAmount(paymentAmount);
        newPayment.setCustomer(irons);
        newPayment.persist();
        BigDecimal invoiceBalance = originalBalance.subtract(paymentAmount);
        assertEquals(invoiceBalance, irons.getBalance());

        List<Invoice> invoices = irons.getInvoices();
        Invoice invoice = invoices.get(0);
        assertEquals(invoiceBalance, invoice.getPendingBalance());
    }


    public void testRecordPaymentDecreasesCustomerBalanceAndLastTwoInvoicePendingAmounts()
    {
        BigDecimal invoiceAmount1 = new BigDecimal("11");
        BigDecimal invoiceAmount2 = new BigDecimal("12");
        BigDecimal invoiceAmount3 = new BigDecimal("13");
        Date today = new Date();

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, -1);
        Date oneMonthsAgo = calendar.getTime();
        calendar.add(Calendar.MONTH, -1);
        Date twoMonthsAgo = calendar.getTime();

        String uniqueName = "Potter" + System.currentTimeMillis();
        Customer potter = CustomerTestUtils.createInvoiceForNewCustomer(uniqueName, today,
                                                                       invoiceAmount1);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(potter, invoiceAmount2, twoMonthsAgo);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(potter, invoiceAmount3, oneMonthsAgo);

        BigDecimal originalBalance = potter.getBalance();

        PaymentComponent paymentComponent =
                GlobalContext.getComponentFactory().getPaymentComponent();
        Payment newPayment = new Payment();
        final BigDecimal paymentAmount = new BigDecimal("26.00");
        newPayment.setAmount(paymentAmount);
        newPayment.setCustomer(potter);
        newPayment.persist();
        BigDecimal remainingInvoiceBalance = originalBalance.subtract(paymentAmount);
        assertEquals(remainingInvoiceBalance, potter.getBalance());

        List<Invoice> invoices = potter.getInvoices(); // oldest first
        assertEquals(BigDecimal.ZERO, invoices.get(0).getPendingBalance()); // paid off
        assertEquals(BigDecimal.ZERO, invoices.get(1).getPendingBalance()); // paid off
        assertEquals(potter.getBalance(), invoices.get(2).getPendingBalance()); // balance
    }




    public CustomerComponent getCustomerComponent()
    {
        return GlobalContext.getComponentFactory().getCustomerComponent();
    }

}
