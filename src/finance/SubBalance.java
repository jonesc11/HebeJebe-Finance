package finance;

import java.util.List;
import java.util.ArrayList;

public class SubBalance {
	
	private double balance;
	private List<Transaction> transactions;
	
	public SubBalance(double initialBalance) {
		transactions = new ArrayList<Transaction>();
		balance = initialBalance;
	}
	
	/*
	 * @requires Account is != null, name.len() > 0, amount > 0
	 * @throws none 
	 * @modifies the specified Account
	 * @effects Creates an Expense on the specified account. If isRecurring is true, the expense is a RecurringExpense. 
	 * @returns none
	 * @param a - An Account a, to add the expense to. 
	 * @param name - a name used to refer to the expense
	 * @param amount - the amount of the expense
	 * @param isRecurring - T/F value to specify if the expense is recurring
	 * @param period - The period in which the expense recurs
	 */
	public double getBalance() {
		return balance;
	}
	
	public List<Transaction> getTransactionHistory() {
		return transactions;
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
