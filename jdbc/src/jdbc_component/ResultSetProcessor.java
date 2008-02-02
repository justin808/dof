package jdbc_component;

import java.sql.*;

public interface ResultSetProcessor
{
    void process(ResultSet rs);
}
