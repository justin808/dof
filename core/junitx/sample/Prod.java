package sample;

public class Prod
{
    private int m_id;


    public int getId()
    {
        return m_id;
    }


    public void setId(int id)
    {
        m_id = id;
    }


    private String m_name;


    public String getName()
    {
        return m_name;
    }


    public void setName(String name)
    {
        m_name = name;
    }


    private int m_price;


    public int getPrice()
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

        Prod product = (Prod) o;

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
        result = m_id;
        result = 31 * result + (m_name != null ? m_name.hashCode() : 0);
        result = 31 * result + m_price;
        return result;
    }


    private Manu m_manufacturerByManufacturerId;


    public Manu getManufacturerByManufacturerId()
    {
        return m_manufacturerByManufacturerId;
    }


    public void setManufacturerByManufacturerId(Manu manufacturerByManufacturerId)
    {
        m_manufacturerByManufacturerId = manufacturerByManufacturerId;
    }
}