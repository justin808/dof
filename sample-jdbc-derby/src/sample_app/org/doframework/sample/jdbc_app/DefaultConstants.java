package org.doframework.sample.jdbc_app;

import java.math.*;

public class DefaultConstants implements Constants
{
    public BigDecimal getLateFeePercentage()
    {
        return new BigDecimal("0.015"); // 1.5%
    }

    public int getDaysBeforeLate()
    {
        return 31;
    }
}
