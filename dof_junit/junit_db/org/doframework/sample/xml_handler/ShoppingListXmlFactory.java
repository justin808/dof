package org.doframework.sample.xml_handler;

import org.doframework.*;
import org.doframework.sample.component.*;
import org.doframework.sample.global.*;
import org.doframework.sample.persistence.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.util.*;


public class ShoppingListXmlFactory implements DependentObjectHandler, ObjectDeletionHelper
{

    ShoppingListComponent shoppingListComponent = ComponentFactory.getShoppingListComponent();
    CustomerComponent customerComponent = ComponentFactory.getCustomerComponent();


    /** @return an Invoice Object object with corresponding name and id */
    ShoppingList createShoppingList(ObjectFileInfo ofi)
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
            String sId = xPath.evaluate("/shopping_list/id", item);
            int id = Integer.parseInt(sId);
            String sCustomerId = xPath.evaluate("/shopping_list/customer_id", item);
            int customerId = Integer.parseInt(sCustomerId);

            Customer customer = customerComponent.getById(customerId);

            ShoppingList shoppingList = new ShoppingList(id);
            shoppingList.setCustomer(customer);

            NodeList itemNodes = (NodeList) xPath
                    .evaluate("/shopping_list/items/item", item, XPathConstants.NODESET);
            int nodeListLength = itemNodes.getLength();
            List<ShoppingListItem> shoppingItems = new ArrayList<ShoppingListItem>(nodeListLength);
            final ProductPersistence productPersistence =
                    GlobalContext.getPersistanceFactory().getProductPersistence();
            for (int i = 0; i < nodeListLength; i++)
            {
                Node node = itemNodes.item(i); // This is a Line Item node
                String sQty = xPath.evaluate("qty", node);
                Integer qty = new Integer(sQty);
                String productId = xPath.evaluate("product_id", node);
                Product product = productPersistence.getById(Integer.parseInt(productId));

                if (product == null)
                {
                    throw new RuntimeException("Product with id " + productId + " does not exist!");
                }

                ShoppingListItem sli = new ShoppingListItem(qty, product);
                shoppingItems.add(sli);
            }
            shoppingList.setItems(shoppingItems);
            return shoppingList;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    public Object create(ObjectFileInfo ofi)
    {
        ShoppingList shoppingList = createShoppingList(ofi);
        shoppingList.setNew(true);
        shoppingListComponent.persist(shoppingList);
        return shoppingList;
    }


    public Object get(ObjectFileInfo ofi)
    {
        return shoppingListComponent.getById(Integer.parseInt(ofi.getPk()));
    }


    public boolean delete(Object objectToDelete, ObjectFileInfo objectFileInfo)
    {
        return shoppingListComponent.delete((ShoppingList) objectToDelete);
    }


    public boolean delete(Object object)
    {
        return delete(object, null);
    }


    /**
     * @param scratchObject which is the type that the given handler creates
     *
     * @return The dependencies of the given scratch object
     */
    public Object[] getDependencies(Object scratchObject)
    {
        Invoice shopping_list = (Invoice) scratchObject;
        List<LineItem> lineItems = shopping_list.getLineItems();
        Set<Product> products = new HashSet<Product>();
        for (Iterator lineItemIterator = lineItems.iterator(); lineItemIterator.hasNext();)
        {
            LineItem lineItem = (LineItem) lineItemIterator.next();
            products.add(lineItem.getProduct());
        }
        Object[] dependencies = new Object[products.size() + 1];
        dependencies[0] = shopping_list.getCustomer();
        int i = 1;
        for (Iterator productIterator = products.iterator(); productIterator.hasNext();)
        {
            Product product = (Product) productIterator.next();
            dependencies[i++] = product;
        }

        return dependencies;
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
        return new Class[]{Customer.class, Product.class};
    }


    /** @return The class object for the class that is returned from this handler. */
    public Class getCreatedClass()
    {
        return Invoice.class;
    }


    public Object extractPrimaryKey(Object object)
    {
        return new Integer(((ShoppingList) object).getId());
    }

}
