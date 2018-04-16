package TestSuite;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import finance.Date;
import finance.RecurringExpense;
import finance.FinanceUtilities.Period;


public class RecurringExpenseTest {
	private double a = 300.00;
	private String n = "groceries";
	private String c = "Food";
	private Period p = Period.MONTHLY;
	private Date d1 = new Date(15, 3, 2018);
	private Date d2 = new Date(5, 10, 2018);
	private Date d3 = new Date(16, 4, 2018);
	private String pri = "parent";
	private RecurringExpense original = new RecurringExpense(a, n, c, p, d1, d2, pri);
	
	
	@Test 
	public void testCreation() { 
		assertTrue(a == original.getAmount());
		assertEquals("fail on getName() on recurringExpense",n, original.getName());
		assertEquals("fail on getCategorey() on recurringExpense", c, original.getCategory());
		assertEquals("fail on getDate() on recurringExpense", d1, original.getDate() );
		assertEquals("fail on getPeriod() on recurringExpense", p, original.getPeriod() );
		assertEquals("fail on getEndDate() on recurringExpense", d2, original.getEndDate() );
		assertEquals("fail on getparentIdentifier() on recurringExpense", pri, original.getParentIdentifier() );
	}
	
	@Test
	public void testGetPeriod() {
		assertEquals("fail on getPeriod() on recurringExpense", p, original.getPeriod() );
	}
	
	@Test
	public void testGetEndDate() {
		assertEquals("fail on getEndDate() on recurringExpense", d2, original.getEndDate() );

	}
	
	@Test
	public void testSetLastUpdate() {
		original.updateLastUpdated(d3);
		assertEquals("fail on updateLastupdate() on recurringExpense", d3, original.getDate() );

	}
	
	@Test
	public void testAmountByDate() {
		assertNotNull(original.amountByDate(d3));
	}

}
