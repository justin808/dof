package com.doframework;

import junit.framework.*;

/**
 User: gordonju Date: Feb 1, 2008 Time: 10:09:28 PM
 */
public class DOFTest extends TestCase
{
    public void testGetFileNamePartsHandlesBasicName()
    {
        String fileToLoad = "customer.25.xml";
        String[] parts = DOF.getFileNameParts(fileToLoad);
        assertEquals("customer", parts[0]);
        assertEquals("25", parts[1]);
        assertEquals("xml", parts[2]);
    }

    public void testGetFileNamePartsHandlesNameWithManyPeriods()
    {
        String fileToLoad = "customer.25.2.3.xml";
        String[] parts = DOF.getFileNameParts(fileToLoad);
        assertEquals("customer", parts[0]);
        assertEquals("25.2.3", parts[1]);
        assertEquals("xml", parts[2]);
    }


}
