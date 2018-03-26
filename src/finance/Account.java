package finance;

import java.util.List;
import java.util.ArrayList;

public class Account {
	
	private double balance;
	private List<SubBalance> subBalances;
	private List<Transaction> transactions;
	private String type;
	
	public Account(double b, String t) {
		balance = b;
		type = t;
		transactions = new ArrayList<Transaction>();
	}
	
	public String getType() {
		return type;
	}
	
	public double getBalance() {
		double b = balance;
		for (int i = 0; i < subBalances.size(); i++) {
			b += subBalances.get(i).getBalance();
		}
		return b;
	}
	
	public List<Transaction> getTransactionHistory() {
		List<Transaction> allTransactions = transactions;
		for(int i = 0; i < subBalances.size(); i++) {
			List<Transaction> temp = subBalances.get(i).getTransactionHistory();
			for(int j = 0; j < temp.size(); j++) {
				allTransactions.add(temp.get(j));
			}
		}
		return allTransactions;
	}
	
	public void addSingleIncome(double tAmount, String tName) {
		SingleIncome newIncome = new SingleIncome(tAmount, tName);
		balance += tAmount;
		transactions.add(newIncome);
	}
	
	public void addRecurringIncome(double tAmount, String tName) {
		RecurringIncome newIncome = new RecurringIncome(tAmount, tName);
		transactions.add(newIncome);
	}
	
	public void addSingleExpense(double tAmount, String tName) {
		SingleExpense newExpense = new SingleExpense(tAmount, tName);
		balance -= tAmount;
		transactions.add(newExpense);
	}
	
	public void addRecurringExpense(double tAmount, String tName) {
		RecurringExpense newExpense = new RecurringExpense(tAmount, tName);
		transactions.add(newExpense);
	}
	
	public void addTransfer(double tAmount, String tName) {
		Transfer newTransfer = new Transfer(tAmount, tName);
		balance -= tAmount;
		transactions.add(newTransfer);
	}	

}
