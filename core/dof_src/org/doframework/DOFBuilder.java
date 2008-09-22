package org.doframework;

/**
 * This is the superclass of both ReferenceBuilder and ScratchBuilder.
 */
public interface DOFBuilder
{

    /**
     * @return The class object for the class that is returned from this builder.
     */
    Class getCreatedClass();


}
