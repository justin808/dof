package org.doframework.sample.component;

import org.doframework.sample.global.*;

public class ManufacturerComponent
{
    public Manufacturer getById(int id)
    {
        return GlobalContext.getPersistanceFactory().getManufacturerPersistence().getById(id);
    }


    public Manufacturer createNew()
    {
        Manufacturer manufacturer = new Manufacturer(GlobalContext.getPersistanceFactory().getManufacturerPersistence().getNextId());
        manufacturer.setNew(true);
        return manufacturer;
    }


    public void persist(Manufacturer manufacturer)
    {
        if (manufacturer.isNew())
        {
            GlobalContext.getPersistanceFactory().getManufacturerPersistence().insert(manufacturer);
            manufacturer.setNew(false);
        }
        else
        {
            GlobalContext.getPersistanceFactory().getManufacturerPersistence().update(manufacturer);

        }

    }



    /**
     * Delete the object with the given pk.
     * <p/>
     * It is critical that this method either check dependencies (products that depend on this record) or depend on
     * getting the sql exception from integrity checks.
     *
     * @param manufacturer The manufacturer object to delete
     */
    public boolean delete(Manufacturer manufacturer)
    {
        return GlobalContext.getPersistanceFactory().getManufacturerPersistence().delete(manufacturer);
    }


    public Manufacturer getByName(String name)
    {
        return GlobalContext.getPersistanceFactory().getManufacturerPersistence().getByName(name);
    }


    public Manufacturer get(int pk)
    {
        return GlobalContext.getPersistanceFactory().getManufacturerPersistence().getById(pk);
    }
}
