package org.doframework.sample.jdbc_app.factory;

import org.doframework.sample.jdbc_app.entity.*;

import java.util.*;

public class MockInvoiceFactory implements InvoiceFactory
{

    Map<Integer, Invoice> invoiceIdToInvoice = new HashMap<Integer, Invoice>();
    static int nextId = 0;


    public Invoice getById(int id)
    {
        return invoiceIdToInvoice.get(id);
    }


    public int getNextId()
    {
        return nextId++;
    }


    public void insert(Invoice invoice)
    {
        invoiceIdToInvoice.put(invoice.getId(), invoice);
    }


    public void update(Invoice invoice)
    {
        invoiceIdToInvoice.put(invoice.getId(), invoice);
    }


	public boolean delete(Invoice invoice) {
		// TODO Auto-generated method stub
		return false;
	}



	public List<Invoice> getInvoicesForCustomer(Customer customer) {
		// TODO Auto-generated method stub
		return null;
	}


}
