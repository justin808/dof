package org.doframework.sample.component.scratch;

import org.doframework.*;
import org.doframework.sample.component.*;

import java.util.*;

public abstract class InvoiceScratchBuilderBase
{
    InvoiceComponent invoiceComponent = ComponentFactory.getInvoiceComponent();

    /**
     * Fetches the object, if it exists, with the given PK set by setScratchReferenceToPrimaryKey(). Otherwise null is
     * returned. Typically, this method should be defined in the superclass for the object defined and the superclass
     * should call getPrimaryKey to see what object should be retrieved. Note, this method <b>MUST</b> go to the
     * persistent store. It it returns null, then the create method is called.
     *
     * @return The object created from the db if it existed, or else null @param pk
     */
    public Object fetch(Object pk)
    {
        return invoiceComponent.getById(pk instanceof Integer ? (Integer)pk : Integer.parseInt(pk + ""));
    }


    /**
     * Return the primary key for the scratch object
     */
    public Object extractPrimaryKey(Object scratchObject)
    {
        return new Integer(((Invoice) scratchObject).getId());
    }

    public Class getCreatedClass()
    {
        return Invoice.class;
    }


}
