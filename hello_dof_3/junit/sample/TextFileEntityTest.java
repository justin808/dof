package sample;
import org.doframework.*;
import static org.junit.Assert.*;
import org.junit.*;

public class TextFileEntityTest
{
    @BeforeClass
    public static void beforeClass()
    {
        DOFGlobalSettings.setDofDir("hello_dof_2/dof_data");
        //JpaUtility.dropAll();
        checkSchemaCreated();
    }


    private static void checkSchemaCreated()
    {
        if (!JpaUtility.tableExists("MANUFACTURER"))
        {
            JpaUtility.createSchema("hello_dof_2.sql");
        }
    }


    @Test
    public void testRequireManufacturer() {
        Manufacturer lipton = (Manufacturer) DOF.require("manufacturer.Lipton.xml");
        assertEquals("Lipton", lipton.getName());
        Manufacturer lipton2 = (Manufacturer) DOF.require("manufacturer.Lipton.xml");
        assertSame(lipton, lipton2);
    }


    @Test
    public void testRequireProduct() {
        Product liptonTea = (Product) DOF.require("product.Lipton__Tea.xml");
        Assert.assertEquals("Tea", liptonTea.getName());
        Assert.assertEquals("Lipton", liptonTea.getManufacturerByManufacturerId().getName());
        Product liptonTea2 = (Product) DOF.require("product.Lipton__Tea.xml");
        assertSame(liptonTea, liptonTea2);
    }

    //@Test
    //public void testDeleteManufacturerIfProductDepends()
    //{
    //    // Make sure that product orange juice is created
    //    ReferenceBuilder product_orangeJuice = new Product_OrangeJuice();
    //    DOF.require(product_orangeJuice);
    //    ReferenceBuilder manufacturer_tropicana = new Manufacturer_Tropicana();
    //    assertFalse("delete of object with dependency did not return false as expected", DOF.delete(manufacturer_tropicana));
    //    //assertTrue(DOF.delete(product_orangeJuice));
    //    //assertTrue(DOF.delete(manufacturer_tropicana));
    //}
    //
    //
}