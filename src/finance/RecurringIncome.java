package finance;

import finance.FinanceUtilities.Period;

public class RecurringIncome extends Income {
	
	private Date endDate;
	private Date lastUpdated;
	private Period period;
	
	public RecurringIncome(double a, String n, String c, Period p, Date d1, Date d2, String pri) {
		amount = a;
		name = n;
		category = c;
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
	
	public double amountByDate(Date d) {
		int periods = d.periodsBetween(lastUpdated, period);
		return periods * amount;
	}

}
