package dof_xml_handler;

import junit.framework.*;
import com.ibm.dof.*;

import java.util.*;
import java.math.*;

import component.*;
import entity.*;
import global.*;

/**
 * User: gordonju Date: Jan 13, 2008 Time: 10:53:59 PM
 */
public class InvoiceXmlFactoryTest extends TestCase
{

    InvoiceComponent invoiceComponent = GlobalContext.getComponentFactory().getInvoiceComponent();
    ProductComponent productComponent = GlobalContext.getComponentFactory().getProductComponent();

    public void testRequireInvoiceLoadsTwoLevelsOfDependencies()
    {
        DOF.delete("invoice.100.xml");
        assertNull(invoiceComponent.getById(100));

        CustomerComponent customerComponent = GlobalContext.getComponentFactory().getCustomerComponent();
        assertNull(customerComponent.getById(25));


        assertNull(productComponent.getById(14)); // product 2 levels deep is null

        Invoice invoice = (Invoice) DOF.require("invoice.100.xml");
        assertNotNull(invoice);
        //assertSame(DOF.require("customer.25.xml"), invoice.getCustomer());
        List<LineItem> lineItems = invoice.getLineItems();
        assertEquals(3, lineItems.size());

        LineItem firstLineItem = lineItems.get(0);
        assertEquals(8.99, firstLineItem.getProduct().getPrice().doubleValue());
        assertEquals(5, firstLineItem.getQuantity());
        assertEquals(13, firstLineItem.getProduct().getId());

        LineItem secondLineItem = lineItems.get(1);
        assertEquals(4.99, secondLineItem.getProduct().getPrice().doubleValue());
        assertEquals(8, secondLineItem.getQuantity());
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
                .multiply(THREE).add(tea.getPrice().multiply(TWO)),
                                                 invoice.getSubTotal());

        // Test persistence
        invoice.persist();
        Invoice invoiceFromDb =
                GlobalContext.getComponentFactory().getInvoiceComponent().getById(invoice.getId());
        assertEquals(invoice.getSubTotal(), invoiceFromDb.getSubTotal());
    }

}
