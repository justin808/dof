package sample;

import org.doframework.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;


public class ScratchObjectTest
{
    @Test
    public void testReferenceInvoiceDependsOnJavaReferences()
    {
        ScratchBuilder scratchManufacturer = new ManufacturerScratchBuilder();
        Manufacturer manufacturer1 = (Manufacturer) DOF.createScratchObject(scratchManufacturer);
        Manufacturer manufacturer2 = (Manufacturer) DOF.createScratchObject(scratchManufacturer);
        assertFalse(manufacturer1.getName().equals(manufacturer2.getName()));

        Manufacturer m = (Manufacturer) scratchManufacturer.fetch(manufacturer1.getName());
        assertNotNull(m);
    }





}
