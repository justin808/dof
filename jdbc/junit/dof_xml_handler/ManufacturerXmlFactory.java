package dof_xml_handler;

import com.doframework.*;
import component.*;
import entity.*;
import global.*;
import org.w3c.dom.*;

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
     * @param fileToLoad File name in form: {objectType}.{objectPk}.{fileType}
     *
     * @return The type of object being created and saved in the DB.
     */
    public Object create(String fileToLoad)
    {
        Manufacturer manufacturer = createManufacturer(fileToLoad);

        manufacturerComponent.insert(manufacturer);
        return manufacturer;
    }

    /**
     * Return the object, if it exists, with the given PK
     *
     * @param pk The primary key of the object to retrieve
     *
     * @return The object created from the db if it existed, or else null
     */
    public Object get(String pk)
    {
        return manufacturerComponent.get(pk);
    }

    /**
     * Delete the object with the given pk.
     * <p/>
     * It is critical that this method either check dependencies (products that depend on this
     * record) or depend on getting the sql exception from integrity checks.
     *
     * @param pk The primary key of the object to delete
     */
    public boolean delete(String pk)
    {
        Manufacturer manufacturer = manufacturerComponent.get(pk);
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
