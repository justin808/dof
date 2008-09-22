package org.doframework;

public interface ObjectDeletionHelper
{

    /**
     * @param object Object to delete
     * @return true if the object deleted correctly. Otherwise return false.
     */
    boolean delete(Object object);


    /**
     * This method is used for deletion of scratch objects. Thus, it is not necessary to implement it
     * if you are not concerned with deleting scratch objects.
     *
     * @param object Which is the type that the given handler is mapped to.
     * @return The dependencies of the given object
     */
    Object[] getDependencies(Object object);


    /**
     * Information on what parent dependencies this object has are used to for operation
     * DOF.deleteAll(). The dependencies are gathered and objects are deleted in the correct order
     * to avoid conflicts.
     * @return
     */
    Class[] getParentDependencyClasses();


    /** Return the primary key for the object */
    Object extractPrimaryKey(Object object);
}
