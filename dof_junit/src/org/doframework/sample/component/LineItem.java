package org.doframework.sample.component;

public class LineItem
{
    private Integer quantity;
    private Product product;
    private Integer price;


    public LineItem(Integer quantity, Product product, Integer price)
    {
        this.quantity = quantity;
        this.product = product;
        this.price = price;
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


    public Integer getPrice()
    {
        return price;
    }


    public void setPrice(Integer price)
    {
        this.price = price;
    }


    public String toString()
    {
        return "LineItem{" + "quantity=" + quantity + ", product=" + product + ", price=" + price +
               '}';
    }
}
