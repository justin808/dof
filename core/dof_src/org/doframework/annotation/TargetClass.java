/*
* IBM Confidential
* OCO Source Materials
* 5724-I68.
* (C) Copyright IBM Corp. 2000, 2004 - All Rights Reserved.
* US Government Users Restricted Rights - Use, duplication or disclosure
* restricted by GSA ADP Schedule Contract with IBM Corp.
*/
package org.doframework.annotation;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Defines the class that this DOF helper will be creating. This information is needed for caching
 * and deletion. This can be defined in a superclass
 */
@Target({TYPE}) @Retention(RUNTIME)
public @interface TargetClass
{
    /**
     * @return The class object for the class that this helper is designed for.
     */
    public abstract Class value();

}