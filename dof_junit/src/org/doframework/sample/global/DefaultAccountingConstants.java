package org.doframework.sample.global;

/**
 * Notice class is stored under junit directory b/c a Mock class
 */
public class DefaultAccountingConstants implements AccountingConstants
{
    static final int MAX_INVOICE_AMOUNT = 10000;


    public int getMaximumInvoiceTotal()
    {
        return MAX_INVOICE_AMOUNT;
    }

}
