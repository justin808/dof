package org.doframework.sample.xml_handler;

import org.doframework.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.doframework.sample.component.*;
import org.doframework.sample.global.*;
import org.doframework.sample.persistence.jdbc_persistence.*;

/**
 * User: gordonju Date: Jan 13, 2008 Time: 10:51:33 PM
 */
public class ProductXmlFactoryTest
{

    ProductComponent productComponent =ComponentFactory.getProductComponent();


    @BeforeClass
    public static void beforeTests()
    {
        GlobalContext.setPersistanceFactory(new JdbcPersistenceFactory());
    }


    @Test
    public void testParseFileProduct14Xml()
    {
        DOF.require("manufacturer.37.xml");
        ProductXmlFactory pxf = new ProductXmlFactory();
        ObjectFileInfo ofi = new ObjectFileInfo("product", "14", "xml");
        ofi.setFileToLoad("product.14.xml");

        Product p = pxf.createProduct(ofi);
        verifyProduct14(p);
    }


    private void verifyProduct14(Product p)
    {
        assertEquals(14, p.getId());
        assertEquals("tea", p.getName());
        assertEquals(new Integer("5"), p.getPrice());
        assertEquals(37, p.getManufacturer().getId());
    }


    @Test(expected = RuntimeException.class)
    public void testProductInsertFailsDependencyDoesNotExist()
    {
        // ensure that the manufacturer 1001 does not exist
        DOF.delete("product.1000.xml");
        // DOF.delete("manufacturer.1001.xml");

        // verify
        ManufacturerComponent manufacturerComponent = ComponentFactory.getManufacturerComponent();
        assertNull(manufacturerComponent.get(1001));


        ProductXmlFactory pxf = new ProductXmlFactory();
        ObjectFileInfo ofi = new ObjectFileInfo("product", "1000", "xml");
        ofi.setFileToLoad("product.1000.xml");
        Product product = pxf.createProduct(ofi);
        // fail("Expected exception b/c no manufacturer 1001");
    }


    @Test (expected = RuntimeException.class)
    public void testProductPersistDependencyDoesNotExist()
    {
        ProductXmlFactory pxf = new ProductXmlFactory();
        Product product = (Product) DOF.createScratchObject("product.scratchdependsOnManu39.xml");
        Manufacturer notYetPersistedManufacturer = new Manufacturer(-1, "dummy");
        product.setManufacturer(notYetPersistedManufacturer);
        productComponent.persist(product);
    }


    @Test
    public void testRequireProductReturnsProduct()
    {
        DOF.delete("invoice.100.xml");
        DOF.delete("product.14.xml");
        DOF.delete("product.15.xml");
        DOF.delete("manufacturer.37.xml");
        ProductXmlFactory productXmlFactory = new ProductXmlFactory();
        ObjectFileInfo ofi = new ObjectFileInfo("product", "14", "xml");
        ofi.setFileToLoad("product.14.xml");

        assertNull(productXmlFactory.get(ofi));
        ManufacturerXmlFactory manufacturerXmlFactory = new ManufacturerXmlFactory();
        ObjectFileInfo ofiManufacturer = new ObjectFileInfo("manufacturer", "37", "xml");
        ofi.setFileToLoad("manufacturer.37.xml");

        assertNull(manufacturerXmlFactory.get(ofiManufacturer));

        // Make sure that product and manufacturer are deleted

        Product p = (Product) DOF.require("product.14.xml");
        assertNotNull(p);
        verifyProduct14(p);
    }


    @Test
    public void testRecursiveDelete()
    {
        Product p = (Product) DOF.require("product.1000.xml");
        DOF.delete("product.1000.xml"); // should delete manufacturer 1001

        assertNull(ComponentFactory.getProductComponent().getById(1000));
        assertNull(ComponentFactory.getManufacturerComponent().getById(1001));


        ProductXmlFactory productXmlFactory = new ProductXmlFactory();
        ObjectFileInfo ofiProduct = new ObjectFileInfo("product", "1000", "xml");
        ofiProduct.setFileToLoad("product.1000.xml");

        assertNull(productXmlFactory.get(ofiProduct));
        ManufacturerXmlFactory manufacturerXmlFactory = new ManufacturerXmlFactory();
        ObjectFileInfo ofiManufacturer = new ObjectFileInfo("manufacturer", "1001", "xml");
        ofiManufacturer.setFileToLoad("manufacturer.1001.xml");

        assertNull(manufacturerXmlFactory.get(ofiManufacturer));

    }


}
