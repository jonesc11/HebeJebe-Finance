package TestSuite;

import static org.junit.Assert.*;

import org.junit.Test;

import finance.Date;
import finance.Transfer;

public class TransferTest {
	double amount = 100;
	String name = "test";
	String category = "testers";
	Date date = new Date(16, 4, 2018);
	String fromResourceIdentifier = "fRI";
	String toResourceIdentifier = "tRI";
	double fromBalanceAfter = 300;
	double toBalanceAfter = 200;
	
	Transfer test = new Transfer(amount, name, category, date, fromResourceIdentifier, toResourceIdentifier, fromBalanceAfter, toBalanceAfter );
	
	@Test
	public void GettersTest() {
		assertEquals(test.getFromResourceIdentifier(), fromResourceIdentifier);
		assertEquals(test.getToResourceIdentifier(), toResourceIdentifier);
		assertTrue(test.getToBalanceAfter() == toBalanceAfter);
		assertTrue(test.getFromBalanceAfter() == fromBalanceAfter);
	}

}
