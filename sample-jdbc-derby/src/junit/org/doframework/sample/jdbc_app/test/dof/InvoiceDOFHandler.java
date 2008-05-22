package org.doframework.sample.jdbc_app.test.dof;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.doframework.DependentObjectHandler;
import org.doframework.ObjectFileInfo;
import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.entity.Customer;
import org.doframework.sample.jdbc_app.entity.Invoice;
import org.doframework.sample.jdbc_app.entity.LineItem;
import org.doframework.sample.jdbc_app.entity.Product;
import org.doframework.sample.jdbc_app.factory.CustomerFactory;
import org.doframework.sample.jdbc_app.factory.InvoiceFactory;
import org.doframework.sample.jdbc_app.factory.JdbcDbUtil;
import org.doframework.sample.jdbc_app.factory.ProductFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




public class InvoiceDOFHandler implements DependentObjectHandler
{

    InvoiceFactory invoiceFactory = GlobalContext.getComponentFactory().getInvoiceFactory();
    CustomerFactory customerFactory =
            GlobalContext.getComponentFactory().getCustomerFactory();
    ProductFactory productFactory = GlobalContext.getComponentFactory().getProductFactory();

    /**
     * @param xmlDescriptionFile
     *
     * @return a Manufacturer object with corresponding name and id
     */
    Invoice createInvoice(String xmlDescriptionFile)
    {
        InputStream is = ClassLoader.getSystemResourceAsStream(xmlDescriptionFile);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document document = documentBuilder.parse(is);
            XPathFactory pathFactory = XPathFactory.newInstance();
            XPath xPath = pathFactory.newXPath();
            Element item = document.getDocumentElement();
            String sId = xPath.evaluate("/invoice/id", item);
            int id = Integer.parseInt(sId);
            String sCustomerId = xPath.evaluate("/invoice/customer_id", item);
            int customerId = Integer.parseInt(sCustomerId);

            Customer customer = customerFactory.getById(customerId);

            String sInvoiceDate = xPath.evaluate("/invoice/invoice_date", item);
            Date invoiceDate = JdbcDbUtil.parseDate(sInvoiceDate);

            String sSubtotal = xPath.evaluate("/invoice/subtotal", item);
            BigDecimal subtotal = new BigDecimal(sSubtotal);

            String sTaxRate = xPath.evaluate("/invoice/tax_rate", item);
            BigDecimal taxRate = new BigDecimal(sTaxRate);

            String sTax = xPath.evaluate("/invoice/tax", item);
            BigDecimal tax = new BigDecimal(sTax);

            String sTotal = xPath.evaluate("/invoice/total", item);
            BigDecimal total = new BigDecimal(sTotal);

            String sPendingBalance = xPath.evaluate("/invoice/pending_balance", item);
            BigDecimal pendingBalance = new BigDecimal(sPendingBalance);

            Invoice invoice = new Invoice();
            invoice.setId(id);
            invoice.setCustomer(customer);
            invoice.setInvoiceDate(invoiceDate);
            invoice.setSubTotal(subtotal);
            invoice.setTaxRate(taxRate);
            invoice.setTax(tax);
            invoice.setTotal(total);
            invoice.setPendingBalance(pendingBalance);

            NodeList lineItemNodes =
                    (NodeList) xPath.evaluate("/invoice/line_items/line_item",
                                              item,
                                              XPathConstants.NODESET);
            int nodeListLength = lineItemNodes.getLength();
            List<LineItem> lineItems = new ArrayList<LineItem>(nodeListLength);
            for (int i = 0; i < nodeListLength; i++)
            {
                Node node = lineItemNodes.item(i); // This is a Line Item node
                String sQty = xPath.evaluate("qty", node);
                BigDecimal qty = new BigDecimal(sQty);
                String productId = xPath.evaluate("product_id", node);
                Product product = productFactory.getById(Integer.parseInt(productId));

                String sPrice = xPath.evaluate("price", node);
                BigDecimal price = new BigDecimal(sPrice);


                if (product == null)
                {
                    throw new RuntimeException("Product with id " + productId + " does not exist!");
                }

                LineItem lineItem = new LineItem(qty, product, price);
                lineItems.add(lineItem);
            }
            invoice.setLineItems(lineItems);


            return invoice;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insert the object into the DB. No check is done to see if the object already exists.
     *
     * @param objectFileInfo
     */
    public Object create(ObjectFileInfo objectFileInfo)
    {
        Invoice invoice = createInvoice(objectFileInfo.getFileToLoad());
        invoiceFactory.insert(invoice);
        return invoice;
    }

    /**
     * Return the object, if it exists, with the given PK
     *
     * @param objectFileInfo
     */
    public Object get(ObjectFileInfo objectFileInfo)
    {
        return invoiceFactory.getById(Integer.parseInt(objectFileInfo.getPk()));
    }

    /**
     * Delete the object with the given pk
     *
     * @param objectFileInfo
     * @param objectToDelete
     */
    public boolean delete(ObjectFileInfo objectFileInfo, Object objectToDelete)
    {
        Invoice Invoice = (Invoice) get(objectFileInfo);
        if (Invoice != null)
        {
            return invoiceFactory.delete(Invoice);
        }
        else
        {
            return false;
        }
    }
}
