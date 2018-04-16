package TestSuite;

import static org.junit.Assert.*;

import org.junit.Test;

import finance.Date;
import finance.DateFactory;

public class DateFactoryTest {
	private DateFactory test = new DateFactory();
	
	@Test
	public void testgetDate() {
		Date test1 = DateFactory.getDate(1, 3, 2010);
		assertNotNull(test1);
	}

}
