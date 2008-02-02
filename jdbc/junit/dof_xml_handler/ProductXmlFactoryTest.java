package dof_xml_handler;

import com.ibm.dof.*;
import component.*;
import entity.*;
import global.*;
import junit.framework.*;

import java.math.*;

/**
 * User: gordonju Date: Jan 13, 2008 Time: 10:51:33 PM
 */
public class ProductXmlFactoryTest extends TestCase
{

    ProductComponent productComponent;

    public void setUp()
    {
        productComponent = GlobalContext.getComponentFactory().getProductComponent();
    }


    public void testParseFileProduct14Xml()
    {
        DOF.require("manufacturer.37.xml");

        String testFile = "product.14.xml";
        ProductXmlFactory pxf = new ProductXmlFactory();
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
        ManufacturerComponent manufacturerComponent =
                GlobalContext.getComponentFactory().getManufacturerComponent();
        assertNull(manufacturerComponent.get("30"));

        String testFile = "product.30.xml";
        ProductXmlFactory pxf = new ProductXmlFactory();
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
            productComponent.insert(productFile);
            fail("Expected exception b/c manufacturer 30 must not exist.");
        }
        catch (Exception e)
        {
            // this is OK
        }

    }


    public void testRequireProductReturnsProduct()
    {
        DOF.delete("product.14.xml");
        DOF.delete("product.15.xml");
        DOF.delete("manufacturer.37.xml");
        ProductXmlFactory productXmlFactory = new ProductXmlFactory();
        assertNull(productXmlFactory.get("14"));
        ManufacturerXmlFactory manufacturerXmlFactory = new ManufacturerXmlFactory();
        assertNull(manufacturerXmlFactory.get("37"));

        // Make sure that product and manufacturer are deleted

        Product p = (Product) DOF.require("product.14.xml");
        assertNotNull(p);
        verifyProduct14(p);
    }

    public void testRecursiveDelete()
    {
        DOF.require("product.30.xml");
        DOF.delete("product.30.xml"); // should delete manufacturer 30

        ProductXmlFactory productXmlFactory = new ProductXmlFactory();
        assertNull(productXmlFactory.get("30"));
        ManufacturerXmlFactory manufacturerXmlFactory = new ManufacturerXmlFactory();
        assertNull(manufacturerXmlFactory.get("30"));

    }


}
