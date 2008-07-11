package org.doframework.sample.jdbc_app.factory;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.entity.Manufacturer;
import org.doframework.sample.jdbc_app.entity.Product;



public class JdbcProductFactoryImpl implements ProductFactory
{
    public JdbcProductFactoryImpl()
    {
    }

    public void insert(Product product)
    {
        String sql = "insert into product values (" + product.getId() + ", '"
                     + product.getName() + "'," + product
                .getPrice() + "," + product.getManufacturer().getId() + ")";
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
            BigDecimal price = new BigDecimal(sPrice);
            String sManufacturerId = rows[0][3];
            int manufacturerId = Integer.parseInt(sManufacturerId);
            ManufacturerFactory manufacturerFactory =
                    GlobalContext.getFactoryLookupService().getManufacturerFactory();
            Manufacturer manufacturer = manufacturerFactory.get(manufacturerId + "");
            return new Product(id, name, price, manufacturer);
        }
    }
}
