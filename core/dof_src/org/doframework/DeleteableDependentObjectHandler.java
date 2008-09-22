package org.doframework;

import java.util.*;

public interface DeleteableDependentObjectHandler
{

    Class getType();


    Collection<Class> getParentTypes();


    /**
     * This method is used for doing recursive deletion of a scratch object. Since for scratch
     * objects, we do not have information on what the parent objects are based on the file.
     * @return
     */
    Collection getParentObjects(Object dependentObject);


}
