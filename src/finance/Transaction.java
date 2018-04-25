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
	
	public void updateAmount(double a) {
		this.amount = a;
		dbParser.updateTransaction(this.resourceIdentifier, "Amount", this.amount);
	}
	
	public void updateName(String n) {
		this.name = n;
		dbParser.updateTransaction(this.resourceIdentifier, "Description", this.name);
	}
	
	public void updateDate(Date d) {
		this.date = d;
		dbParser.updateTransaction(this.resourceIdentifier, "DateTime", this.date.format());
	}
	
	public void updateCategory(String c) {
		this.category = c;
		dbParser.updateTransaction(this.resourceIdentifier, "Category", this.category);
	}
	
	public void updateParent(String parentRI) {
		this.parentIdentifier = parentRI;
		dbParser.updateTransaction(this.resourceIdentifier, "ParentIdentifier", this.parentIdentifier);
	}
	
	public void delete() {
		Parser.removeResource(this.resourceIdentifier);
		dbParser.deleteTransaction(this.resourceIdentifier);
	}
}