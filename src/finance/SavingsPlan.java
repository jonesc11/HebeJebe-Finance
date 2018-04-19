package finance;

public class SavingsPlan {
	
	private String name;
	private double amount;
	private double balance;
	private Date date;
	private String resourceIdentifier;
	private String parentIdentifier;
	
	public SavingsPlan() {
		
	}
	
	public SavingsPlan(String n, double a, Date d, String parentRI) {
		name = n;
		amount = a;
		balance = 0;
		date = d;
		parentIdentifier = parentRI;
	}
	
	public String getName() {
		return name;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getResourceIdentifier() {
		return resourceIdentifier;
	}
	
	public String getParentResourceIdentifier() {
		return parentIdentifier;
	}
	
	public void setResourceIdentifier(String ri) {
		resourceIdentifier = ri;
	}
	
	public void updateName(String n) {
		this.name = n;
	}
	
	public void updateAmount(double a) {
		this.amount = a;
	}
	
	public void updateBalance(double b) {
		this.balance = b;
	}
	
	public void updateDate(Date d) {
		this.date = d;
	}
}