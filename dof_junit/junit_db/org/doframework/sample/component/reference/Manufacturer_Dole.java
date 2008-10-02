package org.doframework.sample.component.reference;

/**
 * DO NOT REUSE -- test case in InvoiceJavaTest for delete will fail if this is reused.
 */
public class Manufacturer_Dole extends ManufacturerReferenceBuilder
{
    public Object getPrimaryKey()
    {
        return "Dole";
    }
}