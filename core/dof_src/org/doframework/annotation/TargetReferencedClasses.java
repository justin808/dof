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
 * This annotation is used for deletion of reference classes. It may be defined in a super class of
 * a RefererenceBuilder implementation.
 * @see org.doframework.ReferenceBuilder
 * @see org.doframework.DeletionHelper
 */
@Target({TYPE}) @Retention(RUNTIME)
public @interface TargetReferencedClasses
{

    /**
     * Information on what parent dependency classes this object has are used to for operation
     * DOF.deleteAll(). The dependencies are gathered and objects are deleted in the correct order
     * to avoid conflicts. These are analogous to related-one tables in a database. These are not
     * the classes that refer to the deletionTarget, but rather the classes of objects that the
     * instances of the deletionTarget will have handles to.
     * @return Array of classes that the deletion target will refer to.
     */
    Class[] value() default {};

}
