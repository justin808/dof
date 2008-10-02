package org.doframework.sample.component;

/**
 * Simple Java Bean class. No business logic in this class.
 *
 * @see CustomerComponent
 */
public class Customer extends Entity
{

    private String name;
    private Integer balance;
    private boolean isOverdue;
    private String phoneNumber;


    /**
     * This constructor is needed for retrieving from the DB
     */
    public Customer(int id, String name, String phoneNumber, int balance, boolean isOverdue)
    {
        super(id);
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.isOverdue = isOverdue;
        setNew(false);

    }


    public Customer(int id)
    {
        super(id);
        setNew(true);
        setBalance(0);
    }


    public Customer setName(String name)
    {
        this.name = name;
        return this;
    }


    public String getName()
    {
        return name;
    }


    public Customer setBalance(Integer balance)
    {
        this.balance = balance;
        return this;
    }


    public Integer getBalance()
    {
        return balance;
    }


    public Customer setOverdue(boolean isOverdue)
    {
        this.isOverdue = isOverdue;
        return this;
    }


    public boolean isOverdue()
    {
        return isOverdue;
    }


    public String getPhoneNumber()
    {
        return phoneNumber;
    }


    public Customer setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
        return this;
    }


    public String toString()
    {
        return "Customer{" + "name='" + name + '\'' + ", balance=" + balance + ", isOverdue=" +
               isOverdue + ", phoneNumber='" + phoneNumber + '\'' + '}';
    }


}
