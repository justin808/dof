package org.doframework.sample.persistence;

/**
 * Base interface for all persistence classes. Provides next id service for the entity type.
 */
public interface BasePersistence
{
    /**
     * Gets the next unique ID
     *
     * @return next unique ID
     */
    int getNextId();

}
