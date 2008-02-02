package com.ibm.dof;

public interface DependentObjectHandler
{

    /**
     * Insert the object into the DB. No check is done to see if the object already exists.
     * @param fileToLoad File name in form: {objectType}.{objectPk}.{fileType}
     * @return The type of object being created and saved in the DB
     */
    Object create(String fileToLoad);

    /**
     * Return the object, if it exists, with the given PK
     * @param pk The primary key of the object to retrieve
     * @return The object created from the db if it existed, or else null
     */
    Object get(String pk);

    /**
     * Delete the object with the given pk. The framework will try to delete the objects dependencies
     * as well. It is CRITICAL that this method not delete the requested object and return false if
     * there are any existing dependencies upon this object. For example, if this is a request to delete
     * a customer record and invoices depend upon this customer record, it must simply return false.
     *
     * @param pk The primary key of the object to delete
     * @return true if requested object is deleted.
     *
     */
    boolean delete(String pk);
}
