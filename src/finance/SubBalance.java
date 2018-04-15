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
	
	public SubBalance(String n, double b, IAccount p) {
		name = n;
		balance = b;
		parent = p;
		transactions = new HashMap<String, Transaction>();
		recurringTransactions = new HashMap<String, Transaction>();
	}
	
	public SubBalance(String n, double b, IAccount p, Map<String, Transaction> tr) {
		name = n;
		balance = b;
		parent = p;
		transactions = tr;
		recurringTransactions = new HashMap<String, Transaction>();
	}
	
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
	
	public List<Transaction> getTransactionHistory() {
		List<Transaction> t = new ArrayList<Transaction>(transactions.values());
		return t;
	}
	
	public String addSingleIncome(double a, String n, String c, Date d) {
		SingleIncome newIncome = new SingleIncome(a, n, c, d);
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
	
	public String addRecurringIncome(double a, String n, String c, Period p, Date d1, Date d2) {
		RecurringIncome newIncome = new RecurringIncome(a, n, c, p, d1, d2);
		
		int i = 0;
		while(transactions.get("t" + i) != null)
			i++;
		
		String newIdentifier = "t" + i;
		
		newIncome.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, newIncome);
		transactions.put(newIdentifier, newIncome);
		
		return newIdentifier;
	}
	
	public String addSingleExpense(double a, String n, String c, Date d) {
		SingleExpense newExpense = new SingleExpense(a, n, c, d);
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
	
	public String addRecurringExpense(double a, String n, String c, Period p, Date d1, Date d2) {
		RecurringExpense newExpense = new RecurringExpense(a, n, c, p, d1, d2);
		
		int i = 0;
		while(transactions.get("t" + i) != null)
			i++;
		
		String newIdentifier = "t" + i;
		
		newExpense.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, newExpense);
		transactions.put(newIdentifier, newExpense);
		
		return newIdentifier;
	}
	
	public String addTransfer(double a, String n) {
		Transfer newTransfer = new Transfer(a, n);

		int i = 0;
		while(transactions.get("t" + i) != null)
			i++;
		
		String newIdentifier = "t" + i;
		
		newTransfer.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, newTransfer);
		transactions.put("t" + i, newTransfer);
		
		return newIdentifier;
	}	
	
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
