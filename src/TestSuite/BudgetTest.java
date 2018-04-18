package TestSuite;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import finance.Account;
import finance.Budget;
import finance.Transaction;
import finance.User;
import finance.Date;

public class BudgetTest {
	protected double limit = 1000;
	protected String name = "resopsible";
	protected int duration = 7;
	protected String resourceIdentifier = "test";
	protected double balance;
	protected User user = new User("testing@tester.org", "password", "salt", "firstName", "lastName");
	
	Budget test = new Budget();
	Budget test1 = new Budget(name, limit, duration, user);

	@Test
	public void GettersTest() {
		assertTrue(test1.getLimit() == 1000);
		assertTrue(test1.getDuration() == duration);
		assertEquals(test1.getName(), name);
	}
	
	@Test
	public void testGetRID() {
		test1.setResourceIdentifier("testing");
		assertEquals("testing", test1.getResourceIdentifier());
	}
	
	@Test
	public void testGetTransactionHistoryWithNoTransactions() {
		HashMap<String, Account> accounts = new HashMap<String, Account>();
		Account account = new Account("Collin", "Checking", 5000);
		User test2 = new User("testing@tester.org", "password", "firstName", "lastName", accounts);
		accounts.put("test1", account);
		Budget test3 = new Budget(name, limit, duration, test2);

		List<Transaction> test1 = test3.getTransactionHistory();
		assertTrue(test1.size() == 0);
	}
	
	@Test
	public void testGetTransactionHistory() {
		HashMap<String, Account> accounts = new HashMap<String, Account>();
		Account account = new Account("Collin", "Checking", 5000);
		User test2 = new User("testing@tester.org", "password", "firstName", "lastName", accounts);
		accounts.put("test1", account);
		account.addSingleExpense(20, "paper", "office", new Date(16, 4, 2018));
		Budget test3 = new Budget(name, limit, duration, test2);
		List<Transaction> test1 = test3.getTransactionHistory();
		assertTrue(test1.size() == 1);
	}
	
	@Test
	public void testGetBalance() {
		HashMap<String, Account> accounts = new HashMap<String, Account>();
		Account account = new Account("Collin", "Checking", 5000);
		User test2 = new User("testing@tester.org", "password", "firstName", "lastName", accounts);
		accounts.put("test1", account);
		account.addSingleExpense(20, "paper", "office", new Date(16, 4, 2018));
		Budget test3 = new Budget(name, limit, duration, test2);

		assertTrue( test3.getBalance() == 4980);
	}

}
