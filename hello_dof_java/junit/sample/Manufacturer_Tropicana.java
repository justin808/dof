package sample;
import org.doframework.annotation.*;
import org.doframework.*;

import javax.persistence.*;

@TargetClass(Manufacturer.class)
public class Manufacturer_Tropicana implements ReferenceBuilder, DeletionHelper {
    private static final Long LONG_ZERO = new Long(0);


    public Object fetch() {
        String queryText = "select manu from Manufacturer as manu where manu.name = ?1";
        Object[] args = new Object[] {getPrimaryKey()};
        return JpaUtility.fetchObject(queryText, args);
    }


    public Object create() {
        final Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName((String) getPrimaryKey());
        JpaUtility.persistObject(manufacturer);
        return manufacturer;
    }


    public Object getPrimaryKey()
    {
        return "Tropicana";
    }


    public boolean okToDelete(Object object)
    {
        String queryText = "select count(prod) from Product as prod where prod.manufacturer.id = :manuId";
        Query query = JpaUtility.getEntityManager()
                .createQuery(queryText);
        query.setParameter("manuId", ((Manufacturer) object).getId());
        Object result = query.getSingleResult();
        //System.out.println("result = " + result);
        return ((result.equals(LONG_ZERO)));
    }


    public Object extractPrimaryKey(Object object)
    {
        return ((Manufacturer)object).getName();
    }


    public Object[] getReferencedObjects(Object object)
    {
        return new Object[0];
    }


    public boolean delete(final Object objectToDelete)
    {
        return JpaUtility.deleteObject(objectToDelete);
    }




}