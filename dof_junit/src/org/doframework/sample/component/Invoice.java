package org.doframework.sample.component;

import java.util.*;

/**
 * Simple Java Bean class. No business logic in this class.
 *
 * @see org.doframework.sample.component.InvoiceComponent
 */
public class Invoice extends Entity
{
    private Customer customer;
    private Date invoiceDate;
    private Integer total;
    private Integer pendingBalance;

    private List<LineItem> lineItems = new ArrayList<LineItem>();


    public Invoice(int id)
    {
        super(id);
    }


    public Customer getCustomer()
    {
        return customer;
    }


    public Invoice setCustomer(Customer customer)
    {
        this.customer = customer;
        return this;
    }


    public Date getInvoiceDate()
    {
        return invoiceDate;
    }


    public Invoice setInvoiceDate(Date invoiceDate)
    {
        this.invoiceDate = invoiceDate;
        return this;
    }


    public Integer getTotal()
    {
        return total;
    }


    public Invoice setTotal(Integer total)
    {
        this.total = total;
        this.pendingBalance = total;
        return this;
    }


    public Integer getPendingBalance()
    {
        return pendingBalance;
    }


    public Invoice setPendingBalance(Integer pendingBalance)
    {
        this.pendingBalance = pendingBalance;
        return this;
    }


    public List<LineItem> getLineItems()
    {
        return this.lineItems;
    }


    public Invoice setLineItems(List<LineItem> lineItems)
    {
        this.lineItems = lineItems;
        return this;
    }





}
