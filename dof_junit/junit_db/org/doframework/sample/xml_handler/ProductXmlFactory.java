package org.doframework.sample.xml_handler;

import org.w3c.dom.*;
import org.doframework.sample.component.*;
import org.doframework.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;

public class ProductXmlFactory implements DependentObjectHandler, ScratchPkProvider,
                                          ObjectDeletionHelper
{


    ProductComponent productComponent = ComponentFactory.getProductComponent();


    /**
     * @return a Manufacturer object with corresponding name and id
     */
    Product createProduct(ObjectFileInfo ofi)
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
            String sId = xPath.evaluate("/product/id", item);
            int id = Integer.parseInt(sId);
            String name = xPath.evaluate("/product/name", item);
            String sPrice = xPath.evaluate("/product/price", item);
            Integer price = new Integer(sPrice);
            String sManufacturerId = xPath.evaluate("/product/manufacturer_id", item);
            int manufacturerId = Integer.parseInt(sManufacturerId);
            ManufacturerComponent manufacturerComponent =
                    ComponentFactory.getManufacturerComponent();
            Manufacturer manufacturer = manufacturerComponent.getById(manufacturerId);
            if (manufacturer == null)
            {
                throw new RuntimeException("Manufacturer with id " + manufacturerId + " does not exist!");
            }
            return new Product(id, name, price, manufacturer);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    public Object create(ObjectFileInfo ofi)
    {
        Product product = createProduct(ofi);
        product.setNew(true);
        productComponent.persist(product);
        return product;
    }


    public Object get(ObjectFileInfo ofi)
    {
        return productComponent.getById(Integer.parseInt(ofi.getPk()));
    }




    public boolean delete(Object product, ObjectFileInfo objectFileInfo)
    {
        return productComponent.delete((Product) product);
    }


    public boolean delete(Object object)
    {
        return delete(object, null);
    }


    /** @return The class object for the class that is returned from this handler. */
    public Class getCreatedClass()
    {
        return Product.class;
    }


    public String getScratchPk()
    {
        String big = System.currentTimeMillis() + "";
        String rightDigits = big.substring(big.length() - 8);
        return rightDigits;
    }


    /**
     * @param scratchObject which is the type that the given handler creates
     *
     * @return The dependencies of the given scratch object
     */
    public Object[] getDependencies(Object scratchObject)
    {
        return new Object[]{((Product) scratchObject).getManufacturer()};
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
        return new Class[] { Manufacturer.class};
    }


    public Object extractPrimaryKey(Object object)
    {
        return new Integer(((Product) object).getId());
    }


}
