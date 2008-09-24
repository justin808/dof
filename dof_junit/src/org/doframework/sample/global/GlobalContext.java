package org.doframework.sample.global;

import org.doframework.sample.persistence.*;
import org.doframework.sample.persistence.jdbc_persistence.*;

/**
 * This class is used for the dependency location pattern. Consider statically importing this class to get persistence
 * and constants resources.
 */
public class GlobalContext
{
    // Default to JdbcPersistenceFactory --> JUnits can override
    private static PersistenceFactory persistanceFactory = new JdbcPersistenceFactory();
    private static AccountingConstants accountingConstants = new DefaultAccountingConstants();


    public static void setPersistanceFactory(PersistenceFactory persistanceFactory)
    {
        GlobalContext.persistanceFactory = persistanceFactory;
    }


    public static PersistenceFactory getPersistanceFactory()
    {
        return persistanceFactory;
    }


    public static void setAccountingConstants(AccountingConstants accountingConstants)
    {
        GlobalContext.accountingConstants = accountingConstants;
    }


    public static AccountingConstants getAccountingConstants()
    {
        return accountingConstants;
    }
}
