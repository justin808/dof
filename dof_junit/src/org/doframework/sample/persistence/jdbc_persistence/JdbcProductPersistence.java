package org.doframework.sample.persistence.jdbc_persistence;

import org.doframework.sample.component.*;
import org.doframework.sample.persistence.*;
import org.doframework.*;

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


    public Product getByManufacturerNameProductName(String manufacturerName, String productName)
    {
        String sql = "select * from product where name = '" + productName + "' and manufacturer_id = " +
                "(select id from manufacturer where name = '" + manufacturerName + "')";
        //if (DOF.dofDebug)
        //{
        //    System.out.println("sql = " + sql);
        //}
        String[][] rows = JdbcDbUtil.executeMultiColumnQuery(sql);
        if (rows.length == 0)
        {
            return null;
        }
        else
        {
            return getProductFromRow(rows, sql);
        }

    }


    public int countInvoicesWithProductId(int productId)
    {
        String sql = "select count(invoice_id) from line_item li where li.product_id = " + productId;
        return JdbcDbUtil.executeSingleIntQuery(sql);
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
            return getProductFromRow(rows, sql);
        }
    }


    private Product getProductFromRow(String[][] rows, String sql)
    {
        if (rows.length != 1)
        {
            throw new IllegalArgumentException(
                    "Called getProductFromRow with more than one row returned " +
                    "from query: " + sql);
        }


        int productId = Integer.parseInt(rows[0][0]);
        String name = rows[0][1];
        String sPrice = rows[0][2];
        Integer price = new Integer(sPrice);
        String sManufacturerId = rows[0][3];
        int manufacturerId = Integer.parseInt(sManufacturerId);
        ManufacturerComponent manufacturerComponent = ComponentFactory.getManufacturerComponent();
        Manufacturer manufacturer = manufacturerComponent.getById(manufacturerId);
        return new Product(productId, name, price, manufacturer);
    }


    public String getTableName()
    {
        return "product";
    }
}
