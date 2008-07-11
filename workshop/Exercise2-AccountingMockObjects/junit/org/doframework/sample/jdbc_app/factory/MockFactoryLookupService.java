package org.doframework.sample.jdbc_app.factory;

public class MockFactoryLookupService implements SampleApp_FactoryLookupService
{
	private CustomerFactory a_customerFactory = new MockCustomerFactory();
	private InvoiceFactory a_invoiceFactory = new MockInvoiceFactory();


	public CustomerFactory getCustomerFactory() {
		return a_customerFactory;
	}


	public InvoiceFactory getInvoiceFactory() {
		return a_invoiceFactory;
	}


	public ManufacturerFactory getManufacturerFactory() {
		// TODO Auto-generated method stub
		return null;
	}


	public PaymentFactory getPaymentFactory() {
		// TODO Auto-generated method stub
		return null;
	}


	public ProductFactory getProductFactory() {
		// TODO Auto-generated method stub
		return null;
	}


}
