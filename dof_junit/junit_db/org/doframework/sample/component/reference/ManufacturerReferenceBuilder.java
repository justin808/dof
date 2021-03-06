package org.doframework.sample.component.reference;

import org.doframework.*;
import org.doframework.annotation.*;
import org.doframework.sample.component.*;

@TargetClass(Manufacturer.class)
abstract class ManufacturerReferenceBuilder implements ReferenceBuilder
{
    ManufacturerComponent manufacturerComponent = ComponentFactory.getManufacturerComponent();


    public Object fetch()
    {
        return manufacturerComponent.getByName((String) getPrimaryKey());
    }

    public Object create()
    {
        Manufacturer manufacturer = manufacturerComponent.createNew();
        manufacturer.setName((String) getPrimaryKey());
        manufacturerComponent.persist(manufacturer);
        return manufacturer;
    }

    public boolean delete(Object objectToDelete)
    {
        return manufacturerComponent.delete((Manufacturer) objectToDelete);
    }

    public ReferenceBuilder[] getReferenceJavaDependencies()
    {
        return null;
    }

}
