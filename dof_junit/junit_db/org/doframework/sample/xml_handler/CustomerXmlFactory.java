package org.doframework.sample.xml_handler;

import org.doframework.*;
import org.doframework.sample.component.*;
import org.doframework.sample.global.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;

public class CustomerXmlFactory implements DependentObjectHandler, ScratchPkProvider,
                                           ObjectDeletionHelper
{

    CustomerComponent customerComponent = ComponentFactory.getCustomerComponent();


    public Customer createCustomer(ObjectFileInfo ofi)
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


    public Object create(ObjectFileInfo ofi)
    {
        Customer customer = createCustomer(ofi);
        customer.setNew(true);
        customerComponent.persist(customer);
        return customer;
    }


    public Object get(ObjectFileInfo ofi)
    {
        return customerComponent.getById(Integer.parseInt(ofi.getPk()));
    }


    public boolean delete(Object o, ObjectFileInfo objectFileInfo)
    {
        return customerComponent.delete((Customer) o);
    }


    public boolean delete(Object object)
    {
        return delete(object, null);
    }


    /**
     * This method is used for deletion of scratch objects. Thus, it is not necessary to implement
     * it if you are not concerned with deleting scratch objects.
     *
     * @param object Which is the type that the given handler is mapped to.
     *
     * @return The dependencies of the given object
     */
    public Object[] getDependencies(Object object)
    {
        return null;
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
        return null;
    }


    /** @return The class object for the class that is returned from this handler. */
    public Class getCreatedClass()
    {
        return Customer.class;
    }


    /**
     * Implement this provide a scratch primary key.
     *
     * @return
     */
    public String getScratchPk()
    {
        return GlobalContext.getPersistanceFactory().getCustomerPersistence().getNextId() + "";
    }


    public Object extractPrimaryKey(Object object)
    {
        return new Integer(((Customer) object).getId());
    }

}
