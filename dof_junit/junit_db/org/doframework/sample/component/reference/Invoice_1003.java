package org.doframework.sample.component.reference;

import org.doframework.*;
import org.doframework.annotation.*;
import org.doframework.sample.component.*;

import java.util.*;

@TargetClass(Invoice.class)

public class Invoice_1003 extends InvoiceBuilder
{

    private static final int PRIMARY_KEY = 1003;


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
        Customer customer35 = (Customer) DOF.require(new Customer_JohnAdams());
        Product product105 = (Product) DOF.require(new Product_DoleGreenJuice());
        Product product106 = (Product) DOF.require(new Product_DoleRedJuice());

        invoice.setCustomer(customer35).setInvoiceDate((new GregorianCalendar(2008, 0, 5)).getTime()); // jan 5, 2008
        invoiceComponent.addLineItem(invoice, 5, product105, product105.getPrice());
        invoiceComponent.addLineItem(invoice, 10, product106, product106.getPrice());
        invoice.setNew(true);

        invoiceComponent.persist(invoice);
        return invoice;
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
        return new ReferenceBuilder[]{new Customer_JohnAdams(),
                new Product_DoleGreenJuice(),
                new Product_DoleRedJuice()};

    }


}