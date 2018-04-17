package finance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Budget {
	
	protected double limit;
	protected String name;
	protected int duration;
	protected String resourceIdentifier;
	protected double balance;
	protected User user;
	
	public Budget() {
		
	}
	
	public Budget(String n, double l, int d, User u) {
		this.name = n;
		this.limit = l;
		this.duration = d;
		this.user = u;
	}
	
	public double getLimit() { 
		return limit; 
	}
	
	public String getName() { 
		return name; 
	}
	
	public int getDuration() {
		return duration;
	}
	
	public String getResourceIdentifier() {
		return resourceIdentifier;
	}
	
	public void setResourceIdentifier(String identifier) {
		resourceIdentifier = identifier;
	}
	
	public double getBalance() {
		return user.getBalance();
	}
	
	public List<Transaction> getTransactionHistory() {
		return user.getTransactionHistory();
	}
	
	
}