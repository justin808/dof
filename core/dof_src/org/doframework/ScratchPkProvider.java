package org.doframework;

/**
 * Implement this method to provide a custom scratch PK. If this is not implemented,
 * then the default of System.currTimeInMillis() + "" is used.
 *                                                        *
 */
public interface ScratchPkProvider
{
    /**
     * Implement this provide a scratch primary key.
     * @return
     */
    String getScratchPk();

    
}
