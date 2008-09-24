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
public class ShoppingListXmlFactoryTest
{

    ShoppingListComponent shoppingListComponent =ComponentFactory.getShoppingListComponent();


    @BeforeClass
    public static void beforeTests()
    {
        GlobalContext.setPersistanceFactory(new JdbcPersistenceFactory());
    }


    @Test
    public void testCreateInvoiceAndShoppingListAndDeleteAll()
    {
        ShoppingList sl = (ShoppingList) DOF.require("shopping_list.100.xml");
        assertNotNull(sl);
    }



}