package component;

import entity.*;

import java.util.*;

public class MockInvoiceComponent implements InvoiceComponent
{
    private static Map<Integer, Invoice> invoiceIdToInvoice = new HashMap<Integer, Invoice>();
    private static int lastInvoiceId = 0;
    private static int NEW_INVOICE_ID = 1000;


    public Invoice getById(int invoiceId)
    {
        return invoiceIdToInvoice.get(invoiceId);
    }

    public void insert(Invoice invoice) throws DuplicateRecordException
    {
        persist(invoice);
    }

    public void update(Invoice invoice) throws DuplicateRecordException
    {
        persist(invoice);
    }

    public void persist(Invoice invoice) throws DuplicateRecordException
    {
        if (invoice.getId() == NEW_INVOICE_ID)
        {
            invoice.setId(lastInvoiceId++);
        }
        invoiceIdToInvoice.put(invoice.getId(), invoice);
    }


    public boolean delete(Invoice invoice)
    {
        Invoice invoice1 = invoiceIdToInvoice.remove(invoice.getId());
        return invoice1 != null;
    }

    public List<Invoice> getInvoicesForCustomer(Customer customer)
    {
        List<Invoice> result = new ArrayList<Invoice>();
        Collection<Invoice> allInvoices = invoiceIdToInvoice.values();
        for (Iterator<Invoice> iterator = allInvoices.iterator(); iterator.hasNext();)
        {
            Invoice invoice = iterator.next();
            if (invoice.getCustomer().getId() == customer.getId())
            {
                result.add(invoice);
            }
        }

        Collections.sort(result, new Comparator<Invoice>()
        {

            public int compare(Invoice o1, Invoice o2)
            {
                return o1.getInvoiceDate().compareTo(o2.getInvoiceDate());
            }
        });


        return result;

    }

    public int getNextId()
    {
        return NEW_INVOICE_ID++;
    }


}
