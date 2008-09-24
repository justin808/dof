package org.doframework.sample.component;

public class ShoppingListItem
{
    private Integer quantity;
    private Product product;


    public ShoppingListItem(Integer quantity, Product product)
    {
        this.quantity = quantity;
        this.product = product;
    }


    public Integer getQuantity()
    {
        return quantity;
    }


    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }


    public Product getProduct()
    {
        return product;
    }


    public void setProduct(Product product)
    {
        this.product = product;
    }

}