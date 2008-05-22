package org.doframework.sample.jdbc_app.factory;

import java.util.List;

import org.doframework.sample.jdbc_app.entity.Customer;
import org.doframework.sample.jdbc_app.entity.Invoice;

public interface InvoiceFactory extends Factory
{

    Invoice getById(int invoiceId);

    void insert(Invoice invoice) throws DuplicateRecordException;

    void update(Invoice invoice) throws DuplicateRecordException;

    boolean delete(Invoice invoice);


    /**
     * @return All the invoices for the customer, ordered chronologically
     */
    List<Invoice> getInvoicesForCustomer(Customer customer);

    int getNextId();
}
