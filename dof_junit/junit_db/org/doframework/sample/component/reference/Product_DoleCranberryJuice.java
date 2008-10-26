package org.doframework.sample.component.reference;

/**
 * DO NOT REUSE -- test case in InvoiceJavaTest for delete will fail if this is reused.
*/
public class Product_DoleCranberryJuice extends ProductReferenceBuilder
{
    public String getProductName()
    {
        return "Cranberry Juice";
    }


    ManufacturerReferenceBuilder getManufacturerReferenceBuilder()
    {
        return new Manufacturer_NewDole();
    }

}
