package org.doframework;
// Released under the Eclipse Public License - v 1.0


/**
 This is the basic interface that "handler" classes must implement. Note, the choice of the PK is up to the handler. The
 PK does not need to correspond to the official PK in the database because it is up to the create, get, and delete
 operations as they are implemented to decide what to do with the PK.

 @author Justin Gordon
 @date January, 2008

 */
public interface DependentObjectHandler
{

    /**
     Inserts the object into the DB. No check should done to see if the object already exists, as the get method is
     called before create to do that check. Note that the framework will read any listed dependencies in the fileToLoad
     and it will recursively create those dependencies first.

     @param fileToLoad File name in form: {objectType}.{objectPk}.{fileType}

     @return The type of object being created and saved in the DB
     */
    Object create(String fileToLoad);

    /**
     Fetches the object, if it exists, with the given PK. Otherwise null is returned.

     @param pk The primary key of the object to retrieve

     @return The object created from the db if it existed, or else null
     */
    Object get(String pk);

    /**
     Delete the object with the given pk. Note that the framework will automatically try to delete the object's
     dependencies as well in a breadth first manner. It is CRITICAL that this method not delete the requested object and
     return false if there are any existing dependencies upon this object. For example, if this is a request to delete a
     customer record and invoices depend upon this customer record, it must simply return false.

     @param pk The primary key of the object to delete

     @return true if requested object is deleted.
     */
    boolean delete(String pk);
}
