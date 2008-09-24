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
        Manufacturer dofManufacturer = (Manufacturer) DOF.require(new Manufacturer_50());
        assertEquals(50, dofManufacturer.getId());
        ManufacturerPersistence manufacturerPersistence = GlobalContext.getPersistanceFactory().getManufacturerPersistence();
        Manufacturer manuallyRetrievedManufacturer = manufacturerPersistence.getById(50);
        assertEquals(manuallyRetrievedManufacturer, dofManufacturer);
    }


}
