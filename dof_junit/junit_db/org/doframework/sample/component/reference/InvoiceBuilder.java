package org.doframework.sample.component.reference;

import org.doframework.sample.component.*;
import org.doframework.*;

public abstract class InvoiceBuilder implements ReferenceBuilder
{
    InvoiceComponent invoiceComponent = ComponentFactory.getInvoiceComponent();


    /**
     * Fetches the object, if it exists, with the given PK. Otherwise null is returned. Typically, this method should be
     * defined in the superclass for the object defined.
     *
     * @return The object created from the db if it existed, or else null */
    public Object fetch()
    {
        return invoiceComponent.getById((Integer)getPrimaryKey());
    }


    public boolean delete(Object objectToDelete)
    {
        return invoiceComponent.delete((Invoice) objectToDelete);
    }


    public Class getCreatedClass()
    {
        return Invoice.class;
    }

}
