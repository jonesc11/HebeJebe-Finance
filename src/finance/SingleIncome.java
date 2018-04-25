package finance;

public class SingleIncome extends Income {
	
	private double balanceAfter;
	
	/*
	 * @requires a > 0, n.len() > 0, c.len() > 0, d != null, b > 0, pri.len() > 0 
	 * @throws none 
	 * @modifies SingleIncome
	 * @effects creates a new singleincome object
	 * @returns this
	 * @param double a - amount of income
	 * @param string n - name of income
	 * @param string c - catergory of income
	 * @param DAte d - date of income
	 * @param double b - balance of sincome
	 * @param string pri - parent account of the income
	 */
	public SingleIncome(double a, String n, String c, Date d, double b, String pri) {
		amount = a;
		name = n;
		category = c;
		date = d;
		balanceAfter = b;
		parentIdentifier = pri;
	}
	
	/*
	 * @requires none 
	 * @throws none 
	 * @modifies none
	 * @effects none
	 * @returns the balance after a single income is added (aka current balance)
	 */
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
		IAccount oldParent = (IAccount)Parser.getResource(this.parentIdentifier);
		oldParent.updateBalance(oldParent.getBalance() - this.amount);
		IAccount newParent = (IAccount)Parser.getResource(parentRI);
		newParent.updateBalance(newParent.getBalance() + this.amount);
		this.parentIdentifier = parentRI;
		dbParser.updateTransaction(this.resourceIdentifier, "ParentIdentifier", this.parentIdentifier);
	}

}
