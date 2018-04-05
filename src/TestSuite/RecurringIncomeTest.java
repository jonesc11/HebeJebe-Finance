package TestSuite;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import finance.Date;
import finance.RecurringIncome;
import finance.FinanceUtilities.Period;


public class RecurringIncomeTest {
	private double a = 300.00;
	private String n = "tutoring pay";
	private String c = "Income";
	private Period p;
	private Date d1 = new Date(30, 3, 2018);
	private Date d2 = new Date(5, 10, 2018);
	private RecurringIncome original = new RecurringIncome(a, n, c, p.WEEKLY, d1, d2);
	
	
	@Test
	public void testCreation() {
		assertTrue(a == original.getAmount());
		assertEquals("fail on getName() on recurringExpense",n, original.getName());
		assertEquals("fail on getCategorey() on recurringExpense", c, original.getCategory());
		assertEquals("fail on getDate() on recurringExpense", d1, original.getDate() );
		assertEquals("fail on getPeriod() on recurringExpense", p.WEEKLY, original.getPeriod() );
		assertEquals("fail on getEndDate() on recurringExpense", d2, original.getEndDate() );
	}

}
