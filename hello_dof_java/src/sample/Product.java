package sample;

import javax.persistence.*;

@Entity @Table(name = "PRODUCT") public class Product
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


    private int m_price;


    @Basic @Column(name = "PRICE", length = 10) public int getPrice()
    {
        return m_price;
    }


    public void setPrice(int price)
    {
        m_price = price;
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

        Product product = (Product) o;

        if (m_id != product.m_id)
        {
            return false;
        }
        if (m_price != product.m_price)
        {
            return false;
        }
        if (m_name != null ? !m_name.equals(product.m_name) : product.m_name != null)
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
        result = 31 * result + m_price;
        return result;
    }


    private Manufacturer m_manufacturer;


    @ManyToOne
    @JoinColumn(name = "MANUFACTURER_ID", referencedColumnName = "ID")
    public Manufacturer getManufacturer()
    {
        return m_manufacturer;
    }


    public void setManufacturer(Manufacturer manufacturer)
    {
        m_manufacturer = manufacturer;
    }
}
