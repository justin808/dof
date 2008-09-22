package org.doframework.sample.persistence;

import org.doframework.sample.component.*;

public interface ShoppingListPersistence extends BasePersistence
{

    /**
     * Save the new shoppingList record to the database
     *
     * @param shoppingList
     */
    void insert(ShoppingList shoppingList);


    /**
     * Update the existing shoppingList record in the database
     *
     * @param shoppingList
     */
    void update(ShoppingList shoppingList);


    /**
     * Find the shoppingList with the id
     * @param id
     * @return
     */
    ShoppingList getById(int id);


    boolean delete(ShoppingList shoppingList);
}