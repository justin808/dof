package org.doframework.sample.xml_handler;

import org.doframework.*;
import org.doframework.sample.component.*;
import org.doframework.sample.component.reference.*;
import org.doframework.sample.persistence.jdbc_persistence.*;
import org.doframework.sample.global.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

/**
 * User: gordonju Date: Jan 13, 2008 Time: 10:53:59 PM
 */
public class InvoiceXmlHandlerTest
{

    InvoiceComponent invoiceComponent = ComponentFactory.getInvoiceComponent();
    ProductComponent productComponent = ComponentFactory.getProductComponent();
    ManufacturerComponent manufacturerComponent = ComponentFactory.getManufacturerComponent();



    @Test
    public void testRequireInvoiceLoadsTwoLevelsOfDependencies()
    {
        Invoice invoice = (Invoice) DOF.require("invoice.100.xml");
        assertNotNull(invoice);
        //assertSame(DOF.require("customer.25.xml"), invoice.getCustomer());
        List<LineItem> lineItems = invoice.getLineItems();
        assertEquals(3, lineItems.size());

        LineItem firstLineItem = lineItems.get(0);
        assertEquals(new Integer(9), firstLineItem.getProduct().getPrice());
        assertEquals(new Integer(5), firstLineItem.getQuantity());
        assertEquals("Coffee", firstLineItem.getProduct().getName());

        LineItem secondLineItem = lineItems.get(1);
        assertEquals(new Integer(5), secondLineItem.getProduct().getPrice());
        assertEquals(new Integer(8), secondLineItem.getQuantity());
        assertEquals("Tea", secondLineItem.getProduct().getName());

        //assertEquals(8.25, invoice.getTaxRate());
        //assertEquals(15.40, invoice.getShipping());

    }


    /**
     * It helps to load twice because the first load may test creating a new one and the second load tests loading an
     * existing one.
     */
    @Test
    public void testRequireTwice()
    {
        testRequireInvoiceLoadsTwoLevelsOfDependencies();
        testRequireInvoiceLoadsTwoLevelsOfDependencies();
    }


    /**
     * This test demonstrates how 2 parent objects can point to the same grandparent object (Manufacturer 37) and the
     * grandparent still gets deleted.
     */
    @Test
    public void testDeleteInvoice()
    {
        DOF.require("invoice.100.xml");
        DOF.delete("invoice.100.xml");
        assertNull(invoiceComponent.getByInvoiceId(100));
        assertNull(productComponent.getByManufacturerAndName("Starbucks", "Coffee")); // product 2 levels deep is null
        assertNull(productComponent.getByManufacturerAndName("Lipton", "Tea"));
        assertNull(productComponent.getByManufacturerAndName("Lipton", "Green Tea"));
        assertNull(manufacturerComponent.getByName("Starbucks")); // manufacturer 3 levels deep is null
        assertNull(manufacturerComponent.getByName("Lipton"));
    }


    @Test
    public void testNewInvoiceSubtotal()
    {
        Invoice invoice = (Invoice) DOF.createScratchObject("invoice.scratch.xml");
        int originalSubtotal = invoice.getTotal();
        Product coffee = (Product) DOF.require("product.Drinks One__Coffee.xml");
        Product tea = (Product) DOF.require("product.Drinks One__Tea.xml");
        invoiceComponent.addLineItem(invoice, 2, coffee, coffee.getPrice());
        invoiceComponent.addLineItem(invoice, 3, tea, tea.getPrice());

        // triangulation
        assertEquals((long)(originalSubtotal + coffee.getPrice() * 2 + tea.getPrice() * 3),
                     (long)invoice.getTotal());

        // Test persistence
        invoiceComponent.persist(invoice);
        Invoice invoiceFromDb = invoiceComponent.getByInvoiceId(invoice.getId());
        assertEquals(invoice.getTotal(), invoiceFromDb.getTotal());
    }

    @Test
    public void testNewInvoiceSubtotalWithScratch()
    {
        Invoice invoice = (Invoice) DOF.createScratchObject("invoice.scratch.xml");
        List<LineItem> lineItems = invoice.getLineItems();
        int originalTotal = 0;
        for (Iterator<LineItem> lineItemIterator = lineItems.iterator(); lineItemIterator.hasNext();)
        {
            LineItem lineItem = lineItemIterator.next();
            originalTotal  += lineItem.getQuantity() * lineItem.getPrice();
        }


        assertEquals((long)originalTotal, (long)invoice.getTotal());

        // Add one item to lineOne
        LineItem lineOne = lineItems.get(0);
        int oldQty = lineOne.getQuantity();
        lineOne.setQuantity(oldQty + 1);

        invoiceComponent.updateInvoiceTotal(invoice);

        assertEquals((long)originalTotal + lineOne.getPrice(), (long)invoice.getTotal());

        // Test persistence
        invoiceComponent.persist(invoice);
        Invoice invoiceFromDb = invoiceComponent.getByInvoiceId(invoice.getId());
        assertEquals(invoice.getTotal(), invoiceFromDb.getTotal());

    }

    @Test
    public void testNewInvoiceSubtotalWithScratchRunNTimes()
    {
        int countInvoicesBefore = JdbcDbUtil.executeSingleIntQuery("select count(*) from invoice");
        int iterations = 3;
        for (int i = 0; i < iterations; i++)
        {
            testNewInvoiceSubtotalWithScratch();
        }
        int countInvoicesAfter = JdbcDbUtil.executeSingleIntQuery("select count(*) from invoice");
        assertEquals(countInvoicesBefore + iterations, countInvoicesAfter);
    }


    /**
     * Test out using a scratch customer
     */
    @Test
    public void testInvoiceWithScratchCustomer()
    {
        Invoice invoice1 = (Invoice) DOF.createScratchObject("invoice.scratchWithScratchCustomer.xml");

        Map<String, String> scratchReferenceToPk = new HashMap<String, String>();
        scratchReferenceToPk.put("scratchCustomerPk", invoice1.getCustomer().getName() + "");

        Invoice invoice2 = (Invoice) DOF.createScratchObject("invoice.scratchWithScratchCustomer.xml", scratchReferenceToPk);
        assertEquals(invoice1.getCustomer().getId(), invoice2.getCustomer().getId());

        assertTrue(invoice2.getId() > invoice1.getId());
    }


    /**
     * Test out using a scratch customer
     */
    @Test
    public void testTemplateInvoiceCreatesInvoice103()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("template", "103");
        Invoice invoice1 = (Invoice) DOF.createScratchObject("invoice.template.xml", map);
        assertEquals((Object) 103, invoice1.getInvoiceNumber());
    }


    /**
     * Test out using a scratch customer
     */
    @Test
    public void testTemplateInvoiceCreatesInvoiceWithScratchPk()
    {
        Invoice invoice1 = (Invoice) DOF.createScratchObject("invoice.template.xml");
        assertTrue(invoice1.getInvoiceNumber() > 1000);
    }


    //public void test


    @Test
    public void testInvoiceTextCallsJavaDependency()
    {
        DOF.delete("invoice.200.xml");
        //DOF.delete(new Product_DoleBlueberryJuice());
        //DOF.delete(new Product_DoleAcaiJuice());
        //DOF.delete(new Product_DoleMixedBerryJuice());
        assertNull((new Product_DoleBlueberryJuice()).fetch());
        assertNull((new Product_DoleAcaiJuice()).fetch());
        assertNull((new Product_DoleMixedBerryJuice()).fetch());
        assertNull((new Manufacturer_Dole()).fetch());

        Invoice invoice200 = (Invoice) DOF.require("invoice.200.xml");
        assertNotNull((new Product_DoleBlueberryJuice()).fetch());
        assertNotNull((new Product_DoleAcaiJuice()).fetch());
        assertNotNull((new Product_DoleMixedBerryJuice()).fetch());
        assertNotNull((new Manufacturer_Dole()).fetch());

        assertEquals(new Integer(200), invoice200.getInvoiceNumber());
        assertEquals("Jane Doe", invoice200.getCustomer().getName());
        List<LineItem> lineItems = invoice200.getLineItems();
        assertEquals(3, lineItems.size());
        assertEquals("Blueberry Juice", lineItems.get(0).getProduct().getName());

        DOF.delete("invoice.200.xml");
        assertNull((new Product_DoleAcaiJuice()).fetch());
        assertNull((new Product_DoleMixedBerryJuice()).fetch());
        Product doleBlueberryJuice = (Product) (new Product_DoleBlueberryJuice()).fetch();
        assertNull(doleBlueberryJuice + "", doleBlueberryJuice);
        assertNull((new Manufacturer_Dole()).fetch());
    }


    @Test
    public void testScratchInvoiceTextCallsScratchJavaDependency()
    {
        String fileToLoad = "invoice.scratchWithScratchJavaCustomer.xml";
        Invoice invoice1 = (Invoice) DOF.createScratchObject(fileToLoad);
        Map<String, String> scratchReferenceToPk = new HashMap<String, String>();
        scratchReferenceToPk.put("scratchCustomerPk", invoice1.getCustomer().getName() + "");
        Invoice invoice2 = (Invoice) DOF.createScratchObject("invoice.scratchWithScratchCustomer.xml", scratchReferenceToPk);
        assertEquals(invoice1.getCustomer().getId(), invoice2.getCustomer().getId());
        assertTrue(invoice2.getId() > invoice1.getId());
    }


    @Test
    public void testScratchInvoiceTextCallsScratchJavaDependencyDeletesProperlyDeletingIndividually()
    {
        String fileToLoad = "invoice.scratchWithScratchJavaCustomer.xml";
        Invoice invoice1 = (Invoice) DOF.createScratchObject(fileToLoad);
        Map<String, String> scratchReferenceToPk = new HashMap<String, String>();
        scratchReferenceToPk.put("scratchCustomerPk", invoice1.getCustomer().getId() + "");
        Invoice invoice2 = (Invoice) DOF.createScratchObject(
                "invoice.scratchWithScratchJavaCustomer.xml", scratchReferenceToPk);

        int invoice1Id = invoice1.getId();
        int invoice2Id = invoice2.getId();

        int customer1Id = invoice1.getCustomer().getId();
        int customer2Id = invoice2.getCustomer().getId();
        assertEquals(customer1Id, customer2Id);

        int product11Id = invoice1.getLineItems().get(0).getProduct().getId();
        int product12Id = invoice1.getLineItems().get(1).getProduct().getId();
        int product21Id = invoice2.getLineItems().get(0).getProduct().getId();
        int product22Id = invoice2.getLineItems().get(1).getProduct().getId();

        int manufacturer11Id = invoice1.getLineItems().get(0).getProduct().getManufacturer().getId();
        int manufacturer12Id = invoice1.getLineItems().get(1).getProduct().getManufacturer().getId();
        int manufacturer21Id = invoice2.getLineItems().get(0).getProduct().getManufacturer().getId();
        int manufacturer22Id = invoice2.getLineItems().get(1).getProduct().getManufacturer().getId();

        // now check they all clean up
        DOF.delete(invoice1);
        DOF.delete(invoice2);

        assertNull(ComponentFactory.getInvoiceComponent().getByInvoiceId(invoice1Id));
        assertNull(ComponentFactory.getInvoiceComponent().getByInvoiceId(invoice2Id));
        assertNull(ComponentFactory.getCustomerComponent().getById(customer1Id));
        assertNull(ComponentFactory.getCustomerComponent().getById(customer2Id));

        assertNull(ComponentFactory.getProductComponent().getById(product11Id));
        assertNull(ComponentFactory.getProductComponent().getById(product12Id));
        assertNull(ComponentFactory.getProductComponent().getById(product21Id));
        assertNull(ComponentFactory.getProductComponent().getById(product22Id));

        assertNull(ComponentFactory.getManufacturerComponent().getById(manufacturer11Id));
        assertNull(ComponentFactory.getManufacturerComponent().getById(manufacturer12Id));
        assertNull(ComponentFactory.getManufacturerComponent().getById(manufacturer21Id));
        assertNull(ComponentFactory.getManufacturerComponent().getById(manufacturer22Id));

    }


    @Test
    public void testScratchInvoiceTextCallsScratchJavaDependencyDeletesProperlyDeletingAll()
    {
        String fileToLoad = "invoice.scratchWithScratchJavaCustomer.xml";
        Invoice invoice1 = (Invoice) DOF.createScratchObject(fileToLoad);
        Map<String, String> scratchReferenceToPk = new HashMap<String, String>();
        scratchReferenceToPk.put("scratchCustomerPk", invoice1.getCustomer().getId() + "");
        Invoice invoice2 = (Invoice) DOF.createScratchObject(
                "invoice.scratchWithScratchJavaCustomer.xml", scratchReferenceToPk);

        int invoice1Id = invoice1.getId();
        int invoice2Id = invoice2.getId();

        int customer1Id = invoice1.getCustomer().getId();
        int customer2Id = invoice2.getCustomer().getId();
        assertEquals(customer1Id, customer2Id);

        int product11Id = invoice1.getLineItems().get(0).getProduct().getId();
        int product12Id = invoice1.getLineItems().get(1).getProduct().getId();
        int product21Id = invoice2.getLineItems().get(0).getProduct().getId();
        int product22Id = invoice2.getLineItems().get(1).getProduct().getId();

        int manufacturer11Id = invoice1.getLineItems().get(0).getProduct().getManufacturer().getId();
        int manufacturer12Id = invoice1.getLineItems().get(1).getProduct().getManufacturer().getId();
        int manufacturer21Id = invoice2.getLineItems().get(0).getProduct().getManufacturer().getId();
        int manufacturer22Id = invoice2.getLineItems().get(1).getProduct().getManufacturer().getId();

        // now check they all clean up
        DOF.deleteAll(DOF.DeletionOption.scratch_only);

        assertNull(ComponentFactory.getInvoiceComponent().getByInvoiceId(invoice1Id));
        assertNull(ComponentFactory.getInvoiceComponent().getByInvoiceId(invoice2Id));
        assertNull(ComponentFactory.getCustomerComponent().getById(customer1Id));
        assertNull(ComponentFactory.getCustomerComponent().getById(customer2Id));

        assertNull(ComponentFactory.getProductComponent().getById(product11Id));
        assertNull(ComponentFactory.getProductComponent().getById(product12Id));
        assertNull(ComponentFactory.getProductComponent().getById(product21Id));
        assertNull(ComponentFactory.getProductComponent().getById(product22Id));

        assertNull(ComponentFactory.getManufacturerComponent().getById(manufacturer11Id));
        assertNull(ComponentFactory.getManufacturerComponent().getById(manufacturer12Id));
        assertNull(ComponentFactory.getManufacturerComponent().getById(manufacturer21Id));
        assertNull(ComponentFactory.getManufacturerComponent().getById(manufacturer22Id));

    }


    @Test
    public void testScratchInvoiceWithSpecifiedPk()
    {
        int tempId = GlobalContext.getPersistanceFactory().getInvoicePersistence().getNextId();
        assertNull(invoiceComponent.getByInvoiceId(tempId));

        Map<String, String> scratchReferenceToPrimaryKey = new HashMap<String, String>();
        scratchReferenceToPrimaryKey.put("scratch", tempId + "");
        DOF.createScratchObject("invoice.scratch.xml", scratchReferenceToPrimaryKey);
        assertNotNull(invoiceComponent.getByInvoiceNumber(tempId));

    }




}
