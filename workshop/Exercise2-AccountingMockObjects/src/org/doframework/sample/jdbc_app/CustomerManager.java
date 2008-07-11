package org.doframework.sample.jdbc_app;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.doframework.sample.jdbc_app.entity.Customer;
import org.doframework.sample.jdbc_app.entity.Invoice;


public class CustomerManager
{
	/**
	 * This method will create a new Customer.  The customer must be unique and
	 * not exist.
	 * 
	 * @param name
	 * @param phoneNumber
	 * @return
	 */
	public static Customer createNewCustomer(String name, String phoneNumber) {
		// Declare the return value here so we have it.
		//
		// Start creating the new customer and populating its values
		Customer returnCustomer = new Customer();
		returnCustomer.setName(name);
		returnCustomer.setPhoneNumber(phoneNumber);
		// Every new customer is marked new.
		returnCustomer.setNew(true);
		
		// Now save the new customer
		returnCustomer.persist();
		
		return returnCustomer;
	}

    /**
     * Add a late fee invoice for customers with invoices over one month due.
     */
    public static void updateOverDueFlagsAndAddInvoicesForLateFees()
    {
        final BigDecimal lateFeePercentage = GlobalContext.getConstants().getLateFeePercentage();

        // get all customers with balances
        List<Customer> customersWithOutstandingBalances = GlobalContext.getFactoryLookupService().getCustomerFactory().getCustomersWithOutstandingBalances();

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

    public static GregorianCalendar getDateForLateInvoices()
    {
        final int daysBeforeLate = GlobalContext.getConstants().getDaysBeforeLate();
        GregorianCalendar oneMonthAgo = new GregorianCalendar();
        oneMonthAgo.add(Calendar.DAY_OF_MONTH, -1 * daysBeforeLate );
        return oneMonthAgo;
    }
}
