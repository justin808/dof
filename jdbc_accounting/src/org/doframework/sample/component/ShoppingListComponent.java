package org.doframework.sample.component;

import static org.doframework.sample.global.GlobalContext.*;

/**
 * This class orchestrates actions around the Invoice entity Using class rather than interface because we want only one
 * copy of the business logic.
 *
 * @see org.doframework.sample.component.Invoice
 */
public class ShoppingListComponent
{

    public ShoppingList createNew()
    {
        ShoppingList shoppingList = new ShoppingList(getPersistanceFactory().getShoppingListPersistence().getNextId());
        shoppingList.setNew(true);
        return shoppingList;
    }


    /**
     * Saves the new or updates the existing ShoppingList entity in the persistent store
     *
     * @param shoppingList
     */
    public void persist(ShoppingList shoppingList)
    {
        if (shoppingList.isNew())
        {
            getPersistanceFactory().getShoppingListPersistence().insert(shoppingList);
            shoppingList.setNew(false);
        }
        else
        {
            getPersistanceFactory().getShoppingListPersistence().update(shoppingList);
        }

    }


    public ShoppingListItem addItem(ShoppingList shoppingList, Integer quantity, Product product)
    {
        ShoppingListItem item = new ShoppingListItem(quantity, product);
        shoppingList.getItems().add(item);
        return item;
    }

    /*
     * Get the shoppingList by ID
     *
     * @param id
     *
     * @return the shoppingList or null if the shoppingList does not exist with that ID
     */
    public ShoppingList getById(int id)
    {
        return getPersistanceFactory().getShoppingListPersistence().getById(id);
    }


    public boolean delete(ShoppingList shoppingList)
    {
        return getPersistanceFactory().getShoppingListPersistence().delete(shoppingList);
    }
}