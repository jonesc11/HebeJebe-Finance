package finance;

import java.util.Map;

import finance.FinanceUtilities.Period;

import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SubBalance implements IAccount {
	
	private String resourceIdentifier;
	private String name;
	private IAccount parent;
	private double balance;
	private Map<String, Transaction> transactions;
	private Map<String, Transaction> recurringTransactions;
	
	/*
	 * @requires n.len() > 0, b > 0, p != null
	 * @throws none 
	 * @modifies subblance
	 * @effects creates a new subbalance object
	 * @returns this
	 * @param string n - name of subbalance
	 * @param double b - balance of subbalance
	 * @param IAccount p - parent account of the subbalance
	 */
	public SubBalance(String n, double b, IAccount p) {
		name = n;
		balance = b;
		parent = p;
		transactions = new HashMap<String, Transaction>();
		recurringTransactions = new HashMap<String, Transaction>();
	}
	
	/*
	 * @requires n.len() > 0, b > 0, p != null
	 * @throws none 
	 * @modifies subblance
	 * @effects creates a new subbalance object
	 * @returns this
	 * @param string n - name of subbalance
	 * @param double b - balance of subbalance
	 * @param IAccount p - parent account of the subbalance
	 * @param Map<String, Transaction> tr - a map of transaction object associated with the subbalance
	 */
	public SubBalance(String n, double b, IAccount p, Map<String, Transaction> tr) {
		name = n;
		balance = b;
		parent = p;
		transactions = tr;
		recurringTransactions = new HashMap<String, Transaction>();
	}
	
	//getters and setters
	public String getResourceIdentifier() {
		return resourceIdentifier;
	}
	
	public void setResourceIdentifier(String identifier) {
		resourceIdentifier = identifier;
	}
	
	public String getName() {
		return name;
	}
	
	public String getParentIdentifier() {
		return parent.getResourceIdentifier();
	}
	
	/*
	 * @requires Account is != null, name.len() > 0
	 * @throws none 
	 * @modifies none
	 * @effects Returns the balance of this SubBalance 
	 * @returns The value of the balance variable
	 * @param none
	 */
	public double getBalance() {
		return balance;
	}
	
	/*
	 * @requires none
	 * @throws none 
	 * @modifies none
	 * @effects Returns a list of transactions
	 * @returns list of transaction objects
	 * @param none
	 */
	public List<Transaction> getTransactionHistory() {
		List<Transaction> t = new ArrayList<Transaction>(transactions.values());
		return t;
	}
	
	/*
	 * @requires a > 0, n.len() > 0, c.len() > 0, d != null
	 * @throws none 
	 * @modifies subblance
	 * @effects creates a new single income to the subbalance
	 * @returns string of the resource ID
	 * @param Amount a - amount of income
	 * @param string n - name of income
	 * @param Date d - date of income
	 */
	public String addSingleIncome(double a, String n, String c, Date d) {
		SingleIncome newIncome = new SingleIncome(a, n, c, d, this.balance - a, this.resourceIdentifier);
		balance += a;
		
		int i = 0;
		while(transactions.get("t" + i) != null)
			i++;
		
		String newIdentifier = "t" + i;
		
		newIncome.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, newIncome);
		dbParser.updateBalance(this.resourceIdentifier, this.balance);
		transactions.put(newIdentifier, newIncome);
		
		return newIdentifier;
	}
	
	/*
	 * @requires a > 0, n.len() > 0, c.len() > 0, p != null, d1 != null, d2 != null
	 * @throws none 
	 * @modifies subblance
	 * @effects creates a new recurring income to the subbalance
	 * @returns string of the resource ID
	 * @param Amount a - amount of income
	 * @param string n - name of income
	 * @param Date d - date of income
	 * @param Period p - period of recurrence 
	 * @param Date d1 - date of start of income
	 * @param Date d2 - date of end of income
	 */
	public String addRecurringIncome(double a, String n, String c, Period p, Date d1, Date d2) {
		RecurringIncome newIncome = new RecurringIncome(a, n, c, p, d1, d2, this.resourceIdentifier);
		
		int i = 0;
		while(transactions.get("t" + i) != null)
			i++;
		
		String newIdentifier = "t" + i;
		
		newIncome.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, newIncome);
		transactions.put(newIdentifier, newIncome);
		
		return newIdentifier;
	}
	
	/*
	 * @requires a > 0, n.len() > 0, c.len() > 0, d != null
	 * @throws none 
	 * @modifies subblance
	 * @effects creates a new single exepense to the subbalance
	 * @returns string of the resource ID
	 * @param Amount a - amount of income
	 * @param string n - name of income
	 * @param Date d - date of income
	 */
	public String addSingleExpense(double a, String n, String c, Date d) {
		SingleExpense newExpense = new SingleExpense(a, n, c, d, this.balance - a, this.resourceIdentifier);
		balance -= a;
		
		int i = 0;
		while(transactions.get("t" + i) != null)
			i++;
		
		String newIdentifier = "t" + i;
		
		newExpense.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, newExpense);
		dbParser.updateBalance(this.resourceIdentifier, this.balance);
		transactions.put(newIdentifier, newExpense);
		
		return newIdentifier;
	}
	
	/*
	 * @requires a > 0, n.len() > 0, c.len() > 0, p != null, d1 != null, d2 != null
	 * @throws none 
	 * @modifies subblance
	 * @effects creates a new recurring expense to the subbalance
	 * @returns string of the resource ID
	 * @param Amount a - amount of expense
	 * @param string n - name of expense
	 * @param Date d - date of expense
	 * @param Period p - period of recurrence 
	 * @param Date d1 - date of start of expense
	 * @param Date d2 - date of end of expense
	 */
	public String addRecurringExpense(double a, String n, String c, Period p, Date d1, Date d2) {
		RecurringExpense newExpense = new RecurringExpense(a, n, c, p, d1, d2, this.resourceIdentifier);
		
		int i = 0;
		while(transactions.get("t" + i) != null)
			i++;
		
		String newIdentifier = "t" + i;
		
		newExpense.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, newExpense);
		transactions.put(newIdentifier, newExpense);
		
		return newIdentifier;
	}
	
	/*
	 * @requires newTransfer != null
	 * @throws none 
	 * @modifies subblance
	 * @effects creates a new trasnfer to the subbalance
	 * @returns none
	 * @param Transfer newTransfer - the transfer to be added
	 */
	public void addTransfer(Transfer newTransfer) {
		if(this.resourceIdentifier.equals(newTransfer.getFromResourceIdentifier())) {
			this.balance -= newTransfer.getAmount();
		} else if(this.resourceIdentifier.equals(newTransfer.getFromResourceIdentifier())) {
			this.balance += newTransfer.getAmount();
		}
		
		dbParser.updateBalance(this.resourceIdentifier, this.balance);
		transactions.put(newTransfer.getResourceIdentifier(), newTransfer);
	}	
	
	/*
	 * @requires none
	 * @throws none 
	 * @modifies none
	 * @effects recurring transaction and updates amounts wheere needed
	 * @returns none
	 */
	public void checkRecurringTransactions() {
		LocalDateTime now = LocalDateTime.now();
		Date d = DateFactory.getDate(now.getDayOfMonth(), now.getMonthValue(), now.getYear());
		for(int i = 0; i < transactions.size(); i++) {
			Transaction t = transactions.get(i);
			if(t instanceof RecurringIncome) {
				balance += ((RecurringIncome)t).amountByDate(d);
				((RecurringIncome)t).updateLastUpdated(d);
			}
			else if(t instanceof RecurringExpense) {
				balance -= ((RecurringExpense)t).amountByDate(d);
				((RecurringExpense)t).updateLastUpdated(d);
			}
		}
		dbParser.updateBalance(this.resourceIdentifier, this.balance);
	}

}
