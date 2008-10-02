package org.doframework.sample.xml_handler;

import org.doframework.*;
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






    @Test (expected = RuntimeException.class)
    public void testProductPersistDependencyDoesNotExist()
    {
        ProductXmlHandler pxf = new ProductXmlHandler();
        Product product = (Product) DOF.createScratchObject("product.scratchdependsOnManu39.xml");
        Manufacturer notYetPersistedManufacturer = new Manufacturer(-1, "dummy");
        product.setManufacturer(notYetPersistedManufacturer);
        productComponent.persist(product);
    }





}
