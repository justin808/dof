package org.doframework;

/**
 * Interface to support helper classes that know how to take care of deletion.
 * These can be implemented by the same classes specified in "handler_mappings.properties" that
 * implement the DependentObjectHandler interface, or they can be specified in a file named
 * "deletion_mappings.properties" which has a mapping of class name to associated ObjectDeltionHelper.
 *
 */
public interface ObjectDeletionHelper
{

    /**
     * Delete the passed in object. If other objects depend on this object, do not delete the object
     * and return false.
     * @param object Object to delete
     * @return true if the object deleted correctly. Otherwise return false.
     */
    boolean delete(Object object);


    /**
     * Method to get the dependencies of an object. These are the objects are referenced by foreign
     * keys of this object.
     * @param object Which is the type that the given handler is mapped to and from which we want
     * to find the dependencies of.
     * @return The dependencies of the given object
     */
    Object[] getDependencies(Object object);


    /**
     * Information on what parent dependency classes this object has are used to for operation
     * DOF.deleteAll(). The dependencies are gathered and objects are deleted in the correct order
     * to avoid conflicts.
     * @return
     */
    Class[] getDependencyClasses();


    /** Return the primary key for the object */
    Object extractPrimaryKey(Object object);
}
