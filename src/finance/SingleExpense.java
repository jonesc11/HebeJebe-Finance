package finance;

public class SingleExpense extends Expense {
	
	private double balanceAfter;
	
	/*
	 * @requires a > 0, n.len() > 0, c.len() > 0, d != null, b > 0, pri.len() > 0 
	 * @throws none 
	 * @modifies SingleExpense
	 * @effects creates a new singleexpense object
	 * @returns this
	 * @param double a - amount of expense
	 * @param string n - name of expense
	 * @param string c - catergory of expense
	 * @param DAte d - date of expense
	 * @param double b - balance of expense
	 * @param string pri - parent account of the expense
	 */
	public SingleExpense(double a, String n, String c, Date d, double b, String pri) {
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
		parent.updateBalance(parent.getBalance() + (this.amount - a));
		this.amount = a;
		dbParser.updateTransaction(this.resourceIdentifier, "Amount", this.amount);
	}
	
	public void updateParent(String parentRI) {
		IAccount oldParent = (IAccount)Parser.getResource(this.parentIdentifier);
		oldParent.updateBalance(oldParent.getBalance() + this.amount);
		IAccount newParent = (IAccount)Parser.getResource(parentRI);
		newParent.updateBalance(newParent.getBalance() - this.amount);
		this.parentIdentifier = parentRI;
		dbParser.updateTransaction(this.resourceIdentifier, "ParentIdentifier", this.parentIdentifier);
	}
	
}
