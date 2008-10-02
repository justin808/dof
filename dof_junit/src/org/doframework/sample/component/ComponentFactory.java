package org.doframework.sample.component;

public class ComponentFactory
{
    private static CustomerComponent customerComponent = new CustomerComponent();
    private static InvoiceComponent invoiceComponent = new InvoiceComponent();
    private static PaymentComponent paymentComponent = new PaymentComponent();
    private static ProductComponent productComponent = new ProductComponent();
    private static ManufacturerComponent manufacturerComponent = new ManufacturerComponent();


    public static CustomerComponent getCustomerComponent()
    {
        return customerComponent;
    }


    public static InvoiceComponent getInvoiceComponent()
    {
        return invoiceComponent;
    }


    public static PaymentComponent getPaymentComponent()
    {
        return paymentComponent;
    }


    public static ProductComponent getProductComponent()
    {
        return productComponent;
    }


    public static ManufacturerComponent getManufacturerComponent()
    {
        return manufacturerComponent;
    }



}
