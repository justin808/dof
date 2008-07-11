package org.doframework.sample.jdbc_app.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.factory.InvoiceFactory;


public class Invoice
{
    private Date invoiceDate;
    private Customer customer;
    private BigDecimal subTotal;
    private BigDecimal taxRate;
    private BigDecimal tax;
    private BigDecimal total;
    private int id;

    private BigDecimal pendingBalance;
    private boolean isNew;

    private List<LineItem> lineItems = new ArrayList<LineItem>();

    /**
     * return a new Invoice with the next Invoice Id from the Invoice Component
     */
    public Invoice()
    {
        setId(getNewInvoiceId());
        setNew(true);
    }

    private int getNewInvoiceId()
    {
        InvoiceFactory invoiceFactory =
                GlobalContext.getFactoryLookupService().getInvoiceFactory();
        return invoiceFactory.getNextId();
    }

    /**
     * Create an invoice object from an existing invoice
     * @param invoiceId
     */
    public Invoice(int invoiceId)
    {
        setId(invoiceId);
        setNew(false);
    }


    public Date getInvoiceDate()
    {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate)
    {
        this.invoiceDate = invoiceDate;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public BigDecimal getSubTotal()
    {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal)
    {
        this.subTotal = subTotal;
        this.pendingBalance = subTotal;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void persist()
    {
        InvoiceFactory invoiceFactory =
                GlobalContext.getFactoryLookupService().getInvoiceFactory();
        if (isNew())
        {
            if (getInvoiceDate() == null)
            {
                setInvoiceDate(new Date());
            }
            BigDecimal customerBalance = customer.getBalance();
            BigDecimal newCustomerBalance = customerBalance.add(getSubTotal());
            customer.setBalance(newCustomerBalance);
            customer.persist();
            invoiceFactory.insert(this);
            setNew(false);
        }
        else
        {
            invoiceFactory.update(this);
        }
     }

    public BigDecimal getPendingBalance()
    {
        return pendingBalance;
    }

    public void setPendingBalance(BigDecimal pendingBalance)
    {
        this.pendingBalance = pendingBalance;
    }

    public boolean isNew()
    {
        return isNew;
    }

    public void setNew(boolean aNew)
    {
        isNew = aNew;
    }

    public List<LineItem> getLineItems()
    {
        return this.lineItems;
    }

    public void setLineItems(List<LineItem> lineItems)
    {
        this.lineItems = lineItems;
    }

    public LineItem addLineItem(BigDecimal quantity, Product product, BigDecimal price)
    {
        LineItem lineItem = new LineItem(quantity, product, price);
        lineItems.add(lineItem);
        updateSubtotal();
        return lineItem;
    }

    private void updateSubtotal()
    {
        BigDecimal subtotal = new BigDecimal(0);
        for (LineItem lineItem: lineItems)
        {
            subtotal = subtotal.add(lineItem.getQuantity().multiply(lineItem.getPrice()));
        }
        setSubTotal(subtotal);
    }

    public BigDecimal getTaxRate()
    {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate)
    {
        this.taxRate = taxRate;
    }

    public BigDecimal getTax()
    {
        return tax;
    }

    public void setTax(BigDecimal tax)
    {
        this.tax = tax;
    }

    public BigDecimal getTotal()
    {
        return total;
    }

    public void setTotal(BigDecimal total)
    {
        this.total = total;
    }
}
