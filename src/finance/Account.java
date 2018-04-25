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
	private Map<String, Transaction> recurringTransactions;
	
	public Account(String n, String t, double b) {
		this.name = n;
		this.type = t;
		this.balance = b;
		this.transactions = new HashMap<String, Transaction>();
		this.subBalances = new HashMap<String, IAccount>();
		this.recurringTransactions = new HashMap<String, Transaction>();
	}
	
	public Account(String n, String t, double b, Map<String, Transaction> tr, Map<String, IAccount> sb) {
		this.name = n;
		this.type = t;
		this.balance = b;
		this.transactions = tr;
		this.subBalances = sb;
		this.recurringTransactions = new HashMap<String, Transaction>();
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
	
	public void updateBalance(double b) {
		this.balance = b;
		dbParser.updateBalance(this.resourceIdentifier, this.balance);
	}
	
	public void updateName(String n) {
		this.name = n;
		dbParser.updateAccount(this.resourceIdentifier, "AccountName", this.name);
	}
	
	public void updateType(String t) {
		this.type = t;
		dbParser.updateAccount(this.resourceIdentifier, "AccountType", this.type);
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
		dbParser.updateBalance(this.resourceIdentifier, this.balance);
		
		return newIdentifier;
	}
	
	public String addSingleIncome(double a, String n, String c, Date d) {
		SingleIncome newIncome = new SingleIncome(a, n, c, d, this.balance + a, this.resourceIdentifier);
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
		recurringTransactions.put(newIdentifier, newIncome);
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
		recurringTransactions.put(newIdentifier, newExpense);
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
		for(int i = 0; i < recurringTransactions.size(); i++) {
			Transaction t = recurringTransactions.get(i);
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
	
	public double getProjection(Date d) {
		double amount = 0;
		for(int i = 0; i < recurringTransactions.size(); i++) {
			Transaction t = recurringTransactions.get(i);
			if(t instanceof RecurringIncome) {
				amount += ((RecurringIncome)t).amountByDate(d);
				((RecurringIncome)t).updateLastUpdated(d);
			}
			else if(t instanceof RecurringExpense) {
				amount -= ((RecurringExpense)t).amountByDate(d);
				((RecurringExpense)t).updateLastUpdated(d);
			}
		}
		return balance + amount;
	}
	
	public double getTotalProjection(Date d) {
		double amount = 0;
		for(int i = 0; i < recurringTransactions.size(); i++) {
			Transaction t = recurringTransactions.get(i);
			if(t instanceof RecurringIncome) {
				amount += ((RecurringIncome)t).amountByDate(d);
				((RecurringIncome)t).updateLastUpdated(d);
			}
			else if(t instanceof RecurringExpense) {
				amount -= ((RecurringExpense)t).amountByDate(d);
				((RecurringExpense)t).updateLastUpdated(d);
			}
		}
		
		for(int i = 0; i < subBalances.size(); i++) {
			amount += ((SubBalance) subBalances.get(i)).getProjection(d);
		}
		return balance + amount;
	}
	
	public void delete() {
		Iterator<Transaction> tIter = transactions.values().iterator();
		while(tIter.hasNext()) {
			Transaction t = tIter.next();
			t.delete();
		}
		
		Iterator<IAccount> sbIter = subBalances.values().iterator();
		while(sbIter.hasNext()) {
			IAccount sb = sbIter.next();
			sb.delete();
		}
		
		Parser.removeResource(this.resourceIdentifier);
		dbParser.deleteAccount(this.resourceIdentifier);
	}

}
