package finance;

import finance.FinanceUtilities.Period;

public class RecurringExpense extends Expense {
	
	private Date endDate;
	private Date lastUpdated;
	private Period period;
	/*
	 * @requires a > 0, n.len() > 0, c.len() > 0, p != null, d1 != null, d2!= null, b > 0, pri.len() > 0 
	 * @throws none 
	 * @modifies Recurring income
	 * @effects creates a new recurringExpense object
	 * @returns this
	 * @param double a - amount of Expense
	 * @param string n - name of Expense
	 * @param string c - catergory of Expense
	 * @param Period p - period of Expense
	 * @param DAte d1 - date of start of Expense
	 * @param Date d2 - date of end of Expense
	 * @param double b - balance of Expense
	 * @param string pri - parent account of the Expense
	 */
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
	
	/*
	 * @requires d != null 
	 * @throws none 
	 * @modifies none
	 * @effects none
	 * @returns the amount of money that will be had in expense by a certain date
	 * @param DAte d - the date in which the amount of money will be checked against
	 */
	public double amountByDate(Date d) {
		int periods = d.periodsBetween(lastUpdated, period);
		return periods * amount;
	}

}
