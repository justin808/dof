package org.doframework.sample.jdbc_app;


import java.math.BigDecimal;

import org.doframework.sample.jdbc_app.factory.SampleApp_FactoryLookupService;
import org.doframework.sample.jdbc_app.factory.JdbcFactoryLookupService;

public class GlobalContext
{
    private static SampleApp_FactoryLookupService factoryLookupService;
    private static Constants constants;

    {
        GlobalContext.setConstants(new DefaultConstants());
    }


    public synchronized static SampleApp_FactoryLookupService getFactoryLookupService()
    {
        if (factoryLookupService == null)
        {
            factoryLookupService = new JdbcFactoryLookupService();
        }
        return factoryLookupService;
    }

    public static void setFactoryLookupService(SampleApp_FactoryLookupService componentFactory)
    {
        GlobalContext.factoryLookupService = componentFactory;
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
