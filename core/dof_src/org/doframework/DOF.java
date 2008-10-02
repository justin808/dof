package org.doframework;

// Released under the Eclipse Public License-v1.0


import java.util.*;
import java.util.regex.*;


/**
 * This class is the engine of the Dependent Object Framework. The general problem this framework
 * solves is how does a JUnit test ensure that a persistent object needed for a test exists in the
 * database (or any persistent store). Alternative solutions to this problem include running SQL
 * scripts to populate the database and restoring database backups. Both methods are inconvenient.
 * <p/> There are two basic categorizations of objects when running JUnits against a DB. <p/> 1.
 * Reference Objects: Also known as "Shared persistent test fixtures". These objects are immutable
 * and thus can be shared between tests and betweeen repeated runs of a single test. These objects
 * maybe cached by the DOF so that they are retrieved without even going to the persistent store.
 * <p/> 2. Scratch Objects: Also known as "Transient persistent test fixtures". These are persisted
 * objects that test will modify. These objects are only visible to the test using them. Thus, these
 * objects have unique primary keys. They may depend on reference objects or other scratch objects.
 * Modification to these objects do not affect other tests. <p/> The Dependent Object Framework
 * allows the test writer to specify what objects (reference and scratch) are required for a test.
 * Reference and Scratch objects can be specified either using <p/> 1. Java builder classes. See
 * interfaces org.doframework.ReferenceObject and org.doframework.ScratchObject for more details.
 * The advantage of using Java to define objects, rather than using some file format, like XML, is
 * that the classes will support automated refactorings. The test writer creates one java builder
 * class per reference or scratch object. <p/> 2. Using a combination of text files with associated
 * handler classes that can process the text files. The test writer provides: <p/> 1. An appropriate
 * handler class for each object type. The handler class implements the interface
 * DepedendentObjectHandler and needs to be specfied in a file called handler_mappings.properties
 * that exists somewhere in the classpath or at directory specified as system property
 * <b>DOF_DIR</b>. This class knows how to create, get, and delete objects of a given type and given
 * a format for the description files. Note, the description files can be of any form because the
 * test writer is responsible for writing the code that processes the description files. <p/> 2. For
 * each object, a data file containing information to create each object, including the
 * specification of any object dependencies. For example a "product" record might specify what
 * manufacturer record is required. In order to be located by the framework, all DOF data files must
 * exist in the classpath, or they must be under a directory passed as a system parameter "DOF_DIR".
 * <p/> There are only 3 methods that JUnit tests will call: <b>require</b>(fileToLoad),
 * <b>createScratchObject</b> and <b>delete</b>(fileToLoad).<p> <p/> The fileToLoad encodes the
 * object type, the object primary key, and the file type, and it may specify a subdirectory. <p/>
 * Example:
 * <pre>
 * public void testNewInvoiceSubtotal()
 * {
 * // Get objects needed for test
 * Customer johnSmith = (Customer) DOF.require("customer.25.xml");
 * Product coffee = (Product) DOF.require("product.13.xml");
 * Product tea = (Product) DOF.require("product.14.xml");
 * .... rest of the test
 * }
 * </pre>
 * The product file may specify the manufacturer required, note in the XML comment using the form
 * <b>@require("fileToLoad")</b> that indicates the dependency on manufacturer 35. <p/>
 * <pre>
 * product.13.xml
 * &lt;!-- @require("manufacturer.35.xml") --&gt;
 * &lt;product&gt;
 * &lt;id&gt;13&lt;/id&gt;
 * &lt;name&gt;coffee&lt;/name&gt;
 * &lt;price&gt;8.99&lt;/price&gt;
 * &lt;manufacturer_id&gt;35&lt;/manufacturer_id&gt;
 * &lt;/product&gt;
 * </pre>
 * <b>Configuration</b><p/> The user of the DOF is responsible for having a file named
 * "handler_mappings.properties" located in the classpath or at directory specified as VM system
 * property DOF_DIR. Please consult the sample file for more documentation on configurable
 * parameters. The key aspect of the properties file is the mapping of object types to handlers:
 * <p/> <code>objectType.fileSuffix=DependentObjectHandlerImplementationClassName</code> <p/> This
 * is an example of a line in the mappings file: <p/> <code>customer.xml=dof_xml_handler.CustomerXmlFactory</code>
 * <p/> It states that a customer.PK.xml file maps to the handler class
 * dof_xml_handler.CustomerXmlFactory Note, the CustomerXmlFactory class must implement interface
 * <b>DependentObjectHandler</b>. Even though fileToLoad uses the period as the delimiter, the
 * primary key may contain periods because the first and last periods are used to find the object
 * type and the file suffix. This also means that object types may NOT contain a period. <p/> You
 * may specify regexp matches like this. Regexps to be recognized as such will contain at least one
 * '*', '?', or '+'.
 * <pre>
 * {regular expression}=class
 * \\w+\\.xml=dof_xml_handler.GenericXmlFactory
 * # NOTE: you must use double backslashes to mean a backslash
 * </pre>
 * For regexp documentation, consult the javadoc for java.util.regexp.Pattern<p/>In case you
 * do not like the form of "objectType.PK.fileType", you may specify a custom
 * ObjectFileInfoProcessor class by putting in this property in file handler_mappings.properties
 * <pre>
 * ObjectFileInfoProcessor=FullClassName
 * </pre>
 * </p> Note: For verbose output of when objects are created and deleted, set system property
 * <b>-DDOF_DEBUG=TRUE</b><p/> Note: For a listing of how file contents with scratch keys get
 * replaced, set system property <b>-DDOF_PRINT_FILES=TRUE</b>
 *
 * @author Justin Gordon
 * @date January, 2008
 * @see DependentObjectHandler
 * @see ObjectFileInfoProcessor
 */
public class DOF
{


    //////////////////////////////////////////////////////////////////////////////////////////////
    // public API Using Java to define objects //////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Used to ensure that a given object exists in the persistent store and to get that object.
     * @param referenceBuilder An instance of ReferenceBuilder that defines the object requested.
     * @return The object that this ReferenceBuilder creates
     */
    public static Object require(ReferenceBuilder referenceBuilder)
    {
        // First check local cache of loaded files
        Object resultObject = dofObjectCache.get(referenceBuilder);
        if (resultObject == null)
        {
            // Second, check if object exists in DB
            resultObject = referenceBuilder.fetch();

            if (resultObject == null)
            {
                // Finally, do creation, first creating dependencies

                // JG - I do not think we need to call the creation of the reference objects,
                // as the java code should be doing this directly.
                // The list of dependencies is only needed for delete.

                //ReferenceBuilder[] dependencies = referenceObject.getReferenceJavaDependencies();
                //if (dependencies != null)
                //{
                //    for (int i = 0; i < dependencies.length; i++)
                //    {
                //        require(dependencies[i]);
                //    }
                //}

                if (dofDebug)
                {
                    System.out.println("DOF: Loading Reference Object: " +
                                       referenceBuilder.getClass().getName());
                }

                // Create object given file of data
                resultObject = referenceBuilder.create();

                // Save the data in the cache
                dofObjectCache
                        .put(referenceBuilder, resultObject);
            }

            if (resultObject == null)
            {
                throw new RuntimeException(
                        "DbJUnitHandler failed to create reference object from " +
                        referenceBuilder.getClass().getName());
            }

        }
        return resultObject;
    }


    public static Object getCachedObject(Class clazz,
                                           Object pk)
    {
        return dofObjectCache.get(clazz, pk);
    }


    /**
     * Use this method to delete an object. This method will opportunistically try to delete all of
     * the parent dependencies. Note, the deletion is greedy. Even if the object requested to be
     * deleted does not exist, then it's parent objects will still try to be deleted. This method is
     * useful when setting up tests, as the object definition files often need frequent tweaking.
     * Note, it is critical that objects created with the require method be deleted using this
     * method because the require method caches the created objects by the file name.
     * <p/>
     * Deletion is greedy in that referenced objects will try to get deleted even if this object to
     * be deleted does not exist.
     *
     * @return true if requested object deleted successfully, false if object could not be deleted,
     *         maybe because another object depends upon it. Note, the return value from deleting
     *         dependency objects for the requested object is discarded. For example, if you request
     *         an invoice to be deleted, the return value only reflects if that requested invoice
     *         was deleted, and not if the parent customer record of that invoice is deleted. If the
     *         object does not exist, then theis method returns true, indicating that object was
     *         previously deleted.
     */
    public static boolean delete(ReferenceBuilder dependentObject)
    {
        return delete(dependentObject, true);
    }


    /**
     * Use this method to delete an object. This method will opportunistically try to delete all of
     * the parent dependencies. Note, the deletion is greedy. Even if the object requested to be
     * deleted does not exist, then it's parent objects will still try to be deleted. This method is
     * useful when setting up tests, as the object definition files often need frequent tweaking.
     * Note, it is critical that objects created with the require method be deleted using this
     * method because the require method caches the created objects by the file name.
     * <p/>
     *
     * @param greedy If true, then try to delete parent objects too! If false, only delete this
     *               object.
     *
     * @return true if requested object deleted successfully or does not exist, false if object
     *         could not be deleted,
     *         maybe because another object depends upon it. Note, the return value from deleting
     *         dependency objects for the requested object is discarded. For example, if you request
     *         an invoice to be deleted, the return value only reflects if that requested invoice
     *         was deleted, and not if the parent customer record of that invoice is deleted. If the
     *         object does not exist, then theis method returns true, indicating that object was
     *         previously deleted.
     */
    public static boolean delete(ReferenceBuilder referenceBuilder, boolean greedy)
    {
        Object objectToDelete = dofObjectCache.get(referenceBuilder); // will check DB too
        if (objectToDelete != null)
        {
            boolean deletionOk = delete(objectToDelete, greedy);
            if (dofDebug)
            {
                System.out.println("DOF: Deleting Reference Object: " +
                                   referenceBuilder.getClass().getName());
            }
            return deletionOk;
        }
        else
        {
            if (greedy)
            {
                // still try to clean up dependencies
                ReferenceBuilder[] referencBuilders = referenceBuilder.getReferenceJavaDependencies();
                if (referencBuilders != null)
                {
                    for (int i = 0; i < referencBuilders.length; i++)
                    {
                        ReferenceBuilder referencBuilder = referencBuilders[i];
                        delete(referencBuilder, greedy);
                    }
                }
            }
            return true;
        }
    }
        //boolean deletedThisObject = false;
        //Object referenceObjectToDelete = referenceBuilder.fetch();
        //if (referenceObjectToDelete == null)
        //{
        //    deletedThisObject = false;
        //}
        //else
        //{
        //    deletedThisObject = referenceBuilder.delete(referenceObjectToDelete);
        //}
        //if (greedy)
        //{
        //    if (referenceBuilder instanceof HasReferenceTextDependencies)
        //    {
        //        String[] referenceTextDependencies =
        //                ((HasReferenceTextDependencies) referenceBuilder)
        //                        .getReferenceTextDependencies();
        //        for (int i = 0; i < referenceTextDependencies.length; i++)
        //        {
        //            String referenceTextDependency = referenceTextDependencies[i];
        //            delete(referenceTextDependency);
        //        }
        //    }
        //    ReferenceBuilder[] dependentObjects =
        //            ((ReferenceBuilder) referenceBuilder).getReferenceJavaDependencies();
        //    if (dependentObjects != null)
        //    {
        //        for (int i = 0; i < dependentObjects.length; i++)
        //        {
        //            ReferenceBuilder ido = dependentObjects[i];
        //            delete(ido);
        //        }
        //    }
        //}
        //return deletedThisObject;



    ///**
    // * Use this method to delete an object. This method will opportunistically try to delete all of
    // * the parent dependencies. Note, the deletion is greedy. Even if the object requested to be
    // * deleted does not exist, then it's parent objects will still try to be deleted. This method is
    // * useful when setting up tests, as the object definition files often need frequent tweaking.
    // * Note, it is critical that objects created with the require method be deleted using this
    // * method because the require method caches the created objects by the file name.
    // * <p/>
    // *
    // * @return true if requested object deleted successfully, false if object could not be deleted,
    // *         maybe because another object depends upon it. Note, the return value from deleting
    // *         dependency objects for the requested object is discarded. For example, if you request
    // *         an invoice to be deleted, the return value only reflects if that requested invoice
    // *         was deleted, and not if the parent customer record of that invoice is deleted. If the
    // *         object does not exist, then theis method returns true, indicating that object was
    // *         previously deleted.
    // */
    //public static boolean delete(DeletableScratchBuilder deletableScratchObject)
    //{
    //    return deleteScratchObject(deletableScratchObject.getCreatedObject());
    //}

    //public static boolean deleteUsingDeletableScratchObject(DeletableScratchBuilder deletableScratchObject)
    //{
    //    boolean okToDeleteThisObject = false;
    //    okToDeleteThisObject = deletableScratchObject.deleteCreatedObject();
    //    if (okToDeleteThisObject)
    //    {
    //        if (deletableScratchObject instanceof HasTextDependencies)
    //        {
    //            String[] referenceTextDependencies = ((HasTextDependencies) deletableScratchObject)
    //                    .getReferenceTextDependencies();
    //            if (referenceTextDependencies != null)
    //            {
    //                for (int i = 0; i < referenceTextDependencies.length; i++)
    //                {
    //                    delete(referenceTextDependencies[i]);
    //                }
    //            }
    //
    //            Object[] scratchTextDependencies =
    //                    ((HasTextDependencies) deletableScratchObject).getScratchTextDependencies();
    //            if (scratchTextDependencies != null)
    //            {
    //                for (int i = 0; i < scratchTextDependencies.length; i++)
    //                {
    //                    Object scratchTextDependency = scratchTextDependencies[i];
    //                    deleteScratchObject(scratchTextDependency);
    //                }
    //            }
    //        }
    //
    //
    //        ReferenceBuilder[] referenceJavaDependencies =
    //                deletableScratchObject.getReferenceJavaBuilderDependencies();
    //        if (referenceJavaDependencies != null)
    //        {
    //            for (int i = 0; i < referenceJavaDependencies.length; i++)
    //            {
    //                delete(referenceJavaDependencies[i]);
    //            }
    //        }
    //
    //        DeletableScratchBuilder[] referencedDeletableScratchObjects =
    //                deletableScratchObject.getScratchJavaBuilderDependencies();
    //        if (referencedDeletableScratchObjects != null)
    //        {
    //            for (int i = 0; i < referencedDeletableScratchObjects.length; i++)
    //            {
    //                delete(referencedDeletableScratchObjects[i]);
    //            }
    //        }
    //    }
    //    return okToDeleteThisObject;
    //}


    /**
     * Deletes the objectToDelete, using the associated ObjectDeletionHelper. If there is no associated
     * ObjectDeletionHelper, then nothing is done.
     * @param objectToDelete
     * @return
     */
    public static boolean delete(Object objectToDelete)
    {
        return delete(objectToDelete, true);
    }


    /**
     * Deletes the objectToDelete, using the associated ObjectDeletionHelper. If there is no associated
     * ObjectDeletionHelper, then nothing is done.
     * @param objectToDelete Object to delete from persistence store
     * @param greedy Delete parent object dependencies as well as this object
     * @return True if object deleted. False if object could not be deleted due to dependencies.
     *  False if no object deletion helper was found.
     */
    public static boolean delete(Object objectToDelete, boolean greedy)
    {
        String className = objectToDelete.getClass().getName();
        ObjectDeletionHelper deletionHelper = DOFGlobalSettings
                .getInstance().getDeletionHelperForClass(className);
        if (deletionHelper == null)
        {
            if (dofDebug)
            {
                System.out.println("WARNING: No ObjectDeletionHelper for class: " + className);
                System.out.println("Object not deleted: " + objectToDelete);
            }
            return false;
        }


        boolean deletedOk = deletionHelper.delete(objectToDelete);
        if (dofDebug)
        {
            System.out.println("DOF: Deletion Helper: " + deletionHelper.getClass().getName() +
                               ": Deleting Object " + objectToDelete.getClass().getName() + ": " +
                               objectToDelete + ": Successful = " + deletedOk);
        }
        if (deletedOk)
        {
            dofObjectCache.remove(objectToDelete, deletionHelper.extractPrimaryKey(objectToDelete));
        }
        if (greedy && deletedOk)
        {
            Object[] dependencies = deletionHelper.getDependencies(objectToDelete);
            if (dependencies != null)
            {
                for (int i = 0; i < dependencies.length; i++)
                {
                    delete(dependencies[i]);
                }
            }
        }
        return deletedOk;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // public API Using Files to define objects //////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * The principal entry point for this class. If the object was previously requested, it is
     * returned from map of the file name to the object. Otherwise, the database is then checked. If
     * the object exists in the database (or whatever persistent store used by the handler classes),
     * then it is simply returned. If the object does not exist, then it is created. During the
     * creation process, the object definition file may specify other objects that are "required"
     * and those will get loaded recursively depth first. Thus, if object A depends on object B that
     * depends on object C, then a request for object A invokes the request for object B which
     * invokes the request for object C.
     * <p/>
     * Note that the fileToLoad uses a specific format to define the file to load which encodes
     * the:
     * <pre>
     * 1. Object Type
     * 2. Object PK
     * 3. Object File Type (like xml)
     * </pre>
     * <p/>
     * The file processor looks for dependencies in the definition files using this pattern:<p>
     * <b>@require("{object_type.object_pk.extension}")</b>
     * <p/>
     * where {object_type.object_pk.extension} might be something like "manufacturer.35.xml".
     * <p/>
     * Thus, in an XML file, one would use the commented form:<p> <code> &lt;!--
     * @require("manufacturer.35.xml") --&gt; </code><p> Even though fileToLoad uses the period as
     * the delimiter, the primary key may contain periods because the first and last periods are
     * used to find the object type and the file suffix. This also means that object types may NOT
     * contain a period.
     * <p/>
     * Note, the difference between require() and createScratchObject() is that require() expects
     * that the PK of the object must match exactly what is encoded in the fileToLoad.
     * createScratchObject() can be used to in the same manner as require, but by passing in value
     * for the primary key. See createScratchObject() for more details.
     *
     * @param fileToLoad           File name in form: {objectType}.{objectPk}.{fileType} or a
     *                             customized version of this
     * @param scratchReferenceToPk
     *
     * @return The Object requested
     */
    public static Object require(String fileToLoad, Map<String, String> scratchReferenceToPk)
    {
        FileToLoadAndScratchReference so = new FileToLoadAndScratchReference();
        so.fileToLoad = fileToLoad;
        so.scratchReference = null;
        return requireWorker(so, false, scratchReferenceToPk);
    }


    /**
     * Same as calling require iwhtout any mappings of scratchReferences to pk values
     *
     * @param fileToLoad
     *
     * @return
     */
    public static Object require(String fileToLoad)
    {
        return require(fileToLoad, new HashMap<String, String>());
    }


    /**
     * This works similarly to require() except for one major thing. The primary key when using
     * require() is encoded
     * in the fileToLoad name. When using createScratchObject, the primaryKey is either: <pre>
     * 1. Created using a your handler class for this object type if that handler implements
     * ScratchPkProvider
     * 2. Using the default ScratchPkProvider specified in file handler_mappings.properties
     * 3. Using the system default of System.currentTimeMillis() + ""
     * </pre>
     * <p/>
     * The primary key is listed in the template file as <b>{{pk}}</b> and that string gets replaced
     * when you call ObjectFileInfo.getFileContentsAsString() or ObjectFileInfo.getFileContentsAsInputStream().
     * <p/>
     * <p/>
     * Optionally, you can specify <b>{{pk:fileToLoadPK}}</b> where fileToLoadPk is the part of the
     * file name that is the PK when using require(). This allows scratch files to be used either as
     * pure scratch objects with a brand new key, or you can pass in a key when calling this
     * method. For example, if the file is called "invoice.myScratchInvoice.xml", and invoice is the
     * file type, and myScratchInvoice is the part of the file name that maps to the PK, then you
     * can put in "myScratchInvoice" as a key in the map of scratchReferenceToPrimaryKey. In other
     * words, the part of the file name that maps to the primary key for reference objects is a
     * special identifier.
     * <p/>
     * When you call this method, you can specify a Map<scratchReference, primaryKey>. This allows
     * you to specify a map with scratchReference --> thePkYouDesire. If you do that, thePkYouDesire
     * is substituted in places indicated in the file. If you don't specify the map or don't have a
     * reference , then a new PK is generated.<p/>
     * <p/>
     * All object definition files can specify dependencies as scratch objects. There are 2 parts to
     * this. Suppose you
     * are creating an invoice and want to depend on a scratch customer:<pre>
     * 1. List out a the dependency creation with this pattern in a comment:
     * @createScratchObject("customer.scratch.xml", "scratchCustomerPk")
     * 2. In the place of the customer id, put in {{pk:scratchCustomerPk}}
     * </pre>
     * Here's what happens. The DOF reads the invoice file and sees the dependency on a scratch
     * customer. The scratch customer is created, and the resulting primary key is substituted in
     * the place of {{pk:scratchCustomerPk}<p/>
     * <p/>
     * Suppose that you had the scratchCustomerPk already. Then you could pass the mapping of that
     * tag to its pk in the Map parameter.
     *
     * @param fileToLoad
     * @param scratchReferenceToPrimaryKey Map<scratchReference, primaryKey>
     *
     * @return The object created by the command, or possibly fetched
     */
    public static Object createScratchObject(String fileToLoad,
                                             Map<String, String> scratchReferenceToPrimaryKey)
    {
        FileToLoadAndScratchReference so = new FileToLoadAndScratchReference();
        so.fileToLoad = fileToLoad;
        so.scratchReference = null;
        return requireWorker(so, true, scratchReferenceToPrimaryKey);
    }


    public static Object createScratchObject(String fileToLoad)
    {
        return createScratchObject(fileToLoad, new HashMap<String, String>());
    }





    public static Object createScratchObject(ScratchBuilder scratchBuilder)
    {
        return createScratchObject(scratchBuilder, new HashMap<String, Object>());
    }


    /**
     *
     * @param scratchBuilder
     * @param scratchReferenceToObject Map that is passed into the create method of the
     * scratchBuilder. Since the scratchBuilder is your implementation, the value of the map can be
     * any type of object. For example, you can map the actual object or the primary key of the object.
     * @return Object of type defined by the scratchBuilder class
     */
    public static Object createScratchObject(ScratchBuilder scratchBuilder,
                                             Map<String, Object> scratchReferenceToObject)
    {
        return createScratchObject(scratchBuilder, scratchReferenceToObject, null);
    }


    /**
     *
     * @param scratchBuilder
     * @param scratchReferenceToObject
     * @return
     */
    static Object createScratchObject(ScratchBuilder scratchBuilder,
                                      Map<String, Object> scratchReferenceToObject,
                                      String scratchReference)
    {
        // create dependencies
        //if (scratchBuilder instanceof DeletableScratchBuilder)
        //{
        //    scratchJavaObjectsCreated.add((DeletableScratchBuilder) scratchBuilder);
        //}



        // I'm not sure if this is a good idea to be storing scratch objects
        if (scratchReference != null && scratchReferenceToObject != null)
        {
            Object pk = scratchReferenceToObject.get(scratchReference);
            if (pk != null)
            {
                Object fetchedObject = dofObjectCache.get(scratchBuilder.getCreatedClass(),
                                                            pk);
                if (fetchedObject == null)
                {
                    fetchedObject = scratchBuilder.fetch(pk);
                }
                if (fetchedObject != null)
                {
                    return fetchedObject;
                }
            }
        }

        if (dofDebug)
        {
            System.out.println(
                    "DOF: Creating Scratch Object: " + scratchBuilder.getClass().getName() + (
                            (scratchReferenceToObject != null && scratchReferenceToObject.size() > 0) ?
                            ", scratchReferenceToObject = " + scratchReferenceToObject : ""));
        }

        Object resultObject = scratchBuilder.create(scratchReferenceToObject);
        dofObjectCache.put(scratchBuilder, resultObject);

        return resultObject;
    }


    /**
     * Use this method to delete a reference object. This method will opportunistically try to
     * delete all of the parent dependencies. Note, the deletion is greedy. Even if the object
     * requested to be deleted does not exist, then it's parent objects will still try to be
     * deleted. This method is useful when setting up tests, as the object definition files often
     * need frequent tweaking. Note, it is critical that objects created with the require method be
     * deleted using this method because the require method caches the created objects by the file
     * name.
     * <p/>
     * Note, this method takes the same parameter as the require method to facilitate copying and
     * pasting the require line.
     *
     * @param fileToLoad File name in form: {objectType}.{objectPk}.{fileType}, or possibly the
     *                   custom definition of the file naming pattern.
     *
     * @return true if requested object deleted successfully, false if object could not be deleted,
     *         maybe because another object depends upon it. Note, the return value from deleting
     *         dependency objects for the requested object is discarded. For example, if you request
     *         an invoice to be deleted, the return value only reflects if that requested invoice
     *         was deleted, and not if the parent customer record of that invoice is deleted. If the
     *         object does not exist, then theis method returns true, indicating that object was
     *         previously deleted.
     */
    public static boolean delete(String fileToLoad)
    {
        Set<String> processedDeletions = new HashSet<String>();
        return deleteObjectWorker(fileToLoad, processedDeletions);
    }


    /**
     * Clears the cache (map) of {file loaded} to {object returned}.
     * <p/>
     * The below example demonstrates how the objects returned on successive calls to <b>require</b>
     * with the same fileToLoad return the same object, unless the object is deleted, or the file
     * cache is cleared. A reason this might be done is that code changes the object returned and
     * persists it, and you want to fetch the object cleanly from the database again using a
     * DOF.require call. Typically, you won't need to use this method, as the file cache speeds up
     * performance of your tests.
     * <pre>
     * public void testDeleteManufacturer()
     * {
     * Manufacturer m1 = (Manufacturer) DOF.require("manufacturer.20.xml");
     * assertTrue(DOF.delete("manufacturer.20.xml"));
     * Manufacturer m2 = (Manufacturer) DOF.require("manufacturer.20.xml");
     * assertNotSame(m1, m2);
     * Manufacturer m3 = (Manufacturer) DOF.require("manufacturer.20.xml");
     * assertSame(m2, m3);
     * DOF.removeEntryFromCache();
     * Manufacturer m4 = (Manufacturer) DOF.require("manufacturer.20.xml");
     * assertNotSame(m3, m4);
     * }
     * </pre>
     * @param objectToRemove
     * @param objectPrimaryKey
     */
    public static void removeEntryFromCache(Object objectToRemove, Object objectPrimaryKey)
    {
        dofObjectCache.remove(objectToRemove, objectPrimaryKey);
    }


    /**
     * Used to take the fileToLoad and to convert it into the ObjectFileInfo
     *
     * @param fileToLoad, which may contain a leading path
     *
     * @return The ObjectFileInfo containing the object type, pk, and file type
     */
    public static ObjectFileInfo getObjectFileInfo(String fileToLoad)
    {
        ObjectFileInfo ofi =
                DOFGlobalSettings.getInstance().getObjectFileInfoProcessor().getObjectFileInfo(fileToLoad);
        ofi.setFileToLoad(fileToLoad);
        return ofi;
    }


    /**
     * Set this to true to print messages to System.out when objects are loaded or deleted from the
     * database. This property can also be set by setting VM System Property:<p/> -DDOF_DEBUG=TRUE
     *
     * @param b
     */
    public static void setDofDebug(boolean b)
    {
        dofDebug = b;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // private members and methods ///////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * we keep a cache of the loaded objects to avoid searching the DB every time.
     */
    static
    {   // Make sure we init first
        DOFGlobalSettings.getInstance();
    }

    private static final DOFObjectCache dofObjectCache =
            new DOFObjectCache();

    private static Pattern repIncludeDependency;
    private static Pattern repIncludeScratch;
    private static Pattern repIncludeDependencyJava;
    private static Pattern repIncludeScratchJava;


    public static boolean dofDebug = System.getProperty("DOF_DEBUG", "").equalsIgnoreCase("TRUE");

    static boolean dofPrintDescriptionFiles =
            System.getProperty("DOF_PRINT_FILES", "").equalsIgnoreCase("TRUE");

    //private final static Map<Object, ObjectFileInfo> scratchObjectToObjectFileInfo =
    //        new HashMap<Object, ObjectFileInfo>();
    //


    private static void checkRepIncludeDependencyInitialized()
    {
        // Original pattern -- use the @require("type.pk.ext")
        //repIncludeDependency = Pattern.compile("\\$require\\s*\\(\\s*\"(.*)\"\\s*\\);");

        // Changed 3/20/2008 to use annotation format:
        // Leading @ and removed the trailing semicolon
        if (repIncludeDependency == null)
        {
            repIncludeDependency = Pattern.compile("@require\\s*\\(\\s*\"(.*)\"\\s*\\)");
            repIncludeScratch = Pattern.compile(
                    "@createScratchObject\\s*\\(\\s*\"(.*)\"\\s*,\\s*\"(.*)\\s*\"\\)");
            repIncludeDependencyJava = Pattern.compile("@requireJava\\s*\\(\\s*\"(.*)\"\\s*\\)");
            repIncludeScratchJava = Pattern.compile(
                    "@createScratchObjectJava\\s*\\(\\s*\"(.*)\"\\s*,\\s*\"(.*)\\s*\"\\)");
        }
    }


    /**
     * No need for constructor.
     */
    private DOF()
    {
    }


    /**
     * @param fileToLoadAndScratchReference
     * @param isScratchObject
     * @param scratchReferenceToPk
     */

    private static Object requireWorker(FileToLoadAndScratchReference fileToLoadAndScratchReference,
                                        boolean isScratchObject,
                                        Map<String, String> scratchReferenceToPk)
    {
        // First check local cache of loaded files
        ObjectFileInfo objectFileInfo = getObjectFileInfo(fileToLoadAndScratchReference.fileToLoad);
        boolean hasScratchReference = false;
        boolean lookupByPrimaryKey = !isScratchObject; // some scratch objects will have a PK
        String scratchPk = null;

        // Check for a specified value for scratch PK
        String fileNamePk = objectFileInfo.getPk();
        if (isScratchObject)
        {
            hasScratchReference = fileToLoadAndScratchReference.scratchReference != null &&
                                  fileToLoadAndScratchReference.scratchReference.length() > 0;
            scratchPk = hasScratchReference ?
                        scratchReferenceToPk.get(fileToLoadAndScratchReference.scratchReference) :
                        null;
            if (scratchPk == null && !hasScratchReference)
            {
                // Check if the pk was set already, and is put into the referenceObjects
                // if so, then use it

                // There are two options:
                // 1. use pk::filePK (using the file naming pattern)
                // 2. Plain key of "pk"
                scratchPk = scratchReferenceToPk.get(fileNamePk);
                if (scratchPk == null)
                {
                    scratchPk = scratchReferenceToPk.get(fileNamePk);
                }
            }
            if (scratchPk != null)
            {
                // Check if object exists in DB
                objectFileInfo.setPk(scratchPk);
                lookupByPrimaryKey = true;
            }
        }

        Object resultObject = lookupByPrimaryKey ?
                              dofObjectCache.get(objectFileInfo) :
                              null;

        if (resultObject == null)
        {
            // Get handler class for object
            String objectType = objectFileInfo.getObjectType();
            String pk = fileNamePk;
            String fileType = objectFileInfo.getFileType().toLowerCase();

            DependentObjectHandler dependentObjectHandler =
                    DOFGlobalSettings.getInstance().getDependentObjectHandlerForObjectTypeFileType(objectType, fileType);

            loadDependencies(fileToLoadAndScratchReference.fileToLoad, scratchReferenceToPk);

            loadDependencyScratchObjects(fileToLoadAndScratchReference.fileToLoad,
                                         scratchReferenceToPk);

            if (dofDebug)
            {
                System.out.println("DOF: Loading" + (isScratchObject ? " Scratch Object" : "") +
                                   ": ObjectFileInfo = " + objectFileInfo);
            }

            // This is where the magic substitution happens for recursive scratch object creation.
            if (isScratchObject)
            {
                objectFileInfo.setScratchMode(true);

                // If we don't have a scratch PK, we generate one
                if (scratchPk == null)
                {
                    scratchPk = getScratchPk(dependentObjectHandler);
                    if (hasScratchReference) // then we want to save this PK for other objects. This is used for
                    // recursive scratch creation
                    {
                        scratchReferenceToPk
                                .put(fileToLoadAndScratchReference.scratchReference, scratchPk);
                    }
                    objectFileInfo
                            .setPk(scratchPk); // need to set the pk for computing the hash key below
                }
            }
            objectFileInfo.setScratchReferenceToPk(scratchReferenceToPk);

            // Create object given file of data
            resultObject = dependentObjectHandler.create(objectFileInfo);
            if (resultObject == null)
            {
                throw new RuntimeException("DOF failed to create object with pk " + pk);
            }

            // Save the data in the cache
            dofObjectCache.put(objectFileInfo, resultObject);

        }
        return resultObject;
    }


    /**
     * First look on the handler class, then look at the default one defined in the properties file
     *
     * @param dofHandler
     *
     * @return
     */
    private static String getScratchPk(Object dofHandler)
    {
        String scratchPk;
        if (dofHandler instanceof ScratchPkProvider)
        {
            scratchPk = ((ScratchPkProvider) dofHandler).getScratchPk();
        }
        else if (defaultScratchPrimaryKeyProvider != null)
        {
            scratchPk = defaultScratchPrimaryKeyProvider.getScratchPk();
        }
        else
        {
            scratchPk = System.currentTimeMillis() + " " + Math.random();;
        }
        return scratchPk;
    }


    /**
     * @param fileToLoad
     * @param processedDeletions
     *
     * @return true if requested object is deleted. Note, the return value from deleting dependency
     *         objects for the requested object is discarded. For example, if you request an invoice
     *         to be deleted, the return value only reflects if that requested invoice was deleted,
     *         and not if the parent customer record of that invoice is deleted.
     */
    private static boolean deleteObjectWorker(String fileToLoad, Set<String> processedDeletions)
    {
//        System.out.println("DOF.deleteObjectWorker, fileToLoad = " + scratchBuilder + ", objectTypePk = " + dofObjectCache);
        ObjectFileInfo objectFileInfo = getObjectFileInfo(fileToLoad);
        String objectType = objectFileInfo.getObjectType();
        String fileType = objectFileInfo.getFileType().toLowerCase();

        DependentObjectHandler doh = DOFGlobalSettings.getInstance().getDependentObjectHandlerForObjectTypeFileType(objectType, fileType);

        // delete passed in object first
        boolean deletedObjectfromFileToLoad;
        Object objectToDelete = dofObjectCache.get(objectFileInfo); // cache will call handler

        if (objectToDelete != null)
        {
            deletedObjectfromFileToLoad = doh.delete(objectToDelete, objectFileInfo);
            if (dofDebug)
            {
                System.out.println("DOF: Deleting: ObjectFileInfo = " + objectFileInfo +
                                   ", deleted = " + deletedObjectfromFileToLoad);
            }
        }
        else
        {
            deletedObjectfromFileToLoad = true; // b/c already deleted
            if (dofDebug)
            {
                System.out.println("Object from file " + fileToLoad + " does not exist. " +
                                   "Continuing deletion of dependencies.");
            }
        }

        if (deletedObjectfromFileToLoad)
        {
            processedDeletions.add(fileToLoad);
        }

//        System.out.println("DOF.deleteObjectWorker removing fileToLoad = " + scratchBuilder);
        if (deletedObjectfromFileToLoad)
        {
            dofObjectCache.remove(objectFileInfo);
            // then delete the dependencies
            deleteDependencies(fileToLoad, processedDeletions);
        }

        return deletedObjectfromFileToLoad;
    }


    private static void deleteDependencies(String fileToLoad, Set<String> processedDeletions)
    {
        String textForFile = DOFGlobalSettings.getResourceAsString(fileToLoad);

        ArrayList<String> dependencies = getRequiredDependecies(textForFile);
        for (int i = dependencies.size() - 1; i >= 0; i--)
        {
            String requiredPath = dependencies.get(i);
            try
            {
                if (!processedDeletions.contains(requiredPath))
                {
                    deleteObjectWorker(requiredPath, processedDeletions);
                }
                //else
                //{
                //    System.out
                //            .println("Duplicate dependency caught on deleting: " +
                //                     requiredPath +
                //                     ", already processed = " +
                //                     processedDeletions);
                //}
            }
            catch (Exception e)
            {
                System.out
                        .println("Could not delete path = " + requiredPath +
                                 ", Possibly other objects depend on this object. " + e);
            }
        }

        ArrayList<String> dependenciesJava = getRequiredDependeciesJava(textForFile);
        for (Iterator<String> iterator = dependenciesJava.iterator(); iterator.hasNext();)
        {
            String className = iterator.next();
            ReferenceBuilder referenceObject =
                    (ReferenceBuilder) getNewInstanceOfClassForNameThrowingRuntimeException(
                            className);
            delete(referenceObject);
        }
    }


    private static void loadDependencies(String fileName, Map<String, String> scratchReferenceToPk)
    {
        String textForFile = DOFGlobalSettings.getResourceAsString(fileName);

        ArrayList<String> dependencies = getRequiredDependecies(textForFile);
        for (Iterator<String> iterator = dependencies.iterator(); iterator.hasNext();)
        {
            String requiredPath = iterator.next();
            if (requiredPath.equals(fileName))
            {
                System.out.println(
                        "WARNING: You listed a file a file as a dependency of itself. Please see: " +
                        fileName);
                continue;
            }

            ObjectFileInfo dependencyObjectFileInfo = getObjectFileInfo(requiredPath);
            if (!dofObjectCache
                    .containsKey(dependencyObjectFileInfo))
            {
                FileToLoadAndScratchReference so = new FileToLoadAndScratchReference();
                so.fileToLoad = requiredPath;
                so.scratchReference = null;
                requireWorker(so, false, scratchReferenceToPk);
            }
        }


        ArrayList<String> dependenciesJava = getRequiredDependeciesJava(textForFile);
        for (Iterator<String> iterator = dependenciesJava.iterator(); iterator.hasNext();)
        {
            String className = iterator.next();
            ReferenceBuilder referenceObject =
                    (ReferenceBuilder) getNewInstanceOfClassForNameThrowingRuntimeException(
                            className);
            require(referenceObject);
        }
    }


    private static Object getNewInstanceOfClassForNameThrowingRuntimeException(String className)
    {
        Object o;
        try
        {
            Class requiredDependency = Class.forName(className);
            o = requiredDependency.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        return o;
    }


    private static void loadDependencyScratchObjects(String fileName,
                                                     Map<String, String> scratchReferenceToPk)
    {

        String textForFile = DOFGlobalSettings.getResourceAsString(fileName);

        ArrayList<FileToLoadAndScratchReference> dependencies =
                getRequiredScratchObjects(textForFile);
        for (Iterator<FileToLoadAndScratchReference> iterator = dependencies.iterator();
             iterator.hasNext();)
        {
            FileToLoadAndScratchReference fileToLoadAndScratchReference = iterator.next();
            String requiredPath = fileToLoadAndScratchReference.fileToLoad;

            if (requiredPath.equals(fileName))
            {
                System.out.println(
                        "WARNING: You listed a file a file as a dependency of itself. Please see: " +
                        fileName);
                continue;
            }

            requireWorker(fileToLoadAndScratchReference, true, scratchReferenceToPk);
            // NEXT line does not make sense
            // TODO -- write test that if you use the the same scratch file multiple times, you only get one object
            // then again, how can you depend on
            //String hashKey = getObjectFileInfo(requiredPath).getKeyForHashing();
            //if (!dofObjectCache.containsKey(hashKey))
            //{
            //    System.out.println("Calling requireWorker  hashKey = " + hashKey + ", scratchReferenceToPK = " + scratchReferenceToPk + ", fileToLoadAnd " + fileToLoadAndScratchReference);
            // requireWorker(fileToLoadAndScratchReference, true, scratchReferenceToPk);
            //}
            //else
            //{
            //    System.out
            //            .println("Duplicate dependency caught on loading: " +
            //                     requiredPath +
            //                     ", already processed");
            //}
        }


        ArrayList<ScratchJavaAndScratchReference> scratchReferenceArrayList =
                getRequiredScratchJavaObjects(textForFile);
        for (Iterator<ScratchJavaAndScratchReference> iterator = scratchReferenceArrayList.iterator();
             iterator.hasNext();)
        {
            ScratchJavaAndScratchReference fileToLoadAndScratchReference = iterator.next();
            ScratchBuilder scratchBuilder = fileToLoadAndScratchReference.scratchBuilder;
            String scratchReference = fileToLoadAndScratchReference.scratchReference;

            Object scratchObject = null;

            // maybe this scratch object was already created
            String scratchRequestedPk = scratchReferenceToPk.get(scratchReference);
            if (scratchRequestedPk != null)
            {
                scratchObject = dofObjectCache.get(scratchBuilder, scratchRequestedPk);
            }

            // If not found, then create
            if (scratchObject == null)
            {
                scratchObject = DOF.createScratchObject(scratchBuilder); // , scratchReferenceToPk);
            }

            scratchReferenceToPk.put(fileToLoadAndScratchReference.scratchReference,
                                     scratchBuilder.extractPrimaryKey(scratchObject) + "");
        }
    }


    private static ArrayList<String> getRequiredDependecies(String requireText)
    {
        checkRepIncludeDependencyInitialized();
        Pattern pattern = repIncludeDependency;
        return getSingleStringMatches(requireText, pattern);
    }


    private static ArrayList<String> getRequiredDependeciesJava(String requireText)
    {
        checkRepIncludeDependencyInitialized();
        Pattern includeDependencyJava = repIncludeDependencyJava;
        return getSingleStringMatches(requireText, includeDependencyJava);
    }


    private static ArrayList<String> getSingleStringMatches(String requireText, Pattern pattern)
    {
        Matcher matcher = pattern.matcher(requireText);
        int pos = 0;
        ArrayList<String> matches = new ArrayList<String>();
        while (pos < requireText.length() && matcher.find(pos))
        {
            String requireMatch = matcher.group(1);
            pos = matcher.end();
            matches.add(requireMatch);
        }
        return matches;
    }



    //static List<DeletableScratchBuilder> scratchJavaObjectsCreated =
    //        new ArrayList<DeletableScratchBuilder>();



    public enum DeletionOption{ all, scratch_only, reference_only };


    /**
     * Deletes all cached (created by the DOF) objects based on the deletion option.
     *
     * First pass will delete the objects individually by order computed by using the
     * ScratchObjectDeletionHelper.getParentDependencyClasses(). I.e., leaf level objects are deleted
     * before objects that have dependencies.
     *
     * Second pass will try to delete any remaining objects individually, which will recursively
     * try to delete dependencies.
     *
     * @param deletionOption specifies to delete all created and referenced objects, or only those
     * that are scratch objects or reference objects.
     */
    public static void deleteAll(DeletionOption deletionOption)
    {

        List<Class> classesInDeletionOrder = DOFGlobalSettings.getInstance().getObjectDeletionOrder();

        // All text scratch objects
            for (Iterator classIterator = classesInDeletionOrder.iterator(); classIterator.hasNext();)
            {
                Class deletionClass = (Class) classIterator.next();
                if (deletionOption == DeletionOption.all ||
                    deletionOption == DeletionOption.scratch_only)
                {
                    deleteObjectsForClass(deletionClass, dofObjectCache.getScratchObjects());
                }
                if (deletionOption == DeletionOption.all ||
                    deletionOption == DeletionOption.reference_only)
                {
                    deleteObjectsForClass(deletionClass, dofObjectCache.getReferenceObjects());
                }
            }
        System.out.println("After delete all");
        System.out.println("dofObjectCache scratch= " + dofObjectCache.getScratchObjects());
        System.out.println("dofObjectCache refs = " + dofObjectCache.getReferenceObjects());

    }


    private static void deleteObjectsForClass(Class deletionClass, DOFObjectCache.MaxSizeObjectCache loadedObjects)
    {
        Set<Map.Entry<String, Object>> entries = loadedObjects.entrySet();
        for (Iterator entryIterator = entries.iterator(); entryIterator.hasNext();)
        {
            Map.Entry<String, Object> stringObjectEntry =
                    (Map.Entry<String, Object>) entryIterator.next();
            Object objectToDelete = (Object) stringObjectEntry.getValue();

            if (deletionClass.isInstance(objectToDelete))
            {
                ObjectDeletionHelper sodh = DOFGlobalSettings.getInstance()
                        .getDeletionHelperForClass(objectToDelete.getClass().getName());
                boolean deletedOk = sodh.delete(objectToDelete);
                if (deletedOk)
                {
                    entryIterator.remove();
                }
            }
        }
    }

    //
            //DOFBuilder dofBuilder = loadedObject.dofBuilder;
            //if (dofBuilder != null)
            //{
            //    if (dofBuilder instanceof DeletableScratchBuilder)
            //    {
            //        boolean removed = ((DeletableScratchBuilder) dofBuilder).deleteCreatedObject();
            //        if (removed)
            //        {
            //            loadedObjectIterator.remove();
            //        }
            //    }
            //}
            //else // filename must not be null
            //{
            //    DependentObjectHandler doh = DOFGlobalSettings.getScratchDeletionHelperForClass(
            //            objectToDelete.getClass());
            //    ObjectFileInfo fileInfo = getObjectFileInfo(loadedObject.fileName);
            //    doh.delete(fileInfo, objectToDelete);

            //}

            //// All java scratch objects
            //for (Iterator deletableScratchObjectIterator = delet.iterator();
            //     deletableScratchObjectIterator.hasNext();)
            //{
            //    DeletableScratchBuilder deletableScratchObject =
            //            (DeletableScratchBuilder) deletableScratchObjectIterator.next();
            //    DOF.delete(deletableScratchObject);
            //}

            //// All text scratch objects
            //for (Iterator deletableScratchObjectIterator = scratchJavaObjectsCreated.iterator();
            //     deletableScratchObjectIterator.hasNext();)
            //{
            //    DeletableScratchBuilder deletableScratchObject =
            //            (DeletableScratchBuilder) deletableScratchObjectIterator.next();
            //    DOF.delete(deletableScratchObject);
            //}
        //
        //
        //if (deletionOption == DeletionOption.all || deletionOption == DeletionOption.reference_only)
        //{
        //    // java objects
        //
        //
        //
        //    // text objects
        //}
    //}




    static class FileToLoadAndScratchReference
    {
        String fileToLoad;
        String scratchReference;


        public int hashCode()
        {
            return (getComparisionKey()).hashCode();
        }


        public boolean equals(Object obj)
        {
            if (obj instanceof FileToLoadAndScratchReference)
            {
                FileToLoadAndScratchReference so = (FileToLoadAndScratchReference) obj;
                return (getComparisionKey()).equals(so.getComparisionKey());
            }
            else
            {
                return false;
            }

        }


        private String getComparisionKey()
        {
            return fileToLoad + ":" + scratchReference;
        }


        public String toString()
        {
            return getComparisionKey();
        }
    }


    static class ScratchJavaAndScratchReference
    {
        ScratchBuilder scratchBuilder;
        String scratchReference;


        public int hashCode()
        {
            return (getComparisionKey()).hashCode();
        }


        public boolean equals(Object obj)
        {
            if (obj instanceof FileToLoadAndScratchReference)
            {
                FileToLoadAndScratchReference so = (FileToLoadAndScratchReference) obj;
                return (getComparisionKey()).equals(so.getComparisionKey());
            }
            else
            {
                return false;
            }
        }


        private String getComparisionKey()
        {
            return scratchBuilder + ":" + scratchReference;
        }


        public String toString()
        {
            return getComparisionKey();
        }
    }


    private static ArrayList<FileToLoadAndScratchReference> getRequiredScratchObjects(String requireText)
    {
        checkRepIncludeDependencyInitialized();
        Matcher matcher = repIncludeScratch.matcher(requireText);
        int pos = 0;
        ArrayList<FileToLoadAndScratchReference> matches =
                new ArrayList<FileToLoadAndScratchReference>();
        while (pos < requireText.length() && matcher.find(pos))
        {
            FileToLoadAndScratchReference fileToLoadAndScratchReference =
                    new FileToLoadAndScratchReference();
            fileToLoadAndScratchReference.fileToLoad = matcher.group(1);
            fileToLoadAndScratchReference.scratchReference = matcher.group(2);
            pos = matcher.end();
            matches.add(fileToLoadAndScratchReference);
        }
        return matches;
    }


    private static ArrayList<ScratchJavaAndScratchReference> getRequiredScratchJavaObjects(String requireText)
    {
        checkRepIncludeDependencyInitialized();
        Matcher matcher = repIncludeScratchJava.matcher(requireText);
        int pos = 0;
        ArrayList<ScratchJavaAndScratchReference> matches =
                new ArrayList<ScratchJavaAndScratchReference>();
        while (pos < requireText.length() && matcher.find(pos))
        {
            ScratchJavaAndScratchReference scratchJavaAndScratchReference =
                    new ScratchJavaAndScratchReference();
            String scratchBuilderClassName = matcher.group(1);
            scratchJavaAndScratchReference.scratchBuilder =
                    (ScratchBuilder) getNewInstanceOfClassForNameThrowingRuntimeException(
                            scratchBuilderClassName);
            scratchJavaAndScratchReference.scratchReference = matcher.group(2);
            pos = matcher.end();
            matches.add(scratchJavaAndScratchReference);
        }
        return matches;
    }


    private static ScratchPkProvider defaultScratchPrimaryKeyProvider;


    static
    {
        String defaultScratchPrimaryKeyProviderClassName =
                DOFGlobalSettings.getInstance().getDefaultScratchPrimaryKeyProviderClassName();
        if (defaultScratchPrimaryKeyProviderClassName != null &&
            defaultScratchPrimaryKeyProviderClassName.trim().length() > 0)
        {
            try
            {
                Class<? extends ScratchPkProvider> scratchClass =
                        (Class<? extends ScratchPkProvider>) Class
                                .forName(defaultScratchPrimaryKeyProviderClassName);
                defaultScratchPrimaryKeyProvider = scratchClass.newInstance();
            }
            catch (Throwable e)
            {
                throw new RuntimeException(e);
            }
        }
    }


}
