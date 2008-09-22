package org.doframework.sample.component.scratch;

import org.doframework.*;
import org.doframework.sample.component.*;

import java.util.*;


/**
 * Simple example of depending on Java References
 */
public class Invoice__ScratchJavaScratchDependencies extends InvoiceScratchBuilderBase
        implements ScratchBuilder
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

        Customer customer = (Customer) scratchReferenceToPrimaryKey.get("scratchCustomer");
        if (customer == null)
        {
            Object customerPk = scratchReferenceToPrimaryKey.get("customerPk");
            if (customerPk != null)
            {
                customer = (Customer) DOF.getCachedObject(Customer.class, customerPk);
            }
        }

        if (customer == null)
        {
            Customer_Scratch customer_scratch = new Customer_Scratch();
            customer = (Customer) DOF.createScratchObject(customer_scratch);
        }
        invoice.setCustomer(customer).setInvoiceDate((new GregorianCalendar(2008, 0, 5)).getTime()); // jan 5, 2008
        Product_Scratch productScratchBuilder1 = new Product_Scratch();
        Product productScratch1 = (Product) DOF.createScratchObject(productScratchBuilder1);
        invoiceComponent.addLineItem(invoice, 6, productScratch1, productScratch1.getPrice());
        Product_Scratch productScratchBuilder2 = new Product_Scratch();
        Product productScratch2 = (Product) DOF.createScratchObject(productScratchBuilder2);
        invoiceComponent.addLineItem(invoice, 6, productScratch2, productScratch2.getPrice());
        invoice.setNew(true);

        invoiceComponent.persist(invoice);
        return invoice;
    }




}