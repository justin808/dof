package org.doframework.sample.component.reference;

import org.doframework.*;
import org.doframework.sample.component.*;

public class Customer_JohnDoe extends CustomerReferenceBuilder implements ReferenceBuilder
{

    public Object getPrimaryKey()
    {
        return "John Doe";
    }


}
