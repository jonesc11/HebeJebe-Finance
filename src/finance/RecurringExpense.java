package finance;

import finance.FinanceUtilities.Period;

public class RecurringExpense extends Expense {
	
	public RecurringExpense(double a, String n, Period p, Date startDate) {
		amount = a;
		name = n;
	}

}
