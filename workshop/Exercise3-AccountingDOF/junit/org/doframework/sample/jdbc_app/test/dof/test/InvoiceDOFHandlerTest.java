package org.doframework.sample.jdbc_app.test.dof.test;

import java.math.BigDecimal;
import java.util.List;

import junit.framework.TestCase;

import org.doframework.DOF;
import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.entity.Customer;
import org.doframework.sample.jdbc_app.entity.Invoice;
import org.doframework.sample.jdbc_app.entity.LineItem;
import org.doframework.sample.jdbc_app.entity.Product;
import org.doframework.sample.jdbc_app.factory.CustomerFactory;
import org.doframework.sample.jdbc_app.factory.InvoiceFactory;
import org.doframework.sample.jdbc_app.factory.ProductFactory;

/**
 * User: gordonju Date: Jan 13, 2008 Time: 10:53:59 PM
 */
public class InvoiceDOFHandlerTest extends TestCase
{

    InvoiceFactory invoiceFactory = GlobalContext.getFactoryLookupService().getInvoiceFactory();
    ProductFactory productFactory = GlobalContext.getFactoryLookupService().getProductFactory();

    public void testRequireInvoiceLoadsTwoLevelsOfDependencies()
    {
        DOF.delete("invoice.100.xml");
        assertNull(invoiceFactory.getById(100));

        CustomerFactory customerFactory =
                GlobalContext.getFactoryLookupService().getCustomerFactory();
        assertNull(customerFactory.getById(25));


        assertNull(productFactory.getById(14)); // product 2 levels deep is null

        Invoice invoice = (Invoice) DOF.require("invoice.100.xml");
        assertNotNull(invoice);
        //assertSame(DOF.require("customer.25.xml"), invoice.getCustomer());
        List<LineItem> lineItems = invoice.getLineItems();
        assertEquals(3, lineItems.size());

        LineItem firstLineItem = lineItems.get(0);
        assertEquals(8.99, firstLineItem.getProduct().getPrice().doubleValue());
        assertEquals(new BigDecimal(5), firstLineItem.getQuantity());
        assertEquals(13, firstLineItem.getProduct().getId());

        LineItem secondLineItem = lineItems.get(1);
        assertEquals(4.99, secondLineItem.getProduct().getPrice().doubleValue());
        assertEquals(new BigDecimal(8), secondLineItem.getQuantity());
        assertEquals(14, secondLineItem.getProduct().getId());

        //assertEquals(8.25, invoice.getTaxRate().doubleValue());
        //assertEquals(15.40, invoice.getShipping().doubleValue());

    }

    public void testNewInvoiceSubtotal()
    {
        // Get objects needed for test
        Customer johnSmith = (Customer) DOF.require("customer.25.xml");
        Product coffee = (Product) DOF.require("product.13.xml");
        Product tea = (Product) DOF.require("product.14.xml");

        BigDecimal THREE = new BigDecimal("3");
        BigDecimal TWO = new BigDecimal("2");

        Invoice invoice = new Invoice();
        invoice.setCustomer(johnSmith);
        invoice.addLineItem(THREE, coffee, coffee.getPrice());
        invoice.addLineItem(TWO, tea, tea.getPrice());

        // triangulation
        assertEquals(coffee.getPrice()
                .multiply(THREE).add(tea.getPrice().multiply(TWO)), invoice.getSubTotal());

        // Test persistence
        invoice.persist();
        Invoice invoiceFromDb =
                GlobalContext.getFactoryLookupService().getInvoiceFactory().getById(invoice.getId());
        assertEquals(invoice.getSubTotal(), invoiceFromDb.getSubTotal());
    }

}
