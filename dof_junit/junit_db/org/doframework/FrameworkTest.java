package org.doframework;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.doframework.sample.xml_handler.*;
import org.doframework.sample.component.*;
import org.doframework.sample.global.*;
import org.doframework.sample.persistence.jdbc_persistence.*;

public class FrameworkTest
{

    @BeforeClass
    static public void beforeTests()
    {
        GlobalContext.setPersistanceFactory(new JdbcPersistenceFactory());
    }


    @Test
    public void testParseFileManufacturer()
    {
        ManufacturerXmlHandler mxf = new ManufacturerXmlHandler();
        ObjectFileInfo ofi = new ObjectFileInfo("manufacturer", "Starbucks", "xml");
        ofi.setFileToLoad("manufacturer.Starbucks.xml");

        Manufacturer m = mxf.createManufacturer(ofi);
        assertEquals("Starbucks", m.getName());
    }


    /**
     * Note: this test is OK only b/c manufacturer has no dependencies. I.e., normally we depend
     * upon the framework for creating dependencies.
     */
    @Test
    public void testManufacturerInsert()
    {
        String sql = "select count(*) from manufacturer where name = '7 Up'";

        ManufacturerXmlHandler mxf = new ManufacturerXmlHandler();
        ObjectFileInfo ofi = new ObjectFileInfo("manufacturer", "7 Up", "xml");
        ofi.setFileToLoad("manufacturer.7 Up.xml");

        Manufacturer m = mxf.createManufacturer(ofi);

        // first delete just in case it exists
        ManufacturerComponent manufacturerComponent = ComponentFactory.getManufacturerComponent();
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


    /**
     * This will test that we have our files set up so they are accessible. In this example, we are
     * using the method InputStream is = ClassLoader.getSystemResourceAsStream(xmlDescriptionFile);
     * Thus, we need to add the test_data directory to the classpath. In IntelliJ, this is done by
     * adding the directory as a "project library"
     */
    @Test
    public void testParsingCustomer()
    {
        CustomerXmlHandler mxf = new CustomerXmlHandler();
        ObjectFileInfo ofi = new ObjectFileInfo("customer", "John Smith", "xml");
        ofi.setFileToLoad("customer.John Smith.xml");
        Customer customer = mxf.createCustomer(ofi);
        assertEquals("John Smith", customer.getName());
    }


    @Test(expected = RuntimeException.class)
    public void testProductInsertFailsDependencyDoesNotExist()
    {
        // ensure that the manufacturer 1001 does not exist
        DOF.delete("product.Lipton__Foobar.xml");
        // DOF.delete("manufacturer.1001.xml");

        // verify
        ManufacturerComponent manufacturerComponent = ComponentFactory.getManufacturerComponent();
        assertNull(manufacturerComponent.get(1001));


        ProductXmlHandler pxf = new ProductXmlHandler();
        ObjectFileInfo ofi = new ObjectFileInfo("product", "Lipton__Foobar", "xml");
        ofi.setFileToLoad("product.Lipton__Foobar.xml");
        Product product = (Product) pxf.create(ofi);
        // fail("Expected exception b/c no manufacturer 1001");
    }


    @Test
    public void testRequireProductReturnsProduct()
    {
        DOF.delete("invoice.100.xml");
        DOF.delete("product.Lipton__Tea.xml");
        DOF.delete("product.Lipton__Green Tea.xml");
        DOF.delete("manufacturer.Lipton.xml");
        ProductXmlHandler productXmlFactory = new ProductXmlHandler();
        ObjectFileInfo ofi = new ObjectFileInfo("product", "Lipton__Tea", "xml");
        ofi.setFileToLoad("product.Lipton__Tea.xml");

        assertNull(productXmlFactory.get(ofi));
        ManufacturerXmlHandler manufacturerXmlFactory = new ManufacturerXmlHandler();
        ObjectFileInfo ofiManufacturer = new ObjectFileInfo("manufacturer", "Lipton", "xml");
        ofi.setFileToLoad("manufacturer.Lipton.xml");

        assertNull(manufacturerXmlFactory.get(ofiManufacturer));

        // Make sure that product and manufacturer are deleted

        Product p = (Product) DOF.require("product.Lipton__Tea.xml");
        assertNotNull(p);
        verifyProductLiptonTea(p);
    }




    private void verifyProductLiptonTea(Product p)
    {
        assertEquals("Tea", p.getName());
        assertEquals(new Integer("5"), p.getPrice());
        assertEquals("Lipton", p.getManufacturer().getName());
    }


    @Test
    public void testRecursiveDelete()
    {
        String productManuProdName = "Fuzzy Drinks__Root Beer";
        String productRootBeer = "product." + productManuProdName + ".xml";
        Product p = (Product) DOF.require(productRootBeer);
        DOF.delete(productRootBeer); // should delete manufacturer 1001

        String manufacturerName = "Fuzzy Drinks";
        assertNull(ComponentFactory.getProductComponent().getByManufacturerAndName(manufacturerName,
                                                                                   "Root Beer"));
        assertNull(ComponentFactory.getManufacturerComponent().getByName(manufacturerName));


        ProductXmlHandler productXmlFactory = new ProductXmlHandler();
        ObjectFileInfo ofiProduct = new ObjectFileInfo("product", productManuProdName, "xml");
        ofiProduct.setFileToLoad(productRootBeer);

        assertNull(productXmlFactory.get(ofiProduct));
        ManufacturerXmlHandler manufacturerXmlFactory = new ManufacturerXmlHandler();
        ObjectFileInfo ofiManufacturer = new ObjectFileInfo("manufacturer", manufacturerName, "xml");
        ofiManufacturer.setFileToLoad("manufacturer.Fuzzy Drinks.xml");
        assertNull(manufacturerXmlFactory.get(ofiManufacturer));

    }


}
