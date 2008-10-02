package org.doframework.sample.component.reference;

/**
 * DO NOT REUSE -- test case in InvoiceJavaTest for delete will fail if this is reused.
 */
public class Manufacturer_Pepsi extends ManufacturerReferenceBuilder
{
    public Object getPrimaryKey()
    {
        return "Pepsi";
    }
}