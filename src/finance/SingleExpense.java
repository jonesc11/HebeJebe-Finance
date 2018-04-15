package finance;

public class SingleExpense extends Expense {
	
	private double balanceAfter;
	
	public SingleExpense(double a, String n, String c, Date d, double b) {
		amount = a;
		name = n;
		category = c;
		date = d;
		balanceAfter = b - a;
	}
	
	public double getBalanceAfter() {
		return balanceAfter;
	}
	
}
