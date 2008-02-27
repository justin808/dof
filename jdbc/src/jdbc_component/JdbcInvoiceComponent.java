package jdbc_component;

import component.*;
import entity.*;

import java.util.*;
import java.math.*;

import global.*;

public class JdbcInvoiceComponent extends JdbcBaseComponent implements InvoiceComponent
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
            int id = Integer.parseInt(rows[0][0]);
            int customerId = Integer.parseInt(rows[0][1]);
            CustomerComponent customerComponent =
                    GlobalContext.getComponentFactory().getCustomerComponent();
            Customer customer = customerComponent.getById(customerId);
            Date invoiceDate = JdbcDbUtil.parseDate(rows[0][2]);
            BigDecimal amount = new BigDecimal(rows[0][3]);
            BigDecimal pendingBalance = new BigDecimal(rows[0][4]);
            List<LineItem> lineItems = getLineItems(id);
            Invoice invoice = new Invoice(id);
            invoice.setCustomer(customer);
            invoice.setInvoiceDate(invoiceDate);
            invoice.setSubTotal(amount);
            invoice.setPendingBalance(pendingBalance);
            invoice.setLineItems(lineItems);
            return invoice;
        }
    }

    private List<LineItem> getLineItems(int id)
    {
        String sql =
                "select * from line_item where invoice_id = " + id + " order by line_number";
        String[][] rows = JdbcDbUtil.executeMultiColumnQuery(sql);
        List<LineItem> lineItems = new ArrayList<LineItem>(rows.length);
        for (int i = 0; i < rows.length; i++)
        {
            String[] row = rows[i];
            int productId = Integer.parseInt(row[4]);
            Product product = GlobalContext.getComponentFactory().getProductComponent().getById(productId);
            BigDecimal qty = new BigDecimal(row[3]);
            BigDecimal price = new BigDecimal(row[5]);
            LineItem lineItem = new LineItem(qty, product, price);
            lineItems.add(lineItem);
        }
        return lineItems;


    }
    public void insert(Invoice invoice) throws DuplicateRecordException
    {

        String sql =
                "insert into invoice (id, customer_id, invoice_date, amount, pending_balance) " +
                "values (" + invoice.getId() + ", " + invoice.getCustomer().getId() + ", '"
                + JdbcDbUtil.formatDate(invoice.getInvoiceDate()) + "','"
                + invoice.getSubTotal() + "', '"
                + invoice.getPendingBalance() + "')";
        JdbcDbUtil.update(sql);
    }

    public void update(Invoice invoice) throws DuplicateRecordException
    {
        String sql =
                "update invoice " +
                "set amount = '" + invoice.getSubTotal() + "', pending_balance = '" +
                invoice.getPendingBalance() + "' where id = " + invoice.getId();
        JdbcDbUtil.update(sql);
    }

    public boolean delete(Invoice invoice)
    {
        String sql =
                "delete from invoice where id = " + invoice.getId();
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
     *
     * @param query
     * @param customer optional to avoid refetching customers when creating Invoice records
     * @return
     */
    private List<Invoice> query(String query, Customer customer)
    {
        String sql = "select * from invoice where " + query;
        String[][] data = JdbcDbUtil.executeMultiColumnQuery(sql);
        List<Invoice> invoices = new ArrayList<Invoice>();
        for (int row = 0; row < data.length; row++)
        {
            Invoice invoice = getInvoiceForRow(data[row], customer);
            invoices.add(invoice);
        }
        return invoices; // if none found

    }

    private Invoice getInvoiceForRow(String[] columns, Customer customer)
    {
        Invoice invoice = new Invoice(Integer.parseInt(columns[0]));
        if (customer == null)
        {
            customer =
                    GlobalContext.getComponentFactory()
                            .getCustomerComponent()
                            .getById(Integer.parseInt(columns[1]));
        }
        invoice.setCustomer(customer);
        String sDate = columns[2];
        Date date = JdbcDbUtil.parseDate(sDate);
        invoice.setInvoiceDate(date);
        invoice.setSubTotal(new BigDecimal(columns[3]));
        invoice.setPendingBalance(new BigDecimal(columns[4]));
        return invoice;
    }

    public String getTableName()
    {
        return "invoice";
    }

}
