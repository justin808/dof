package org.doframework;

// Released under the Eclipse Public License - v 1.0


/**
 This is the basic interface that "handler" classes must implement. Note, the choice of the PK is up to the handler. The
 PK does not need to correspond to the official PK in the database because it is up to the create, get, and delete
 operations as they are implemented to decide what to do with the PK.
 <p/>
 Consider having the implementation of this class also extend org.doframework.ScratchObjectDeletionHelper


 @author Justin Gordon
 @date January, 2008

 */
public interface DependentObjectHandler
{
    /**
     Inserts the object into the DB. No check should done to see if the object already exists, as the get method is
     called before create to do that check. Note that the framework will read any listed dependencies in the fileToLoad
     and it will recursively create those dependencies first.<p/>

     To get the contents to process, either call:<p/>
     <code>
     String contents = objectFileInfo.getFileContentsAsString();
     </code>
     or
     <code>
     InputStream is = objectFileInfo.getFileContentsAsInputStream();
     </code>

     It is critical that you create methods do not try to directly get resources, because
     with scratch objects, the primary key is swapped out.

     @param objectFileInfo contains the pk, file contents, objectType, fileType

     @return The type of object being created and saved in the DB
     */
    Object create(ObjectFileInfo objectFileInfo);


    /**
     Fetches the object, if it exists, with the given PK. Otherwise null is returned.
     <p/>
     Use objectFileInfo.getPk() to get the primary key of the object to fetch.

     @param objectFileInfo contains pk, objectType, fileType, and fileToLoad

     @return The object created from the db if it existed, or else null
     */
    Object get(ObjectFileInfo objectFileInfo);


    /**
     Delete the object with the given pk. Note that the framework will automatically try to delete the object's
     dependencies as well in a breadth first manner. It is CRITICAL that this method not delete the requested object and
     return false if there are any existing dependencies upon this object. For example, if this is a request to delete a
     customer record and invoices depend upon this customer record, it must simply return false.<p/>

     This method is passed the objectToDelete as a convenience as many systems will use that object
     as part of the deletion code.

     @param objectToDelete Object requested for deletion, never null

     @return true if requested object is deleted.
     */
    boolean delete(Object objectToDelete);


    /** @return The class object for the class that is returned from this handler. */
    Class getCreatedClass();


}
