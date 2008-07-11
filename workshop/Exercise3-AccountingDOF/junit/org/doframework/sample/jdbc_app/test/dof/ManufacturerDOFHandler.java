package org.doframework.sample.jdbc_app.test.dof;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.doframework.DependentObjectHandler;
import org.doframework.ObjectFileInfo;
import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.entity.Manufacturer;
import org.doframework.sample.jdbc_app.factory.ManufacturerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ManufacturerDOFHandler implements DependentObjectHandler
{

    ManufacturerFactory manufacturerFactory =
            GlobalContext.getFactoryLookupService().getManufacturerFactory();


    /**
     * @param xmlDescriptionFile
     *
     * @return a Manufacturer object with corresponding name and id
     */
    public Manufacturer createManufacturer(String xmlDescriptionFile)
    {
        InputStream is = ClassLoader.getSystemResourceAsStream(xmlDescriptionFile);

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

    /**
     * Insert the object into the DB. No check is done to see if the object already exists.
     *
     * @param objectFileInfo
     */
    public Object create(ObjectFileInfo objectFileInfo)
    {
        Manufacturer manufacturer = createManufacturer(objectFileInfo.getFileToLoad());

        manufacturerFactory.insert(manufacturer);
        return manufacturer;
    }

    /**
     * Return the object, if it exists, with the given PK
     *
     * @param objectFileInfo
     */
    public Object get(ObjectFileInfo objectFileInfo)
    {
        return manufacturerFactory.get(objectFileInfo.getPk());
    }

    /**
     * Delete the object with the given pk.
     * <p/>
     * It is critical that this method either check dependencies (products that depend on this
     * record) or depend on getting the sql exception from integrity checks.
     *
     * @param objectFileInfo
     * @param objectToDelete
     */
    public boolean delete(ObjectFileInfo objectFileInfo, Object objectToDelete)
    {
        Manufacturer manufacturer = manufacturerFactory.get(objectFileInfo.getPk());
        if (manufacturer != null)
        {
            return manufacturerFactory.delete(manufacturer);
        }
        else
        {
            return false;
        }

    }

}
