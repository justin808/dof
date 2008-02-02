package global;

import global.*;
import component.*;

public class MockComponentFactory implements ComponentFactory
{

    public CustomerComponent getCustomerComponent()
    {
        return new MockCustomerComponent();
    }

    public InvoiceComponent getInvoiceComponent()
    {
        return new MockInvoiceComponent();
    }

    public PaymentComponent getPaymentComponent()
    {
        return new MockPaymentComponent();
    }

    public ProductComponent getProductComponent()
    {
        return new MockProductComponent();
    }

    public ManufacturerComponent getManufacturerComponent()
    {
        return new MockManufacturerComponent();
    }
}
