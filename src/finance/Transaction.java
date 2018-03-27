package finance;

public abstract class Transaction {
	
	protected double amount;
	protected String name;
	protected Date date;
	protected String category;
	protected String resourceIdentifier;
	
	public double getAmount() { 
		return amount; 
	}
	
	public String getName() { 
		return name; 
	}
	
	public Date getDate() {
		return getDate();
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getResourceIdentifier() {
		return resourceIdentifier;
	}
}