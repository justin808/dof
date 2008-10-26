package org.doframework;

/**
 * This class provides scratch primary keys based on taking the digits of the
 * current time, omitting the first 8 digits.
 */
public class IntScratchPkProvider implements ScratchPkProvider
{
    /**
     * Implement this provide a scratch primary key.
     */
    public String getScratchPk()
    {
        String big = System.currentTimeMillis() + (int)(Integer.MAX_VALUE * Math.random()) + "";
        String rightDigits = big.substring(big.length() - 8);
        return rightDigits;
    }

}
