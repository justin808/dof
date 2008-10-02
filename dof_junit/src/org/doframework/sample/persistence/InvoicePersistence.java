package org.doframework.sample.persistence;

import org.doframework.sample.component.*;

public interface InvoicePersistence extends BasePersistence
{

    /**
     * Save the new invoice record to the database
     *
     * @param invoice
     */
    void insert(Invoice invoice);


    /**
     * Update the existing invoice record in the database
     *
     * @param invoice
     */
    void update(Invoice invoice);


    /**
     * Find the invoice with the id
     * @param id
     * @return
     */
    Invoice getById(int id);


    boolean delete(Invoice invoice);


    int getNextInvoiceNumber();


    Invoice getByInvoiceNumber(int invoiceNumber);
}
