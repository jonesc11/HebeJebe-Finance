package finance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Budget {
	
	protected double limit;
	protected String description;
	protected int duration;
	protected double balance;
	protected Date startDate;
	protected Date endDate;
	protected String resourceIdentifier;
	protected String parentIdentifier;
	
	public Budget() {
		
	}
	
	/*
	 * @requires n.len() > 0, l > 0, d > 0, u != null 
	 * @throws none 
	 * @modifies none
	 * @effects creates a budget object
	 * @returns this
	 * @param string n - name of budget
	 * @param double l - limit of budget
	 * @param int d - duration of the budget in days
	 * @param User u - user making the budget
	 */
	public Budget(String desc, double l, int dur, String parentRI, Date d1, Date d2) {
		this.description = desc;
		this.limit = l;
		this.duration = dur;
		this.parentIdentifier = parentRI;
		this.startDate = d1;
		this.endDate = d2;
	}
	
	//getters
	public double getLimit() { 
		return limit; 
	}
	
	public String getDescription() { 
		return description; 
	}
	
	public int getDuration() {
		return duration;
	}
	
	public String getResourceIdentifier() {
		return resourceIdentifier;
	}
	
	public void setResourceIdentifier(String identifier) {
		this.resourceIdentifier = identifier;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void updateBalance(double b) {
		balance = b;
	}
	
}
