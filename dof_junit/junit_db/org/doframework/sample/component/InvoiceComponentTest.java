package org.doframework.sample.component;

import org.doframework.*;
import org.doframework.sample.global.GlobalContext;
import org.doframework.sample.persistence.jdbc_persistence.JdbcPersistenceFactory;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

public class InvoiceComponentTest
{

    public void setUp()
    {
        GlobalContext.setPersistanceFactory(new JdbcPersistenceFactory());
    }

    @Test
    public void testNewInvoiceSubtotal()
    {
        InvoiceComponent invoiceComponent = ComponentFactory.getInvoiceComponent();

        // Get objects needed for test
        Customer johnSmith = (Customer) DOF.require("customer.John Smith.xml");
        Product dietCherryCola = (Product) DOF.require("product.Drinks Unlimited__Diet Cherry Cola.xml");
        Product dietSnapple = (Product) DOF.require("product.Drinks Unlimited__Diet Tea.xml");

        Integer THREE = new Integer("3");
        Integer TWO = new Integer("2");

        Invoice invoice = invoiceComponent.createNew();
        invoice.setInvoiceNumber(invoiceComponent.getNextInvoiceNumber());
        invoice.setCustomer(johnSmith);
        invoice.setInvoiceDate(new Date());
        invoiceComponent.addLineItem(invoice, THREE, dietCherryCola, dietCherryCola.getPrice());
        invoiceComponent.addLineItem(invoice, TWO, dietSnapple, dietSnapple.getPrice());

        // triangulation
        assertEquals(new Integer(dietCherryCola.getPrice() * THREE + dietSnapple.getPrice() * TWO), invoice.getTotal());

        // Test persistence
        invoiceComponent.persist(invoice);
        Invoice invoiceFromDb = ComponentFactory.getInvoiceComponent().getByInvoiceId(invoice.getId());
        assertEquals(invoice.getTotal(), invoiceFromDb.getTotal());
    }


}
