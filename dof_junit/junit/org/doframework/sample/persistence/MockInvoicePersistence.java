package org.doframework.sample.persistence;

import org.doframework.sample.component.*;

import java.util.*;

public class MockInvoicePersistence implements InvoicePersistence
{

    Map<Integer, Invoice> invoiceIdToInvoice = new HashMap<Integer, Invoice>();
    static int nextId = 0;


    public Invoice getInvoiceById(int id)
    {
        return invoiceIdToInvoice.get(id);
    }


    public int getNextId()
    {
        return nextId++;
    }


    public void insert(Invoice invoice)
    {
        invoiceIdToInvoice.put(invoice.getId(), invoice);
    }


    public void update(Invoice invoice)
    {
        invoiceIdToInvoice.put(invoice.getId(), invoice);
    }


    /**
     * Find the invoice with the id
     *
     * @param id
     *
     * @return
     */
    public Invoice getById(int id)
    {
        return null;
    }


    public boolean delete(Invoice invoice)
    {
        return (invoiceIdToInvoice.remove(invoice.getId()) != null);
    }


    public int getNextInvoiceNumber()
    {
        return nextId;
    }


    public Invoice getByInvoiceNumber(int invoiceNumber)
    {
        return null;
    }


}
