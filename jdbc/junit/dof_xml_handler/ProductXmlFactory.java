package dof_xml_handler;

import com.doframework.*;
import component.*;
import entity.*;
import global.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.math.*;

public class ProductXmlFactory implements DependentObjectHandler
{


    ProductComponent m_productComponent = GlobalContext.getComponentFactory().getProductComponent();

    /**
     * @param xmlDescriptionFile xml file describing the product
     *
     * @return a Manufacturer object with corresponding name and id
     */
    Product createProduct(String xmlDescriptionFile)
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
            String sId = xPath.evaluate("/product/id", item);
            int id = Integer.parseInt(sId);
            String name = xPath.evaluate("/product/name", item);
            String sPrice = xPath.evaluate("/product/price", item);
            BigDecimal price = new BigDecimal(sPrice);
            String sManufacturerId = xPath.evaluate("/product/manufacturer_id", item);
            int manufacturerId = Integer.parseInt(sManufacturerId);
            ManufacturerComponent manufacturerComponent =
                    GlobalContext.getComponentFactory().getManufacturerComponent();
            Manufacturer manufacturer = manufacturerComponent.get(manufacturerId + "");
            if (manufacturer == null)
            {
                throw new RuntimeException("Manufacturer with id " +
                                           manufacturerId +
                                           " does not exist!");
            }
            return new Product(id, name, price, manufacturer);
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
        Product product = createProduct(fileToLoad);
        m_productComponent.insert(product);
        return product;
    }

    /**
     * Return the object, if it exists, with the given PK
     *
     * @param id The primary key of the object to retrieve
     *
     * @return The object created from the db if it existed, or else null
     */
    public Object get(String id)
    {
        return m_productComponent.getById(Integer.parseInt(id));
    }

    /**
     * Delete the object with the given pk
     *
     * @param id The primary key of the object to delete
     */
    public boolean delete(String id)
    {
        Product product = m_productComponent.getById(Integer.parseInt(id));
        if (product != null)
        {
            return m_productComponent.delete(product);
        }
        else
        {
            return false;
        }
    }
}
