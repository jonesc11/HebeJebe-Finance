package finance;

import finance.FinanceUtilities.Period;

public class RecurringExpense extends Expense {
	
	private Date endDate;
	private Period period;
	
	public RecurringExpense(double a, String n, String c, Period p, Date d1, Date d2) {
		amount = a;
		name = n;
		category = c;
		date = d1;
		endDate = d2;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public Period getPeriod() {
		return period;
	}

}
