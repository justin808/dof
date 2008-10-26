package org.doframework;

import org.junit.*;
import org.doframework.annotation.*;
import sample.*;
import static org.junit.Assert.*;

/** User: gordonju Date: Sep 20, 2008 Time: 12:26:49 PM */
public class HandlerMappingsTest
{
    @BeforeClass
    public static void before()
    {
        DOFGlobalSettings.setDofDir("core/dof_junit/sample");
    }

    @Test
    public void testLoadDeletionHelpers()
    {
        DeletionHelper deletionHelper =
                DOFGlobalSettings.getInstance().getDeletionHelperForClass(Prod.class);
        assertEquals(sample.ProdDeletionHelper.class, deletionHelper.getClass());
        TargetReferencedClasses annotation =
                deletionHelper.getClass().getAnnotation(TargetReferencedClasses.class);
        Class[] classes = annotation.value();
        assertEquals(Manu.class, classes[0]);
    }

    //public void testDOFPropertiesIsolatedProperly()
    //{
    //
    //
    //}
    //
    //
    //public void testDOFScratchDeletionHelpersIsolatedProperly()
    //{
    //
    //
    //}


}
