package org.doframework.sample.xml_handler;

import org.doframework.*;
import org.doframework.sample.component.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;

public class ManufacturerXmlFactory implements DependentObjectHandler, ObjectDeletionHelper
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
            String sId = xPath.evaluate("/manufacturer/id", item);
            int id = Integer.parseInt(sId);
            String name = xPath.evaluate("/manufacturer/name", item);
            return new Manufacturer(id, name);
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
        return manufacturerComponent.get(Integer.parseInt(ofi.getPk()));
    }


    public boolean delete(Object o)
    {
        return manufacturerComponent.delete((Manufacturer) o);
    }


    /** @return The class object for the class that is returned from this handler. */
    public Class getCreatedClass()
    {
        return Manufacturer.class;
    }


    /**
     * This method is used for deletion of scratch objects. Thus, it is not necessary to implement
     * it if you are not concerned with deleting scratch objects.
     *
     * @param object Which is the type that the given handler is mapped to.
     *
     * @return The dependencies of the given object
     */
    public Object[] getDependencies(Object object)
    {
        return null;
    }


    /**
     * Information on what parent dependencies this object has are used to for operation
     * DOF.deleteAll(). The dependencies are gathered and objects are deleted in the correct order
     * to avoid conflicts.
     *
     * @return
     */
    public Class[] getParentDependencyClasses()
    {
        return null;
    }


    public Object extractPrimaryKey(Object object)
    {
        return new Integer(((Manufacturer)object).getId());
    }
}
