package dof_xml_handler;

import component.*;
import entity.*;
import global.*;
import org.w3c.dom.*;
import org.doframework.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;

public class ManufacturerXmlFactory implements DependentObjectHandler
{

    ManufacturerComponent manufacturerComponent =
            GlobalContext.getComponentFactory().getManufacturerComponent();


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
        Manufacturer manufacturer = createManufacturer(objectFileInfo.fileToLoad);

        manufacturerComponent.insert(manufacturer);
        return manufacturer;
    }

    /**
     * Return the object, if it exists, with the given PK
     *
     * @param objectFileInfo
     */
    public Object get(ObjectFileInfo objectFileInfo)
    {
        return manufacturerComponent.get(objectFileInfo.pk);
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
        Manufacturer manufacturer = manufacturerComponent.get(objectFileInfo.pk);
        if (manufacturer != null)
        {
            return manufacturerComponent.delete(manufacturer);
        }
        else
        {
            return false;
        }

    }

}
