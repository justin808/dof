package component;

import entity.*;

import java.util.*;

public interface InvoiceComponent extends Component
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
