package component;

import entity.*;

public class MockProductComponent implements ProductComponent
{

    public void insert(Product product)
    {

    }

    public boolean delete(Product product)
    {
        return false;
    }

    public Product getById(int id)
    {
        return null;
    }
}
