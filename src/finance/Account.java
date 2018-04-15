package finance;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

import finance.FinanceUtilities.Period;

public class Account implements IAccount {
	
	private String name;
	private String type;
	private double balance;
	private String resourceIdentifier;
	private Map<String, IAccount> subBalances;
	private Map<String, Transaction> transactions;
	
	public Account(String n, String t, double b) {
		this.name = n;
		this.type = t;
		this.balance = b;
		this.transactions = new HashMap<String, Transaction>();
		this.subBalances = new HashMap<String, IAccount>();
	}
	
	public Account(String n, String t, double b, Map<String, Transaction> tr, Map<String, IAccount> sb) {
		this.name = n;
		this.type = t;
		this.balance = b;
		this.transactions = tr;
		this.subBalances = sb;
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
		return balance;
	}
	
	public double getTotal() {
		double b = balance;
		for (int i = 0; i < subBalances.size(); i++) {
			b += subBalances.get(i).getBalance();
		}
		return b;
	}
	
	public List<String> getSubBalanceResourceIdentifiers() {
		List<String> subBalanceRIs = new ArrayList<String>(subBalances.keySet());
		return subBalanceRIs;
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
	
	public String createSubBalance(String n, Double b) {
		SubBalance sb = new SubBalance(n, b, this);
		
		int i = 0;
		while(subBalances.get("sb" + i) != null) 
			i++;
		
		String newIdentifier = "sb" + i;
		
		sb.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, sb);
		dbParser.insertSubBalance(sb, this.resourceIdentifier);
		subBalances.put(newIdentifier, sb);
		
		return newIdentifier;
	}
	
	public String addSingleIncome(double a, String n, String c, Date d) {
		SingleIncome newIncome = new SingleIncome(a, n, c, d, this.balance);
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
		SingleExpense newExpense = new SingleExpense(a, n, c, d, this.balance);
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
				this.balance += ((RecurringIncome)t).amountByDate(d);
				((RecurringIncome)t).updateLastUpdated(d);
			}
			else if(t instanceof RecurringExpense) {
				this.balance -= ((RecurringExpense)t).amountByDate(d);
				((RecurringExpense)t).updateLastUpdated(d);
			}
			
		}
		dbParser.updateBalance(this.resourceIdentifier, this.balance);
		for(int i = 0; i < subBalances.size(); i++) {
			subBalances.get(i).checkRecurringTransactions();
		}
	}

}
