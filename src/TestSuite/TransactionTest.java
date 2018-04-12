package TestSuite;

import static org.junit.Assert.*;

import org.junit.Test;

import finance.Date;
import finance.SingleExpense;

public class TransactionTest {
	private double a = 300.00;
	private String n = "dog treats";
	private String c = "Other";
	private Date d = new Date(30, 3, 2018);
	private SingleExpense original = new SingleExpense(a, n, c, d);

	@Test
	public void testGetters() {
		assertEquals(n, original.getName());
		assertTrue(a == original.getAmount());
		assertEquals(c, original.getCategory());
		assertEquals(d, original.getDate());
	}
	
	@Test
	public void SetResourceIDTest() {
		original.setResourceIdentifier("test1");
		assertEquals("test1", original.getResourceIdentifier());
	}

}
