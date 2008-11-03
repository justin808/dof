package sample;
import org.doframework.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.io.*;
import java.util.*;

public class EntityTest
{
    @BeforeClass
    public static void beforeClass()
    {
        DOFGlobalSettings.setDofDir("hello_dof_java/src/META-INF");
        //JpaUtility.dropAll();
        checkSchemaCreated();
    }


    private static void checkSchemaCreated()
    {
        if (!JpaUtility.tableExists("MANUFACTURER"))
        {
            createSchema();
        }
    }


    private static void createSchema()
    {
        System.out.println("Creating Schema");
        String schemaSql = DOFGlobalSettings.getResourceAsStringFromDofDefsDir("hello_dof.sql");
        for (StringTokenizer stringTokenizer = new StringTokenizer(schemaSql, ";");
             stringTokenizer.hasMoreTokens();)
        {
            String sql = stringTokenizer.nextToken();
            sql = sql.trim();
            if (sql.length() == 0)
            {
                continue;
            }
            //System.out.println("sql = " + sql);
            JpaUtility.executeNativeSql(sql.toString());
        }
        System.out.println("Schema completion successful");
    }


    @Test
    public void testRequireManufacturer() {
        ReferenceBuilder rbTropicana = new Manufacturer_Tropicana();
        Manufacturer tropicana = (Manufacturer) DOF.require(rbTropicana);
        assertEquals(rbTropicana.getPrimaryKey(), tropicana.getName());
        Manufacturer tropicana2 = (Manufacturer) DOF.require(rbTropicana);
        assertSame(tropicana, tropicana2);
    }

    @Test
    public void testRequireProduct() {
        ReferenceBuilder rbOrangeJuice = new Product_OrangeJuice();
        Product orangeJuice = (Product) DOF.require(rbOrangeJuice);
        org.junit.Assert.assertEquals(rbOrangeJuice.getPrimaryKey(), orangeJuice.getName());
        Product orangeJuice2 = (Product) DOF.require(rbOrangeJuice);
        assertSame(orangeJuice, orangeJuice2);
    }

    @Test
    public void testDeleteManufacturerIfProductDepends()
    {
        // Make sure that product orange juice is created
        ReferenceBuilder product_orangeJuice = new Product_OrangeJuice();
        DOF.require(product_orangeJuice);
        ReferenceBuilder manufacturer_tropicana = new Manufacturer_Tropicana();
        assertFalse("delete of object with dependency did not return false as expected", DOF.delete(manufacturer_tropicana));
        //assertTrue(DOF.delete(product_orangeJuice));
        //assertTrue(DOF.delete(manufacturer_tropicana));
    }


    @Test
    public void testScratchManufacturer()
    {
        ScratchBuilder scratchManufacturer = new ManufacturerScratchBuilder();
        Manufacturer manufacturer1 = (Manufacturer) DOF.createScratchObject(scratchManufacturer);
        Manufacturer manufacturer2 = (Manufacturer) DOF.createScratchObject(scratchManufacturer);
        assertFalse(manufacturer1.getName().equals(manufacturer2.getName()));

        Manufacturer m = (Manufacturer) scratchManufacturer.fetch(manufacturer1.getName());
        assertNotNull(m);
    }


    @Test
    public void testScratchProductCreatesScratchManufacturer()
    {
        ScratchBuilder scratchProduct = new ProductScratchBuilder();
        Product product1 = (Product) DOF.createScratchObject(scratchProduct);
        Product product2 = (Product) DOF.createScratchObject(scratchProduct);
        assertFalse(product1.getName().equals(product2.getName()));
        assertFalse(product1.getManufacturer().getName().equals(product2.getManufacturer().getName()));

        Product p = (Product) scratchProduct.fetch(product1.getName());
        assertNotNull(p);
    }


}