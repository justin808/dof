package org.doframework.sample.jdbc_app.factory.test;

import org.doframework.DOF;
import org.doframework.sample.jdbc_app.entity.Customer;
import org.doframework.sample.jdbc_app.factory.CustomerFactory;

/**
 * This TestCase contains sample tests that use the DOF framework.
 * 
 * @author Donald S. Bell
 *
 */
public class DOFSamplesJdbcCustomerFactoryImplTest extends JdbcFactoryImplTest {

	/**
     * This tests to make sure the factory can retreive an existing customer.
     * @Test
     */
    public void testDOFSampleGetCustomer25() {
    	// Make sure customer 25 is in the DB.
    	Customer dofCustomer = (Customer) DOF.require("customer.25.xml");
    	
    	// Retreive customer 25.
    	CustomerFactory factory = getCustomerFactory();
    	int customerId = dofCustomer.getId();
    	Customer factoryCustomer = factory.getById(customerId);
    	
    	// Now see if the two objects are equal.
    	assertEquals(dofCustomer, factoryCustomer);
    }
    
    /**
     * This tests that when a customer's name is updated it is saved in the
     * database and is able to be retreived with the updated name.
     * @Test
     */
    public void testDOFSampleUpdateCustomer25() {
    	
    	// Make sure we have our customer 25
    	Customer dofCustomer = (Customer) DOF.require("customer.25.xml");
    	
    	// Now update the customer's name with a different name and save
    	// it
    	dofCustomer.setName("Jane Doe");
    	CustomerFactory factory = getCustomerFactory();
    	factory.update(dofCustomer);
    	
    	// Now retreive the customer from the factory (which should also
    	// be the database.
    	int customerId = dofCustomer.getId();
    	Customer dbCustomer = factory.getById(customerId);
    	
    	// Now make sure the objects are equal
    	assertTrue(dofCustomer.equals(dbCustomer));
    	
    	// Because we changed the customer object we now need to delete
    	// it from DB because other tests could depend on specific values
    	// that DOF initializes the object too.
    	DOF.delete("customer.25.xml");
    	// We can do this delete because the other tests will do a
    	// DOF.require(....) and that will recreate the object to
    	// the original value.
    }
    
    /**
     * This tests the deleting of a customer.
     * @Test
     */
    public void testDOFSampleDeleteCustomer05()
    {
    	// Make sure we have our customer 05
    	Customer dofCustomer = (Customer) DOF.createScratchObject("customer.05.xml");
    	
    	// Get the customer's ID so we can try to look it up later.
    	int customerId = dofCustomer.getId();
    	
    	// Now delete the customer.
    	CustomerFactory factory = getCustomerFactory();
    	factory.delete(dofCustomer);

    	// Now try to relookup the customer.
    	Customer dbCustomer = factory.getById(customerId);
    	
    	// If the delete happened the dbCustomer will be null.
    	assertNull(dbCustomer);
    	
    	// Because we deleted the DOF object (e.g. Customer object) outside
    	// of the DOF framework, we need to clear the DOF cache.
    	DOF.clearFileCache();    	
    }

}
