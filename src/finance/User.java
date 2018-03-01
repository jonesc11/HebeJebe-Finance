package finance;

import java.util.List;

import finance.FinanceUtilities.Period;

public class User {
	private String email;
	private List<Account> accounts;	 
	
	public User() {
		
	}
	
	/*
	 * @requires Account is != null 
	 * @throws none 
	 * @modifies the specified Account
	 * @effects Creates an Expense on the specified account. If isRecurring is true, the expense is a RecurringExpense. 
	 * @returns none
	 * @param a - An Account a, to add the expense to. 
	 * @param isRecurring - T/F value to specify if the expense is recurring
	 * @param period - The period in which the expense recurs
	 */
	public void createExpense(Account a, boolean isRecurring, Period period) {
		
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
}
