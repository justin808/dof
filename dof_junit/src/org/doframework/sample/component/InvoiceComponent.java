package org.doframework.sample.component;

import static org.doframework.sample.global.GlobalContext.*;

/**
 * This class orchestrates actions around the Invoice entity Using class rather than interface because we want only one
 * copy of the business logic.
 *
 * @see Invoice
 */
public class InvoiceComponent
{

    public Invoice createNew()
    {
        Invoice invoice = new Invoice(getPersistanceFactory().getInvoicePersistence().getNextId());
        invoice.setNew(true);
        return invoice;
    }


    /**
     * Saves the new or updates the existing Invoice entity in the persistent store
     *
     * @param invoice
     */
    public void persist(Invoice invoice)
    {
        final int maxInvoiceAmount = getAccountingConstants().getMaximumInvoiceTotal();
        if (invoice.getTotal() > maxInvoiceAmount)
        {
            throw new InvoiceLimitExceededException("Invoice amount '" + invoice.getTotal() +
                                                    "' is greater than limit '" + maxInvoiceAmount + "'.");
        }

        if (invoice.isNew())
        {
            getPersistanceFactory().getInvoicePersistence().insert(invoice);
            invoice.setNew(false);
        }
        else
        {
            getPersistanceFactory().getInvoicePersistence().update(invoice);
        }
        Customer customer = invoice.getCustomer();

        // Until we add a test for multiple invoices, this makes the tests pass
        // Incorrect code
        //customer.setBalance(invoice.getTotal());

        // CORRECT CODE
        int oldBalance = customer.getBalance();
        int newBalance = oldBalance + invoice.getTotal();
        customer.setBalance(newBalance);


        ComponentFactory.getCustomerComponent().persist(customer);
    }


    public LineItem addLineItem(Invoice invoice, Integer quantity, Product product, Integer price)
    {
        LineItem lineItem = new LineItem(quantity, product, price);
        invoice.getLineItems().add(lineItem);
        updateInvoiceTotal(invoice);
        return lineItem;
    }


    public void updateInvoiceTotal(Invoice invoice)
    {
        Integer total = new Integer(0);
        for (LineItem lineItem : invoice.getLineItems())
        {
            total += lineItem.getQuantity() * lineItem.getPrice();
        }
        invoice.setTotal(total);
    }


    /**
     * Get the invoice by ID
     *
     * @param id
     *
     * @return the invoice or null if the invoice does not exist with that ID
     */
    public Invoice getByInvoiceId(int id)
    {
        return getPersistanceFactory().getInvoicePersistence().getById(id);
    }


    /**
     * Get the invoice by Invoice Number
     *
     * @param invoiceNumber
     *
     * @return the invoice or null if the invoice does not exist with that ID
     */
    public Invoice getByInvoiceNumber(int invoiceNumber)
    {
        return getPersistanceFactory().getInvoicePersistence().getByInvoiceNumber(invoiceNumber);
    }


    public boolean delete(Invoice invoice)
    {
        return getPersistanceFactory().getInvoicePersistence().delete(invoice);
    }


    public int getNextInvoiceNumber()
    {
        return getPersistanceFactory().getInvoicePersistence().getNextInvoiceNumber();
    }
}
