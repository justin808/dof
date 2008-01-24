package component;

import entity.*;

public interface ProductComponent
{
    void insert(Product product);

    boolean delete(Product product);

    Product getById(int id);
}
