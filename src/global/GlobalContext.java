package global;

import java.math.*;

public class GlobalContext
{
    private static ComponentFactory componentFactory;
    private static Constants constants;

    {
        GlobalContext.setConstants(new PropertyFileConstants());
    }


    synchronized public static ComponentFactory getComponentFactory()
    {
        if (componentFactory == null)
        {
            componentFactory = new JdbcComponentFactory();
        }
        return componentFactory;
    }

    public static void setComponentFactory(ComponentFactory componentFactory)
    {
        GlobalContext.componentFactory = componentFactory;
    }

    public static Constants getConstants()
    {
        return constants;
    }

    public static void setConstants(Constants constants)
    {
        GlobalContext.constants = constants;
    }

    public static BigDecimal currencyRound(BigDecimal bigDecimal)
    {
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
