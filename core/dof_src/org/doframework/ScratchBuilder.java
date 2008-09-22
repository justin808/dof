package org.doframework;

import java.util.*;

/**
 * This is the basic interface for DOF Java "Scratch Object" builder classes. An instance of the
 * implementor of this interface should create a "scratch Object", which is a uniquely generated,
 * "fresh, transient, non-shared test fixture object". That mouthful is hereafter referred to as a
 * "scratch object".
 * <p/>
 * Objects in the DOF are defined by either text definition files with a handler class, or they are
 * defined by individual Java classes.  The advantage of using Java to define objects, rather than
 * using some file format, like XML, is that the classes will support automated refactorings.<p/>
 * <p/>
 * There are essentially two types of persisted objects for testing: reference and scratch ones. The
 * advantage of scratch ones is that tests do not need to worry about multiple tests changing these
 * objects and thus having erratic tests that sometimes pass and sometimes fail. Scratch objects
 * should get unique primary keys.
 * <p/>
 * Scratch objects are for modification by tests. Such scratch objects can depend on other scratch
 * or reference objects. I.e., it is acceptable and often advisable for multiple scratch objects to
 * depend on the same reference object. This should be the case when the reference object will not
 * get changed, and thus the test will run faster than if the created scratch object had to create a
 * scratch dependency.
 * <p/>
 * If this object depends on reference objects created with text files, be sure to implement the
 * interface HasReferenceTextDependencies so that those objects get properly deleted if delete is
 * called. Likewise, if this object depends on scratch objects created with text files, be sure to
 * implement the interface HasScratchTextDependencies so that those objects get properly deleted if
 * delete is called.
 * <p/>
 * Note, scratch objects are not cached by the DOF, unlike reference ones. However, the builder
 * objects are cached for the ability to call cleanup methods.
 *
 * @author Justin Gordon
 * @date September, 2008
 * @see org.doframework.DOFBuilder
 * @see org.doframework.ReferenceBuilder
 */


public interface ScratchBuilder extends DOFBuilder
{


    /**
     * Fetches the object, if it exists, with the given PK. Otherwise null is returned.
     * <p/>
     * This method is only called if ScratchBuilder.PK was defined in the map passed into this
     * object. Typically, this method should be defined in the superclass for the object defined and
     * the superclass should call getPrimaryKey to see what object should be retrieved. Note, this
     * method <b>MUST</b> go to the persistent store. It it returns null, then the create method is
     * called.
     *
     * @return The object created from the db if it existed, or else null @param pk
     */
    Object fetch(Object pk);


    /** Return the primary key for the scratch object */
    Object extractPrimaryKey(Object scratchObject);


    /**
     * The implementation of this method must insert the defined object into the DB, using a
     * <b>unique</b> primary key.
     * <p/>
     * It is important that implementations should get handles to dependencies by calling
     * DOF.getReferenceObject() and DOF.createScratchObject(), rather than any other way, as the
     * referenced objects will be properly created and cached for fast access.
     * <p/>
     * The Map argument allows 2 things: 1. Ability to pass arbitrary values into the create method
     * 2. Ability to allow two scratch objects to depend on the same parent scratch object.
     * <p/>
     * For example, you might have two scratch invoices depend on the same parent scratch customer.
     * In that example, you would create the first scratch invoice with its corresponding scratch
     * customer. Then you would create a map, with a key being something like "customer" (the
     * scratch reference) and the value being whatever uniquely defines the customer. The
     * ScratchBuilder for the invoice should use that value instead of creating a new scratch
     * customer.
     * <p/>
     * Another option is that you may want to pre-specify the primary key that is used to create the
     * scratch object. The key value of ScratchBuilder.PK ("pk") is reserved for this purpose.
     * Otherwise, since <b>your</b> implementation will be doing the processing of the map, you are
     * free to choose how you use the map's keys and values.
     *
     * @param scratchReferenceToPrimaryKey Map which may contain values to substitute for primary
     *                                     keys of dependent parent objects. The purpose of this is
     *                                     to allow two scratch objetcts to be created that depend
     *                                     on the same object, such as two scratch invoices that
     *                                     depend on the same customer. Map can also be used to pass
     *                                     arbitrary values to the builder. Thus, instead of passing a
     * primary key by key, one could pass in the actual object in the map. Since you'll be implementing
     * what happens with the map, you're free to do as you wish with the map. Additionally, when
     * fetching a scratch object by primary key, please use method DOF.getCachedObject(Class, Primary Key))
     *
     * @return An object that was created and saved in the DB
     */
    Object create(Map scratchReferenceToPrimaryKey);
}
