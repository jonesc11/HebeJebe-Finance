package finance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Budget {
	
	protected double limit;
	protected String name;
	protected Date date;
	protected Date endDate;
	protected String resourceIdentifier;
	protected double balance;
	private Map<String, Transaction> transactions;
	private Map<String, IAccount> subBalances;
	
	public Budget(String n, double l, Date d, Date endD, double b) {
		this.name = n;
		this.balance = b;
		this.limit = l;
		this.date = d;
		this.endDate = endD;
		this.transactions = new HashMap<String, Transaction>();
		this.subBalances = new HashMap<String, IAccount>();
	}
	public double getLimit() { 
		return limit; 
	}
	
	public String getName() { 
		return name; 
	}
	
	public Date getDate() {
		return date;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public String getResourceIdentifier() {
		return resourceIdentifier;
	}
	
	public void setResourceIdentifier(String identifier) {
		resourceIdentifier = identifier;
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
	
	
}