package org.doframework.sample.jdbc_app.factory;



public abstract class JdbcBaseFactory implements Factory
{
    abstract public String getTableName();

    public int getNextId()
    {
        String sql = "select max(id) from " + getTableName();
        int maxId = JdbcDbUtil.executeSingleIntQuery(sql);
        int nextValue = maxId + 1;
        return nextValue;
    }
}
