package TestSuite;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;

import finance.Account;
import finance.Runner;
import finance.SingleExpense;
import finance.Date;


public class AccountTest {
	
	private String name = "Hannah";
	private String type = "Trust";
	private double balance = 10000;

	private Account correct = new Account(name, type, balance);
	private Account correct2 = new Account("Collin", "Checking", balance);
	private Account correct3 = new Account("Elizabeth", "Savings", balance);
	
	@Test
	public void testGetters() {
		assertEquals(name, correct.getName());
		assertTrue(balance == correct.getBalance());
		assertEquals(type, correct.getType());
	}
	
	@Test
	public void resourceIdentifierItems() {
	
		correct.setResourceIdentifier("test1");
		assertEquals("test1", correct.getResourceIdentifier());		
	}
	
	@Test
	public void addSingleExpenseTest() {
	
		correct2.addSingleExpense(20, "turtle", "test", new Date(5, 4, 2018) );
		
		assertTrue(correct2.getBalance() == 9980);
		
				
	}

}