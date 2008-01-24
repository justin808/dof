package jdbc_component;

import component.*;

public abstract class JdbcBaseComponent implements Component
{
    abstract public String getTableName();

    public int getNextId()
    {
        String sql = "select next value for " + getTableName() + "_sequence from dual";
        int nextValue = JdbcDbUtil.executeSingleIntQuery(sql);
        return nextValue;
    }
}
