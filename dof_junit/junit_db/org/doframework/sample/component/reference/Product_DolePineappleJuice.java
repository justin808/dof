package org.doframework.sample.component.reference;

/**
 * DO NOT REUSE -- test case in InvoiceJavaTest for delete will fail if this is reused.
*/
public class Product_DolePineappleJuice extends ProductReferenceBuilder
{
    public String getProductName()
    {
        return "Pineapple Juice";
    }


    ManufacturerReferenceBuilder getManufacturerReferenceBuilder()
    {
        return new Manufacturer_Dole();
    }



}