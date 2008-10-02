package org.doframework.sample.component;

public class Product extends Entity
{
    private String name;
    private Integer price;
    private Manufacturer manufacturer;


    public Product(int id)
    {
        super(id);
    }


    public Product(Integer productId, String name, Integer price, Manufacturer manufacturer)
    {
        super(productId);
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


    public Integer getPrice()
    {
        return price;
    }


    public void setPrice(Integer price)
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


    public String toString()
    {
        return "Product{" + "name='" + name + '\'' + ", price=" + price + ", manufacturer=" +
               manufacturer + '}';
    }
}
