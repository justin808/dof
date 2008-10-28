package org.doframework.sample.component.reference;

import org.doframework.annotation.*;
import org.doframework.sample.component.*;

@TargetClass(Customer.class)

public class Customer_JaneDoe extends CustomerReferenceBuilder
{

    public Object getPrimaryKey()
    {
        return "Jane Doe";
    }



}