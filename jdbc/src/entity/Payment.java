package entity;

import component.*;

import java.util.*;
import java.math.*;

import global.*;

public class Payment
{
    private int id;
    private BigDecimal amount;
    private Date paymentDate;
    private Customer customer;
    private boolean isNew;


    public Payment()
    {
        setId(getPaymentComponent().getNextId());
        setNew(true);
    }

    public Payment(int id)
    {
        setId(id);
        setNew(false);
    }


    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public Date getPaymentDate()
    {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate)
    {
        this.paymentDate = paymentDate;
    }

    /**
     * Update customer balance and invoice balances
     */
    public void persist()
    {
        if (!isNew())
        {
            throw new RuntimeException("Attempt to re-persist payment ");
        }

        final Customer customer = getCustomer();
        BigDecimal balance = customer.getBalance();
        BigDecimal newBalance = balance.subtract(getAmount());
        customer.setBalance(newBalance);
        customer.persist();
        if (paymentDate == null)
        {
            setPaymentDate(new Date());
        }
        getPaymentComponent().insert(this);

        updateInvoicePendingBalances();
    }

    private void updateInvoicePendingBalances()
    {
        BigDecimal remainingPaymentToReduceInvoices = getAmount();
        List<Invoice> invoices = customer.getInvoices();
        for (Invoice invoice : invoices)
        {
            BigDecimal invoicePendingBalance = invoice.getPendingBalance();
            if (invoicePendingBalance.compareTo(BigDecimal.ZERO) > 0)
            {
                if (invoicePendingBalance.compareTo(remainingPaymentToReduceInvoices) <= 0)
                {
                    invoice.setPendingBalance(BigDecimal.ZERO);
                    remainingPaymentToReduceInvoices = remainingPaymentToReduceInvoices.subtract(invoicePendingBalance);
                    invoice.persist();
                }
                else
                {
                    invoice.setPendingBalance(invoicePendingBalance.subtract(remainingPaymentToReduceInvoices));
                    remainingPaymentToReduceInvoices = BigDecimal.ZERO;
                    invoice.persist();
                    break;
                }
            }
        }
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public PaymentComponent getPaymentComponent()
    {
        return GlobalContext.getComponentFactory().getPaymentComponent();
    }

    public boolean isNew()
    {
        return isNew;
    }

    public void setNew(boolean aNew)
    {
        isNew = aNew;
    }
}
