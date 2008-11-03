package sample;

import org.doframework.*;
import org.doframework.annotation.*;

import javax.persistence.*;
import java.util.*;

@TargetClass(sample.ManufacturerScratchBuilder.class)

public class ManufacturerScratchBuilder implements ScratchBuilder, DeletionHelper
{
    private static final Long LONG_ZERO = new Long(0);


    public Object fetch(Object pk)
    {
        String queryText = "select manu from Manufacturer as manu where manu.name = ?1";
        Object[] args = new Object[]{pk};
        return JpaUtility.fetchObject(queryText, args);
    }


    public Object create(Map scratchReferenceToPrimaryKey)
    {
        final Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName((String) getUniquePrimaryKey());
        JpaUtility.persistObject(manufacturer);
        return manufacturer;
    }


    private Object getUniquePrimaryKey()
    {
        return "MANU:" + System.currentTimeMillis() +  ":" + Math.random();
    }


    public boolean delete(Object objectToDelete)
    {
        return JpaUtility.deleteObject(objectToDelete);
    }


    public boolean okToDelete(Object object)
    {
        String queryText =
                "select count(prod) from Product as prod where prod.manufacturer.id = :manuId";
        Query query = JpaUtility.getEntityManager()
                .createQuery(queryText);
        query.setParameter("manuId", ((Manufacturer) object).getId());
        Object result = query.getSingleResult();
        //System.out.println("result = " + result);
        return ((result.equals(LONG_ZERO)));
    }


    public Object extractPrimaryKey(Object scratchObject)
    {
        return ((Manufacturer) scratchObject).getName();
    }


    public Object[] getReferencedObjects(Object object)
    {
        return new Object[0];
    }


}
