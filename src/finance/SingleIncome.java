package finance;

public class SingleIncome extends Income {
	
	private double balanceAfter;
	
	public SingleIncome(double a, String n, String c, Date d, double b) {
		amount = a;
		name = n;
		category = c;
		date = d;
		balanceAfter = b + a;
	}
	
	public double getBalanceAfter() {
		return balanceAfter;
	}

}
