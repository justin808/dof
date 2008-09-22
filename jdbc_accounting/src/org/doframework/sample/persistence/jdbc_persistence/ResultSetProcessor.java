package org.doframework.sample.persistence.jdbc_persistence;

import java.sql.*;


public interface ResultSetProcessor
{
    void process(ResultSet rs);
}

