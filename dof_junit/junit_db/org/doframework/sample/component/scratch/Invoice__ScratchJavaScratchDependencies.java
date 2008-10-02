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

    public Object create(Map scratchReferenceToPrimaryKey)
    {
        Invoice invoice = invoiceComponent.createNew();
        invoice.setInvoiceNumber(invoiceComponent.getNextInvoiceNumber());

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
            customer = (Customer) DOF.createScratchObject(new Customer_Scratch());
        }
        invoice.setCustomer(customer).setInvoiceDate((new GregorianCalendar(2008, 0, 5)).getTime()); // jan 5, 2008
        Product_Scratch productScratchBuilder1 = new Product_Scratch();
        Product productScratch1 = (Product) DOF.createScratchObject(productScratchBuilder1);
        invoiceComponent.addLineItem(invoice, 6, productScratch1, productScratch1.getPrice());
        Product_Scratch productScratchBuilder2 = new Product_Scratch();
        Product productScratch2 = (Product) DOF.createScratchObject(productScratchBuilder2);
        invoiceComponent.addLineItem(invoice, 6, productScratch2, productScratch2.getPrice());
        invoiceComponent.persist(invoice);
        return invoice;
    }




}