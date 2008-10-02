package org.doframework.sample.persistence;

public class MockPersistenceFactory implements PersistenceFactory
{
    private static CustomerPersistence customerPersistence = new MockCustomerPersistence();
    private static InvoicePersistence invoicePersistence = new MockInvoicePersistence();
    private static PaymentPersistence paymentPersistence = new MockPaymentPersistence();
    private static ProductPersistence productPersistence = new MockProductPersistence();
    private static ManufacturerPersistence manufacturerPersistence = new MockManufacturerPersistence();


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
