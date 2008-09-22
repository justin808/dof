package org.doframework.sample.component.scratch;

import org.doframework.*;
import org.doframework.sample.component.*;
import org.doframework.sample.component.reference.*;

import java.util.*;

public class Invoice__ScratchText extends InvoiceScratchBuilderBase implements ScratchBuilder
{
    InvoiceComponent invoiceComponent = ComponentFactory.getInvoiceComponent();

    /**
     * The implementation of this method must insert the defined object into the DB.
     * <p/>
     * Note, for dependencies, it is better to use DOF.requireReference(ReferenceDependentObject) than to get the object
     * on your own, as the object will be cached right before create is called.
     *
     * @return An object that was created and saved in the DB
     * @param scratchReferenceToPrimaryKey
     */
    public Object create(Map scratchReferenceToPrimaryKey)
    {
        Invoice invoice = invoiceComponent.createNew();

        Customer customer = (Customer) DOF.require("customer.55.xml");
        Product product31 = (Product) DOF.require("product.30.xml");
        Product product32 = (Product)DOF.require("product.31.xml");

        invoice.setCustomer(customer).setInvoiceDate((new GregorianCalendar(2008, 0, 5)).getTime()); // jan 5, 2008
        invoiceComponent.addLineItem(invoice, 6, product31, product31.getPrice());
        invoiceComponent.addLineItem(invoice, 11, product32, product32.getPrice());
        invoice.setNew(true);

        invoiceComponent.persist(invoice);
        return invoice;
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
        return ComponentFactory.getInvoiceComponent().getById((Integer)pk);
    }


    /**
     * Return the primary key for the scratch object
     */
    public Object extractPrimaryKey(Object scratchObject)
    {
        return new Integer(((Invoice) scratchObject).getId());
    }
}