package finance;

public abstract class Transaction {
	
	//features all inherited classes have
	protected double amount;
	protected String name;
	protected Date date;
	protected String category;
	protected String resourceIdentifier;
	protected String parentIdentifier;
	
	//getters and setters
	public double getAmount() { 
		return amount; 
	}
	
	public String getName() { 
		return name; 
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getResourceIdentifier() {
		return resourceIdentifier;
	}
	
	public String getParentIdentifier() {
		return parentIdentifier;
	}
	
	public void setResourceIdentifier(String identifier) {
		resourceIdentifier = identifier;
	}
}