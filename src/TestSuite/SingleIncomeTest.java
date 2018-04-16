package TestSuite;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import finance.Date;
import finance.SingleIncome;

public class SingleIncomeTest {
	private double a = 300.00;
	private String n = "yard work for gram";
	private String c = "Other";
	private Date d = new Date(30, 3, 2018);
	private String pri = "parent";
	private double b = 1300;
	private SingleIncome original = new SingleIncome(a, n, c, d, b, pri);
	
	
	@Test
	public void testCreation() {
		assertTrue(a == original.getAmount());
		assertEquals("fail on getName() on singleIncome",n, original.getName());
		assertEquals("fail on getCategorey() on singleIncome", c, original.getCategory());
		assertEquals("fail on getDate() on singleIncome", d, original.getDate() );
		assertEquals("fail on getParentIdentifier() on singleIncome", pri, original.getParentIdentifier());
		assertTrue(b == original.getBalanceAfter());
	}

}
