package org.doframework.sample.persistence;

import org.doframework.sample.component.*;

public interface ManufacturerPersistence extends BasePersistence
{
    void insert(Manufacturer manufacturer);


    boolean delete(Manufacturer manufacturer);


    Manufacturer getByName(String pk);


    void update(Manufacturer manufacturer);


    Manufacturer getById(int id);


    int countProductsWithManufacturerId(int manufacturerId);
}

