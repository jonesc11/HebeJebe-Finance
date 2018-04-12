package TestSuite;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;

import finance.Account;
import finance.Runner;
import finance.SingleExpense;
import finance.RecurringExpense;
import finance.Date;
import finance.FinanceUtilities;
import finance.FinanceUtilities.Period;


public class AccountTest {
	
	private String name = "Hannah";
	private String type = "Trust";
	private double balance = 10000;

	private Account correct = new Account(name, type, balance);
	private Account correct3 = new Account("Elizabeth", "Savings", balance);
	
	@Test
	public void testGetters() {
		assertEquals(name, correct.getName());
		assertTrue(balance == correct.getBalance());
		assertTrue(balance == correct.getTotal());
		assertEquals(type, correct.getType());
	}
	
	@Test
	public void resourceIdentifierItems() {
	
		correct.setResourceIdentifier("test1");
		assertEquals("test1", correct.getResourceIdentifier());		
	}
	
	@Test
	public void addSingleExpenseTest() {
		Account correct2 = new Account("Collin", "Checking", balance);
		String test = correct2.addSingleExpense(20, "turtle", "test", new Date(5, 4, 2018) );
		
		assertTrue(correct2.getBalance() == 9980);
		assertEquals("t0",test);
					
	}
	
	@Test
	public void addRecurringExpenseTest() {
		Account correct2 = new Account("Collin", "Checking", balance);
		String test = correct2.addRecurringExpense(20, "gas", "test", Period.WEEKLY, new Date(5, 4, 2018), new Date(5, 4, 2020) );
		
		assertTrue(correct2.getBalance() == 9980);
		assertEquals("t0",test);
				
	}
	
	@Test
	public void addSingleIncomeTest() {
		Account correct2 = new Account("Collin", "Checking", balance);
		String test = correct2.addSingleExpense(45, "mowed ma's lawn", "test", new Date(5, 4, 2018) );
		
		assertTrue(correct2.getBalance() == 10045);
		assertEquals("t0",test);
					
	}
	
	@Test
	public void addRecurringIncomeTest() {
	
		Account correct2 = new Account("Collin", "Checking", balance);
		String test = correct2.addRecurringIncome(20, "getting mail for grampa", "test", Period.WEEKLY, new Date(5, 4, 2018), new Date(5, 4, 2020) );
		
		assertTrue(correct2.getBalance() == 10020);
		assertEquals("t0",test);
					
	} 
	
	@Test
	public void addTransferTest() {
	
		Account correct2 = new Account("Collin", "Checking", balance);
		String test = correct2.addTransfer(20, "from grams for birthday" );
		
		assertEquals("t0",test);
					
	}

}
