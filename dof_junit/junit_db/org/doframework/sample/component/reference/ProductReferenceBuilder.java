package org.doframework.sample.component.reference;

import org.doframework.sample.component.*;
import org.doframework.*;

public abstract class ProductReferenceBuilder implements ReferenceBuilder
{
    protected ProductComponent productComponent = ComponentFactory.getProductComponent();


    /**
     * Fetches the object, if it exists, with the given PK. Otherwise null is returned. Typically, this method should be
     * defined in the superclass for the object defined.
     *
     * @return The object created from the db if it existed, or else null */
    public Object fetch()
    {
        return productComponent.getById(((Integer)getPrimaryKey()).intValue());
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


    public Class getCreatedClass()
    {
        return Product.class;
    }

}
