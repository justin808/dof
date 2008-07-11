package org.doframework.sample.jdbc_app.factory;


public interface SampleApp_FactoryLookupService
{
    CustomerFactory getCustomerFactory();
    InvoiceFactory getInvoiceFactory();

    PaymentFactory getPaymentFactory();

    ProductFactory getProductFactory();

    ManufacturerFactory getManufacturerFactory();



}
