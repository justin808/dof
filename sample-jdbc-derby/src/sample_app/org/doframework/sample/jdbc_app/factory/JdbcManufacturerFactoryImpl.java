package org.doframework.sample.jdbc_app.factory;

import java.sql.SQLException;

import org.doframework.sample.jdbc_app.entity.Manufacturer;



public class JdbcManufacturerFactoryImpl implements ManufacturerFactory
{
    private static ManufacturerFactory m_instance = new JdbcManufacturerFactoryImpl();

    public JdbcManufacturerFactoryImpl()
    {
    }

    public static ManufacturerFactory getInstance()
    {
        return m_instance;
    }

    public void insert(Manufacturer manufacturer)
    {
        String sql = "insert into manufacturer values (" + manufacturer.getId() + ", '" + manufacturer
                .getName() + "')";
        JdbcDbUtil.update(sql);
    }

    public boolean delete(Manufacturer manufacturer)
    {
        String deleteIfExists = "delete from manufacturer where id = " + manufacturer.getId();

        try
        {
            int rows = JdbcDbUtil.update(deleteIfExists);
            return (rows > 0);
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

    public Manufacturer get(String manufacturerId)
    {
        String sql = "select * from manufacturer where id = " + manufacturerId;
        String[][] rows = JdbcDbUtil.executeMultiColumnQuery(sql);
        if (rows.length == 0)
        {
            return null;
        }
        else
        {
            int id = Integer.parseInt(rows[0][0]);
            String name = rows[0][1];
            return new Manufacturer(id, name);
        }
    }
}
