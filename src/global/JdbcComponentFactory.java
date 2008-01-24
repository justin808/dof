package global;

import component.*;
import jdbc_component.*;

public class JdbcComponentFactory implements ComponentFactory
{

    public CustomerComponent getCustomerComponent()
    {
        return new JdbcCustomerComponent();
    }

    public InvoiceComponent getInvoiceComponent()
    {
        return new JdbcInvoiceComponent();
    }

    public PaymentComponent getPaymentComponent()
    {
        return new JdbcPaymentComponent();
    }

    public ProductComponent getProductComponent()
    {
        return new JdbcProductComponent();
    }

    public ManufacturerComponent getManufacturerComponent()
    {
        return new JdbcManufacturerComponent();
    }
}
