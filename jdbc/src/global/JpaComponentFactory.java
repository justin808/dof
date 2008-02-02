package global;

import component.*;
import jpa_component.*;

public class JpaComponentFactory implements ComponentFactory
{
    public CustomerComponent getCustomerComponent()
    {
        return new JpaCustomerComponent();
    }

    public InvoiceComponent getInvoiceComponent()
    {
        return new JpaInvoiceComponent();
    }

    public PaymentComponent getPaymentComponent()
    {
        return new JpaPaymentComponent();
    }

    public ProductComponent getProductComponent()
    {
        return new JpaProductComponent();
    }

    public ManufacturerComponent getManufacturerComponent()
    {
        return new JpaManufacturerComponent();
    }
}
