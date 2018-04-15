package finance;

public class SingleExpense extends Expense {
	
	private double balanceAfter;
	
	public SingleExpense(double a, String n, String c, Date d, double b, String pri) {
		amount = a;
		name = n;
		category = c;
		date = d;
		balanceAfter = b - a;
		parentIdentifier = pri;
	}
	
	public double getBalanceAfter() {
		return balanceAfter;
	}
	
}
