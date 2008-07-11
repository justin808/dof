package org.justingordon;

public class Change
{
    private int pennies;
    private int nickels;
    private int dimes;
    private int quarters;


    public Change(int pennies, int nickels, int dimes, int quarters)
    {
        this.pennies = pennies;
        this.nickels = nickels;
        this.dimes = dimes;
        this.quarters = quarters;
    }

    public int getPennies()
    {
        return pennies;
    }

    public int getNickels()
    {
        return nickels;
    }

    public int getDimes()
    {
        return dimes;
    }

    public int getQuarters()
    {
        return quarters;
    }


    //public boolean equals(Object obj)
    //{
    //    if (!(obj instanceof Change))
    //    {
    //        return false;
    //    }
    //    Change compareTo = (Change) obj;
    //    return (pennies == compareTo.pennies && nickels == compareTo.nickels && dimes == compareTo.dimes && quarters == compareTo.quarters);
    //}
    //
    //public String toString()
    //{
    //    return "Change{" + "pennies=" + pennies + ", nickels=" + nickels + ", dimes=" + dimes + ", quarters=" +
    //           quarters + '}';
    //}
}
