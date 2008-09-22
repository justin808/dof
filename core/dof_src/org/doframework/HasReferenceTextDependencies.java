package org.doframework;

public interface HasReferenceTextDependencies
{
    /**
     * Used to mix in text dependencies with Java definitions of dependent objects.
     * @return list of file paths to process before this object is created.
     */
    String[] getReferenceTextDependencies();


    ///**
    // * Used to list of scratch objects created by text.
    // * @return Array of Object
    // */
    //Object[] getScratchTextDependencies();
    //

}
