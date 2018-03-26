package finance;

import finance.FinanceUtilities.Period;

public class RecurringIncome extends Income {
	
	public RecurringIncome(double a, String n, Period p, Date startDate) {
		amount = a;
		name = n;
	}

}
