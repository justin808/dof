package org.doframework.sample.jdbc_app.entity;

import java.math.*;


public class Product
{
    private Integer id;
    private String name;
    private BigDecimal price;
    private Manufacturer manufacturer;

    public Product(Integer productId, String name, BigDecimal price, Manufacturer manufacturer)
    {
        id = productId;
        this.name = name;
        this.price = price;
        this.manufacturer = manufacturer;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public Manufacturer getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer)
    {
        this.manufacturer = manufacturer;
    }


}
