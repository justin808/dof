package component;

import entity.*;

import java.util.*;
import java.math.*;

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
