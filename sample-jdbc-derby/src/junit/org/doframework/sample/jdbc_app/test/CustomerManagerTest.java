package org.doframework.sample.jdbc_app.test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import org.doframework.DOF;
import org.doframework.sample.jdbc_app.CustomerManager;
import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.entity.Customer;
import org.doframework.sample.jdbc_app.entity.Invoice;
import org.doframework.sample.jdbc_app.factory.CustomerFactory;
import org.doframework.sample.jdbc_app.factory.DuplicateRecordException;
import org.doframework.sample.jdbc_app.factory.InvoiceFactory;

/**
 * User: gordonju Date: Jan 12, 2008 Time: 1:50:05 PM
 */
public class CustomerManagerTest extends TestCase
{
	/**
	 * This method will delete all of the invoices associated with the
	 * passed in customer.
	 * 
	 * @param customer
	 */
	private void deleteInvoicesFor(Customer customer) {
		
        // For program effiency get the invoice factory 
        InvoiceFactory factory = GlobalContext.getComponentFactory().getInvoiceFactory();
		List<Invoice> invoicesToDelete = customer.getInvoices();
		// For effiency get the number of invoices outside of the for loop
		// this also makes it a little easier to debug.
        int numberOfInvoices = invoicesToDelete.size();
        for (int i = 0; i < numberOfInvoices; i++)
        {
        	Invoice invoiceToDelete = invoicesToDelete.get(i);
        	factory.delete(invoiceToDelete);
        }
	}
	
	/**
	 * This method will attempt to create a Customer who already exists.
	 *
	 */
	public void testDOFSampleCreateNewCustomerDuplicateOf35() {
		// Make sure we have a record for customer 35 - this will
		// be the customer we try to create a second time.
		Customer dofCustomer = (Customer) DOF.require("customer.35.xml");
		
		// A duplicate customer is determined if a customer has the same
		// name and phone number of an existing customer.  Therefore, pull
		// the values of these attributes out of the customer ojbect returned
		// by DOF.  This is useful because if someone changes the DOF customer
		// xml file this test will still work.
		String name = dofCustomer.getName();
		String phoneNumber = dofCustomer.getPhoneNumber();
		
		try {
			// Now try to create the duplicate customer.
			CustomerManager.createNewCustomer(name, phoneNumber);
			
			// An exception should have been thrown so if this line
			// is executed that means an exception was not thrown.
			fail("Was able to create a duplicate customer");
		}
		catch (DuplicateRecordException goodDeal) {
			// This test should have caused this exception to
			// be thrown, therefore if this exception is thrown
			// this is a good thing and the test has passed.
		}
	}
	
    public void testDOFSampleFlagOverdueCustomersWithOneInvoiceAtDifferentDates()
    {
    	// Make sure we have 5 records to in the database - we'll make 3 
    	// of them be records we change and make past due, but we want to
    	// make sure 2 other records do not show up as past due.
    	Customer customer25 = (Customer)DOF.require("customer.25.xml");
    	Customer customer35 = (Customer)DOF.require("customer.35.xml");
    	Customer customer45 = (Customer)DOF.require("customer.45.xml");
    	Customer customer55 = (Customer)DOF.require("customer.55.xml");
    	Customer customer65 = (Customer)DOF.require("customer.65.xml");
    	
        // Make the 3 of the customers past due.
    	// We do this because this business logic is based on the current date
    	// and therefore we must have logic that sets a date that is based on
    	// today's date.
        final GregorianCalendar customer25Date = new GregorianCalendar();
        customer25Date.add(GregorianCalendar.MONTH, -5);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(customer25,
        													 customer25Date.getTime(),
                                                             new BigDecimal(12.34));

        final GregorianCalendar customer45Date = new GregorianCalendar();
        customer45Date.add(GregorianCalendar.DATE, -60);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(customer45,
                                                             customer45Date.getTime(),
                                                             new BigDecimal(12.34));

        final GregorianCalendar customer65Date = new GregorianCalendar();
        customer65Date.add(GregorianCalendar.DATE, -35);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(customer65,
                                                             customer65Date.getTime(),
                                                             new BigDecimal(12.34));
        
        // Make 2 with current invoices
        final GregorianCalendar customer35Date = new GregorianCalendar();
        customer65Date.add(GregorianCalendar.DATE, -5);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(customer35,
                                                             customer35Date.getTime(),
                                                             new BigDecimal(12.34));

        final GregorianCalendar customer55Date = new GregorianCalendar();
        customer65Date.add(GregorianCalendar.DATE, -10);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(customer55,
                                                             customer55Date.getTime(),
                                                             new BigDecimal(12.34));
        
        // Call our sample applications business logic to batch process
        // customer accounts to mark them late.
        CustomerManager.updateOverDueFlagsAndAddInvoicesForLateFees();

        // Now pull out the customers and test to make sure the past
        // dues are marked past due.
        CustomerFactory customerFactory =
                GlobalContext.getComponentFactory().getCustomerFactory();
        Customer tempPastDueCustomer = customerFactory.getById(customer25.getId());
        assertTrue(tempPastDueCustomer.isOverDue());
        tempPastDueCustomer = customerFactory.getById(customer45.getId());
        assertTrue(tempPastDueCustomer.isOverDue());
        tempPastDueCustomer = customerFactory.getById(customer65.getId());
        assertTrue(tempPastDueCustomer.isOverDue());
        
        // Now make sure that our non past due customers are still not
        // past due.
        Customer tempCurrentCustomer = customerFactory.getById(customer35.getId());
        assertFalse(tempCurrentCustomer.isOverDue());
        tempCurrentCustomer = customerFactory.getById(customer55.getId());
        assertFalse(tempCurrentCustomer.isOverDue());

    	// Clean up our objects and related data objects that
        // we created/updated during this test.
        
        // Since we did not create the Invoice objects with the DOF framework we must
        // delete these manually.
        deleteInvoicesFor(customer25);
        deleteInvoicesFor(customer35);
        deleteInvoicesFor(customer45);
        deleteInvoicesFor(customer55);
        deleteInvoicesFor(customer65);
        
        // Now we can have DOF delete the customer objects.
        //
        // The deletes are done in assertTrue statemenents because if
        // DOF returns a false on delete that means our test did not
        // clean up properly.
    	assertTrue(DOF.delete("customer.25.xml"));
    	assertTrue(DOF.delete("customer.35.xml"));
    	assertTrue(DOF.delete("customer.45.xml"));
    	assertTrue(DOF.delete("customer.55.xml"));
    	assertTrue(DOF.delete("customer.65.xml"));
    	
    	// Because we manipulated the DOF objects (e.g. Customer and Invoice
    	/// objects) outside of the DOF framework, we need to clear the 
    	// DOF cache.
    	DOF.clearFileCache();    
    }

    /**
     * This test will create a new unique customer.
     */
    public void testCreateNewCustomer() {
    	
    	// Create the new Customer based on this data
    	String name = "Franky W. Share";
    	String phoneNumber = "479.444.4444";
    	
    	Customer newCustomer = CustomerManager.createNewCustomer(name, phoneNumber);
    	
    	// Now that customer has been created verify that it has been created
    	// properly.
    	if (newCustomer == null) {
    		fail("A null was returned for the newly created customer.");
    	}
    	
    	// To verify the customer we will lookup the customer directly
    	// from the Factory.
    	int customerId = newCustomer.getId();
    	
    	// Make sure the name is the same.
    	String custName = newCustomer.getName();
    	// The name variable is used because we know it will not be null.
    	// Whereas, the custName could be null.
    	if (name.equals(custName) == false) {
    		fail("The new customer name does not match the original name");
    	}
    	
    	String custPhoneNumber = newCustomer.getPhoneNumber();
    	// The phoneNumber variable is used because we know it will not be null.
    	// Whereas, the custPhoneNumbercould be null.
    	if (phoneNumber.equals(custPhoneNumber) == false) {
    		fail("The new customer phone number does not match the original phone number");
    	}
    	
    	CustomerFactory factory = GlobalContext.getComponentFactory().getCustomerFactory();
    	Customer dbCustomer = factory.getById(customerId);
    	
    	// Make sure that the newly created customer matches the
    	// customer in the database.
    	if (newCustomer.equals(dbCustomer) == false) {
    		fail("The returned newly created customer is not equal to the customer returned by the CustomerFactory");
    	}
    	
    	// The new customer was created successfully, so now delete
    	// the customer so this test can be run again in the future.
    	//
    	// The delete is in an assert because the delete needs to return
    	// true - because this means the delete happened successfully.
    	// The delete should always happen successfully, however, the assert
    	// statement verifies this.
    	assertTrue(factory.delete(newCustomer));
    }

    public void brokenTestOverdueCalculationForMultipleInvoicesOverdue()
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

        Customer curren =
                CustomerTestUtils.createInvoiceForNewCustomer("Curren", today, invoiceAmount1);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(curren, fourMonthsAgo, invoiceAmount2);
        CustomerTestUtils.createInvoiceWithAmountForCustomer(curren, twoMonthsAgo, invoiceAmount3);

        BigDecimal oldBalance = curren.getBalance();
        assertEquals(invoiceAmount1.add(invoiceAmount2).add(invoiceAmount3), oldBalance);
        CustomerManager.updateOverDueFlagsAndAddInvoicesForLateFees();

        Customer currenFromDB =
                GlobalContext.getComponentFactory().getCustomerFactory().getById(curren.getId());

        BigDecimal newBalance = currenFromDB.getBalance();
        BigDecimal latePercentage = GlobalContext.getConstants().getLateFeePercentage();

        BigDecimal expectedLateCharge =
                GlobalContext.currencyRound(invoiceAmount2.add(invoiceAmount3).multiply(
                        latePercentage));
        assertEquals(oldBalance.add(expectedLateCharge), newBalance);

        List<Invoice> invoices = curren.getInvoices();
        Invoice lateChargeInvoice = invoices.get(invoices.size() - 1);
        assertEquals(expectedLateCharge, lateChargeInvoice.getSubTotal());
    }


}
