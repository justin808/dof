package org.doframework.sample.jdbc_app.factory;

import org.doframework.sample.jdbc_app.entity.Product;

public interface ProductFactory
{
    void insert(Product product);

    boolean delete(Product product);

    Product getById(int id);
}
