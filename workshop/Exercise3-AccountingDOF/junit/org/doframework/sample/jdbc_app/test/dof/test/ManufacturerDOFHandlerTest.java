package org.doframework.sample.jdbc_app.test.dof.test;

import junit.framework.TestCase;

import org.doframework.DOF;
import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.entity.Manufacturer;
import org.doframework.sample.jdbc_app.factory.JdbcDbUtil;
import org.doframework.sample.jdbc_app.factory.ManufacturerFactory;
import org.doframework.sample.jdbc_app.test.dof.ManufacturerDOFHandler;



/**
 User: gordonju Date: Jan 13, 2008 Time: 10:20:25 PM
 */
public class ManufacturerDOFHandlerTest extends TestCase
{

    ManufacturerFactory manufacturerFactory;

    public void setUp()
    {
        manufacturerFactory = GlobalContext.getFactoryLookupService().getManufacturerFactory();
    }

    public void testParseFile()
    {
        String testFile = "manufacturer.35.xml";
        ManufacturerDOFHandler mxf = new ManufacturerDOFHandler();
        Manufacturer m = mxf.createManufacturer(testFile);
        assertEquals(35, m.getId());
        assertEquals("Starbucks", m.getName());
    }


    /**
     Note: this test is OK only b/c manufacturer has no dependencies. I.e., normally we depend upon the framework for
     creating dependencies.
     */
    public void testManufacturerInsert()
    {
        String sql = "select count(*) from manufacturer where id = 38";
        String testFile = "manufacturer.38.xml";
        ManufacturerDOFHandler mxf = new ManufacturerDOFHandler();
        Manufacturer m = mxf.createManufacturer(testFile);

        // first delete just in case it exists
        manufacturerFactory.delete(m);
        int countAfterDelete = JdbcDbUtil.executeSingleIntQuery(sql);
        assertEquals(0, countAfterDelete);

        manufacturerFactory.insert(m);

        int countAfterInsert = JdbcDbUtil.executeSingleIntQuery(sql);
        assertEquals(1, countAfterInsert);

        assertTrue(manufacturerFactory.delete(m));

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

        Manufacturer manufacturer = manufacturerFactory.get("20");
        assertNull(manufacturer);

    }

    public void testManufacturerDeleteReturnsFalseIfOtherDependencies()
    {
        assertNotNull(DOF.require("product.21.xml"));
        assertNotNull(DOF.require("product.22.xml"));
        assertFalse(DOF.delete("manufacturer.21.xml"));
    }

    public void testProductDeleteReturnsFalseIfOtherDependencies()
    {
        assertNotNull(DOF.require("product.21.xml"));
        assertNotNull(DOF.require("product.22.xml"));
        assertTrue(DOF.delete("product.21.xml")); // true b/c product deletes
        assertNotNull(manufacturerFactory.get("21")); // check manufacturer still there
    }

    public void testRequireManfacturerReturnsManufacturer()
    {
        Manufacturer m = (Manufacturer) DOF.require("manufacturer.35.xml");
        assertNotNull(m);
        assertEquals(35, m.getId());
        assertEquals("Starbucks", m.getName());
    }


    public void testRequireManfacturerTwiceDoesNotCreateManufacturerTwice()
    {
        Manufacturer m1 = (Manufacturer) DOF.require("manufacturer.35.xml");
        Manufacturer m2 = (Manufacturer) DOF.require("manufacturer.35.xml");
        assertSame(m1, m2);
    }


}
