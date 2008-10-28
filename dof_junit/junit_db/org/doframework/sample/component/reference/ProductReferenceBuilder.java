package org.doframework.sample.component.reference;

import org.doframework.sample.component.*;
import org.doframework.*;
import org.doframework.annotation.*;

@TargetClass(Product.class)

public abstract class ProductReferenceBuilder implements ReferenceBuilder
{
    protected ProductComponent productComponent = ComponentFactory.getProductComponent();


    public abstract String getProductName();


    /**
     * Return the logical primary key (unique key for reference purposes) out of the reference
     * object to be used for hash key of cached  objects. The value can be any unique value, such as
     * a unique field, or a combination of fields that is unique.
     * @return the unique key to be used for caching.
     */
    public Object getPrimaryKey()
    {
        return getManufacturerReferenceBuilder().getPrimaryKey() + "__" + getProductName();
    }


    /**
     * Fetches the object, if it exists, with the given PK. Otherwise null is returned. Typically, this method should be
     * defined in the superclass for the object defined.
     *
     * @return The object created from the db if it existed, or else null */
    public Object fetch()
    {
        return productComponent
                .getByManufacturerAndName((String)getManufacturerReferenceBuilder().getPrimaryKey(),
                                          getProductName());
    }


    /**
     * Delete the object passed. Note that the framework will automatically try to delete the object's dependencies as
     * well in a breadth first manner. It is CRITICAL that this method not delete the requested object and return false
     * if there are any existing dependencies upon this object. For example, if this is a request to delete a customer
     * record and invoices depend upon this customer record, it must simply return false.<p/> Typically, this method
     * should be defined in the superclass for the object defined.
     * <p/>
     * This method is passed the objectToDelete as a convenience as many systems will use that object as part of the
     * deletion code.
     *
     * @param objectToDelete Object requested for deletion, never null
     *
     * @return true if requested object is deleted.
     */
    public boolean delete(Object objectToDelete)
    {
        return productComponent.delete((Product) objectToDelete);
    }




    abstract ManufacturerReferenceBuilder getManufacturerReferenceBuilder();


    public Object create()
    {
        ManufacturerReferenceBuilder manufacturerReferenceBuilder = getManufacturerReferenceBuilder();
        Manufacturer manufacturer = (Manufacturer) DOF.require(manufacturerReferenceBuilder);
        Product product = productComponent.createNew();
        product.setName((String) getProductName());
        product.setManufacturer(manufacturer);
        product.setPrice(3);
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
        return new ReferenceBuilder[]{getManufacturerReferenceBuilder()};
    }
}
