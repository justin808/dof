package org.doframework.sample.component;

import org.doframework.sample.global.*;

public class ProductComponent
{
    public void persist(Product product)
    {
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


    public boolean delete(Product product)
    {
        return GlobalContext.getPersistanceFactory().getProductPersistence().delete(product);

    }


    public Product getById(int id)
    {
        return GlobalContext.getPersistanceFactory().getProductPersistence().getById(id);
    }
}
