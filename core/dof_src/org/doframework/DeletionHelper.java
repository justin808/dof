package org.doframework;


import java.util.*;

/**
 *
 *
 */
public interface DeletionHelper
{
    /**
     * Used to get a list of objects that this object depends upon. For example, an invoice record would depend on
     * its customer records, and any product records referenced.
     * @param objectToDelete Object to get the dependencies of
     * @return List of objects that are dependencies
     */
    List<ObjectFileInfo> getMyDependencies(ObjectFileInfo objectToDelete);

    /**
     * Used to get a list of objects that depend on this object. These are the objects that must first be deleted
     * before this object can be deleted. For example, if this is called on a customer record, then this method might
     * return any invoices that depend on that customer record.

     * @param objectToDelete Object to get those objects dependent on it
     * @return List of objects that depend on this object
     */
    List<ObjectFileInfo> getWhoDependsOnMe(ObjectFileInfo objectToDelete);


}
