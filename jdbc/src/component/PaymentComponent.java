package component;

import entity.*;

public interface PaymentComponent extends Component
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
