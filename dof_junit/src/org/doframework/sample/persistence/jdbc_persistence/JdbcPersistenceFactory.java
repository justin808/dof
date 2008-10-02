package org.doframework.sample.persistence.jdbc_persistence;

import org.doframework.sample.persistence.*;


public class JdbcPersistenceFactory implements PersistenceFactory
{

    CustomerPersistence customerPersistence = new JdbcCustomerPersistence();
    InvoicePersistence invoicePersistence = new JdbcInvoicePersistence();
    PaymentPersistence paymentPersistence = new JdbcPaymentPersistence();
    ProductPersistence productPersistence = new JdbcProductPersistence();
    ManufacturerPersistence manufacturerPersistence = new JdbcManufacturerPersistence();


    public CustomerPersistence getCustomerPersistence()
    {
        return customerPersistence;
    }


    public InvoicePersistence getInvoicePersistence()
    {
        return invoicePersistence;
    }


    public PaymentPersistence getPaymentPersistence()
    {
        return paymentPersistence;
    }


    public ProductPersistence getProductPersistence()
    {
        return productPersistence;
    }


    public ManufacturerPersistence getManufacturerPersistence()
    {
        return manufacturerPersistence;
    }


}
