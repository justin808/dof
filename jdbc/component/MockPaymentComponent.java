package component;

import entity.*;

import java.util.*;

public class MockPaymentComponent implements PaymentComponent
{
    private static Map<Integer, Payment> paymentIdToPayment = new HashMap<Integer, Payment>();
    private static int lastPaymentId = 0;
    private static final int NEW_PAYMENT_ID = -1;

    public Payment getById(int paymentId)
    {
        return paymentIdToPayment.get(paymentId);
    }

    public void insert(Payment payment) throws DuplicateRecordException
    {
        if (payment.getId() == NEW_PAYMENT_ID)
        {
            payment.setId(getNextId());
        }
        paymentIdToPayment.put(payment.getId(), payment);
    }


    public void delete(Payment payment)
    {
        paymentIdToPayment.remove(payment.getId());
    }

    public int getNextId()
    {
        return lastPaymentId++;
    }
}
