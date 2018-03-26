package finance;

import java.util.List;
import java.util.ArrayList;

public class SubBalance {
	
	private double balance;
	private List<Transaction> transactions;
	private List<Transaction> recurringTransactions;
	
	public SubBalance(double initialBalance) {
		transactions = new ArrayList<Transaction>();
		balance = initialBalance;
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
		return transactions;
	}
	
	public void addSingleIncome(double a, String n) {
		SingleIncome newIncome = new SingleIncome(a, n);
		balance += a;
		transactions.add(newIncome);
	}
	
	public void addRecurringIncome(double a, String n, ) {
		RecurringIncome newIncome = new RecurringIncome(a, n);
		transactions.add(newIncome);
	}
	
	public void addSingleExpense(double a, String n) {
		SingleExpense newExpense = new SingleExpense(a, n);
		balance -= a;
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
