package org.doframework.sample.component.scratch;

import org.doframework.*;
import org.doframework.sample.component.*;

import java.util.*;


/**
 * Simple example of depending on Java References
 */
public class Invoice__ScratchJavaScratchTextDependencies extends InvoiceScratchBuilderBase
        implements ScratchBuilder
{


    /**
     * The implementation of this method must insert the defined object into the DB.
     * <p/>
     * Note, for dependencies, it is better to use DOF.requireReference(ReferenceDependentObject)
     * than to get the object on your own, as the object will be cached right before create is
     * called.
     *
     * @return An object that was created and saved in the DB
     * @param scratchReferenceToPrimaryKey
     */
    public Object create(Map scratchReferenceToPrimaryKey)
    {
        Invoice invoice = invoiceComponent.createNew();

        Customer customer = (Customer) DOF.createScratchObject("customer.scratch.xml");
        Product productS1 = (Product) DOF.createScratchObject("product.scratch.xml");
        Product productS2 = (Product) DOF.createScratchObject("product.scratch.xml");
        invoice.setCustomer(customer)
                .setInvoiceDate((new GregorianCalendar(2008, 0, 5)).getTime()); // jan 5, 2008
        invoiceComponent.addLineItem(invoice, 6, productS1, productS1.getPrice());
        invoiceComponent.addLineItem(invoice, 11, productS2, productS2.getPrice());
        invoice.setNew(true);

        invoiceComponent.persist(invoice);
        return invoice;
    }



    /**
     * Used to mix in text dependencies with Java definitions of dependent objects.
     *
     * @return list of file paths to process before this object is created.
     */
    public String[] getReferenceTextDependencies()
    {
        return null;
    }


}