package dof_xml_handler;

import com.doframework.*;
import component.*;
import entity.*;
import global.*;
import jdbc_component.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.math.*;
import java.util.*;


public class InvoiceXmlFactory implements DependentObjectHandler
{

    InvoiceComponent invoiceComponent = GlobalContext.getComponentFactory().getInvoiceComponent();
    CustomerComponent customerComponent =
            GlobalContext.getComponentFactory().getCustomerComponent();
    ProductComponent productComponent = GlobalContext.getComponentFactory().getProductComponent();

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

            Customer customer = customerComponent.getById(customerId);

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
                Product product = productComponent.getById(Integer.parseInt(productId));

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
     * @param fileToLoad File name in form: {objectType}.{objectPk}.{fileType}
     *
     * @return The type of object being created and saved in the DB.
     */
    public Object create(String fileToLoad)
    {
        Invoice invoice = createInvoice(fileToLoad);
        invoiceComponent.insert(invoice);
        return invoice;
    }

    /**
     * Return the object, if it exists, with the given PK
     *
     * @param pk The primary key of the object to retrieve
     *
     * @return The object created from the db if it existed, or else null
     */
    public Object get(String pk)
    {
        return invoiceComponent.getById(Integer.parseInt(pk));
    }

    /**
     * Delete the object with the given pk
     *
     * @param pk The primary key of the object to delete
     */
    public boolean delete(String pk)
    {
        Invoice Invoice = (Invoice) get(pk);
        if (Invoice != null)
        {
            return invoiceComponent.delete(Invoice);
        }
        else
        {
            return false;
        }
    }
}
