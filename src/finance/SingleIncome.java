package finance;

public class SingleIncome extends Income {
	
	private double balanceAfter;
	
	public SingleIncome(double a, String n, String c, Date d, double b, String pri) {
		amount = a;
		name = n;
		category = c;
		date = d;
		balanceAfter = b;
		parentIdentifier = pri;
	}
	
	public double getBalanceAfter() {
		return balanceAfter;
	}
	
	public void updateAmount(double a) {
		IAccount parent = (IAccount)Parser.getResource(this.parentIdentifier);
		parent.updateBalance(parent.getBalance() - (this.amount - a));
		this.amount = a;
		dbParser.updateTransaction(this.resourceIdentifier, "Amount", this.amount);
	}
	
	public void updateParent(String parentRI) {
		IAccount oldParent = (IAccount)Parser.getResource(parentRI);
	}

}
