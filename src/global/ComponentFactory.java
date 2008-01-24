package global;

import component.*;

public interface ComponentFactory
{
    CustomerComponent getCustomerComponent();
    InvoiceComponent getInvoiceComponent();

    PaymentComponent getPaymentComponent();

    ProductComponent getProductComponent();

    ManufacturerComponent getManufacturerComponent();



}
