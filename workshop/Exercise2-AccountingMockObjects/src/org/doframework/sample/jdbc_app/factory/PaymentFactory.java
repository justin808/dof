package org.doframework.sample.jdbc_app.factory;


import org.doframework.sample.jdbc_app.entity.Payment;

public interface PaymentFactory extends Factory
{

    Payment getById(int paymentId);

    /**
     * Saves the payment record
     *
     * @param payment
     *
     * @throws DuplicateRecordException
     */
    void insert(Payment payment) throws DuplicateRecordException;


    void delete(Payment payment);

}
