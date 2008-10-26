package org.doframework.sample.component;

import org.doframework.*;
import org.doframework.sample.component.reference.*;
import org.doframework.sample.global.*;
import org.doframework.sample.persistence.*;
import static org.junit.Assert.*;
import org.junit.*;

public class ManufacturerJavaTest
{

    @Test
    public void testReferenceManufacturer()
    {
        Manufacturer dofManufacturer = (Manufacturer) DOF.require(new Manufacturer_TinyJuice());
        assertEquals("Tiny Juice", dofManufacturer.getName());
        ManufacturerPersistence manufacturerPersistence = GlobalContext.getPersistanceFactory().getManufacturerPersistence();
        Manufacturer manuallyRetrievedManufacturer = manufacturerPersistence.getByName("Tiny Juice");
        assertEquals(manuallyRetrievedManufacturer, dofManufacturer);
    }

    @Test
    public void testDeleteJavaReferenceInvoiceThatDoesNotExistDeletesJavaDependencies()
    {
        Object invoice1003 = DOF.require(new Invoice_1003());
        assertNotNull(ComponentFactory.getCustomerComponent().getByName("John Adams"));
        assertNotNull(ComponentFactory.getProductComponent().getByManufacturerAndName("New Dole",
                                                                                      "Green Juice"));
        assertNotNull(ComponentFactory.getProductComponent().getByManufacturerAndName("New Dole",
                                                                                      "Red Juice"));

        DOF.delete(invoice1003, false); // not greedy;

        assertNotNull(ComponentFactory.getCustomerComponent().getByName("John Adams"));
        assertNotNull(ComponentFactory.getProductComponent().getByManufacturerAndName("New Dole",
                                                                                      "Green Juice"));
        assertNotNull(ComponentFactory.getProductComponent().getByManufacturerAndName("New Dole",
                                                                                      "Red Juice"));
        DOF.require(new Invoice_1003());
        DOF.delete(new Invoice_1003());
        assertNull(ComponentFactory.getCustomerComponent().getByName("John Adams"));
        assertNull(ComponentFactory.getProductComponent().getByManufacturerAndName("New Dole",
                                                                                      "Green Juice"));
        assertNull(ComponentFactory.getProductComponent().getByManufacturerAndName("New Dole",
                                                                                   "Red Juice"));


    }


    @Test
    public void testDeleteJavaReferenceInvoiceThatDoesNotExistDeletesTextDependencies()
    {
        Object invoice1004 = DOF.require(new Invoice_1004());
        assertNotNull(ComponentFactory.getCustomerComponent().getByName("James Adams"));
        assertNotNull(ComponentFactory.getProductComponent().getByManufacturerAndName("Drinks One",
                                                                                      "Cola"));
        assertNotNull(ComponentFactory.getProductComponent().getByManufacturerAndName("Drinks One",
                                                                                      "Club Soda"));

        DOF.delete(invoice1004, false); // not greedy;
        assertNotNull(ComponentFactory.getCustomerComponent().getByName("James Adams"));
        assertNotNull(ComponentFactory.getProductComponent().getByManufacturerAndName("Drinks One",
                                                                                      "Cola"));
        assertNotNull(ComponentFactory.getProductComponent().getByManufacturerAndName("Drinks One",
                                                                                      "Club Soda"));
        DOF.require(new Invoice_1004());
        DOF.delete(new Invoice_1004());
        assertNull(ComponentFactory.getCustomerComponent().getByName("James Adams"));
        assertNull(ComponentFactory.getProductComponent().getByManufacturerAndName("Drinks One",
                                                                                      "Cola"));
        assertNull(ComponentFactory.getProductComponent().getByManufacturerAndName("Drinks One",
                                                                                      "Club Soda"));


    }


}
