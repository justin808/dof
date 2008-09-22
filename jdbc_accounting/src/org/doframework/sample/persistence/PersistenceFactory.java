package org.doframework.sample.persistence;

/**
 * Isolate the dependency on persistence code here.
 */
public interface PersistenceFactory
{
    CustomerPersistence getCustomerPersistence();
    InvoicePersistence getInvoicePersistence();
    PaymentPersistence getPaymentPersistence();
    ProductPersistence getProductPersistence();
    ManufacturerPersistence getManufacturerPersistence();
    ShoppingListPersistence getShoppingListPersistence();


}