/**
 * 
 */
package org.doframework.sample.jdbc_app.entity.test;

import org.doframework.sample.jdbc_app.entity.Customer;

import junit.framework.TestCase;

/**
 * This tests the {@link org.doframework.sample.jdbc_app.entity.Customer}class
 * 
 * @author Donald S. Bell
 *
 */
public class CustomerTest extends TestCase {

	/**
	 * Test method for {@link org.doframework.sample.jdbc_app.entity.Customer#equals(java.lang.Object)}
	 * with a new Object value.
	 */
	public void testEqualsWithNewObject() {
		
		Customer customer = new Customer();
		Object newObject = new Object();
		
		assertFalse(customer.equals(newObject));
	}

	/**
	 * Test method for {@link org.doframework.sample.jdbc_app.entity.Customer#equals(java.lang.Object)}
	 * with a null value.
	 */
	public void testEqualsWithNull() {
		Customer customer = new Customer();
		
		assertFalse(customer.equals(null));
	}

	/**
	 * Test method for {@link org.doframework.sample.jdbc_app.entity.Customer#equals(java.lang.Object)}
	 * with itself.
	 */
	public void testEqualsWithSelf() {
		
		Customer customer = new Customer();
		
		assertTrue(customer.equals(customer));
	}

	/**
	 * Test method for {@link org.doframework.sample.jdbc_app.entity.Customer#equals(java.lang.Object)}
	 * with a new Customer which is of equal value.
	 */
	public void testEqualsWithEqualCustomers() {
		
		Customer customer1 = new Customer(1);
		Customer customer2 = new Customer(1);
		
		assertTrue(customer1.equals(customer2));
	}

	/**
	 * Test method for {@link org.doframework.sample.jdbc_app.entity.Customer#equals(java.lang.Object)}
	 * with a new different customer.
	 */
	public void testEqualsWithDifferentCustomers() {
		
		Customer customer1 = new Customer(1);
		Customer customer2 = new Customer(2);
		
		assertFalse(customer1.equals(customer2));
	}
}
