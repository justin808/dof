package org.doframework;


/**
 * If you wish to allow the DOF to delete created scratch objects, then implement this interface. In some test systems,
 * the schema is often recreated, so there is no need to ever delete scratch objects.
 *
 * Unlike ReferenceBuilder objects which have a defined primary key, you should creat a different
 * ScratchBuilder object for each object created if you wish to performan
 *
 *

 */
public interface DeletableScratchBuilder extends ScratchBuilder
{

   // /**
   //  * @return The object that was created with this DOF Scratch Builder instance, or null if the object was deleted
   //  */
   // Object getCreatedObject();
   //
   //
   // /**
   //  * The implementor must have kept track of the object that was created and if this object was already deleted.
   //  * If the object is already deleted, then false must be returned, and the stored reference to the object that
   //  * was created should be cleared. This is because this method may get called multiple time, even though the object
   //  * may have been deleted.
   //  *
   //  * Implementation may look like this.
   //  * <pre>
   //boolean deleteCreatedObject() {
   //  if (customerLastCreated != null)
   //  {
   //     boolean deleted = ComponentFactory.getCustomerComponent().delete(customerLastCreated);
   //     if (deleted)
   //     {
   //       customerLastCreated = null;
   //       return true;
   //     } // else return false
   //  }
   //  return false;
   //}
   //
   //  </pre>
   //  *
   //  * @return true if the object was deleted, or false if the object could not be deleted because it does not exist,
   //  * or because other objects depend on it.
   //  */
   // boolean deleteCreatedObject();
   //
   //
   // /**
   //  * The scratch object must keep track of what was created and what dependencies there were.
   //  *
   //  * @return list of scratch objects that the created object directly created
   //  */
   // DeletableScratchBuilder[] getScratchJavaBuilderDependencies();
   //
   //
   // /**
   //  * This method defines what other objects need to be created (persisted) before this object is
   //  * created. This method is used by the DOF for cleanup purposes.
   //  *
   //  * @return Array of reference objects that this object directly depends on.
   //  */
   // ReferenceBuilder[] getReferenceJavaBuilderDependencies();
   //
   //
}
