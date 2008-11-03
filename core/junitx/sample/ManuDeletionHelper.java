package sample;
import org.doframework.annotation.*;
import org.doframework.*;

@TargetClass(Manu.class)
@TargetReferencedClasses(Manu.class)
public class ManuDeletionHelper implements ReferenceBuilder, DeletionHelper
{
    private static final Long LONG_ZERO = new Long(0);


    public Object fetch() {
        String queryText = "select manu from Manufacturer as manu where manu.name = ?1";
        Object[] args = new Object[] {getPrimaryKey()};
        return fetchObject(queryText, args);
    }


    public Object create() {
        final Manu manufacturer = new Manu();
        manufacturer.setName((String) getPrimaryKey());
        persist(manufacturer);
        return manufacturer;
    }


    public Object getPrimaryKey()
    {
        return "Tropicana";
    }




    public boolean okToDelete(Object object)
    {
        // dummy code!
        return false;
    }


    public Object[] getDependencies(Object object)
    {
        return new Object[0];
    }


    public Class[] getDependencyClasses()
    {
        return new Class[0];
    }


    public Object extractPrimaryKey(Object object)
    {
        return ((Manu)object).getName();
    }


    public Object[] getReferencedObjects(Object object)
    {
        return new Object[0];
    }


    public Class[] getReferencedClasses()
    {
        return null;
    }


    public boolean delete(final Object objectToDelete) {
        return true;
    }


    protected void persist(final Object objectToPersist)
    {
    }


    protected Object fetchObject(String queryText, Object[] args)
    {
        return null;
    }
}