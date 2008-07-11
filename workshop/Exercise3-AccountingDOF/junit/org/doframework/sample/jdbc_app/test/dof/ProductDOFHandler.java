package org.doframework.sample.jdbc_app.test.dof;

import java.io.InputStream;
import java.math.BigDecimal;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.doframework.DependentObjectHandler;
import org.doframework.ObjectFileInfo;
import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.entity.Manufacturer;
import org.doframework.sample.jdbc_app.entity.Product;
import org.doframework.sample.jdbc_app.factory.ManufacturerFactory;
import org.doframework.sample.jdbc_app.factory.ProductFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class ProductDOFHandler implements DependentObjectHandler
{


    ProductFactory m_productComponent = GlobalContext.getFactoryLookupService().getProductFactory();

    /**
     * @param xmlDescriptionFile xml file describing the product
     *
     * @return a Manufacturer object with corresponding name and id
     */
    public Product createProduct(String xmlDescriptionFile)
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
            ManufacturerFactory manufacturerFactory =
                    GlobalContext.getFactoryLookupService().getManufacturerFactory();
            Manufacturer manufacturer = manufacturerFactory.get(manufacturerId + "");
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
        Product product = createProduct(objectFileInfo.getFileToLoad());
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
        return m_productComponent.getById(Integer.parseInt(objectFileInfo.getPk()));
    }

    /**
     * Delete the object with the given pk
     *
     * @param objectFileInfo
     * @param objectToDelete
     */
    public boolean delete(ObjectFileInfo objectFileInfo, Object objectToDelete)
    {
        Product product = m_productComponent.getById(Integer.parseInt(objectFileInfo.getPk()));
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
