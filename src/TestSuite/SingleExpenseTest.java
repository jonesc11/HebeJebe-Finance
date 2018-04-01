package TestSuite;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import finance.Date;
import finance.SingleExpense;

public class SingleExpenseTest {
	private double a = 300.00;
	private String n = "dog treats";
	private String c = "Other";
	private Date d = new Date(30, 3, 2018);
	private SingleExpense original = new SingleExpense(a, n, c, d);
	
	
	@Test
	public void testCreation() {
		assertTrue(a == original.getAmount());
		assertEquals("fail on getName() on singleExpense",n, original.getName());
		assertEquals("fail on getCategorey() on singleExpense", c, original.getCategory());
		assertEquals("fail on getDate() on singleExpense", d, original.getDate() );
	}

}
