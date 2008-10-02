package org.doframework.sample.component.scratch;

import org.doframework.*;
import org.doframework.sample.component.*;
import org.doframework.sample.global.*;

import java.util.*;

public class Product_Scratch implements ScratchBuilder
{

    /**
     * The implementation of this method must insert the defined object into the DB.
     * <p/>
     *
     * @return An object that was created and saved in the DB
     * @param scratchReferenceToPrimaryKey
     */
    public Object create(Map scratchReferenceToPrimaryKey)
    {
        ProductComponent productComponent = ComponentFactory.getProductComponent();
        int id = GlobalContext.getPersistanceFactory().getProductPersistence().getNextId();
        Manufacturer_Scratch manufacturerScratch = new Manufacturer_Scratch();
        Manufacturer scratchManufacturer = (Manufacturer) DOF.createScratchObject(manufacturerScratch);
        Product product = new Product(id, "Grape Juice " + id, 10, scratchManufacturer);
        product.setNew(true);
        productComponent.persist(product);
        //productLastCreated = product;
        //m_dependencies = new DeletableScratchBuilder[]{manufacturerScratch};
        return product;
    }




    /**
     * Fetches the object, if it exists, with the given PK. Otherwise null is returned.
     * <p/>
     * This method is only called if ScratchBuilder.PK was defined in the map passed into this
     * object. Typically, this method should be defined in the superclass for the object defined and
     * the superclass should call getPrimaryKey to see what object should be retrieved. Note, this
     * method <b>MUST</b> go to the persistent store. It it returns null, then the create method is
     * called.
     *
     * @return The object created from the db if it existed, or else null @param pk
     */
    public Object fetch(Object pk)
    {
        return ComponentFactory.getProductComponent().getById(
                pk instanceof Integer ? (Integer) pk : Integer.parseInt(pk + ""));

    }


    /**
     * Return the primary key for the scratch object
     */
    public Object extractPrimaryKey(Object scratchObject)
    {
        Product product = (Product) scratchObject;
        return product.getManufacturer().getName() + "__" + product.getName();
    }


    public Class getCreatedClass()
    {
        return Product.class;
    }

}