package finance;

public abstract class Transaction {
	
	protected double amount;
	protected String name;
	protected Date date;
	
	double getAmount() { 
		return amount; 
	}
	
	String getName() { 
		return name; 
	}
	
	Date getDate() {
		return getDate();
	}
}