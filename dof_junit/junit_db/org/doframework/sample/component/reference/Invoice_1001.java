package org.doframework.sample.component.reference;

import org.doframework.*;
import org.doframework.sample.component.*;

import java.util.*;

public class Invoice_1001 extends InvoiceBuilder implements HasReferenceTextDependencies
{

    InvoiceComponent invoiceComponent = ComponentFactory.getInvoiceComponent();
    private static final int PRIMARY_KEY = 1001;


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
        Invoice invoice = invoiceComponent.createNew();
        invoice.setInvoiceNumber(PRIMARY_KEY);

        // Get required objects
        Customer customer25 = (Customer) DOF.require("customer.John Smith.xml");
        Product product41 = (Product) DOF.require("product.Drinks One__Coffee.xml");
        Product product42 = (Product) DOF.require("product.Drinks One__Tea.xml");
        Product product43 = (Product) DOF.require("product.Drinks One__Kona Coffee.xml");
        invoice.setCustomer(customer25).setInvoiceDate((new GregorianCalendar(2008, 0, 5)).getTime()); // jan 5, 2008
        invoiceComponent.addLineItem(invoice, 5, product41, product41.getPrice());
        invoiceComponent.addLineItem(invoice, 10, product42, product42.getPrice());
        invoiceComponent.addLineItem(invoice, 12, product43, product43.getPrice());
        invoice.setNew(true);
        invoiceComponent.persist(invoice);
        return invoice;
    }


    /**
     * This method defines what other objects need to be created (persisted) before this object is created.
     * <p/>
     * Note, an ReferenceDependentObject can only depend on other other ReferenceDependentObjects
     *
     * @return Array of static dependent objects that this object directly depends on.
     */
    public ReferenceBuilder[] getReferenceJavaDependencies()
    {
        return null;
    }

    /**
     * Used to mix in text dependencies with Java definitions of dependent objects.
     *
     * @return list of file paths to process before this object is created.
     */
    public String[] getReferenceTextDependencies()
    {
        return new String[]{"customer.25.xml", "product.Drinks One__Coffee.xml","product.Drinks One__Tea.xml","product.Drinks One__Kona Coffee.xml" };
    }


    /**
     * Used to list of scratch objects created by text. We need both the objects and file paths used to create the
     * objects
     *
     * @return Array of ObjectAndFile
     */
    public Object[] getScratchTextDependencies()
    {
        return null;
    }
}