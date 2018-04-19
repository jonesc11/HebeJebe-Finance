package finance;

import finance.FinanceUtilities.Period;

public class RecurringIncome extends Income {
	
	private Date endDate;
	private Date lastUpdated;
	private Period period;
	
	/*
	 * @requires a > 0, n.len() > 0, c.len() > 0, p != null, d1 != null, d2!= null, b > 0, pri.len() > 0 
	 * @throws none 
	 * @modifies Recurring income
	 * @effects creates a new recurringincome object
	 * @returns this
	 * @param double a - amount of income
	 * @param string n - name of income
	 * @param string c - catergory of income
	 * @param Period p - period of income
	 * @param DAte d1 - date of start of income
	 * @param Date d2 - date of end of income
	 * @param double b - balance of sincome
	 * @param string pri - parent account of the income
	 */
	public RecurringIncome(double a, String n, String c, Period p, Date d1, Date d2, String pri) {
		amount = a;
		name = n;
		category = c;
		period = p;
		date = d1;
		lastUpdated = d1;
		endDate = d2;
		parentIdentifier = pri;
	}
	
	//getters
	public String getParentIdentifier() {
		return parentIdentifier;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public Period getPeriod() {
		return period;
	}
	
	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	public void updateLastUpdated(Date d) {
		lastUpdated = d;
	}
	
	/*
	 * @requires d != null 
	 * @throws none 
	 * @modifies none
	 * @effects none
	 * @returns the amount of money that will be had in income by a certain date
	 * @param DAte d - the date in which the amount of money will be checked against
	 */
	public double amountByDate(Date d) {
		int periods = d.periodsBetween(lastUpdated, period);
		return periods * amount;
	}

}
