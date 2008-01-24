package component;

import entity.*;


public interface ManufacturerComponent
{
    void insert(Manufacturer manufacturer);

    boolean delete(Manufacturer manufacturer);

    Manufacturer get(String pk);
}
