package org.doframework.sample.persistence;

import org.doframework.sample.component.*;

import java.util.*;

public class MockProductPersistence implements ProductPersistence
{

    Map<Integer, Product> productIdToProduct = new HashMap<Integer, Product>();
    static int nextId = 0;


    public Product getById(int id)
    {
        return productIdToProduct.get(id);
    }


    /**
     * Delete the product
     *
     * @param product to delete
     *
     * @return true if the product was deleted
     */
    public boolean delete(Product product)
    {
        return (productIdToProduct.remove(product.getId()) != null);
    }


    public Product getByManufacturerNameProductName(String manufacturerName, String productName)
    {
        return null;
    }


    public int countInvoicesWithProductId(int productId)
    {
        return 0;
    }


    public int getNextId()
    {
        return nextId++;
    }


    public void insert(Product product)
    {
        productIdToProduct.put(product.getId(), product);
    }


    public void update(Product product)
    {
        productIdToProduct.put(product.getId(), product);
    }

}
