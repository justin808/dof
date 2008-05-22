package org.doframework.sample.jdbc_app.factory;

import java.sql.*;

public interface ResultSetProcessor
{
    void process(ResultSet rs);
}
