package TestSuite;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import finance.Date;

public class DateTest {
	private int day = 30;
	private int month = 3;
	private int year = 2018;
	
	private int wrongDay = 99;
	private int wrongMonth = 19;
	private int wrongYear = -100;
	
	private Date correct = new Date(day, month, year);
	private Date correct2 = new Date(day, month, year);
	private Date earliest = new Date(3, 7, 1996);
	private Date earlier = new Date(3, 7, 1997);
	private Date nextLastest = new Date(4, 7, 1997);
	private Date nextEarly = new Date(3, 6, 1997);


	
	@Test
	public void throwIllegalArgumentExceptionOnWrongDay() {
		try{
			   new Date(wrongDay, month, year);
			   fail(); // FAIL when no exception is thrown
			} catch (IllegalArgumentException e) {
				assertNotNull(e.getMessage());
			}
	}
	
	@Test
	public void throwIllegalArgumentExceptionOnWrongMonthOver() {
		try{
			   new Date(day, wrongMonth, year);
			   fail(); // FAIL when no exception is thrown
			} catch (IllegalArgumentException e) {
				assertNotNull(e.getMessage());
			}
	}
	
	@Test
	public void throwIllegalArgumentExceptionOnWrongMonthUnder() {
		try{
			   new Date(day, -19, year);
			   fail(); // FAIL when no exception is thrown
			} catch (IllegalArgumentException e) {
				assertNotNull(e.getMessage());
				
			}
	}
	
	@Test
	public void throwIllegalArgumentExceptionOnWrongYear() {
		try{
			   new Date(day, month, wrongYear);
			   fail(); // FAIL when no exception is thrown
			} catch (IllegalArgumentException e) {
				assertNotNull(e.getMessage());
			}
	}
	
	@Test
	public void testGetters() {
		assertTrue("Fail on getDay()", day == correct.getDay());
		assertTrue("Fail on getMonth()", month == correct.getMonth());
		assertTrue("Fail on getYear()", year == correct.getYear());
	}
	
	/*@Test
	public void testDateCheckNull() {
		Date(correct);
	}
	*/
	
	@Test
	public void testDayIsLegal() {
		try{
			   new Date(31, 4, year);
			   fail(); // FAIL when no exception is thrown
			} catch (IllegalArgumentException e) {
			   assertNotNull(e.getMessage());
			}
		try{
			   new Date(33, 7, year);
			   fail(); // FAIL when no exception is thrown
			} catch (IllegalArgumentException e) {
				assertNotNull(e.getMessage());
			}
		try{
			   Date test1 = new Date(31, 7, year);
			   assertNotNull(test1);
			} catch (IllegalArgumentException e) {
				assertNotNull(e.getMessage());
			}
		try{
			   new Date(wrongDay, month, year);
			   fail(); // FAIL when no exception is thrown
			} catch (IllegalArgumentException e) {
				assertNotNull(e.getMessage());
			}
		try{
			   new Date(-1, month, year);
			   fail(); // FAIL when no exception is thrown
			} catch (IllegalArgumentException e) {
				assertNotNull(e.getMessage());
			}
		try{
			   new Date(31, 2, year);
			   fail(); // FAIL when no exception is thrown
			} catch (IllegalArgumentException e) {
				assertNotNull(e.getMessage());
			}
		try{
			   new Date(29, 2, year);
			   fail(); // FAIL when no exception is thrown
			} catch (IllegalArgumentException e) {
				assertNotNull(e.getMessage());
			}
		try{
			   new Date(29, 2, 2011);
			   fail(); // FAIL when no exception is thrown
			} catch (IllegalArgumentException e) {
				assertNotNull(e.getMessage());
			}
		try{
			   new Date(29, 2, 3000);
			   fail(); // FAIL when no exception is thrown
			} catch (IllegalArgumentException e) {
				assertNotNull(e.getMessage());
			}
	}
	
	@Test
	public void testIsBefore() {
		assertTrue(earlier.isBefore(correct));
		assertFalse(nextLastest.isBefore(earliest));
		assertTrue(earliest.isBefore(nextEarly));	
	}
	
	@Test
	public void testEquals() {
		assertTrue(correct.equals(correct2));
		assertFalse(correct.equals(earliest));
	}
	
	
	
}
