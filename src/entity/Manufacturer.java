package entity;

import javax.persistence.*;

@Entity @Table(name = "MANUFACTURER")

public class Manufacturer
{
    private int id;
    private String name;


    public Manufacturer()
    {
    }

    public Manufacturer(int id, String name)
    {
        setId(id);
        setName(name);
    }

    @Id
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


    public void setId(Integer id)
    {
        this.id = id;
    }
}
