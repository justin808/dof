package org.doframework.sample.component.scratch;

import org.doframework.*;
import org.doframework.sample.component.*;
import org.doframework.sample.global.*;

import java.util.*;

public class Product_Scratch implements DeletableScratchBuilder
{

    //Product productLastCreated;
    //DeletableScratchBuilder[] m_dependencies;
    private Map<String, String> scratchReferenceToPrimaryKey;


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



    ///**
    // * This method defines what other objects need to be created (persisted) before this object is created.
    // * <p/>
    // * Note, an ReferenceDependentObject can only depend on other other ReferenceDependentObjects
    // *
    // * @return Array of static dependent objects that this object directly depends on.
    // */
    //public ReferenceBuilder[] getReferenceJavaBuilderDependencies()
    //{
    //    return null;
    //}


    ///**
    // * @return The object that was created with this DOF Scratch Builder instance
    // */
    //public Object getCreatedObject()
    //{
    //    return productLastCreated;
    //}


    ///**
    // * The mutable object must keep track of what was created and what dependencies there were.
    // *
    // * @return list of mutable objects that the created object directly created
    // */
    //public DeletableScratchBuilder[] getScratchJavaBuilderDependencies()
    //{
    //    return m_dependencies;
    //}


    //public boolean deleteCreatedObject()
    //{
    //    if (productLastCreated != null)
    //    {
    //        boolean deleted = ComponentFactory.getProductComponent().delete(productLastCreated);
    //        if (deleted)
    //        {
    //            productLastCreated = null;
    //            return true;
    //        } // else return false
    //    }
    //    return false;
    //}



    /**
     * This method will get called immediately after your scratch object builder is constructed to
     * set the mapping of "scratch reference" to "primary key". The purpose of having this map is to
     * provide a means to allow two scratch objects to depend on the same parent scratch object.
     * <p/>
     * For example, you might have two scratch invoices depend on the same parent scratch customer.
     * In that example, you would create the first scratch invoice with its corresponding scratch
     * customer. Then you would create a map, with a key being something like "customer" (the
     * scratch reference) and the value being whatever uniquely defines the customer. The
     * ScratchBuilder for the customer will use that value instead of a new primary key.
     * <p/>
     * Another option is that you may want to pre-specify the primary key that is used to create the
     * scratch object. The key value of ScratchBuilder.PK ("pk") is reserved for this purpose.
     * Otherwise, since your implementation will be doing the processing, you are free to choose how
     * you use the map's keys and values.
     *
     * @param scratchReferenceToPrimaryKey The mapping of scratch tag to primary key.
     */
    public void setScratchReferenceToPrimaryKey(Map<String, String> scratchReferenceToPrimaryKey)
    {
        this.scratchReferenceToPrimaryKey = scratchReferenceToPrimaryKey;
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
        return new Integer(((Product) scratchObject).getId());
    }


    public Class getCreatedClass()
    {
        return Product.class;
    }

}