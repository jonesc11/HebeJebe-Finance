package TestSuite;

import static org.junit.Assert.*;

import org.junit.Test;

import finance.FinanceUtilities;

public class FinanceUtilitiesTest {

	@Test
	public void testCreation() {
		assertNotNull(new FinanceUtilities());
	}

}
