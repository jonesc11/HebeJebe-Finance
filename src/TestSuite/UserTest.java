package TestSuite;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import finance.Account;
import finance.Date;
import finance.User;
import finance.FinanceUtilities.Period;

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
	
	@Test
	public void testCreateAccount() {
		String test1 = test.createAccount("test2", "Savings", 10000);
		assertTrue("a0" == test1);
		assertEquals(test1, test.getAccountResourceIdentifiers());

	}
	
	@Test
	public void testCreateExpenseRecurring() {
		String test1 = test.createExpense("a0", 1000, "rent", "housing", new Date(12, 4, 2018), true, new Date(12, 4, 2019), Period.MONTHLY);
		assertTrue("t0" == test1);
	}
	
	@Test
	public void testCreateExpenseSingle() {
		String test1 = test.createExpense("a0", 25, "movie tickets", "misc", new Date(12, 4, 2018), false, null, null);
		assertTrue("t1" == test1);
	}
	
	@Test
	public void testCreateIncomeSingle() {
		String test1 = test.createExpense("a0", 85, "snow removal", "misc", new Date(12, 4, 2018), false, null, null);
		assertTrue("t2" == test1);
	}
	
	@Test
	public void testCreateIncomeRecurring() {
		String test1 = test.createExpense("a0", 100, "tutoring pay", "income", new Date(12, 4, 2018), true, new Date(12, 4, 2019), Period.MONTHLY);
		assertTrue("t3" == test1);
	}

}
