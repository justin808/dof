package org.doframework.sample.global;

public interface AccountingConstants
{
    /**
     * Retreives the configurable maximum amount for any invoice. Persisting an invoice with an amount greater should
     * throw an org.doframework.sample.component.InvoiceLimitExceededException
     *
     * @return Maximum invoice amount
     */
    int getMaximumInvoiceTotal();
}
