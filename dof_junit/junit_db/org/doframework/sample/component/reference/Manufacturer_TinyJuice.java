package org.doframework.sample.component.reference;

import org.doframework.*;
import org.doframework.sample.component.*;
import org.doframework.annotation.*;

/**
 * DO NOT REUSE -- test case in InvoiceJavaTest for delete will fail if this is reused.
 */
@TargetClass(Manufacturer.class)

public class Manufacturer_TinyJuice extends ManufacturerReferenceBuilder implements ReferenceBuilder
{


    /**
     * @return the primary key of this reference dependent object
     */
    public Object getPrimaryKey()
    {
        return "Tiny Juice";
    }




}
