package org.doframework.sample.persistence.jdbc_persistence;

import org.doframework.sample.component.*;
import org.doframework.sample.persistence.*;

import java.sql.*;

public class JdbcProductPersistence extends JdbcBasePersistence implements ProductPersistence
{

    public void insert(Product product)
    {
        String sql = "insert into product values (" + product.getId() + ", '" + product.getName() + "','" + product
                .getPrice() + "'," + product.getManufacturer().getId() + ")";
        JdbcDbUtil.update(sql);
    }


    public void update(Product product)
    {
        String sql = "update product set name = '" + product.getName()
                + ", price = " + product.getPrice()
                + ", manufacturer_id " + product.getManufacturer().getId()
                + " where id = " + product.getId();
        JdbcDbUtil.update(sql);
    }


    public boolean delete(Product product)
    {
        String deleteIfExists = "delete from product where id = " + product.getId();

        try
        {
            JdbcDbUtil.update(deleteIfExists);
            return true;
        }
        catch (Exception e)
        {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException)
            {
                // assumption that this
                return false;
            }
            else
            {
                throw new RuntimeException(e);
            }
        }

    }


    public Product getById(int id)
    {
        String sql = "select * from product where id = " + id;
        String[][] rows = JdbcDbUtil.executeMultiColumnQuery(sql);
        if (rows.length == 0)
        {
            return null;
        }
        else
        {
            String name = rows[0][1];
            String sPrice = rows[0][2];
            Integer price = new Integer(sPrice);
            String sManufacturerId = rows[0][3];
            int manufacturerId = Integer.parseInt(sManufacturerId);
            ManufacturerComponent manufacturerComponent = ComponentFactory.getManufacturerComponent();
            Manufacturer manufacturer = manufacturerComponent.getById(manufacturerId);
            return new Product(id, name, price, manufacturer);
        }
    }


    public String getTableName()
    {
        return "product";
    }
}
