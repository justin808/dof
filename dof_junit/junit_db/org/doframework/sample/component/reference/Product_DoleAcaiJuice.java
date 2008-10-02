package org.doframework.sample.component.reference;

/**
 * DO NOT REUSE -- test case in InvoiceXMLFactoryTest for delete will fail if this is reused.
 */
public class Product_DoleAcaiJuice extends ProductReferenceBuilder
{
    public String getProductName()
    {
        return "Acai Juice";
    }


    ManufacturerReferenceBuilder getManufacturerReferenceBuilder()
    {
        return new Manufacturer_Dole();
    }


}