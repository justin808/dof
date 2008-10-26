package org.doframework;

/**
 * This is the basic interface for DOF Java "Reference Object" builder classes, each of which will
 * create a well-defined "reference object", which is an "immutable non-transient shared test
 * fixture object". That mouthful is hereafter referred to as a <b>"Reference Object"</b>.
 * <p/>
 * Objects in the DOF are defined by either text definition files with a handler class, or they are
 * defined by individual Java classes.  The advantage of using Java to define objects, rather than
 * using some file format, like XML, is that the classes will support automated refactorings.<p/>
 * <p/>
 * Reference objects MUST be immutable within your tests. In other words, any test code that uses
 * these objects should <b>not</b> cause them to change, directly or indirectly. Such immutable
 * objects can only depend on other reference objects or even scratch objects. The key aspect of
 * reference objects is that they NEVER change in the course of testing.
 * <p/>
 * There are essentially two types of persisted objects for testing: reference (immutable,
 * permanent) and scratch (mutable, transient) ones. The advantage of reference objects is that once
 * they are created, many tests can share the same reference object. With scratch objects, this is
 * not the case, and unique ones must be created for each test. Reference objects can depend on
 * other reference objects, but not on scratch objects. Otherwise, the reference object would not be
 * clearly defined by the implementor of this interface.
 * <p/>
 * If this object depends on objects created with text files, be sure to implement the interface
 * HasReferenceTextDependencies so that those objects get properly deleted if delete is called.
 * <p/>
 * It is <b>critical</b> that reference objects never get deleted other than by calling
 * DOF.delete(ReferenceObject ro) because the DOF caches these objects, and thus if delete is called
 * otherwise, then the object is returned even though it has been deleted!
 * <p/>
 *
 * @author Justin Gordon
 * @date September, 2008
 */

public interface ReferenceBuilder extends DOFBuilder
{

    /**
     * Fetches the object, if it exists, with the given PK. Otherwise null is returned. Typically,
     * this method should be defined in the superclass for the object defined and the superclass
     * should call getPrimaryKey to see what object should be retrieved. Note, this method
     * <b>MUST</b> go to the persistent store. It it returns null, then the create method is
     * called.
     *
     * @return The object created from the db if it existed, or else null @param pk
     */
    Object fetch();


    ///**
    // *
    // * Delete the passed in object. Immediately before this method is called, okToDelete(Object)
    // * is called and that method must return true before this method is called.
    // * <p/>
    // * Typically, this method should be defined in the superclass for the object defined because you
    // * typically have many builder classes for a given object type, and the methods implementations
    // * might be the same for both scratch builders and reference builders.
    // * <p/>
    // * This method is passed the objectToDelete as a convenience as many systems will use that
    // * object as part of the deletion code.
    // * <p/>
    // * In order for this method to work correctly, you must correctly implement method
    // * getReferenceJavaDependencies and possibly implement interface HasReferenceTextDependencies.
    // * <p/>
    // * Also, keep in mind that deletion of reference objects is typically only done during the
    // * development of tests. Cleaning up reference objects in the tear down of each test will lead
    // * to horrible performance issues. The whole point of reference objects is that these objects
    // * can stick around.
    // *
    // * @param objectToDelete Object requested for deletion, never null
    // *
    // * @return true if requested object is deleted.
    // */
    //boolean delete(Object objectToDelete);
    //
    //
    ///**
    // * This method is called to check if an object can be deleted. Generally, an object can be
    // * deleted if no other objects refer to it. It may be possible to rely on relational integrity
    // * to fail a deletion call with a SqlException, but that can get messy depending on if you are
    // * using an ORM such as JPA.
    // *
    // * @param object to be deleted
    // *
    // * @return true if the object is OK for deletion
    // */
    //boolean okToDelete(Object object);


    /**
     * This method is used for caching objects. The value can be any unique value, such as a unique
     * field, or a combination of fields that is unique for the given object type, as the key is combined
     * with the class name. It does not need to be the same as the database physical primary key.
     * This is the value used for linking up objects in the DOF.
     * @return the unique key to be used for caching.
     */
    Object getPrimaryKey();


    /**
     * The implementation of this method must insert the defined object into the DB.
     * <p/>
     * It is important that implementations should get handles to dependencies by calling
     * DOF.getReferenceObject() rather than any other way, as the referenced objects will be
     * properly created and cached for fast access.
     *
     * @return An object that was created and saved in the DB
     */
    Object create();


    ///**
    // * This method defines what other objects need to be created (persisted) before this object is
    // * created. This method is used by the DOF for cleanup purposes.
    // *
    // * @return Array of reference objects that this object directly depends on.
    // */
    //ReferenceBuilder[] getReferenceJavaDependencies();


}

