package org.doframework.sample.xml_handler;

import static org.junit.Assert.*;
import org.junit.*;
import org.doframework.sample.component.*;
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


    @Test
    public void testParseFile()
    {
        ManufacturerXmlFactory mxf = new ManufacturerXmlFactory();
        ObjectFileInfo ofi = new ObjectFileInfo("manufacturer", "35", "xml");
        ofi.setFileToLoad("manufacturer.35.xml");

        Manufacturer m = mxf.createManufacturer(ofi);
        assertEquals(35, m.getId());
        assertEquals("Starbucks", m.getName());
    }


    /**
     * Note: this test is OK only b/c manufacturer has no dependencies. I.e., normally we depend upon the framework for
     * creating dependencies.
     */
    @Test
    public void testManufacturerInsert()
    {
        String sql = "select count(*) from manufacturer where id = 35";

        ManufacturerXmlFactory mxf = new ManufacturerXmlFactory();
        ObjectFileInfo ofi = new ObjectFileInfo("manufacturer", "35", "xml");
        ofi.setFileToLoad("manufacturer.35.xml");

        Manufacturer m = mxf.createManufacturer(ofi);

        // first delete just in case it exists
        manufacturerComponent.delete(m);
        int countAfterDelete = JdbcDbUtil.executeSingleIntQuery(sql);
        assertEquals(0, countAfterDelete);
        GlobalContext.getPersistanceFactory().getManufacturerPersistence().insert(m);

        int countAfterInsert = JdbcDbUtil.executeSingleIntQuery(sql);
        assertEquals(1, countAfterInsert);

        assertTrue(manufacturerComponent.delete(m));

        int countAfterDelete2 = JdbcDbUtil.executeSingleIntQuery(sql);
        assertEquals(0, countAfterDelete2);

    }


    public void testProductDeleteReturnsTrueIfNoOtherDependencies()
    {
        // first make sure the object is there to delete
        assertNotNull(DOF.require("manufacturer.20.xml"));

        // make sure that the dependency is NOT there
        boolean deleted = DOF.delete("manufacturer.20.xml");

        // This could be a bit tricky if other products depended on #20, so
        // we made sure that nothing else does and commented the xml file
        assertTrue("manufacterer failed to delete", deleted);

        Manufacturer manufacturer = manufacturerComponent.getById(20);
        assertNull(manufacturer);

    }


    @Test
    public void testManufacturerDeleteReturnsFalseIfOtherDependencies()
    {
        assertNotNull(DOF.require("product.21.xml"));
        assertNotNull(DOF.require("product.22.xml"));
        assertFalse(DOF.delete("manufacturer.21.xml"));
    }


    @Test
    public void testProductDeleteReturnsFalseIfOtherDependencies()
    {
        assertNotNull(DOF.require("product.21.xml"));
        assertNotNull(DOF.require("product.22.xml"));
        assertTrue(DOF.delete("product.21.xml")); // true b/c product deletes
        assertNotNull(manufacturerComponent.getById(21)); // check manufacturer still there
    }


    @Test
    public void testRequireManfacturerReturnsManufacturer()
    {
        Manufacturer m = (Manufacturer) DOF.require("manufacturer.35.xml");
        assertNotNull(m);
        assertEquals(35, m.getId());
        assertEquals("Starbucks", m.getName());
    }


    @Test
    public void testRequireManfacturerTwiceDoesNotCreateManufacturerTwice()
    {
        Manufacturer m1 = (Manufacturer) DOF.require("manufacturer.35.xml");
        Manufacturer m2 = (Manufacturer) DOF.require("manufacturer.35.xml");
        assertSame(m1, m2);
    }


    @Test
    public void testDeleteManufacturer()
    {
        Manufacturer m1 = (Manufacturer) DOF.require("manufacturer.20.xml");
        assertTrue(DOF.delete("manufacturer.20.xml"));
        Manufacturer m2 = (Manufacturer) DOF.require("manufacturer.20.xml");
        assertNotSame(m1, m2);
        Manufacturer m3 = (Manufacturer) DOF.require("manufacturer.20.xml");
        assertSame(m2, m3);
        DOF.removeEntryFromCache(m3, m3.getId());
        Manufacturer m4 = (Manufacturer) DOF.require("manufacturer.20.xml");
        assertNotSame(m3, m4);
    }


}
