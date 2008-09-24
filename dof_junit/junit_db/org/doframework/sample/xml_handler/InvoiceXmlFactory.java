package org.doframework.sample.xml_handler;

import org.doframework.*;
import org.doframework.sample.component.*;
import org.doframework.sample.global.*;
import org.doframework.sample.persistence.*;
import org.doframework.sample.persistence.jdbc_persistence.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.util.*;


public class InvoiceXmlFactory implements DependentObjectHandler, ObjectDeletionHelper
{

    InvoiceComponent invoiceComponent = ComponentFactory.getInvoiceComponent();
    CustomerComponent customerComponent = ComponentFactory.getCustomerComponent();


    /**
     *
     * @return an Invoice Object object with corresponding name and id
     */
    Invoice createInvoice(ObjectFileInfo ofi)
    {
        InputStream is = ofi.getFileContentsAsInputStream();

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

            String sTotal = xPath.evaluate("/invoice/total", item);
            Integer total = new Integer(sTotal);

            String sPendingBalance = xPath.evaluate("/invoice/pending_balance", item);
            Integer pendingBalance = new Integer(sPendingBalance);

            Invoice invoice = new Invoice(id);
            invoice.setCustomer(customer);
            invoice.setInvoiceDate(invoiceDate);
            invoice.setTotal(total);
            invoice.setPendingBalance(pendingBalance);

            NodeList lineItemNodes =
                    (NodeList) xPath.evaluate("/invoice/line_items/line_item", item, XPathConstants.NODESET);
            int nodeListLength = lineItemNodes.getLength();
            List<LineItem> lineItems = new ArrayList<LineItem>(nodeListLength);
            final ProductPersistence productPersistence = GlobalContext.getPersistanceFactory().getProductPersistence();
            for (int i = 0; i < nodeListLength; i++)
            {
                Node node = lineItemNodes.item(i); // This is a Line Item node
                String sQty = xPath.evaluate("qty", node);
                Integer qty = new Integer(sQty);
                String productId = xPath.evaluate("product_id", node);
                Product product = productPersistence.getById(Integer.parseInt(productId));

                String sPrice = xPath.evaluate("price", node);
                Integer price = new Integer(sPrice);


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


    public Object create(ObjectFileInfo ofi)
    {
        Invoice invoice = createInvoice(ofi);
        invoice.setNew(true);
        invoiceComponent.persist(invoice);
        return invoice;
    }


    public Object get(ObjectFileInfo ofi)
    {
        return invoiceComponent.getById(Integer.parseInt(ofi.getPk()));
    }


    public boolean delete(Object objectToDelete)
    {
        return invoiceComponent.delete((Invoice) objectToDelete);
    }


    /**
     * @param scratchObject which is the type that the given handler creates
     *
     * @return The dependencies of the given scratch object
     */
    public Object[] getDependencies(Object scratchObject)
    {
        Invoice invoice = (Invoice) scratchObject;
        List<LineItem> lineItems = invoice.getLineItems();
        Set<Product> products = new HashSet<Product>();
        for (Iterator lineItemIterator = lineItems.iterator(); lineItemIterator.hasNext();)
        {
            LineItem lineItem = (LineItem) lineItemIterator.next();
            products.add(lineItem.getProduct());
        }
        Object[] dependencies = new Object[products.size() + 1];
        dependencies[0] = invoice.getCustomer();
        int i = 1;
        for (Iterator productIterator = products.iterator(); productIterator.hasNext();)
        {
            Product product = (Product) productIterator.next();
            dependencies[i++] = product;
        }

        return dependencies;
    }


    /**
     * Information on what parent dependencies this object has are used to for operation
     * DOF.deleteAll(). The dependencies are gathered and objects are deleted in the correct order
     * to avoid conflicts.
     *
     * @return
     */
    public Class[] getParentDependencyClasses()
    {
        return new Class[] { Customer.class, Product.class};
    }


    /** @return The class object for the class that is returned from this handler. */
    public Class getCreatedClass()
    {
        return Invoice.class;
    }


    public Object extractPrimaryKey(Object object)
    {
        return new Integer(((Invoice) object).getId());
    }

}
