package component;

import entity.*;

public class MockManufacturerComponent implements ManufacturerComponent
{
    public void insert(Manufacturer manufacturer)
    {

    }

    public boolean delete(Manufacturer manufacturer)
    {
        return false;
    }

    public Manufacturer get(String pk)
    {
        return null;
    }
}
