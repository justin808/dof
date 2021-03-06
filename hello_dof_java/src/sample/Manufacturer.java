package sample;

import javax.persistence.*;
import java.util.*;

@Entity @Table(name = "MANUFACTURER") public class Manufacturer
{
    private long m_id;


    @Id @Column(name = "ID", nullable = false, length = 19)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId()
    {
        return m_id;
    }


    public void setId(long id)
    {
        m_id = id;
    }


    private String m_name;


    @Basic @Column(name = "NAME", length = 100) public String getName()
    {
        return m_name;
    }


    public void setName(String name)
    {
        m_name = name;
    }


    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Manufacturer that = (Manufacturer) o;

        if (m_id != that.m_id)
        {
            return false;
        }
        if (m_name != null ? !m_name.equals(that.m_name) : that.m_name != null)
        {
            return false;
        }

        return true;
    }


    public int hashCode()
    {
        int result;
        result = (int) (m_id ^ (m_id >>> 32));
        result = 31 * result + (m_name != null ? m_name.hashCode() : 0);
        return result;
    }


    private Collection<Product> m_productsById;


    @OneToMany(mappedBy = "manufacturer") public Collection<Product> getProductsById()
    {
        return m_productsById;
    }


    public void setProductsById(Collection<Product> productsById)
    {
        m_productsById = productsById;
    }
}
