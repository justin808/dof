package org.doframework.sample.jdbc_app.test.dof.test;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.doframework.DOF;
import org.doframework.ObjectFileInfo;
import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.entity.Product;
import org.doframework.sample.jdbc_app.factory.ManufacturerFactory;
import org.doframework.sample.jdbc_app.factory.ProductFactory;
import org.doframework.sample.jdbc_app.test.dof.ManufacturerDOFHandler;
import org.doframework.sample.jdbc_app.test.dof.ProductDOFHandler;

/**
 * User: gordonju Date: Jan 13, 2008 Time: 10:51:33 PM
 */
public class ProductDOFHandlerTest extends TestCase
{

    ProductFactory productFactory;

    public void setUp()
    {
        productFactory = GlobalContext.getFactoryLookupService().getProductFactory();
    }


    public void testParseFileProduct14Xml()
    {
        DOF.require("manufacturer.37.xml");

        String testFile = "product.14.xml";
        ProductDOFHandler pxf = new ProductDOFHandler();
        Product p = pxf.createProduct(testFile);
        verifyProduct14(p);
    }

    private void verifyProduct14(Product p)
    {
        assertEquals(14, p.getId());
        assertEquals("tea", p.getName());
        assertEquals(new BigDecimal("4.99"), p.getPrice());
        assertEquals(37, p.getManufacturer().getId());
    }


    public void testProductInsertFailsDependencyDoesNotExist()
    {
        // ensure that the manufacturer 30 does not exist
        DOF.delete("product.30.xml");
        DOF.delete("manufacturer.30.xml");

        // verify
        ManufacturerFactory manufacturerFactory =
                GlobalContext.getFactoryLookupService().getManufacturerFactory();
        assertNull(manufacturerFactory.get("30"));

        String testFile = "product.30.xml";
        ProductDOFHandler pxf = new ProductDOFHandler();
        Product productFile = null;
        try
        {
            productFile = pxf.createProduct(testFile);
            fail("Expected exception b/c no manufacturer 30");
        }
        catch (Exception e)
        {
        }

        try
        {
            productFactory.insert(productFile);
            fail("Expected exception b/c manufacturer 30 must not exist.");
        }
        catch (Exception e)
        {
            // this is OK
        }

    }


    public void testRequireProductReturnsProduct()
    {
        String productFile14 = "product.14.xml";
        DOF.delete(productFile14);
        DOF.delete("product.15.xml");
        String fileToLoadManufacturer37 = "manufacturer.37.xml";
        DOF.delete(fileToLoadManufacturer37);
        ProductDOFHandler productDOFHandler = new ProductDOFHandler();
        ObjectFileInfo objectFileInfoProduct14 = DOF.getObjectFileInfo(productFile14);
        assertNull(productDOFHandler.get(objectFileInfoProduct14));
        ManufacturerDOFHandler manufacturerDOFHandler = new ManufacturerDOFHandler();
        assertNull(manufacturerDOFHandler.get(DOF.getObjectFileInfo(fileToLoadManufacturer37)));

        // Make sure that product and manufacturer are deleted

        Product p = (Product) DOF.require(productFile14);
        assertNotNull(p);
        verifyProduct14(p);
    }

    public void testRecursiveDelete()
    {
        String fileToLoadProduct30 = "product.30.xml";
        DOF.require(fileToLoadProduct30);
        DOF.delete(fileToLoadProduct30); // should delete manufacturer 30

        ProductDOFHandler productDOFHandler = new ProductDOFHandler();
        ObjectFileInfo ofi = DOF.getObjectFileInfo(fileToLoadProduct30);
        assertNull(productDOFHandler.get(ofi));
        ManufacturerDOFHandler manufacturerDOFHandler = new ManufacturerDOFHandler();
        assertNull(manufacturerDOFHandler.get(DOF.getObjectFileInfo("manufacturer.30.xml")));
    }

    public void testParseFileProductSelfReferencing()
    {
        String fileToLoad = "product.31.xml";
        boolean deletedProduct31 = DOF.delete(fileToLoad);
        assertTrue(deletedProduct31);
        Product p = (Product) DOF.require(fileToLoad);
        assertEquals("Gatorade", p.getName());
    }


}
