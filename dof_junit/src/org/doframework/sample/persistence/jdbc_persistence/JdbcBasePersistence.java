package org.doframework.sample.persistence.jdbc_persistence;

import org.doframework.sample.persistence.*;


public abstract class JdbcBasePersistence implements BasePersistence
{
    abstract public String getTableName();


    public int getNextId()
    {
        String sql = "select next value for " + getTableName() + "_sequence from dual";
        int nextValue = JdbcDbUtil.executeSingleIntQuery(sql);
        return nextValue;
    }
}
