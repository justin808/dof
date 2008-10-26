package org.doframework.sample.component;

import org.doframework.sample.global.*;

public class ProductComponent
{
    public void persist(Product product)
    {
        validate(product);
        if (product.isNew())
        {
            GlobalContext.getPersistanceFactory().getProductPersistence().insert(product);
            product.setNew(false);
        }
        else
        {
            GlobalContext.getPersistanceFactory().getProductPersistence().update(product);

        }
    }


    private void validate(Product product)
    {
        if (product.getName() == null || product.getName().length() == 0)
        {
            throw new RuntimeException("Missing product name for " + product);
        }
        if (product.getPrice() == null)
        {
            throw new RuntimeException("Missing product price for " + product);
        }

        if (product.getManufacturer() == null)
        {
            throw new RuntimeException("Missing product manufacturer for " + product);
        }


    }


    public boolean delete(Product product)
    {
        return GlobalContext.getPersistanceFactory().getProductPersistence().delete(product);

    }


    public Product getById(int id)
    {
        return GlobalContext.getPersistanceFactory().getProductPersistence().getById(id);
    }


    public Product createNew()
    {
        Product product = new Product(GlobalContext.getPersistanceFactory()
                .getProductPersistence().getNextId());
        product.setNew(true);
        return product;
    }


    public Object getByManufacturerAndName(String manufacturerName, String productName)
    {
        return GlobalContext.getPersistanceFactory().getProductPersistence().
                getByManufacturerNameProductName(manufacturerName, productName);
    }


    public boolean hasInvoices(Product product)
    {
        int numInvoices = GlobalContext.getPersistanceFactory().getProductPersistence()
                .countInvoicesWithProductId(product.getId());
        return (numInvoices > 0);

    }
}
