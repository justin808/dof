package org.doframework.sample.jdbc_app.factory;

import org.doframework.sample.jdbc_app.entity.Payment;

public class JdbcPaymentFactoryImpl extends JdbcBaseFactory implements PaymentFactory
{
    public Payment createNewPayment()
    {
        return null;
    }

    public Payment getById(int paymentId)
    {
        return null;
    }

    /**
     * Saves the payment record
     *
     * @param payment
     *
     * @throws org.doframework.sample.jdbc_app.factory.DuplicateRecordException
     *
     */
    public void insert(Payment payment) throws DuplicateRecordException
    {

    }

    public void delete(Payment payment)
    {

    }

    public String getTableName()
    {
        return "payment";
    }
}
