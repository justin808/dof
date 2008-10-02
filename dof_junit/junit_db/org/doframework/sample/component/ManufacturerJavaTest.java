package org.doframework.sample.component;

import org.doframework.*;
import org.doframework.sample.component.reference.*;
import org.doframework.sample.global.*;
import org.doframework.sample.persistence.*;
import static org.junit.Assert.*;
import org.junit.*;

public class ManufacturerJavaTest
{

    @Test
    public void testReferenceManufacturer()
    {
        Manufacturer dofManufacturer = (Manufacturer) DOF.require(new Manufacturer_TinyJuice());
        assertEquals("Tiny Juice", dofManufacturer.getName());
        ManufacturerPersistence manufacturerPersistence = GlobalContext.getPersistanceFactory().getManufacturerPersistence();
        Manufacturer manuallyRetrievedManufacturer = manufacturerPersistence.getByName("Tiny Juice");
        assertEquals(manuallyRetrievedManufacturer, dofManufacturer);
    }


}
