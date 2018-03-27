package finance;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.String;

import finance.FinanceUtilities.Period;

public class User {
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private Map<String, Account> accounts;	 
	
	public User(String e, String pw, String fn, String ln) {
		accounts = new HashMap<String, Account>();
		email = e;
		password = pw;
		firstName = fn;
		lastName = ln;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public double getBalance() {
		double balance = 0;
		
		List<Account> accountList = new ArrayList<Account>(accounts.values());
		
		for(int i = 0; i < accountList.size(); i++) {
			balance += accountList.get(i).getBalance();
		}
		
		return balance;
	}
	
	public List<String> getAccountResourceIdentifiers() {
		List<String> accountRIs = new ArrayList<String>(accounts.keySet());
		return accountRIs;
	}
	
	public String createAccount(String name, String type, double balance) {
		Account account = new Account(name, type, balance);
		
		//A really poor way of creating a unique ResourceIdentifier for the new Account
		int i = 1;
		while(accounts.get("a" + i) != null) {
			i++;
		}
		accounts.put("a" + i, account);
		
		return "a" + i;
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
	public String createExpense(String ri, double a, String n, String c, Date d1, boolean isRecurring, Date d2, Period p) {
		Account acc = getAccount(ri);
		String transactionRI;
		
		if(isRecurring) {
			transactionRI = acc.addRecurringExpense(a, n, c, p, d1, d2);
		}
		else {
			transactionRI = acc.addSingleExpense(a, n, c, d1);
		}
		
		return transactionRI;
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
	public String createIncome(String ri, double a, String n, String c, Date d1, boolean isRecurring, Date d2, Period p) {
		Account acc = getAccount(ri);
		String transactionRI;
		
		if(isRecurring) {
			transactionRI = acc.addRecurringIncome(a, n, c, p, d1, d2);
		}
		else {
			transactionRI = acc.addSingleIncome(a, n, c, d1);
		}
		
		return transactionRI;
	}
	
	/*
	 * @requires none
	 * @throws none
	 * @modifies none 
	 * @effects Provides a list of the current User's accounts
	 * @returns A list of Account objects
	 * @param none 
	 */
	public Account getAccount(String resourceIdentifier) {
		return accounts.get(resourceIdentifier);
	}
	
	public List<Account> getAccounts() {
		return new ArrayList<Account>(accounts.values());
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
			List<Transaction> temp = accounts.get("a" + i).getTransactionHistory();
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
