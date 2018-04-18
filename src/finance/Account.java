package finance;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
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
		Iterator<String> keyitr = subBalances.keySet().iterator();
		while (keyitr.hasNext()) {
			b += subBalances.get(keyitr.next()).getBalance();
		}
		return b;
	}
	
	public List<String> getSubBalanceResourceIdentifiers() {
		List<String> subBalanceRIs = new ArrayList<String>(subBalances.keySet());
		return subBalanceRIs;
	}
	
	public List<Transaction> getTransactionHistory() {
		List<Transaction> allTransactions = new ArrayList<Transaction>(transactions.values());
		Iterator<String> keyitr = subBalances.keySet().iterator();
		while (keyitr.hasNext()) {
			List<Transaction> temp = subBalances.get(keyitr.next()).getTransactionHistory();
			for (int j = 0; j < temp.size(); ++j)
				allTransactions.add(temp.get(j));
		}
		return allTransactions;
	}
	
	public String createSubBalance(String n, Double b) {
		SubBalance sb = new SubBalance(n, b, this);
		this.balance -= b;
		
		int ri = Parser.getNextSubBalanceRI();
		
		String newIdentifier = "sb" + ri;
		
		sb.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, sb);
		subBalances.put(newIdentifier, sb);
		Parser.setNextSubBalanceRI(ri+1);
		
		return newIdentifier;
	}
	
	public String addSingleIncome(double a, String n, String c, Date d) {
		SingleIncome newIncome = new SingleIncome(a, n, c, d, this.balance - a, this.resourceIdentifier);
		balance += a;
		
		int ri = Parser.getNextTransactionRI();
		String newIdentifier = "t" + ri;
		
		newIncome.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, newIncome);
		dbParser.updateBalance(this.resourceIdentifier, this.balance);
		transactions.put(newIdentifier, newIncome);
		Parser.setNextTransactionRI(ri+1);
		
		return newIdentifier;
	}
	
	public String addRecurringIncome(double a, String n, String c, Period p, Date d1, Date d2) {
		RecurringIncome newIncome = new RecurringIncome(a, n, c, p, d1, d2, this.resourceIdentifier);
		
		int ri = Parser.getNextTransactionRI();
		String newIdentifier = "t" + ri;
		
		newIncome.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, newIncome);
		transactions.put(newIdentifier, newIncome);
		Parser.setNextTransactionRI(ri+1);
		
		return newIdentifier;
	}
	
	public String addSingleExpense(double a, String n, String c, Date d) {
		SingleExpense newExpense = new SingleExpense(a, n, c, d, this.balance - a, this.resourceIdentifier);
		balance -= a;
		
		int ri = Parser.getNextTransactionRI();
		String newIdentifier = "t" + ri;
		
		newExpense.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, newExpense);
		dbParser.updateBalance(this.resourceIdentifier, this.balance);
		transactions.put(newIdentifier, newExpense);
		Parser.setNextTransactionRI(ri+1);
		
		return newIdentifier;
	}
	
	public String addRecurringExpense(double a, String n, String c, Period p, Date d1, Date d2) {
		RecurringExpense newExpense = new RecurringExpense(a, n, c, p, d1, d2, this.resourceIdentifier);
		
		int ri = Parser.getNextTransactionRI();
		String newIdentifier = "t" + ri;
		
		newExpense.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, newExpense);
		transactions.put(newIdentifier, newExpense);
		Parser.setNextTransactionRI(ri+1);
		
		return newIdentifier;
	}
	
	public void addTransfer(Transfer newTransfer) {
		if(this.resourceIdentifier.equals(newTransfer.getFromResourceIdentifier())) {
			this.balance -= newTransfer.getAmount();
		} else if(this.resourceIdentifier.equals(newTransfer.getFromResourceIdentifier())) {
			this.balance += newTransfer.getAmount();
		}
		
		dbParser.updateBalance(this.resourceIdentifier, this.balance);
		transactions.put(newTransfer.getResourceIdentifier(), newTransfer);
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
		Iterator<String> keyitr = subBalances.keySet().iterator();
		while (keyitr.hasNext()) {
			subBalances.get(keyitr.next()).checkRecurringTransactions();
		}
	}

}
