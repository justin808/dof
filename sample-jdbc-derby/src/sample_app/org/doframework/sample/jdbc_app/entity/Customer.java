package org.doframework.sample.jdbc_app.entity;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.factory.CustomerFactory;
import org.doframework.sample.jdbc_app.factory.InvoiceFactory;



public class Customer
{

    int id;
    private String name;
    private String phoneNumber;
    private BigDecimal balance = BigDecimal.ZERO;
    private boolean isOverdue;
    private boolean isNew;




    public Customer()
    {
        this.id = getCustomerComponent().getNextId();
        setNew(true);
    }


    public Customer(int id)
    {
        this.id = id;
        setNew(false);
    }

    public void persist()
    {
        if (isNew())
        {
            CustomerFactory customerFactory = getCustomerComponent();
            customerFactory.insert(this);
            setNew(false);
        }
        else
        {
            getCustomerComponent().update(this);
        }
    }


    private boolean isNew()
    {
        return isNew;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }


    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }


    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public void delete()
    {
        getCustomerComponent().delete(this);
    }


    public BigDecimal getBalance()
    {
        return balance;
    }

    public void setBalance(BigDecimal balance)
    {
        this.balance = balance;
    }


    public boolean isOverDue()
    {
        return isOverdue();
    }

    public boolean isOverdue()
    {
        return isOverdue;
    }

    public void setOverdue(boolean overdue)
    {
        isOverdue = overdue;
    }

    /**
     * @return All the invoices for the customer, ordered chronologically
     */
    public List<Invoice> getInvoices()
    {
        InvoiceFactory invoiceFactory =
                GlobalContext.getComponentFactory().getInvoiceFactory();
        return invoiceFactory.getInvoicesForCustomer(this);
    }

    public CustomerFactory getCustomerComponent()
    {
        return GlobalContext.getComponentFactory().getCustomerFactory();
    }


    public String toString()
    {
    	// Use a String Buffer because it is more effient and
    	// because it lets our code be a little more flexible.
    	StringBuffer returnString = new StringBuffer();
    	
    	// The number formatter will be used later.
    	NumberFormat formatter = NumberFormat.getInstance();
    	formatter.setMinimumFractionDigits(2);
    	formatter.setMaximumFractionDigits(2);
    	
    	returnString.append("Customer{id=");
    	returnString.append(id);
    	returnString.append(", name='");
    	returnString.append(name);
    	returnString.append("', phoneNumber='");
    	returnString.append(phoneNumber);
    	returnString.append("', balance=");
    	
    	String balanseAsString = formatter.format(balance);
    	returnString.append(balanseAsString);
    	
    	returnString.append(", isOverdue=");
    	returnString.append(isOverdue);
    	returnString.append("}");
    	
    	String theString = returnString.toString();
    	
        return theString;
    }

    public void setNew(boolean aNew)
    {
        isNew = aNew;
    }
    
    public boolean equals(Object objectToTest) {
    	boolean returnValue = false;
    	
    	// First check to see if this the passed in variable is null.
    	if (objectToTest != null) {
    		// Now see if the object is a Customer object so we can do
    		// more comparisons
    		if (objectToTest instanceof Customer) {
    			// Now get the object's string value because we can easily
    			// test that.
    			String stringToTest = objectToTest.toString();
    			String thisString = this.toString();
    			// Now test the string.
    			returnValue = thisString.equals(stringToTest);
    		}
    	}
    	
    	return returnValue;
    }
}
