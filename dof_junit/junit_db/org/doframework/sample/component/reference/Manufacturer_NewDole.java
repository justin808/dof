package org.doframework.sample.component.reference;

/**
 * DO NOT REUSE -- test case in InvoiceJavaTest for delete will fail if this is reused. Used by Invoice_500
 */
public class Manufacturer_NewDole extends ManufacturerReferenceBuilder
{
    public Object getPrimaryKey()
    {
        return "New Dole";
    }
}