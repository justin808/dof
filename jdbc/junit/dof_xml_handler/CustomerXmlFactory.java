package dof_xml_handler;

import component.*;
import entity.*;
import global.*;
import org.w3c.dom.*;
import org.doframework.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;

public class CustomerXmlFactory implements DependentObjectHandler
{

    CustomerComponent customerComponent =
            GlobalContext.getComponentFactory().getCustomerComponent();


    /**
     * @param inputStream
     * @return a Customer object with corresponding name and id
     */
    public Customer createCustomer(InputStream inputStream)
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
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
        Customer Customer = createCustomer(objectFileInfo.getFileContentsAsInputStream());

        customerComponent.insert(Customer);
        return Customer;
    }

    /**
     * Return the object, if it exists, with the given PK
     *
     * @param objectFileInfo
     */
    public Object get(ObjectFileInfo objectFileInfo)
    {
        return customerComponent.getById(Integer.parseInt(objectFileInfo.getPk()));
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
        Customer customer = customerComponent.getById(id);
        if (customer != null)
        {
            return customerComponent.delete(customer);
        }
        else
        {
            return false;
        }

    }

}
