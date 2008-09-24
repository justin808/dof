package org.doframework.sample.persistence.jdbc_persistence;

import org.doframework.sample.component.*;
import static org.doframework.sample.global.GlobalContext.*;
import org.doframework.sample.persistence.*;

import java.util.*;

public class JdbcShoppingListPersistence extends JdbcBasePersistence
        implements ShoppingListPersistence
{
    public ShoppingList getById(int shoppingListId)
    {
        String sql = "select * from shopping_list where id = " + shoppingListId;
        String[][] rows = JdbcDbUtil.executeMultiColumnQuery(sql);
        if (rows.length == 0)
        {
            return null;
        }
        else
        {
            final String[] shoppingListRow = rows[0];
            int fetchedShoppingListId = Integer.parseInt(shoppingListRow[0]);
            if (fetchedShoppingListId != shoppingListId)
            {
                throw new RuntimeException("Query for shoppingList broken: " + sql);
            }

            ShoppingList shoppingList = getShoppingList(shoppingListId, shoppingListRow, null);
            return shoppingList;
        }
    }


    private ShoppingList getShoppingList(int shoppingListId,
                                         String[] shoppingListRow,
                                         Customer customer)
    {
        int customerId = Integer.parseInt(shoppingListRow[1]);
        CustomerPersistence customerPersistence = getPersistanceFactory().getCustomerPersistence();
        if (customer == null)
        {
            customer = customerPersistence.getById(customerId);
        }
        List<ShoppingListItem> lineItems = getItems(shoppingListId);
        ShoppingList shoppingList = new ShoppingList(shoppingListId);
        shoppingList.setName(shoppingListRow[2]);
        shoppingList.setCustomer(customer);
        shoppingList.setItems(lineItems);
        return shoppingList;
    }


    private List<ShoppingListItem> getItems(int id)
    {
        String sql = "select qty, product_id from shopping_list_item where shopping_list_id = " +
                     id + " order by line_number";
        String[][] rows = JdbcDbUtil.executeMultiColumnQuery(sql);
        List<ShoppingListItem> items = new ArrayList<ShoppingListItem>();
        for (int i = 0; i < rows.length; i++)
        {
            String[] row = rows[i];
            int productId = Integer.parseInt(row[1]);
            Product product = getPersistanceFactory().getProductPersistence().getById(productId);
            if (product == null)
            {
                throw new RuntimeException("Could not find product with id " + productId +
                                           " while fetching line items for shoppingList " + id);
            }
            Integer qty = new Integer(row[0]);
            ShoppingListItem lineItem = new ShoppingListItem(qty, product);
            items.add(lineItem);
        }
        return items;


    }


    public void insert(ShoppingList shoppingList) throws DuplicateRecordException
    {

        String sql = "insert into shopping_list (id, customer_id, name) " + "values (" +
                     shoppingList.getId() + ", " + shoppingList.getCustomer().getId() + ", '" +
                     shoppingList.getName() + "')";
        JdbcDbUtil.update(sql);
        insertLineItems(shoppingList);
    }


    private void insertLineItems(ShoppingList shoppingList)
    {
        int line = 0;
        for (ShoppingListItem shoppingListItem : shoppingList.getItems())
        {
            String sql =
                    "insert into shopping_list_item (shopping_list_id, line_number, qty, product_id) " +
                    "values " + "(" + shoppingList.getId() + ", " + line + ", " +
                    shoppingListItem.getQuantity() + ", " + shoppingListItem.getProduct().getId() +
                    ")";
            JdbcDbUtil.update(sql);
            line++;
        }
    }


    public void update(ShoppingList shoppingList) throws DuplicateRecordException
    {
        String sql = "update shopping_list " + "set name = " + shoppingList.getName() +
                     " where id = " + shoppingList.getId();
        JdbcDbUtil.update(sql);
        updateLineItems(shoppingList);
    }


    private void updateLineItems(ShoppingList shoppingList)
    {
        deleteLineItems(shoppingList);
        insertLineItems(shoppingList);
    }


    private void deleteLineItems(ShoppingList shoppingList)
    {
        String sql =
                "delete from shopping_list_item where shopping_list_id = " + shoppingList.getId();
        JdbcDbUtil.update(sql);
    }


    public boolean delete(ShoppingList shoppingList)
    {
        deleteLineItems(shoppingList);
        String sql = "delete from shopping_list where id = " + shoppingList.getId();
        int rowCount = JdbcDbUtil.update(sql);
        return rowCount > 0;
    }


    /** @return All the shoppingLists for the customer, ordered chronologically */
    public List<ShoppingList> getShoppingListsForCustomer(Customer customer)
    {
        String query = "customer_id = " + customer.getId() + " order by shoppingList_date";
        List<ShoppingList> result = query(query, customer);
        return result;
    }


    /**
     * @param query
     * @param customer optional to avoid refetching customers when creating ShoppingList records
     *
     * @return
     */
    private List<ShoppingList> query(String query, Customer customer)
    {
        String sql = "select * from shoppingList where " + query;
        String[][] data = JdbcDbUtil.executeMultiColumnQuery(sql);
        List<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();
        for (int row = 0; row < data.length; row++)
        {
            ShoppingList shoppingList =
                    getShoppingList(Integer.parseInt(data[row][0]), data[row], customer);
            shoppingLists.add(shoppingList);
        }
        return shoppingLists; // if none found

    }


    public String getTableName()
    {
        return "shopping_list";
    }


}