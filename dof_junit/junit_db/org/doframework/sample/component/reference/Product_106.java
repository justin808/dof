package org.doframework.sample.component.reference;

import org.doframework.*;
import org.doframework.sample.component.*;

/**
 * DO NOT REUSE -- test case in InvoiceJavaTest for delete will fail if this is reused.
*/
public class Product_106 extends ProductReferenceBuilder
{
    private static final int PRIMARY_KEY = 106;


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
        Manufacturer manufacturer51 = (Manufacturer) DOF.require(new Manufacturer_51());
        Product product = new Product(PRIMARY_KEY, "Acai Juice", 8, manufacturer51);
        product.setNew(true);
        productComponent.persist(product);
        return product;
    }


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
        return new ReferenceBuilder[]{new Manufacturer_50()};
    }


}