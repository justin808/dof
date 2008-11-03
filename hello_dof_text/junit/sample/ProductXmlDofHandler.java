package sample;

import org.doframework.*;
import org.doframework.annotation.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;

@TargetClass(Product.class)

public class ProductXmlDofHandler implements DependentObjectHandler, DeletionHelper
{
    private static final Long LONG_ZERO = new Long(0);

    public Object create(ObjectFileInfo objectFileInfo)
    {
        Product product = null;
        InputStream is = objectFileInfo.getFileContentsAsInputStream();
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
            String queryText = "select manu from Manufacturer as manu where manu.name = ?1";
            Object[] args = new Object[]{manufacturerName};
            Manufacturer manufacturer = (Manufacturer) JpaUtility.fetchObject(queryText, args);
            if (manufacturer == null)
            {
                throw new RuntimeException(
                        "Manufacturer with name " + manufacturerName + " does not exist!");
            }
            product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setManufacturer(manufacturer);
            JpaUtility.persistObject(product);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return product;
    }

    /**
     * @return String[] { manufacturerer_name, product_name }
     */
    static String[] getManufacturerProductName(String manuNameProductName)
    {
        int posDoubleUnderscore = manuNameProductName.indexOf("__");
        if (posDoubleUnderscore < 0)
        {
            throw new RuntimeException("Could not parse __ from " + manuNameProductName);
        }
        String manuName = manuNameProductName.substring(0, posDoubleUnderscore);
        String prodName = manuNameProductName.substring(posDoubleUnderscore + 2);
        return new String[] { manuName, prodName};
    }


    public Object get(ObjectFileInfo objectFileInfo)
    {
        String[] manuProdName = getManufacturerProductName(objectFileInfo.getPk());
        String queryText = "select prod from Product as prod " +
                           " where prod.manufacturer.name = ?1" +
                           " and prod.name = ?2";
        Object[] args = new Object[]{manuProdName[0], manuProdName[1]};
        return JpaUtility.fetchObject(queryText, args);
    }


    public boolean delete(Object object)
    {
        return JpaUtility.deleteObject(object);
    }


    public boolean okToDelete(Object object)
    {
        // next line needs to change if invoices and line items get added to the model
        return true;
    }


    public Object extractPrimaryKey(Object object)
    {
        return ((Manufacturer) object).getName();
    }


    public Object[] getReferencedObjects(Object object)
    {
        return new Object[]{((Product) object).getManufacturer()};
    }
}