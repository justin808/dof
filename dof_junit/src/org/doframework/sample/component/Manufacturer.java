package org.doframework.sample.component;


public class Manufacturer extends Entity
{
    private String name;


    public Manufacturer(int id)
    {
        super(id);
        setNew(true);
    }


    public Manufacturer(int id, String name)
    {
        super(id);
        setName(name);
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public int hashCode()
    {
        return (id + ":" + name).hashCode();


    }


    public boolean equals(Object obj)
    {
        if (! (obj instanceof Manufacturer)) return false;

        Manufacturer other = (Manufacturer) obj;
        return other.id == id && other.name.equals(name);
    }


    public String toString()
    {
        return "Manufacturer{" + "name='" + name + '\'' + '}';
    }
}
