package TestSuite;

import static org.junit.Assert.*;

import org.junit.Test;
import finance.Budget;

public class BudgetTest {
	Budget test = new Budget();

	@Test
	public void CreationTest() {
		assertNull(test);
	}

}
