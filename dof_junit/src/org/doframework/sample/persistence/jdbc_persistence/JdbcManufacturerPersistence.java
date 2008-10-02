package org.doframework.sample.persistence.jdbc_persistence;

import org.doframework.sample.component.*;
import org.doframework.sample.persistence.*;

import java.sql.*;

public class JdbcManufacturerPersistence extends JdbcBasePersistence implements ManufacturerPersistence
{


    public Manufacturer getById(int id)
    {
        String sql = "select * from manufacturer where id = " + id;
        String[][] rows = JdbcDbUtil.executeMultiColumnQuery(sql);
        if (rows.length == 0)
        {
            return null;
        }
        else
        {
            int idFromDb = Integer.parseInt(rows[0][0]);
            String name = rows[0][1];
            return new Manufacturer(idFromDb, name);
        }
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
            return
            (rows > 0);
        }
        catch (Exception e)
        {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException)
            {
                // assumption that this is OK
                return false;
            }
            else
            {
                throw new RuntimeException(e);
            }
        }

    }


    public Manufacturer getByName(String manufacturerId)
    {
        String sql = "select * from manufacturer where name = '" + manufacturerId + "'";
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


    public void update(Manufacturer manufacturer)
    {
        // TODO
    }


    public String getTableName()
    {
        return "manufacturer";
    }
}

