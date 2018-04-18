package finance;

import finance.FinanceUtilities.Period;

public class RecurringExpense extends Expense {
	
	private Date endDate;
	private Date lastUpdated;
	private Period period;
	
	public RecurringExpense(double a, String n, String c, Period p, Date d1, Date d2, String pri) {
		amount = a;
		name = n;
		category = c;
		period = p;
		date = d1;
		lastUpdated = d1;
		endDate = d2;
		parentIdentifier = pri;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public Period getPeriod() {
		return period;
	}
	
	public void updateLastUpdated(Date d) {
		lastUpdated = d;
	}
	
	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	public double amountByDate(Date d) {
		int periods = d.periodsBetween(lastUpdated, period);
		return periods * amount;
	}

}
