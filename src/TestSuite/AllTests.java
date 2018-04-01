package TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	SingleExpenseTest.class,
	DateTest.class,
	AccountTest.class
	
	
})
public class AllTests {

}
