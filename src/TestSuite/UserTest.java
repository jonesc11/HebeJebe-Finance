package TestSuite;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import finance.Account;
import finance.Date;
import finance.Transaction;
import finance.User;
import finance.FinanceUtilities.Period;

public class UserTest {
	private String email = "test@test.com";
	private String password = "123abc";
	private String firstName = "Hannah";
	private String lastName = "Deen";
	private String salt = "abc123youandmegirl";
	private Account account = new Account("Collin", "Checking", 5000);
	private HashMap<String, Account> accounts = new HashMap<String, Account>();
	private String resourceIdentifier;
	
	public User test = new User(email, password, salt, firstName, lastName);

	@Test
	public void testGetters() {
		assertEquals(firstName, test.getFirstName());
		assertEquals(lastName, test.getLastName());
		assertTrue(0 == test.getBalance());
		assertEquals(email, test.getEmail());
	}
	
	@Test
	public void testGetBalance() {
		User test2 = new User("testing@tester.org", password, firstName, lastName, accounts);
		accounts.put("test1", account);
		assertTrue(5000 == test2.getBalance());
	}
	
	@Test
	public void testSetResourceID() {
		test.setResourceIdentifier("test1");
		assertEquals("test1", test.getResourceIdentifier());
	}
	
	@Test
	public void testCreateAccount() {
		String test1 = test.createAccount("test2", "Savings", 10000);
		assertNotNull(test1);
	}
	
	@Test
	public void testGetAccountRID() {
		test.createAccount("test2", "Savings", 10000);
		List<String> test1 = test.getAccountResourceIdentifiers();
		assertTrue(test1.size() == 1);	
	}
	
	@Test
	public void testCreateExpenseRecurring() {
		test.createAccount("test2", "Savings", 10000);
		String test1 = test.createExpense("a0", 1000, "rent", "housing", new Date(12, 4, 2018), true, new Date(12, 4, 2019), Period.MONTHLY);
		assertNotNull(test1);
	}
	
	@Test
	public void testCreateExpenseSingle() {
		test.createAccount("test2", "Savings", 10000);
		String test1 = test.createExpense("a0", 25, "movie tickets", "misc", new Date(12, 4, 2018), false, null, null);
		assertNotNull(test1);
	}
	
	@Test
	public void testCreateIncomeSingle() {
		test.createAccount("test2", "Savings", 10000);
		String test1 = test.createExpense("a0", 85, "snow removal", "misc", new Date(12, 4, 2018), false, null, null);
		assertNotNull(test1);
	}
	
	@Test
	public void testCreateIncomeRecurring() {
		test.createAccount("test2", "Savings", 10000);
		String test1 = test.createExpense("a0", 100, "tutoring pay", "income", new Date(12, 4, 2018), true, new Date(12, 4, 2019), Period.MONTHLY);
		assertNotNull(test1);
	}
	
	@Test
	public void testGetTransactionHistoryWithNoTransactions() {
		test.createAccount("test2", "Savings", 10000);
		List<Transaction> test1 = test.getTransactionHistory();
		assertTrue(test1.size() == 0);
	}
	
	@Test
	public void testGetTransactionHistory() {
		test.createAccount("test2", "Savings", 10000);
		test.createExpense("a0", 85, "snow removal", "misc", new Date(12, 4, 2018), false, null, null);
		test.createExpense("a0", 85, "snow removal", "misc", new Date(12, 4, 2018), false, null, null);
		List<Transaction> test1 = test.getTransactionHistory();
		assertTrue(test1.size() == 2);
	}
	
	@Test
	public void testGetAccounts() {
		User test2 = new User("testing@tester.org", password, firstName, lastName, accounts);
		account.addRecurringIncome(200, "raking", "income", Period.WEEKLY, new Date(12, 4, 2018), new Date(12, 4, 2019));

		accounts.put("test1", account);

		List<Account> test1 = test2.getAccounts();
		assertTrue(test1.size() == 1);
	}
	
	@Test
	public void testGetTransactionHistoryWithAccount() {
		
		User test2 = new User("testing@tester.org", password, firstName, lastName, accounts);
		account.addRecurringIncome(200, "raking", "income", Period.WEEKLY, new Date(12, 4, 2018), new Date(12, 4, 2019));

		accounts.put("test1", account);

		List<Transaction> test1 = test2.getTransactionHistory(account);
		assertTrue(test1.size() == 1);
	}
	
	@Test
	public void testGetTransactionHistoryWithAccountAndNoTransactions() {
		
		User test2 = new User("testing@tester.org", password, firstName, lastName, accounts);
		accounts.put("test1", account);

		List<Transaction> test1 = test2.getTransactionHistory(account);
		assertTrue(test1.size() == 0);
	}

}
