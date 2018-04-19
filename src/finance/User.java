package finance;

import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.Iterator;
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
	private Budget budget;
	private SavingsPlan savingsPlan;
	
	public User(String e, String pw, String s, String fn, String ln) {
		this.email = e;
		this.password = pw;
		this.salt = s;
		this.firstName = fn;
		this.lastName = ln;
		this.accounts = new HashMap<String, Account>();
		this.budget = null;
		this.savingsPlan = null;
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
	
	public SavingsPlan getSavingsPlan() {
		return savingsPlan;
	}
	
	public Budget getBudget() {
		return budget;
	}
	
	public void setResourceIdentifier (String identifier) {
		this.resourceIdentifier = identifier;
	}
	
	public void updateEmail(String e) {
		this.email = e;
		dbParser.updateUser(this.resourceIdentifier, "UserIdentifier", this.email);
	}
	
	public void updateFirstName(String fn) {
		this.firstName = fn;
		dbParser.updateUser(this.resourceIdentifier, "FirstName", this.firstName);
	}
	
	public void updateLastName(String ln) {
		this.lastName = ln;
		dbParser.updateUser(this.resourceIdentifier, "LastName", this.lastName);
	}
	
	public void updatePassword(String pw, String s) {
		this.salt = s;
		dbParser.updateUser(this.resourceIdentifier, "Salt", this.salt);
		this.password = this.salt + Parser.getSHA256Hash(pw);
		dbParser.updateUser(this.resourceIdentifier, "Password", this.password);
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
		
		int ri = Parser.getNextAccountRI();
		
		String newIdentifier = "a" + ri;
		
		account.setResourceIdentifier(newIdentifier);
		Parser.addResource(newIdentifier, account);
		accounts.put(newIdentifier, account);
		Parser.setNextAccountRI(ri+1);
		
		return newIdentifier;
	}
	
	public String createBudget(String desc, double l, int dur, Date d1, Date d2) {
		budget = new Budget(desc, l, dur, this.resourceIdentifier, d1, d2);
		
		budget.setResourceIdentifier(this.resourceIdentifier.replaceAll("u", "b"));
		Parser.addResource(budget.getResourceIdentifier(), budget);
		dbParser.insertBudget(this.resourceIdentifier, budget);
		
		return budget.getResourceIdentifier();
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
			budget.updateBalance(budget.getBalance() + a);
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
		
		int ri = Parser.getNextTransactionRI();
		String newIdentifier = "t" + ri;
		newTransfer.setResourceIdentifier(newIdentifier);
		Parser.setNextTransactionRI(ri+1);
		
		from.addTransfer(newTransfer);
		to.addTransfer(newTransfer);
		
		return newIdentifier;
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
		Iterator<String> keys = accounts.keySet().iterator();
		while (keys.hasNext()) {
			accounts.get(keys.next()).checkRecurringTransactions();
		}
	}
	
	public double getProjection(Date d) {
		double amount = 0;
		Iterator<Account> iter = accounts.values().iterator();
		while(iter.hasNext()) {
			Account a = iter.next();
			amount += a.getProjection(d);
		}
		return amount;
	}
	
	public String createSavingsPlan(String n, double a, Date d) {
		savingsPlan = new SavingsPlan(n, a, d, this.resourceIdentifier);
		
		savingsPlan.setResourceIdentifier(this.resourceIdentifier.replaceAll("u", "sp"));
		Parser.addResource(savingsPlan.getResourceIdentifier(), savingsPlan);
		dbParser.insertSavingsPlan(this.resourceIdentifier, savingsPlan);
		
		return savingsPlan.getResourceIdentifier();
	}
	
	public void deleteSavingsPlan() {
		Parser.removeResource(this.savingsPlan.getResourceIdentifier());
		dbParser.deleteSavingsPlan(this.resourceIdentifier);
		this.savingsPlan = null;
	}
	
	public void deleteBudget() {
		Parser.removeResource(this.budget.getResourceIdentifier());
		dbParser.deleteBudget(this.resourceIdentifier);
		this.budget = null;
	}
	
	public void delete() {
		for(int i = 0; i < accounts.size(); i++) {
			accounts.get(i).delete();
		}
		Parser.removeResource(savingsPlan.getResourceIdentifier());
		Parser.removeResource(budget.getResourceIdentifier());
		Parser.removeUser(this.resourceIdentifier);
		dbParser.deleteUser(this.resourceIdentifier);
	}

}
