package jdbc_component;

import component.*;
import entity.*;

import java.sql.*;

public class JdbcManufacturerComponent implements ManufacturerComponent
{
    private static ManufacturerComponent m_instance = new JdbcManufacturerComponent();

    public JdbcManufacturerComponent()
    {
    }

    public static ManufacturerComponent getInstance()
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
