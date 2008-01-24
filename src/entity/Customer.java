package entity;

import global.*;

import java.util.*;
import java.math.*;

import component.*;


public class Customer
{

    int id;
    private String name;
    private String phoneNumber;
    private BigDecimal balance = BigDecimal.ZERO;
    private boolean isOverdue;
    private boolean isNew;



    public static Customer createNewCustomer(String name, String phoneNumber)
    {
        final Customer customer = new Customer();
        customer.setName(name);
        customer.setPhoneNumber(phoneNumber);
        customer.setNew(true);
        return customer;
    }

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
            CustomerComponent customerComponent = getCustomerComponent();
            customerComponent.insert(this);
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
        InvoiceComponent invoiceComponent =
                GlobalContext.getComponentFactory().getInvoiceComponent();
        return invoiceComponent.getInvoicesForCustomer(this);
    }

    public CustomerComponent getCustomerComponent()
    {
        return GlobalContext.getComponentFactory().getCustomerComponent();
    }


    public String toString()
    {
        return "Customer{" +
               "id=" +
               id +
               ", name='" +
               name +
               '\'' +
               ", phoneNumber='" +
               phoneNumber +
               '\'' +
               ", balance=" +
               balance +
               ", isOverdue=" +
               isOverdue +
               '}';
    }

    public void setNew(boolean aNew)
    {
        isNew = aNew;
    }
}
