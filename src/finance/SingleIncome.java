package finance;

public class SingleIncome extends Income {
	
	private double balanceAfter;
	
	public SingleIncome(double a, String n, String c, Date d, double b, String pri) {
		amount = a;
		name = n;
		category = c;
		date = d;
		balanceAfter = b + a;
		parentIdentifier = pri;
	}
	
	public double getBalanceAfter() {
		return balanceAfter;
	}

}
