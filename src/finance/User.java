package finance;

import java.util.List;

import finance.FinanceUtilities.Period;

public class User {
	private String email;
	private List<Account> accounts;	 
	
	public User() {
		
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
	 * @requires Acocunt is != null
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
	 * @returns none
	 * @param none 
	 */
	public void displayTransactionHistory() {
	}
	
	/*
	 * @requires none
	 * @throws none
	 * @modifies none 
	 * @effects Provides a list of sub-balances from the specified account a
	 * @returns none
	 * @param Account a - An Account a, to display the sub-balances from
	 */
	public void displayTransactionHistory(Account a) {
		
	}

}
