package org.doframework.sample.xml_handler;

import org.doframework.*;
import org.doframework.annotation.*;
import org.doframework.sample.component.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;

@TargetClass(Manufacturer.class)
public class ManufacturerXmlHandler implements DependentObjectHandler, DeletionHelper, ScratchPkProvider
{

    ManufacturerComponent manufacturerComponent = ComponentFactory.getManufacturerComponent();


    public Manufacturer createManufacturer(ObjectFileInfo ofi)
    {
        InputStream is = ofi.getFileContentsAsInputStream();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document document = documentBuilder.parse(is);
            XPathFactory pathFactory = XPathFactory.newInstance();
            XPath xPath = pathFactory.newXPath();
            Element item = document.getDocumentElement();
            String name = xPath.evaluate("/manufacturer/name", item);
            Manufacturer manufacturer = manufacturerComponent.createNew();
            manufacturer.setName(name);
            return manufacturer;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    public Object create(ObjectFileInfo ofi)
    {
        Manufacturer manufacturer = createManufacturer(ofi);
        manufacturer.setNew(true);
        manufacturerComponent.persist(manufacturer);
        return manufacturer;
    }


    public Object get(ObjectFileInfo ofi)
    {
        return manufacturerComponent.getByName(ofi.getPk());
    }




    public Class getCreatedClass()
    {
        return Manufacturer.class;
    }



    public boolean delete(Object object)
    {
        return manufacturerComponent.delete((Manufacturer) object);
    }


    public boolean okToDelete(Object object)
    {
        return !manufacturerComponent.hasProducts((Manufacturer)object);
    }


    public Object[] getReferencedObjects(Object object)
    {
        return null;
    }


    public Class[] getReferencedClasses()
    {
        return null;
    }


    public Object extractPrimaryKey(Object object)
    {
        return (((Manufacturer) object).getName());
    }


    public String getScratchPk()
    {
        String big = "Manufacturer: " + System.currentTimeMillis() + " " + Math.random();
        return big;
    }


}
