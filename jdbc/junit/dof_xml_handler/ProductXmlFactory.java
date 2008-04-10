package dof_xml_handler;

import component.*;
import entity.*;
import global.*;
import org.w3c.dom.*;
import org.doframework.*;

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
     * @param objectFileInfo
     */
    public Object create(ObjectFileInfo objectFileInfo)
    {
        Product product = createProduct(objectFileInfo.fileToLoad);
        m_productComponent.insert(product);
        return product;
    }

    /**
     * Return the object, if it exists, with the given PK
     *
     * @param objectFileInfo
     */
    public Object get(ObjectFileInfo objectFileInfo)
    {
        return m_productComponent.getById(Integer.parseInt(objectFileInfo.pk));
    }

    /**
     * Delete the object with the given pk
     *
     * @param objectFileInfo
     * @param objectToDelete
     */
    public boolean delete(ObjectFileInfo objectFileInfo, Object objectToDelete)
    {
        Product product = m_productComponent.getById(Integer.parseInt(objectFileInfo.pk));
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
