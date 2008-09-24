package org.doframework.sample.persistence.jdbc_persistence;

import org.doframework.sample.component.*;
import static org.doframework.sample.global.GlobalContext.*;
import org.doframework.sample.persistence.*;

import java.util.*;

public class JdbcInvoicePersistence extends JdbcBasePersistence implements InvoicePersistence
{
    public Invoice getById(int invoiceId)
    {
        String sql = "select * from invoice where id = " + invoiceId;
        String[][] rows = JdbcDbUtil.executeMultiColumnQuery(sql);
        if (rows.length == 0)
        {
            return null;
        }
        else
        {
            final String[] invoiceRow = rows[0];
            int fetchedInvoiceId = Integer.parseInt(invoiceRow[0]);
            if (fetchedInvoiceId != invoiceId)
            {
                throw new RuntimeException("Query for invoice broken: " + sql);
            }

            Invoice invoice = getInvoice(invoiceId, invoiceRow, null);
            return invoice;
        }
    }


    private Invoice getInvoice(int invoiceId, String[] invoiceRow, Customer customer)
    {
        int customerId = Integer.parseInt(invoiceRow[1]);
        CustomerPersistence customerPersistence = getPersistanceFactory().getCustomerPersistence();
        if (customer == null)
        {
            customer = customerPersistence.getById(customerId);
        }
        Date invoiceDate = JdbcDbUtil.parseDate(invoiceRow[2]);
        Integer total = new Integer(invoiceRow[3]);
        Integer pendingBalance = new Integer(invoiceRow[4]);
        List<LineItem> lineItems = getLineItems(invoiceId);
        Invoice invoice = new Invoice(invoiceId);
        invoice.setCustomer(customer);
        invoice.setInvoiceDate(invoiceDate);
        invoice.setTotal(total);
        invoice.setPendingBalance(pendingBalance);
        invoice.setLineItems(lineItems);
        return invoice;
    }


    private List<LineItem> getLineItems(int id)
    {
        String sql = "select qty, product_id, price from line_item where invoice_id = " + id + " order by line_number";
        String[][] rows = JdbcDbUtil.executeMultiColumnQuery(sql);
        List<LineItem> lineItems = new ArrayList<LineItem>(rows.length);
        for (int i = 0; i < rows.length; i++)
        {
            String[] row = rows[i];
            int productId = Integer.parseInt(row[1]);
            Product product = getPersistanceFactory().getProductPersistence().getById(productId);
            if (product == null)
            {
                throw new RuntimeException("Could not find product with id " + productId +
                                           " while fetching line items for invoice " + id);
            }
            Integer qty = new Integer(row[0]);
            Integer price = new Integer(row[2]);
            LineItem lineItem = new LineItem(qty, product, price);
            lineItems.add(lineItem);
        }
        return lineItems;


    }


    public void insert(Invoice invoice) throws DuplicateRecordException
    {

        String sql = "insert into invoice (id, customer_id, invoice_date, total, pending_balance) " + "values (" +
                     invoice.getId() + ", " + invoice.getCustomer().getId() + ", " +
                     JdbcDbUtil.formatDate(invoice.getInvoiceDate()) + ", " + invoice.getTotal() + ", " +
                     invoice.getPendingBalance() + ")";
        JdbcDbUtil.update(sql);
        insertLineItems(invoice);
    }


    private void insertLineItems(Invoice invoice)
    {
        int line = 0;
        for(LineItem lineItem : invoice.getLineItems())
        {
            String sql = "insert into line_item (invoice_id, line_number, qty, product_id, price) " + "values (" +
                         invoice.getId() + ", " + line + ", " + lineItem.getQuantity() + ", " + lineItem.getProduct().getId() + ", " + lineItem.getPrice() + ")";
            JdbcDbUtil.update(sql);
            line++;
        }
    }


    public void update(Invoice invoice) throws DuplicateRecordException
    {
        String sql = "update invoice " + "set total = " + invoice.getTotal() + ", pending_balance = " +
                     invoice.getPendingBalance() + " where id = " + invoice.getId();
        JdbcDbUtil.update(sql);
        updateLineItems(invoice);
    }


    private void updateLineItems(Invoice invoice)
    {
        deleteLineItems(invoice);
        insertLineItems(invoice);
    }


    private void deleteLineItems(Invoice invoice)
    {
        String sql = "delete from line_item where invoice_id = " + invoice.getId();
        JdbcDbUtil.update(sql);
    }


    public boolean delete(Invoice invoice)
    {
        deleteLineItems(invoice);
        String sql = "delete from invoice where id = " + invoice.getId();
        int rowCount = JdbcDbUtil.update(sql);
        return rowCount > 0;
    }


    /**
     * @return All the invoices for the customer, ordered chronologically
     */
    public List<Invoice> getInvoicesForCustomer(Customer customer)
    {
        String query = "customer_id = " + customer.getId() + " order by invoice_date";
        List<Invoice> result = query(query, customer);
        return result;
    }


    /**
     * @param query
     * @param customer optional to avoid refetching customers when creating Invoice records
     *
     * @return
     */
    private List<Invoice> query(String query, Customer customer)
    {
        String sql = "select * from invoice where " + query;
        String[][] data = JdbcDbUtil.executeMultiColumnQuery(sql);
        List<Invoice> invoices = new ArrayList<Invoice>();
        for (int row = 0; row < data.length; row++)
        {
            Invoice invoice = getInvoice(Integer.parseInt(data[row][0]), data[row], customer);
            invoices.add(invoice);
        }
        return invoices; // if none found

    }




    public String getTableName()
    {
        return "invoice";
    }


}
