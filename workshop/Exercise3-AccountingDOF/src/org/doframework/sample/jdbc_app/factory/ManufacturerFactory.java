package org.doframework.sample.jdbc_app.factory;

import org.doframework.sample.jdbc_app.entity.Manufacturer;


public interface ManufacturerFactory
{
    void insert(Manufacturer manufacturer);

    boolean delete(Manufacturer manufacturer);

    Manufacturer get(String pk);
}
