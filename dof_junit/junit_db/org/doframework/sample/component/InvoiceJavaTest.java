package org.doframework.sample.component;

import org.doframework.*;
import org.doframework.sample.component.reference.*;
import org.doframework.sample.component.scratch.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;


public class InvoiceJavaTest
{
    @Test
    public void testReferenceInvoiceDependsOnJavaReferences()
    {
        Invoice dofInvoice = (Invoice) DOF.require(new Invoice_500());
        assertEquals((Object) 500, dofInvoice.getInvoiceNumber());
        assertEquals("John Doe", dofInvoice.getCustomer().getName());
        List<LineItem> lineItems = dofInvoice.getLineItems();
        int total = getLineItemTotal(lineItems);
        assertEquals(total, (int)dofInvoice.getTotal());
        assertEquals(total, (int)dofInvoice.getPendingBalance());
    }


    @Test
    public void testReferenceInvoiceDependsOnTextReferences()
    {
        Invoice dofInvoice = (Invoice) DOF.require(new Invoice_1001());
        assertEquals((Object) 1001, dofInvoice.getInvoiceNumber());
        assertEquals("John Smith", dofInvoice.getCustomer().getName());
        List<LineItem> lineItems = dofInvoice.getLineItems();
        int total = getLineItemTotal(lineItems);

        assertEquals(total, (int) dofInvoice.getTotal());
        assertEquals(total, (int) dofInvoice.getPendingBalance());
    }


    private int getLineItemTotal(List<LineItem> list)
    {
        int total = 0;
        for (Iterator lineItemIterator = list.iterator(); lineItemIterator.hasNext();)
        {
            LineItem lineItem = (LineItem) lineItemIterator.next();
            total += lineItem.getQuantity() * lineItem.getPrice();
        }
        return total;
    }


    @Test
    public void testReferenceInvoiceDependsOnTextReferencesWithDelete()
    {
        // ensure object created
        DOF.require(new Invoice_1001());
        String manufacturerName = "Drinks One";
        assertNotNull(ComponentFactory.getInvoiceComponent().getByInvoiceNumber(1001));

        assertNotNull(ComponentFactory.getProductComponent().getByManufacturerAndName(manufacturerName,
                                                                                   "Coffee"));
        assertNotNull(ComponentFactory.getProductComponent().getByManufacturerAndName(manufacturerName,
                                                                                   "Tea"));
        assertNotNull(ComponentFactory.getProductComponent().getByManufacturerAndName(manufacturerName,
                                                                                   "Kona Coffee"));

        // delete object
        DOF.delete(new Invoice_1001());

        assertNull(ComponentFactory.getInvoiceComponent().getByInvoiceNumber(1001));
        assertNull(ComponentFactory.getProductComponent().getByManufacturerAndName(manufacturerName,
                                                                                   "Coffee"));
        assertNull(ComponentFactory.getProductComponent().getByManufacturerAndName(manufacturerName,
                                                                                   "Tea"));
        assertNull(ComponentFactory.getProductComponent().getByManufacturerAndName(manufacturerName,
                                                                                   "Kona Coffee"));
    }

    // Example of using a test to delete objects while working out the JUnit tests
    //@Test
    //public void test()
    //{
    //    DOF.delete(new Invoice_500(), false);
    //}

    @Test
    public void testReferenceInvoiceDeleteNotGreedyJava()
    {
        // ensure object created
        DOF.require(new Invoice_500());
        assertNotNull(DOF.getCachedObject(Invoice.class, "500"));
        assertNotNull(ComponentFactory.getInvoiceComponent().getByInvoiceNumber(500));
        assertNotNull(ComponentFactory
                .getProductComponent().getByManufacturerAndName("New Dole", "Pineapple Juice"));
        assertNotNull(ComponentFactory.getProductComponent().getByManufacturerAndName("New Dole",
                                                                                      "Cranberry Juice"));

        // delete object
        DOF.delete(new Invoice_500());

        assertNull(ComponentFactory.getInvoiceComponent().getByInvoiceId(500));
        assertNull(ComponentFactory
                .getProductComponent().getByManufacturerAndName("New Dole", "Pineapple Juice"));
        assertNull(ComponentFactory
                .getProductComponent().getByManufacturerAndName("New Dole", "Cranberry Juice"));
    }


    @Test
    public void testReferenceInvoiceDeleteGreedyJava()
    {
        // ensure object created
        DOF.require(new Invoice_1002());

        // delete object
        DOF.delete(new Invoice_1002(), true);

        assertNull(ComponentFactory.getInvoiceComponent().getByInvoiceId(1002));
        assertNull(ComponentFactory.getProductComponent().getById(105));
        assertNull(ComponentFactory.getProductComponent().getById(106));
    }


    @Test
    public void testScratchInvoiceDependsOnJavaReferences()
    {
        Invoice dofInvoice1 = (Invoice) DOF.createScratchObject(new Invoice__ScratchJava());
        Invoice dofInvoice2 = (Invoice) DOF.createScratchObject(new Invoice__ScratchJava());
        assertEquals(dofInvoice1.getId() + 1, dofInvoice2.getId());

        assertEquals("Jane Doe", dofInvoice1.getCustomer().getName());
        List<LineItem> lineItems = dofInvoice1.getLineItems();
        int total = getLineItemTotal(lineItems);
        assertEquals(total, (int) dofInvoice1.getTotal());
        assertEquals(total, (int) dofInvoice1.getPendingBalance());

    }


    @Test
    public void testScratchInvoiceDependsOnTextReferences()
    {
        Invoice dofInvoice1 = (Invoice) DOF.createScratchObject(new Invoice__ScratchText());
        Invoice dofInvoice2 = (Invoice) DOF.createScratchObject(new Invoice__ScratchText());
        assertEquals(dofInvoice1.getId() + 1, dofInvoice2.getId());

        assertEquals("John Smith", dofInvoice1.getCustomer().getName());
        List<LineItem> lineItems = dofInvoice1.getLineItems();
        int total = getLineItemTotal(lineItems);
        assertEquals(total, (int) dofInvoice1.getTotal());
        assertEquals(total, (int) dofInvoice1.getPendingBalance());
    }


    @Test
    public void testScratchInvoiceDependsOnJavaScratchs()
    {
        Invoice dofInvoice1 = (Invoice) DOF.createScratchObject(new Invoice__ScratchJavaScratchDependencies());
        Invoice dofInvoice2 = (Invoice) DOF.createScratchObject(new Invoice__ScratchJavaScratchDependencies());
        assertEquals(dofInvoice1.getId() + 1, dofInvoice2.getId());

        assertEquals(dofInvoice1.getCustomer().getId() + 1, dofInvoice2.getCustomer().getId());
        List<LineItem> lineItems = dofInvoice1.getLineItems();
        int total = getLineItemTotal(lineItems);
        assertEquals(total, (int) dofInvoice1.getTotal());
        assertEquals(total, (int) dofInvoice1.getPendingBalance());
    }


    @Test
    public void testScratchInvoiceDependsOnTextScratchs()
    {
        Invoice dofInvoice1 = (Invoice) DOF.createScratchObject(new Invoice__ScratchJavaScratchTextDependencies());
        Invoice dofInvoice2 = (Invoice) DOF.createScratchObject(new Invoice__ScratchJavaScratchTextDependencies());
        assertEquals(dofInvoice1.getId() + 1, dofInvoice2.getId());

        assertTrue(dofInvoice1.getCustomer().getId() < dofInvoice2.getCustomer().getId());
        List<LineItem> lineItems = dofInvoice1.getLineItems();
        int total = getLineItemTotal(lineItems);
        assertEquals(total, (int) dofInvoice1.getTotal());
        assertEquals(total, (int) dofInvoice1.getPendingBalance());
    }


    @Test
    public void testScratchInvoiceDelete()
    {
        // ensure object created
        Invoice__ScratchJavaScratchDependencies scratchJavaScratchDependencies =
                new Invoice__ScratchJavaScratchDependencies();
        Invoice scratchInvoice = (Invoice) DOF.createScratchObject(scratchJavaScratchDependencies);
        int customerId = scratchInvoice.getCustomer().getId();
        int invoiceId = scratchInvoice.getId();
        List<LineItem> lineItems = scratchInvoice.getLineItems();
        Product product1 = lineItems.get(0).getProduct();
        int productId1 = product1.getId();
        int manufacturerId1 = product1.getManufacturer().getId();
        Product product2 = lineItems.get(1).getProduct();
        int productId2 = product2.getId();
        int manufacturerId2 = product1.getManufacturer().getId();

        assertNotNull(ComponentFactory.getCustomerComponent().getById(customerId));
        assertNotNull(ComponentFactory.getProductComponent().getById(productId1));
        assertNotNull(ComponentFactory.getProductComponent().getById(productId2));
        assertNotNull(ComponentFactory.getManufacturerComponent().getById(manufacturerId1));
        assertNotNull(ComponentFactory.getManufacturerComponent().getById(manufacturerId2));
        assertNotNull(ComponentFactory.getInvoiceComponent().getByInvoiceId(invoiceId));

        // delete object
        DOF.delete(scratchInvoice);

        assertNull(ComponentFactory.getCustomerComponent().getById(customerId));
        assertNull(ComponentFactory.getProductComponent().getById(productId1));
        assertNull(ComponentFactory.getProductComponent().getById(productId2));
        assertNull(ComponentFactory.getManufacturerComponent().getById(manufacturerId1));
        assertNull(ComponentFactory.getManufacturerComponent().getById(manufacturerId2));
        assertNull(ComponentFactory.getInvoiceComponent().getByInvoiceId(invoiceId));
    }

    @Test
    public void testDeleteAllScratchObjectsPureJava()
    {
        Invoice__ScratchJavaScratchDependencies scratchJavaScratchDependencies1 =
                new Invoice__ScratchJavaScratchDependencies();
        Invoice scratchInvoice1 = (Invoice) DOF.createScratchObject(scratchJavaScratchDependencies1);
        Invoice__ScratchJavaScratchDependencies scratchJavaScratchDependencies2 =
                new Invoice__ScratchJavaScratchDependencies();
        Invoice scratchInvoice2 = (Invoice) DOF.createScratchObject(scratchJavaScratchDependencies2);

        deleteAllCheckForScratchInvoices(scratchInvoice1, scratchInvoice2);

    }




    @Test
    public void testDeleteAllScratchObjectsJavaDependsText()
    {
        Invoice__ScratchJavaScratchTextDependencies scratchJavaScratchDependencies1 =
                new Invoice__ScratchJavaScratchTextDependencies();
        Invoice scratchInvoice1 = (Invoice) DOF.createScratchObject(scratchJavaScratchDependencies1);
        Invoice__ScratchJavaScratchTextDependencies scratchJavaScratchDependencies2 =
                new Invoice__ScratchJavaScratchTextDependencies();
        Invoice scratchInvoice2 = (Invoice) DOF.createScratchObject(scratchJavaScratchDependencies2);

        deleteAllCheckForScratchInvoices(scratchInvoice1, scratchInvoice2);

    }


    private void deleteAllCheckForScratchInvoices(Invoice scratchInvoice1, Invoice scratchInvoice2)
    {
        int invoiceId1 = scratchInvoice1.getId();
        int invoiceId2 = scratchInvoice2.getId();

        int customerId1 = scratchInvoice1.getCustomer().getId();
        int customerId2 = scratchInvoice2.getCustomer().getId();

        List<Integer> productIdsCreated = new ArrayList<Integer>();
        List<Integer> manufacturerIdsCreated = new ArrayList<Integer>();

        List<LineItem> lineItems1 = scratchInvoice1.getLineItems();
        for (Iterator lineItemIterator = lineItems1.iterator(); lineItemIterator.hasNext();)
        {
            LineItem lineItem = (LineItem) lineItemIterator.next();
            productIdsCreated.add(lineItem.getProduct().getId());
            manufacturerIdsCreated.add(lineItem.getProduct().getManufacturer().getId());
        }

        List<LineItem> lineItems2 = scratchInvoice2.getLineItems();
        for (Iterator lineItemIterator = lineItems2.iterator(); lineItemIterator.hasNext();)
        {
            LineItem lineItem = (LineItem) lineItemIterator.next();
            productIdsCreated.add(lineItem.getProduct().getId());
            manufacturerIdsCreated.add(lineItem.getProduct().getManufacturer().getId());
        }


        for (Iterator iterator = productIdsCreated.iterator(); iterator.hasNext();)
        {
            Integer id = (Integer) iterator.next();
            assertNotNull(ComponentFactory.getProductComponent().getById(id));
        }

        for (Iterator iterator = manufacturerIdsCreated.iterator(); iterator.hasNext();)
        {
            Integer id = (Integer) iterator.next();
            assertNotNull(ComponentFactory.getManufacturerComponent().getById(id));
        }

        DOF.deleteAll(DOF.DeletionOption.scratch_only);

        for (Iterator iterator = productIdsCreated.iterator(); iterator.hasNext();)
        {
            Integer id = (Integer) iterator.next();
            assertNull(ComponentFactory.getProductComponent().getById(id));
        }

        for (Iterator iterator = manufacturerIdsCreated.iterator(); iterator.hasNext();)
        {
            Integer id = (Integer) iterator.next();
            assertNull(ComponentFactory.getProductComponent().getById(id));
        }

        InvoiceComponent invoiceComponent = ComponentFactory.getInvoiceComponent();
        assertNull(invoiceComponent.getByInvoiceId(invoiceId1));
        assertNull(invoiceComponent.getByInvoiceId(invoiceId2));

        CustomerComponent customerComponent = ComponentFactory.getCustomerComponent();
        assertNull(customerComponent.getById(customerId1));
        assertNull(customerComponent.getById(customerId2));
    }


    @Test
    public void testTwoScratchInvoicesDependOnSameCustomerAllJava()
    {
        Invoice__ScratchJavaScratchDependencies scratchInvoiceBuilder =
                new Invoice__ScratchJavaScratchDependencies();

        Invoice invoice1 = (Invoice) DOF.createScratchObject(scratchInvoiceBuilder);
        Customer customer = invoice1.getCustomer();

        Map scratchReferenceToObject = new HashMap();
        scratchReferenceToObject.put("scratchCustomer", customer);
        Invoice invoice2 = (Invoice) DOF.createScratchObject(scratchInvoiceBuilder,
                                                             scratchReferenceToObject);
        assertEquals(invoice1.getCustomer().getId(), invoice2.getCustomer().getId());
    }

@Test
public void testNewInvoiceSubtotal()
{
    Invoice invoice = (Invoice) DOF.createScratchObject(new Invoice__ScratchJavaScratchDependencies());
    int originalSubtotal = invoice.getTotal();
    Product acaiJuice = (Product) DOF.require(new Product_TinyJuiceAcaiJuice());
    Product blueberryJuice = (Product) DOF.require(new Product_TinyJuiceBlueberryJuice());
    InvoiceComponent invoiceComponent = ComponentFactory.getInvoiceComponent();
    invoiceComponent.addLineItem(invoice, 2, acaiJuice, acaiJuice.getPrice());
    invoiceComponent.addLineItem(invoice, 3, blueberryJuice, blueberryJuice.getPrice());

    // triangulation
    assertEquals((long) (originalSubtotal + acaiJuice.getPrice() * 2 + blueberryJuice.getPrice() * 3),
                 (long) invoice.getTotal());

    // Test persistence
    invoiceComponent.persist(invoice);
    Invoice invoiceFromDb = invoiceComponent.getByInvoiceId(invoice.getId());
    assertEquals(invoice.getTotal(), invoiceFromDb.getTotal());
}


}