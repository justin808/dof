package org.doframework.sample.jdbc_app.test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.doframework.sample.jdbc_app.entity.test.CustomerTest;
import org.doframework.sample.jdbc_app.factory.JdbcDbUtil;
import org.doframework.sample.jdbc_app.factory.test.DOFSamplesJdbcCustomerFactoryImplTest;
import org.doframework.sample.jdbc_app.factory.test.JdbcCustomerFactoryImplTest;
import org.doframework.sample.jdbc_app.test.dof.test.CustomerDOFHandlerTest;
import org.doframework.sample.jdbc_app.test.dof.test.InvoiceDOFHandlerTest;
import org.doframework.sample.jdbc_app.test.dof.test.ManufacturerDOFHandlerTest;
import org.doframework.sample.jdbc_app.test.dof.test.ProductDOFHandlerTest;

public class AllTestSuite extends TestCase
{
	/**
	 * Normally this would not be needed, but because to make this
	 * sample easy to run this method is here to setup the database
	 * if needed.
	 * 
	 * BTW - this method is normally not needed because it is assumed
	 * that you would have a deployment (install) script that would 
	 * actually create the database instance.
	 */
	private static void createDBIfNeeded()
	{
		// To see if the DB has been created sucessfully already
		// just run a query and if it runs ok then we are good.
		try
		{
			JdbcDbUtil.executeMultiColumnQuery("SELECT COUNT(*) FROM CUSTOMER");
			System.out.println("DB structure already created. Therefore DB structure not initialized.");
			
		}
		catch (RuntimeException re)
		{
			// Since we got this we need to create the DB.
			createTables();
		}
	}

	public static void createTables()
	{
	    System.out.println("Creating Schema");
	    InputStream is = ClassLoader.getSystemResourceAsStream("accounting.sql");
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String s;
	    StringBuffer sql = new StringBuffer();
	    try
	    {
	        while ((s = br.readLine()) != null)
	        {
	            sql.append(s);
	            if (s.length() > 0 && s.charAt(s.length() - 1) == ';')
	            {
	            	String sqlAsString = sql.substring(0, sql.length() - 1);
	            	System.out.println("About to run SQL Command: " + sqlAsString);
	                org.doframework.sample.jdbc_app.factory.JdbcDbUtil.update(sqlAsString);
	                sql.delete(0, sql.length());
	            }
	        }
	    }
	    catch (IOException e)
	    {
	        throw new RuntimeException(e);
	    }
	}
	
    public static Test suite()
    {
    	// Normally this method call would not be in the JUnit
    	// test suite, but this method enables you to just to
    	// run the test suite without setting up the database.
    	createDBIfNeeded();
    	
        TestSuite suite = new TestSuite();
        // Test our DOF Handlers
        suite.addTest(new TestSuite(ManufacturerDOFHandlerTest.class));
        suite.addTest(new TestSuite(CustomerDOFHandlerTest.class));
        suite.addTest(new TestSuite(ProductDOFHandlerTest.class));
        suite.addTest(new TestSuite(InvoiceDOFHandlerTest.class));
        
        // Test our custom JDBC persistence framework
        suite.addTest(new TestSuite(DOFSamplesJdbcCustomerFactoryImplTest.class));
        suite.addTest(new TestSuite(JdbcCustomerFactoryImplTest.class));
        
        // Test our sample application's business fucntionality.
        //TODO: Are these tests really valid?  Need to talk to Justin.
        // This first test does not does not seem to run.
//        suite.addTest(new TestSuite(AccountingTest.class));
        suite.addTest(new TestSuite(CustomerManagerTest.class));
        
        //
        // Test the sample applications entity objects
        //
        // Normally these tests would probably be at the beginning of the suite
        // but since this sample is about showing off DOF these tests are listed
        // here at the end - these tests help ensure our sample application code
        // is correct.
        suite.addTest(new TestSuite(CustomerTest.class));
        
        return suite;
    }
}
