package TestSuite;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import finance.Account;
import finance.Date;
import finance.IAccount;
import finance.SubBalance;
import finance.Transaction;
import finance.FinanceUtilities.Period;

public class SubBalanceTest {
	private String name = "saving for austin";
	private IAccount parent = new Account("Elizabeth", "Savings", 10000);
	private double balance = 700;
	
	private SubBalance test = new SubBalance(name, balance, parent);
	
	@Test
	public void testGetters() {
		assertEquals(name, test.getName());
		assertEquals(parent.getResourceIdentifier(), test.getParentIdentifier());
		assertTrue(balance == test.getBalance());
		
	}
	
	@Test
	public void testCreation() {
		Map<String, Transaction> transactions = new HashMap<String, Transaction>();
		SubBalance test2 = new SubBalance(name, balance, parent, transactions);
		
		assertNotNull(test2);
		
	}
	
	@Test
	public void testResourceID() {
		test.setResourceIdentifier("test1");
		assertEquals("test1", test.getResourceIdentifier());
	}
	
	@Test
	public void addSingleExpenseTest() {
		Account correct2 = new Account("Collin", "Checking", balance);
		SubBalance test2 = new SubBalance(name, balance, correct2);
		String test = test2.addSingleExpense(20, "turtle", "test", new Date(5, 4, 2018) );
		
		//assertTrue(correct2.getBalance() == 680);
		assertTrue("t0" == test);
					
	}
	
	@Test
	public void addRecurringExpenseTest() {
		Account correct2 = new Account("Collin", "Checking", balance);
		SubBalance test2 = new SubBalance(name, balance, correct2);
		String test = test2.addRecurringExpense(20, "gas", "test", Period.WEEKLY, new Date(5, 4, 2018), new Date(5, 4, 2020) );
		
		assertTrue(correct2.getBalance() == 680);
		assertEquals("t0",test);
				
	}
	
	@Test
	public void addSingleIncomeTest() {
		Account correct2 = new Account("Collin", "Checking", balance);
		SubBalance test2 = new SubBalance(name, balance, correct2);
		String test = test2.addSingleExpense(45, "mowed ma's lawn", "test", new Date(5, 4, 2018) );
		
		assertTrue(correct2.getBalance() == 745);
		assertEquals("t0",test);
					
	}
	
	@Test
	public void addRecurringIncomeTest() {
	
		Account correct2 = new Account("Collin", "Checking", balance);
		SubBalance test2 = new SubBalance(name, balance, correct2);
		String test = test2.addRecurringIncome(20, "getting mail for grampa", "test", Period.WEEKLY, new Date(5, 4, 2018), new Date(5, 4, 2020) );
		
		assertTrue(correct2.getBalance() == 720);
		assertEquals("t0",test);
					
	} 
	
	@Test
	public void addTransferTest() {
	
		Account correct2 = new Account("Collin", "Checking", balance);
		SubBalance test2 = new SubBalance(name, balance, correct2);
		String test = test2.addTransfer(20, "from grams for birthday" );
		
		assertEquals("t0",test);
					
	}

}
