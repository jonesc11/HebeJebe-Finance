package finance;

import java.util.Map;
import java.util.Random;
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
	private String salt;
	private Map<String, Account> accounts;
	private String resourceIdentifier;
	
	public User(String e, String pw, String s, String fn, String ln) {
		this.email = e;
		this.password = pw;
		this.salt = s;
		this.firstName = fn;
		this.lastName = ln;
		this.accounts = new HashMap<String, Account>();
	}
	
	public User(String e, String pw, String fn, String ln, Map<String, Account> a) {
		email = e;
		password = pw;
		firstName = fn;
		lastName = ln;
		accounts = a;
	}
	
	public String getResourceIdentifier () {
		return this.resourceIdentifier;
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
	
	public void setResourceIdentifier (String identifier) {
		this.resourceIdentifier = identifier;
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
	
	public List<String> getSubBalanceResourceIdentifiers() {
		List<String> subBalanceRIs = new ArrayList<String>();
		for(int i = 0; i < this.accounts.size(); i++) {
			subBalanceRIs.addAll(this.accounts.get(i).getSubBalanceResourceIdentifiers());
		}
		return subBalanceRIs;
	}
	
	public String createAccount(String name, String type, double balance) {
		Account account = new Account(name, type, balance);
		
		//A really poor way of creating a unique ResourceIdentifier for the new Account
		int i = 0;
		while(accounts.get("a" + i) != null)
			i++;
		
		String newIdentifier = "a" + i;
		
		account.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, account);
		accounts.put(newIdentifier, account);
		
		return newIdentifier;
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
		
		if(isRecurring)
			transactionRI = acc.addRecurringIncome(a, n, c, p, d1, d2);
		else
			transactionRI = acc.addSingleIncome(a, n, c, d1);
		
		return transactionRI;
	}
	
	public String createTransfer(double a, String n, String c, Date d, String fromRI, String toRI) {
		Account from = getAccount(fromRI);
		Account to = getAccount(toRI);
		
		Transfer newTransfer = new Transfer(a, n, c, d, fromRI, toRI, from.getBalance(), to.getBalance());
		
		String identifier = from.addTransfer(newTransfer);
		to.addTransfer(newTransfer);
		newTransfer.setResourceIdentifier(identifier);
		
		return identifier;
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
		for (String id : accounts.keySet())
			allTransactions.addAll(accounts.get(id).getTransactionHistory());
		
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
	
	public void checkRecurringTransactions() {
		for(int i = 0; i < accounts.size(); i++) {
			accounts.get(i).checkRecurringTransactions();
		}
	}

}
