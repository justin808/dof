package org.doframework;

import junit.framework.*;

/**
 User: gordonju Date: Feb 1, 2008 Time: 10:09:28 PM
 */
public class DOFTest extends TestCase
{
    public void testGetFileNamePartsHandlesBasicName()
    {
        String fileToLoad = "customer.25.xml";
        FileNameParts parts = DOF.getFileNameParts(fileToLoad);
        assertEquals("customer", parts.objectType);
        assertEquals("25", parts.pk);
        assertEquals("xml", parts.fileType);
    }

    public void testGetFileNamePartsHandlesNameWithManyPeriods()
    {
        String fileToLoad = "customer.25.2.3.xml";
        FileNameParts fnp = DOF.getFileNameParts(fileToLoad);
        assertEquals("customer", fnp.objectType);
        assertEquals("25.2.3", fnp.pk);
        assertEquals("xml", fnp.fileType);
    }

    public void testGetPropertiesErrorMessageWhenPropertiesFile()
    {
        if (DOF.DOF_DEFS_DIR.length() == 0)
        {
            fail("Please define DOF_DIR when invoking the test runner. Use VM parameter -DDOF_DIR={directory to core/test_data}");
        }
        final String resourceAsString = DOF.getResourceAsString("product.40.xml");
        assertNotNull(resourceAsString);
    }


    public void testGetFileWhenUnderSubdirectoryUsingDofDefsDir()
    {
        if (DOF.DOF_DEFS_DIR.length() == 0)
        {
            fail("Please define DOF_DIR when invoking the test runner. Use VM parameter -DDOF_DIR={directory to core/test_data}");
        }
        final String filePath = "manufacturers/manufacturer.48.xml";
        final String resourceAsString = DOF.getResourceAsString(filePath);
        assertNotNull(resourceAsString);
        FileNameParts fnp = DOF.getFileNameParts(filePath);
        assertEquals("manufacturer", fnp.objectType);
        assertEquals("48", fnp.pk);
        assertEquals("xml", fnp.fileType);
    }


    public void testGetFileWhenUnderSubdirectoryUsingClassPath()
    {
        String oldValue = "";
        if (DOF.DOF_DEFS_DIR.length() > 0)
        {
            oldValue = System.setProperty("DOF_DIR", "");
        }
        final String filePath = "manufacturers/manufacturer.48.xml";
        final String resourceAsString = DOF.getResourceAsString(filePath);
        assertNotNull(resourceAsString);
        System.setProperty("DOF_DIR", oldValue);
    }

    public void testGetClassNameWorkWithRegexp()
    {
        String objectType = "ABC";
        String fileType = "xml";
        String className = HandlerMappings.getHandlerClassNameForObject(objectType, fileType);
        assertEquals("dof_xml_handler.GenericXmlFactory", className);
    }


}
