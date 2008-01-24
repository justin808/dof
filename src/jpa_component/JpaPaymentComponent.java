package jpa_component;

import jdbc_component.*;
import component.*;
import entity.*;

public class JpaPaymentComponent extends JdbcBaseComponent implements PaymentComponent
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
     * @throws component.DuplicateRecordException
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
