package manager;

import junit.framework.*;

import java.util.*;
import java.math.*;

import component.*;
import global.*;
import entity.*;

/**
 * User: gordonju Date: Jan 12, 2008 Time: 1:50:05 PM
 */
public class CustomerManagerTest extends TestCase
{

    // Use static initialization, rather than setup which gets called once per test
    {
        CustomerTestUtils.initMockDependencies();
    }


    /**
     * Triangulation example
     */
    public void testOneMonthAgo()
    {
        GregorianCalendar testDate = CustomerManager.getDateForLateInvoices();
        final int daysBeforeLate = GlobalContext.getConstants().getDaysBeforeLate();

        testDate.add(Calendar.DAY_OF_MONTH, daysBeforeLate);
        testDate.add(Calendar.HOUR, 1);
        assertTrue(testDate.getTime().getTime() > (new Date().getTime()));
    }

    public void testFlagOverdueCustomersWithOneInvoiceAtDifferentDates()
    {

        // SETUP 3 records
        final GregorianCalendar ironsDate = new GregorianCalendar();
        ironsDate.add(GregorianCalendar.MONTH, -5);
        Customer irons = CustomerTestUtils.createInvoiceForNewCustomer("Irons", ironsDate.getTime(),
                                                                    new BigDecimal(12.34));

        final GregorianCalendar machadoDate = new GregorianCalendar();
        machadoDate.add(GregorianCalendar.DATE, -1);
        Customer machado = CustomerTestUtils.createInvoiceForNewCustomer("Machado", machadoDate.getTime(),
                                                                      new BigDecimal(12.34));

        final GregorianCalendar slaterDate = new GregorianCalendar();
        slaterDate.add(GregorianCalendar.DATE, -35);
        Customer slater = CustomerTestUtils.createInvoiceForNewCustomer("slater", slaterDate.getTime(),
                                                                     new BigDecimal(12.34));

        CustomerManager.updateOverDueFlagsAndAddInvoicesForLateFees();

        CustomerComponent customerComponent = GlobalContext.getComponentFactory().getCustomerComponent();
        irons = customerComponent.getById(irons.getId());
        assertTrue(irons.isOverDue());
        slater = customerComponent.getById(slater.getId());
        assertTrue(slater.isOverDue());
        machado = customerComponent.getById(machado.getId());
        assertFalse(machado.isOverDue());
    }

    public void testOverdueCalculationForMultipleInvoicesOverdue()
    {
        BigDecimal invoiceAmount1 = new BigDecimal("11");
        BigDecimal invoiceAmount2 = new BigDecimal("12");
        BigDecimal invoiceAmount3 = new BigDecimal("13");
        Date today = new Date();

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, -2);
        Date twoMonthsAgo = calendar.getTime();
        calendar.add(Calendar.MONTH, -2);
        Date fourMonthsAgo = calendar.getTime();

        Customer curren = CustomerTestUtils.createInvoiceForNewCustomer("Curren", today,
                                                                        invoiceAmount1);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(curren, invoiceAmount2, fourMonthsAgo);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(curren, invoiceAmount3, twoMonthsAgo);

        BigDecimal oldBalance = curren.getBalance();
        assertEquals(invoiceAmount1.add(invoiceAmount2).add(invoiceAmount3), oldBalance);
        CustomerManager.updateOverDueFlagsAndAddInvoicesForLateFees();

        Customer currenFromDB =
                GlobalContext.getComponentFactory().getCustomerComponent().getById(curren.getId());

        BigDecimal newBalance = currenFromDB.getBalance();
        BigDecimal latePercentage = GlobalContext.getConstants().getLateFeePercentage();

        BigDecimal expectedLateCharge = GlobalContext.currencyRound(invoiceAmount2.add(invoiceAmount3).multiply(latePercentage));
        assertEquals(oldBalance.add(expectedLateCharge),
                     newBalance);

        List<Invoice> invoices = curren.getInvoices();
        Invoice lateChargeInvoice = invoices.get(invoices.size() - 1);
        assertEquals(expectedLateCharge, lateChargeInvoice.getSubTotal());
    }


}
