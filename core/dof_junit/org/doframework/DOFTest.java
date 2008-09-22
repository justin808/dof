package org.doframework;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 IMPT: You need this VM Param set before running this test
    -DDOF_DIR=/home/gordonju/dev/DOF/core/dof
 */
public class DOFTest
{
    @Test
    public void testGetObjectFileInfoHandlesBasicName()
    {
        String fileToLoad = "customer.25.xml";
        ObjectFileInfo ofi = DOF.getObjectFileInfo(fileToLoad);
        assertEquals("customer", ofi.getObjectType());
        assertEquals("25", ofi.getPk());
        assertEquals("xml", ofi.getFileType());
    }


    @Test
    public void testGetObjectFileInfoHandlesNameWithManyPeriods()
    {
        String fileToLoad = "customer.25.2.3.xml";
        ObjectFileInfo ofi = DOF.getObjectFileInfo(fileToLoad);
        assertEquals("customer", ofi.getObjectType());
        assertEquals("25.2.3", ofi.getPk());
        assertEquals("xml", ofi.getFileType());
    }


    @Test
    public void testGetPropertiesErrorMessageWhenPropertiesFile()
    {
        if (DOFGlobalSettings.DOF_DIR.length() == 0)
        {
            fail("Please define DOF_DIR when invoking the test runner. Use VM parameter -DDOF_DIR={directory to core/test_data}");
        }
        final String resourceAsString = DOF.getResourceAsString("product.41.xml");
        assertNotNull(resourceAsString);
    }


    @Test
    public void testGetFileWhenUnderSubdirectoryUsingDofDefsDir()
    {
        if (DOFGlobalSettings.DOF_DIR.length() == 0)
        {
            fail("Please define DOF_DIR when invoking the test runner. Use VM parameter -DDOF_DIR={directory to core/dof/test_data}");
        }
        final String filePath = "manufacturer.40.xml";
        final String resourceAsString = DOF.getResourceAsString(filePath);
        assertNotNull(resourceAsString);
        ObjectFileInfo ofi = DOF.getObjectFileInfo(filePath);
        assertEquals("manufacturer", ofi.getObjectType());
        assertEquals("40", ofi.getPk());
        assertEquals("xml", ofi.getFileType());
    }


    @Test
    public void testGetFileWhenUnderSubdirectoryUsingClassPath()
    {
        String oldValue = "";
        if (DOFGlobalSettings.DOF_DIR.length() > 0)
        {
            oldValue = System.setProperty("DOF_DIR", "");
        }
        final String filePath = "manufacturer.40.xml";
        final String resourceAsString = DOF.getResourceAsString(filePath);
        assertNotNull(resourceAsString);
        System.setProperty("DOF_DIR", oldValue);
    }


    @Test
    public void testGetClassNameWorkWithRegexp()
    {
        String objectType = "product";
        String fileType = "xml";
        String className = DOFGlobalSettings.getInstance().getDOFHandlerClassName(objectType, fileType);
        assertEquals("org.doframework.sample.xml_handler.ProductXmlFactory", className);
    }


    //@Test
    //public void testMaxCachedObjectsNotExceeded()
    //{
    //    fail("blah");
    //
    //}

}
