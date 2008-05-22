package org.doframework.sample.jdbc_app;


import java.math.BigDecimal;

import org.doframework.sample.jdbc_app.factory.SampleApp_FactoryLookupService;
import org.doframework.sample.jdbc_app.factory.JdbcFactory;

public class GlobalContext
{
    private static SampleApp_FactoryLookupService componentFactory;
    private static Constants constants;

    {
        GlobalContext.setConstants(new DefaultConstants());
    }


    public synchronized static SampleApp_FactoryLookupService getComponentFactory()
    {
        if (componentFactory == null)
        {
            componentFactory = new JdbcFactory();
        }
        return componentFactory;
    }

    public static void setComponentFactory(SampleApp_FactoryLookupService componentFactory)
    {
        GlobalContext.componentFactory = componentFactory;
    }

    public synchronized static Constants getConstants()
    {
    	if (constants == null) {
    		constants = new DefaultConstants();
    	}
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
