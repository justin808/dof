package org.doframework.sample.xml_handler;

import static org.junit.Assert.*;
import org.junit.*;
import org.doframework.sample.component.*;
import org.doframework.sample.component.reference.*;
import org.doframework.sample.persistence.jdbc_persistence.*;
import org.doframework.sample.global.*;
import org.doframework.*;

public class ManufacturerXmlFactoryTest
{

    ManufacturerComponent manufacturerComponent = ComponentFactory.getManufacturerComponent();


    @BeforeClass
    static public void beforeClass()
    {
        GlobalContext.setPersistanceFactory(new JdbcPersistenceFactory());
    }






    public void testProductDeleteReturnsTrueIfNoOtherDependencies()
    {
        // first make sure the object is there to delete
        assertNotNull(DOF.require("manufacturer.Arrowhead.xml"));

        // make sure that the dependency is NOT there
        boolean deleted = DOF.delete("manufacturer.Arrowhead.xml");

        // This could be a bit tricky if other products depended on #20, so
        // we made sure that nothing else does and commented the xml file
        assertTrue("manufacterer failed to delete", deleted);

        Manufacturer manufacturer = manufacturerComponent.getById(20);
        assertNull(manufacturer);

    }


    @Test
    public void testManufacturerDeleteReturnsFalseIfOtherDependencies()
    {
        assertNotNull(DOF.require("product.Pepsi__Cola.xml"));
        assertNotNull(DOF.require("product.Pepsi__Diet Cola.xml"));
        assertFalse(DOF.delete(new Manufacturer_Pepsi()));
    }


    @Test
    public void testProductDeleteReturnsFalseIfOtherDependencies()
    {
        assertNotNull(DOF.require("product.Pepsi__Cola.xml"));
        assertNotNull(DOF.require("product.Pepsi__Diet Cola.xml"));
        assertTrue(DOF.delete("product.Pepsi__Cola.xml")); // true b/c product deletes
        assertNotNull(manufacturerComponent.getByName("Pepsi")); // check manufacturer still there
    }


    @Test
    public void testRequireManfacturerReturnsManufacturer()
    {
        Manufacturer m = (Manufacturer) DOF.require("manufacturer.Starbucks.xml");
        assertNotNull(m);
        assertEquals("Starbucks", m.getName());
    }


    @Test
    public void testRequireManfacturerTwiceDoesNotCreateManufacturerTwice()
    {
        Manufacturer m1 = (Manufacturer) DOF.require("manufacturer.Starbucks.xml");
        Manufacturer m2 = (Manufacturer) DOF.require("manufacturer.Starbucks.xml");
        assertSame(m1, m2);
    }


    @Test
    public void testDeleteManufacturer()
    {
        DOF.require("product.Arrowhead__Water.xml");
        DOF.delete("product.Arrowhead__Water.xml");
        Manufacturer m1 = (Manufacturer) DOF.require("manufacturer.Arrowhead.xml");
        assertTrue(DOF.delete("manufacturer.Arrowhead.xml"));
        Manufacturer m2 = (Manufacturer) DOF.require("manufacturer.Arrowhead.xml");
        assertNotSame(m1, m2);
        Manufacturer m3 = (Manufacturer) DOF.require("manufacturer.Arrowhead.xml");
        assertSame(m2, m3);
        DOF.removeEntryFromCache(m3, m3.getName());
        Manufacturer m4 = (Manufacturer) DOF.require("manufacturer.Arrowhead.xml");
        assertNotSame(m3, m4);
    }


}
