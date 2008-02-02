package component;

import entity.*;

import java.util.*;
import java.math.*;

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
