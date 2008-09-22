package org.doframework.sample.component.reference;

import org.doframework.*;
import org.doframework.sample.component.*;

/**
 * DO NOT REUSE -- test case in InvoiceJavaTest for delete will fail if this is reused.
 */
public class Manufacturer_52 extends ManufacturerReferenceBuilder
{

    ManufacturerComponent manufacturerComponent = ComponentFactory.getManufacturerComponent();
    private static final int PRIMARY_KEY = 52;


    /**
     * @return the primary key of this reference dependent object
     */
    public Object getPrimaryKey()
    {
        return new Integer(PRIMARY_KEY);
    }


    /**
     * The implementation of this method must insert the defined object into the DB.
     * <p/>
     *
     * @return An object that was created and saved in the DB
     */
    public Object create()
    {
        Manufacturer manufacturer = new Manufacturer(PRIMARY_KEY, "Manfacturer 52");
        manufacturer.setNew(true);
        manufacturerComponent.persist(manufacturer);
        return manufacturer;
    }

}