package TestSuite;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import finance.Account;
import finance.User;

public class UserTest {
	private String email = "test@test.com";
	private String password = "123abc";
	private String firstName = "Hannah";
	private String lastName = "Deen";
	private Map<String, Account> accounts;
	private String resourceIdentifier;
	
	public User test = new User(email, password, firstName, lastName);

	@Test
	public void testGetters() {
		assertEquals(firstName, test.getFirstName());
		assertEquals(lastName, test.getLastName());
		assertTrue(0 == test.getBalance());
		assertEquals(email, test.getEmail());
	}
	
	@Test
	public void testSetResourceID() {
		test.setResourceIdentifier("test1");
		assertEquals("test1", test.getResourceIdentifier());
	}

}
