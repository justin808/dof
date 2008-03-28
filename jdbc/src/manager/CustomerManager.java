package manager;

import global.*;

import java.util.*;
import java.math.*;

import entity.*;

public class CustomerManager
{

    /**
     * Add a late fee invoice for customers with invoices over one month due.
     */
    public static void updateOverDueFlagsAndAddInvoicesForLateFees()
    {
        final BigDecimal lateFeePercentage = GlobalContext.getConstants().getLateFeePercentage();

        // get all customers with balances
        List<Customer> customersWithOutstandingBalances = GlobalContext.getComponentFactory().getCustomerComponent().getCustomersWithOutstandingBalances();

        Date dateLateInvoices = getDateForLateInvoices().getTime();
        // get customers who have an invoice not paid over 30 days old
        // we'll update the any overdue flags to false when a payment is received, so, since time
        // only goes forward, we don't need to handle those
        for (Iterator<Customer> iterator = customersWithOutstandingBalances.iterator(); iterator.hasNext();)
        {
            Customer customer = iterator.next();
            List<Invoice> invoices = customer.getInvoices();
            BigDecimal overDueTotal = BigDecimal.ZERO;
            for (Iterator<Invoice> iterator1 = invoices.iterator(); iterator1.hasNext();)
            {
                Invoice invoice = iterator1.next();
                if (invoice.getInvoiceDate().compareTo(dateLateInvoices) <= 0)
                {
                    if (invoice.getPendingBalance().compareTo(new BigDecimal(0)) > 0)
                    {
                        overDueTotal = overDueTotal.add(invoice.getSubTotal());
                    }
                }
                else
                {
                    break; // newer than late period
                }
            }
            if (overDueTotal.compareTo(BigDecimal.ZERO) > 0)
            {
                customer.setOverdue(true);
                Invoice newInvoice = new Invoice();
                newInvoice.setSubTotal(GlobalContext.currencyRound(overDueTotal.multiply(
                        lateFeePercentage)));
                newInvoice.setCustomer(customer);
                newInvoice.persist();// persists the customer
            }
        }
    }

    static GregorianCalendar getDateForLateInvoices()
    {
        final int daysBeforeLate = GlobalContext.getConstants().getDaysBeforeLate();
        GregorianCalendar oneMonthAgo = new GregorianCalendar();
        oneMonthAgo.add(Calendar.DAY_OF_MONTH, -1 * daysBeforeLate );
        return oneMonthAgo;
    }
}
