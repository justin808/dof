package org.doframework.sample.component;

import java.util.*;

/**
 * Simple Java Bean class. No business logic in this class.
 *
 * @see InvoiceComponent
 */
public class ShoppingList extends Entity
{
    private Customer customer;
    private String name;
    private List<ShoppingListItem> items = new ArrayList<ShoppingListItem>();


    public ShoppingList(int id)
    {
        super(id);
    }


    public Customer getCustomer()
    {
        return customer;
    }


    public ShoppingList setCustomer(Customer customer)
    {
        this.customer = customer;
        return this;
    }

    public String getName()
    {
        return name;
    }


    public ShoppingList setName(String name)
    {
        this.name = name;
        return this;
    }



    public List<ShoppingListItem> getItems()
    {
        return this.items;
    }


    public ShoppingList setItems(List<ShoppingListItem> items)
    {
        this.items = items;
        return this;
    }





}