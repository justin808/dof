package org.doframework.sample.jdbc_app.factory;



public class JdbcFactoryLookupService implements SampleApp_FactoryLookupService
{

    public CustomerFactory getCustomerFactory()
    {
        return new JdbcCustomerFactoryImpl();
    }

    public InvoiceFactory getInvoiceFactory()
    {
        return new JdbcInvoiceFactoryImpl();
    }

    public PaymentFactory getPaymentFactory()
    {
        return new JdbcPaymentFactoryImpl();
    }

    public ProductFactory getProductFactory()
    {
        return new JdbcProductFactoryImpl();
    }

    public ManufacturerFactory getManufacturerFactory()
    {
        return new JdbcManufacturerFactoryImpl();
    }
}
