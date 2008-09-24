package org.doframework.sample.component.reference;

import org.doframework.*;
import org.doframework.sample.component.*;

abstract class ManufacturerReferenceBuilder implements ReferenceBuilder
{
    ManufacturerComponent manufacturerComponent = ComponentFactory.getManufacturerComponent();


    /**
     * This method defines what other objects need to be created (persisted) before this object is created. Typically,
     * this method should be defined in the superclass for the object defined, and the implementation should use the
     * known foreign keys and the naming pattern to generate the right class objects.
     * <p/>
     * Note, a StaticDependentObject can only depend on other other StaticDependentObjects
     *
     * @return Array of static dependent objects that this object directly depends on.
     */
    public ReferenceBuilder[] getReferenceJavaDependencies()
    {
        return null;
    }


    /**
     * Fetches the object, if it exists, with the given PK. Otherwise null is returned. Typically, this method should be
     * defined in the superclass for the object defined.
     *
     * @return The object created from the db if it existed, or else null */
    public Object fetch()
    {
        return manufacturerComponent.getById((Integer)getPrimaryKey());
    }


    public boolean delete(Object objectToDelete)
    {
        return manufacturerComponent.delete((Manufacturer)objectToDelete);
    }


    public Class getCreatedClass()
    {
        return Manufacturer.class;
    }

}
