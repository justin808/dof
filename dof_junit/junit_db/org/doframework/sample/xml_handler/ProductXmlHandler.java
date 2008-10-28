package org.doframework.sample.xml_handler;

import org.w3c.dom.*;
import org.doframework.sample.component.*;
import org.doframework.sample.persistence.*;
import org.doframework.sample.global.*;
import org.doframework.*;
import org.doframework.annotation.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;

@TargetClass(Product.class)
@TargetReferencedClasses({Manufacturer.class})

public class ProductXmlHandler implements DependentObjectHandler, ScratchPkProvider, DeletionHelper
{


    ProductComponent productComponent = ComponentFactory.getProductComponent();


    /**
     * @return a Manufacturer object with corresponding name and id
     */
    public Product createProduct(ObjectFileInfo ofi)
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
            String name = xPath.evaluate("/product/name", item);
            String sPrice = xPath.evaluate("/product/price", item);
            Integer price = new Integer(sPrice);
            String manufacturerName = xPath.evaluate("/product/manufacturer_name", item);
            ManufacturerComponent manufacturerComponent =
                    ComponentFactory.getManufacturerComponent();
            Manufacturer manufacturer = manufacturerComponent.getByName(manufacturerName);
            if (manufacturer == null)
            {
                throw new RuntimeException("Manufacturer with name " + manufacturerName + " does not exist!");
            }
            Product product = productComponent.createNew();
            product.setName(name);
            product.setManufacturer(manufacturer);
            product.setPrice(price);
            return product;
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
        String manuNameProductName = ofi.getPk();
        return ProductXmlHandler
                .getProductFromManufacturerNameProductName(manuNameProductName);
    }


    public boolean delete(Object object)
    {
        return productComponent.delete((Product) object);
    }


    public boolean okToDelete(Object object)
    {
        return !productComponent.hasInvoices((Product) object);
    }


    public String getScratchPk()
    {
        String big = "Product: " + System.currentTimeMillis() + " " + Math.random();
        //String rightDigits = big.substring(big.length() - 8);
        return big; // rightDigits;
    }


    /**
     * @param scratchObject which is the type that the given handler creates
     *
     * @return The dependencies of the given scratch object
     */
    public Object[] getReferencedObjects(Object scratchObject)
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
    public Class[] getReferencedClasses()
    {
        return new Class[] { Manufacturer.class};
    }


    public Object extractPrimaryKey(Object object)
    {
        Product product = (Product) object;
        return product.getManufacturer().getName() + "__" + product.getName();
    }


    static Product getProductFromManufacturerNameProductName(String manuNameProductName)
    {
        ProductPersistence productPersistence = GlobalContext.getPersistanceFactory().getProductPersistence();
        int posDoubleUnderscore = manuNameProductName.indexOf("__");
        if (posDoubleUnderscore < 0)
        {
            throw new RuntimeException("Could not parse __ from " + manuNameProductName);
        }
        String manuName = manuNameProductName.substring(0, posDoubleUnderscore);
        String prodName = manuNameProductName.substring(posDoubleUnderscore + 2);
        Product product = productPersistence.getByManufacturerNameProductName(manuName, prodName);
        //if (product == null)
        //{
        //    throw new RuntimeException("Could not find product with name: " + prodName +
        //                               " from manufacturer: " + manuName);
        //}
        return product;
    }
}
