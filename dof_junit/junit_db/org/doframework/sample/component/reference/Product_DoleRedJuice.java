package org.doframework.sample.component.reference;

/**
 * DO NOT REUSE -- test case in InvoiceXMLFactoryTest for delete will fail if this is reused.
 */
public class Product_DoleRedJuice extends ProductReferenceBuilder
{
    public String getProductName()
    {
        return "Red Juice";
    }


    ManufacturerReferenceBuilder getManufacturerReferenceBuilder()
    {
        return new Manufacturer_NewDole();
    }


}