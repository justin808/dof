package org.doframework.sample.jdbc_app.entity;

import java.math.*;


public class LineItem
{
    private BigDecimal quantity;
    private Product product;
    private BigDecimal price;

    public LineItem(BigDecimal quantity, Product product, BigDecimal price)
    {
        this.quantity = quantity;
        this.product = product;
        this.price = price;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity)
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

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }
}
