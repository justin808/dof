package org.doframework.sample.component.reference;

/**
 * DO NOT REUSE -- test case in InvoiceXMLFactoryTest for delete will fail if this is reused.
 */
public class Product_DoleGreenJuice extends ProductReferenceBuilder
{
    public String getProductName()
    {
        return "Green Juice";
    }


    ManufacturerReferenceBuilder getManufacturerReferenceBuilder()
    {
        return new Manufacturer_NewDole();
    }


}