package org.doframework.sample.component.reference;

/**
 * DO NOT REUSE -- test case in InvoiceJavaTest for delete will fail if this is reused.
 */
public class Product_TinyJuiceUnfilteredAppleJuice extends ProductReferenceBuilder
{
    public String getProductName()
    {
        return "Unfiltered Apple Juice";
    }


    ManufacturerReferenceBuilder getManufacturerReferenceBuilder()
    {
        return new Manufacturer_TinyJuice();
    }


}