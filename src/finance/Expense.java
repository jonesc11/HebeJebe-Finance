package finance;

public abstract class Expense extends Transaction{
	
	protected String name;
	protected double amount;
	
	public String getName() {
		return name;
	}
	
	public double getAmount() {
		return amount;
	}
}
