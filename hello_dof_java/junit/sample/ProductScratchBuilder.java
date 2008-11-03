package sample;

import org.doframework.*;
import org.doframework.annotation.*;

import javax.persistence.*;
import java.util.*;

@TargetClass(ProductScratchBuilder.class)

public class ProductScratchBuilder implements ScratchBuilder, DeletionHelper
{
    private static final Long LONG_ZERO = new Long(0);


    public Object fetch(Object pk)
    {
        String queryText = "select prod from Product as prod where prod.name = ?1 ";
        Object[] args = new Object[]{pk};
        return JpaUtility.fetchObject(queryText, args);
    }


    public Object create(Map scratchReferenceToPrimaryKey)
    {
        // CREATE DEPENDENCY OBJECT
        Manufacturer manufacturer = (Manufacturer) DOF.createScratchObject(new ManufacturerScratchBuilder());

        Product product = new Product();
        product.setName((String) getUniquePrimaryKey());
        product.setPrice(4);
        product.setManufacturer(manufacturer);
        JpaUtility.persistObject(product);
        return product;

    }


    private Object getUniquePrimaryKey()
    {
        return "PROD:" + System.currentTimeMillis() +  ":" + Math.random();
    }


    public boolean delete(Object objectToDelete)
    {
        return JpaUtility.deleteObject(objectToDelete);
    }


    public boolean okToDelete(Object object)
    {
        return true; // nothing else depends on Product for the 2 object example
    }


    public Object extractPrimaryKey(Object scratchObject)
    {
        final Product product = (Product) scratchObject;
        return product.getManufacturer().getName() + "__" + product.getName();
    }


    public Object[] getReferencedObjects(Object object)
    {
        return new Object[0];
    }


}