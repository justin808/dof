package sample;
import org.doframework.*;
import org.doframework.annotation.*;

@TargetReferencedClasses({Manufacturer.class})
@TargetClass(Product.class)

public class Product_OrangeJuice implements ReferenceBuilder, DeletionHelper {

    public Object fetch()
    {
        String queryText = "select prod from Product as prod where prod.name = ?1 ";
        Object[] args = new Object[]{getPrimaryKey()};
        return JpaUtility.fetchObject(queryText, args);
    }

    public Object getPrimaryKey() {
        return "Orange Juice";
    }

    public Object create() {
        Product product = new Product();
        product.setName((String) getPrimaryKey());
        product.setPrice(4);
        Manufacturer manufacturer = (Manufacturer) DOF.require(new Manufacturer_Tropicana());
        product.setManufacturerByManufacturerId(manufacturer);
        JpaUtility.persistObject(product);
        return product;
    }


    public boolean okToDelete(Object object)
    {
        return true; // nothing else depends on Product for the 2 object example
    }


    public Object extractPrimaryKey(Object object)
    {
        return ((Product)object).getName();
    }


    public Object[] getReferencedObjects(Object object)
    {
        return new Object[] { ((Product)object).getManufacturerByManufacturerId() };
    }


    public Class[] getReferencedClasses()
    {
        return new Class[] {Manufacturer.class};
    }




    public boolean delete(final Object objectToDelete) {
        return JpaUtility.deleteObject(objectToDelete);
    }


}