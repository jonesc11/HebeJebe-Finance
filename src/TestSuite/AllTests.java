package TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	SingleExpenseTest.class,
	SingleIncomeTest.class,
	RecurringExpenseTest.class,
	RecurringIncomeTest.class,
	DateTest.class,
	AccountTest.class,
	TransactionTest.class,
	BudgetTest.class,
	UserTest.class
	
	
	
})
public class AllTests {

}
