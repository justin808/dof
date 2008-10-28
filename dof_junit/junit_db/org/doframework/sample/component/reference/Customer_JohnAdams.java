package org.doframework.sample.component.reference;

import org.doframework.*;
import org.doframework.annotation.*;
import org.doframework.sample.component.*;


public class Customer_JohnAdams extends CustomerReferenceBuilder implements ReferenceBuilder
{

    public Object getPrimaryKey()
    {
        return "John Adams";
    }


}