package finance;

import java.util.List;
import java.util.ArrayList;

import finance.FinanceUtilities.Period;

public class User {
	private String email;
	private List<Account> accounts;	 
	
	public User(String e) {
		accounts = new ArrayList<Account>();
		email = e;
	}
	
	/*
	 * @requires Account is != null, name.len() > 0, amount > 0
	 * @throws none 
	 * @modifies the specified Account
	 * @effects Creates an Expense on the specified account. If isRecurring is true, the expense is a RecurringExpense. 
	 * @returns none
	 * @param a - An Account a, to add the expense to. 
	 * @param name - a name used to refer to the exepnse
	 * @param amount - the amount of the expense
	 * @param isRecurring - T/F value to specify if the expense is recurring
	 * @param period - The period in which the expense recurs
	 */
	public void createExpense(Account a, String name, double amount, boolean isRecurring, Period period) {
		
	}

	/*
	 * @requires Account is != null
	 * @throws none
	 * @modifies the specified Account
	 * @effects Creates an Income on the specified account. If isRecurring is true, the expense is a RecurringExpense. 
	 * @returns none
	 * @param a - An Account a, to add the income to.
	 * @param isRecurring - T/F value to specify if the income is recurring
	 * @param period - The period in which the income recurs
	 */
	public void createIncome(Account a, boolean isRecurring, Period period) {
		
	}
	
	
	/*
	 * @requires none
	 * @throws none
	 * @modifies none 
	 * @effects Provides a list of sub-balances from all accounts
	 * @returns A list of transactions from each account
	 * @param none 
	 */
	public List<Transaction> getTransactionHistory() {
		List<Transaction> allTransactions = new ArrayList<Transaction>();
		for(int i = 0; i < accounts.size(); i++) {
			List<Transaction> temp = accounts.get(i).getTransactionHistory();
			for(int j = 0; j < temp.size(); j++) {
				allTransactions.add(temp.get(j));
			}
		}
		return allTransactions;
	}
	
	/*
	 * @requires none
	 * @throws none
	 * @modifies none 
	 * @effects Provides a list of sub-balances from the specified account a
	 * @returns A list of transaction from the specified account a
	 * @param Account a - An Account a, to display the sub-balances from
	 */
	public List<Transaction> getTransactionHistory(Account a) {
		return a.getTransactionHistory();
	}

}
