package org.doframework.sample.jdbc_app.factory;

public interface Factory
{
    static int ID_NEW = -1;

    int getNextId();
}
