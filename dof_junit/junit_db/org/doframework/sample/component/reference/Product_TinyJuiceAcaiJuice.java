package org.doframework.sample.component.reference;

import org.doframework.annotation.*;
import org.doframework.sample.component.*;

/**
 * DO NOT REUSE -- test case in InvoiceJavaTest for delete will fail if this is reused.
*/
@TargetClass(Product.class)

public class Product_TinyJuiceAcaiJuice extends ProductReferenceBuilder
{

    public String getProductName()
    {
        return "Acai Juice";
    }


    ManufacturerReferenceBuilder getManufacturerReferenceBuilder()
    {
        return new Manufacturer_TinyJuice();
    }
}