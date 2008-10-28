package sample;

import org.doframework.*;
import org.doframework.annotation.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import javax.persistence.*;
import java.io.*;

@TargetClass(sample.Manufacturer.class)

public class ManufacturerXmlDofHandler implements DependentObjectHandler, DeletionHelper
{
    private static final Long LONG_ZERO = new Long(0);


    public Object create(ObjectFileInfo objectFileInfo)
    {
        InputStream is = objectFileInfo.getFileContentsAsInputStream();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Manufacturer manufacturer = null;
        try
        {
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document document = documentBuilder.parse(is);
            XPathFactory pathFactory = XPathFactory.newInstance();
            XPath xPath = pathFactory.newXPath();
            Element item = document.getDocumentElement();
            String name = xPath.evaluate("/manufacturer/name", item);
            manufacturer = new Manufacturer();
            manufacturer.setName(name);
            JpaUtility.persistObject(manufacturer);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return manufacturer;
    }


    public Object get(ObjectFileInfo objectFileInfo)
    {
        String queryText = "select manu from Manufacturer as manu where manu.name = ?1";
        Object[] args = new Object[]{objectFileInfo.getPk()};
        return JpaUtility.fetchObject(queryText, args);
    }


    public boolean delete(Object object)
    {
        return JpaUtility.deleteObject(object);
    }


    public boolean okToDelete(Object object)
    {
        String queryText =
                "select count(prod) from Product as prod where prod.manufacturerByManufacturerId.id = :manuId";
        Query query = JpaUtility.getEntityManager()
                .createQuery(queryText);
        query.setParameter("manuId", ((Manufacturer) object).getId());
        Object result = query.getSingleResult();
        //System.out.println("result = " + result);
        return ((result.equals(LONG_ZERO)));
    }


    public Object extractPrimaryKey(Object object)
    {
        return ((Manufacturer) object).getName();
    }


    public Object[] getReferencedObjects(Object object)
    {
        return new Object[0];
    }
}
