package org.doframework.sample.jdbc_app.factory.test;

import junit.framework.TestCase;

import org.doframework.sample.jdbc_app.GlobalContext;
import org.doframework.sample.jdbc_app.factory.CustomerFactory;

public abstract class JdbcFactoryImplTest extends TestCase {

	protected static CustomerFactory getCustomerFactory() {
		
		CustomerFactory returnValue = GlobalContext.getComponentFactory().getCustomerFactory();
		
		return returnValue;
	}


}