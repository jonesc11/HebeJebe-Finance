package TestSuite;

import static org.junit.Assert.*;

import org.junit.Test;

import finance.Transfer;

public class TransferTest {
	double amount = 100;
	String name = "test";
	
	Transfer test = new Transfer(amount, name);
	
	@Test
	public void CreationTest() {
		assertNotNull(test);
	}

}
