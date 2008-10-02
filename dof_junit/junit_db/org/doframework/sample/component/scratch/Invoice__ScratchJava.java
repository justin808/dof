package org.doframework.sample.component.scratch;

import org.doframework.*;
import org.doframework.sample.component.*;
import org.doframework.sample.component.reference.*;

import java.util.*;


/**
 * Simple example of scratch object depending on Java reference objects
 */
public class Invoice__ScratchJava extends InvoiceScratchBuilderBase implements ScratchBuilder
{

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
        invoice.setInvoiceNumber(invoiceComponent.getNextInvoiceNumber());

        Customer customer36 = (Customer) DOF.require(new Customer_JaneDoe());
        Product product103 = (Product) DOF.require(new Product_TinyJuiceAppleJuice());
        Product product104 = (Product) DOF.require(new Product_TinyJuiceBlueberryJuice());
        invoice.setCustomer(customer36).setInvoiceDate((new GregorianCalendar(2008, 0, 5)).getTime()); // jan 5, 2008
        invoiceComponent.addLineItem(invoice, 6, product103, product103.getPrice());
        invoiceComponent.addLineItem(invoice, 11, product104, product104.getPrice());
        invoice.setNew(true);

        invoiceComponent.persist(invoice);
        return invoice;
    }



}
