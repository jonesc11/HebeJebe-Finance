package finance;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import finance.FinanceUtilities.Period;

public class Account {
	
	private String name;
	private String type;
	private double balance;
	private String resourceIdentifier;
	private List<SubBalance> subBalances;
	private Map<String, Transaction> transactions;
	
	public Account(String n, String t, double b) {
		name = n;
		type = t;
		balance = b;
		transactions = new HashMap<String, Transaction>();
		this.subBalances = new ArrayList<SubBalance>();
	}
	
	public String getResourceIdentifier () {
		return this.resourceIdentifier;
	}
	
	public void setResourceIdentifier (String identifier) {
		this.resourceIdentifier = identifier;
	}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public double getBalance() {
		double b = balance;
		for (int i = 0; i < subBalances.size(); i++) {
			b += subBalances.get(i).getBalance();
		}
		return b;
	}
	
	public List<Transaction> getTransactionHistory() {
		List<Transaction> allTransactions = new ArrayList<Transaction>(transactions.values());
		for(int i = 0; i < subBalances.size(); i++) {
			List<Transaction> temp = subBalances.get(i).getTransactionHistory();
			for(int j = 0; j < temp.size(); j++) {
				allTransactions.add(temp.get(j));
			}
		}
		return allTransactions;
	}
	
	public String addSingleIncome(double a, String n, String c, Date d) {
		SingleIncome newIncome = new SingleIncome(a, n, c, d);
		balance += a;
		
		int i = 0;
		while(transactions.get("t" + i) != null)
			i++;
		
		String newIdentifier = "t" + i;
		
		newIncome.setResourceIdentifier(newIdentifier);
		Runner.addResource(newIdentifier, newIncome);
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
		Runner.addResource(newIdentifier, newIncome);
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
		Runner.addResource(newIdentifier, newExpense);
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
		Runner.addResource(newIdentifier, newExpense);
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
		Runner.addResource(newIdentifier, newTransfer);
		transactions.put("t" + i, newTransfer);
		
		return newIdentifier;
	}	

}
