package finance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import finance.FinanceUtilities.Period;

public class BudgetFactory extends Budget{
	
	protected Date startDate;
	protected Date endDate;
	protected double limit;
	protected double balance;
	protected String name;
	protected Budget current;
	protected Period period = Period.DAILY;
	protected String resourceIdentifier;
	
	public BudgetFactory(Date s, Date e, double l, double b, String n)
	{
		startDate = s;
		endDate = e;
		limit = l;
		balance = b;
		name = n;
		
		current = new Budget(name, limit, e.periodsBetween(s, period), balance);
	}
	
	public double getLimit() { 
		return current.getLimit(); 
	}
	
	public String getName() { 
		return current.getName(); 
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public String getResourceIdentifier() {
		return current.getResourceIdentifier();
	}
	
	public void setResourceIdentifier(String identifier) {
		current.setResourceIdentifier(identifier);
	}
	
	public double getBalance() {
		return current.getBalance();
	}
	
	public List<Transaction> getTransactionHistory() {
		return current.getTransactionHistory();
	}
	
	
}
