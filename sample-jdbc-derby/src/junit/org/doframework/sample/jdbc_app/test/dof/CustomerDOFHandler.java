package org.doframework.sample.jdbc_app.test.dof;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.doframework.DependentObjectHandler;
import org.doframework.ObjectFileInfo;
import org.doframework.ScratchPkProvider;
import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.entity.Customer;
import org.doframework.sample.jdbc_app.factory.CustomerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class CustomerDOFHandler implements DependentObjectHandler, ScratchPkProvider
{

    CustomerFactory customerFactory =
            GlobalContext.getComponentFactory().getCustomerFactory();


    /**
     * @param xmlDescriptionFile File describing the customer record
     *
     * @return a Customer object with corresponding name and id
     */
    public Customer createCustomer(String xmlDescriptionFile)
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
            String sId = xPath.evaluate("/customer/customer_id", item);
            int id = Integer.parseInt(sId);
            String name = xPath.evaluate("/customer/name", item);
            String phoneNumber = xPath.evaluate("/customer/phone_number", item);
            Customer customer = new Customer(id);
            customer.setName(name);
            customer.setPhoneNumber(phoneNumber);
            return customer;
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
        Customer Customer = createCustomer(objectFileInfo.getFileToLoad());

        customerFactory.insert(Customer);
        return Customer;
    }

    /**
     * Return the object, if it exists, with the given PK
     *
     * @param objectFileInfo
     */
    public Object get(ObjectFileInfo objectFileInfo)
    {
        return customerFactory.getById(Integer.parseInt(objectFileInfo.getPk()));
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
        int id = Integer.parseInt(objectFileInfo.getPk());
        Customer customer = customerFactory.getById(id);
        if (customer != null)
        {
            return customerFactory.delete(customer);
        }
        else
        {
            return false;
        }

    }

    public String getScratchPk()
    {
        String big = System.currentTimeMillis() + "";
        String rightDigits = big.substring(big.length() -8 );
        return rightDigits;
    }

}
