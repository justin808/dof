package global;

import java.math.*;

public class PropertyFileConstants implements Constants
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
