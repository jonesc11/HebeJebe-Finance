package finance;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import finance.FinanceUtilities.Period;

import java.util.Iterator;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.Map;
import java.util.HashMap;

public class dbParser {
	
	private static MongoClient mongo = null;
	private static MongoDatabase db = null;
	
	public static void initDB() {
		ServerAddress serverAddress = new ServerAddress ("ec2-18-217-228-55.us-east-2.compute.amazonaws.com", 27017) ;
		MongoCredential credentials = MongoCredential.createCredential("finance", "finance", "Finance".toCharArray()) ;	
		
		mongo = new MongoClient(serverAddress, credentials, MongoClientOptions.builder().build());
		db = mongo.getDatabase("finance");
	}
	
	public static void readFromDB() {
		MongoCollection<Document> users = db.getCollection("users");
		FindIterable<Document> iterDoc = users.find();
		Iterator<Document> it = iterDoc.iterator();
		
		//Iterate through all User Documents, create User objects based on these Documents
		while(it.hasNext()) {
			Document d = it.next();
			
			String identifier = d.getString("ResourceIdentifier");
			String email = d.getString("UserIdentifier");
			String password = d.getString("Password");
			String firstName = d.getString("FirstName");
			String lastName = d.getString("LastName");
			
			Map<String, Account> accounts = getAccountsFromDB(identifier);
			
			User u = new User(email, password, firstName, lastName, accounts);
			u.setResourceIdentifier(identifier);
			
			if(d.getBoolean("SavingsPlan", false)) {
				String spName = d.getString("SavingsPlanName");
				double spAmount = d.getDouble("SavingsPlanAmount");
				double spBalance = d.getDouble("SavingsPlanBalance");
				String dateString = d.getString("SavingsPlanDate");
				int year = Integer.parseInt(dateString.substring(0,4));
				int month = Integer.parseInt(dateString.substring(5,7));
				int day = Integer.parseInt(dateString.substring(8, 10));
				Date spDate = DateFactory.getDate(day, month, year);
				
				String spIdentifier = u.createSavingsPlan(spName, spAmount, spDate);
				SavingsPlan savingsPlan = Parser.getSavingsPlan(spIdentifier);
				savingsPlan.updateBalance(spBalance);
			}
			
			if(d.getBoolean("Budget", false)) {
				double bLimit = d.getDouble("BudgetLimit");
				String bDescription = d.getString("BudgetDescription");
				double bBalance = d.getDouble("BudgetBalance");
				int bDuration = d.getInteger("BudgetDuration");
				String startDateString = d.getString("BudgetStartDate");
				int year = Integer.parseInt(startDateString.substring(0,4));
				int month = Integer.parseInt(startDateString.substring(5,7));
				int day = Integer.parseInt(startDateString.substring(8, 10));
				Date bStartDate = DateFactory.getDate(day, month, year);
				String endDateString = d.getString("BudgetEndDate");
				year = Integer.parseInt(endDateString.substring(0,4));
				month = Integer.parseInt(endDateString.substring(5,7));
				day = Integer.parseInt(endDateString.substring(8, 10));
				Date bEndDate = DateFactory.getDate(day, month, year);
				
				String bIdentifier = u.createBudget(bDescription, bLimit, bDuration, bStartDate, bEndDate);
				Budget budget = Parser.getBudget(bIdentifier);
				budget.updateBalance(bBalance);
			}
			
			Parser.addUser(identifier, u);
		}
		
		Parser.updateNextRIs();
	}
	
	public static Map<String, Account> getAccountsFromDB(String pIdentifier) {
		MongoCollection<Document> accounts = db.getCollection("accounts");
		FindIterable<Document> iterDoc = accounts.find(Filters.eq("ParentIdentifier", pIdentifier));
		Iterator<Document> it = iterDoc.iterator();
		Map<String, Account> accountsList = new HashMap<String, Account>();
		
		while(it.hasNext()) {
			Document d = it.next();
			
			String identifier = d.getString("ResourceIdentifier");
			String name = d.getString("AccountName");
			String type = d.getString("AccountType");
			Double balance = d.getDouble("LatestBalance");
			
			Map<String, IAccount> subBalances = getSubBalancesFromDB(identifier);
			Map<String, Transaction> transactions = getTransactionsFromDB(identifier);
			
			Account a = new Account(name, type, balance, transactions, subBalances);
			a.setResourceIdentifier(identifier);
			
			Parser.addResource(identifier, a);
			accountsList.put(identifier, a);
		}
		
		return accountsList;
	}
	
	public static Map<String, IAccount> getSubBalancesFromDB(String pIdentifier) {
		MongoCollection<Document> accounts = db.getCollection("accounts");
		FindIterable<Document> iterDoc = accounts.find(Filters.eq("ParentIdentifier", pIdentifier));
		Iterator<Document> it = iterDoc.iterator();
		Map<String, IAccount> subBalancesList = new HashMap<String, IAccount>();
		
		while(it.hasNext()) {
			Document d = it.next();
			
			String identifier = d.getString("ResourceIdentifier");
			String name = d.getString("AccountName");
			Double balance = d.getDouble("LatestBalance");
			
			Map<String, Transaction> transactions = getTransactionsFromDB(identifier);
			
			SubBalance a = new SubBalance(name, balance, (IAccount)Parser.getResource(pIdentifier), transactions);
			a.setResourceIdentifier(identifier);
			
			Parser.addResource(identifier, a);
			subBalancesList.put(identifier, a);
		}
		
		return subBalancesList;
	}
	
	public static Map<String, Transaction> getTransactionsFromDB(String pIdentifier) {
		MongoCollection<Document> transactions = db.getCollection("transactions");
		FindIterable<Document> iterDoc = transactions.find(Filters.eq("ParentIdentifier", pIdentifier));
		Iterator<Document> it = iterDoc.iterator();
		Map<String, Transaction> transactionsList = new HashMap<String, Transaction>();
		
		while(it.hasNext()) {
			Document d = it.next();
			
			String identifier = d.getString("ResourceIdentifier");
			String name = d.getString("Description");
			String type = d.getString("TransactionType");
			Double amount = d.getDouble("Amount");
			String dateString = d.getString("DateTime");
			int year = Integer.parseInt(dateString.substring(0,4));
			int month = Integer.parseInt(dateString.substring(5,7));
			int day = Integer.parseInt(dateString.substring(8, 10));
			Date date = DateFactory.getDate(day, month, year);
			String category = d.getString("Category");
			Boolean recurring = d.getBoolean("Recurring");
			Transaction t = null;
			
			if(type.equals("Income")) {
				if(recurring) {
					String endDateString = d.getString("RecurringUntil");
					int endYear = Integer.parseInt(endDateString.substring(0,4));
					int endMonth = Integer.parseInt(endDateString.substring(5,7));
					int endDay = Integer.parseInt(endDateString.substring(8, 10));
					Date endDate = DateFactory.getDate(endDay, endMonth, endYear);
					int recurFrequency = d.getInteger("RecurringFrequency");
					Period period;
					if(recurFrequency == 30) {
						period = Period.MONTHLY;
					} else if(recurFrequency == 365) {
						period = Period.YEARLY;
					} else {
						period = Period.DAILY;
					}
					t = new RecurringIncome(amount, name, category, period, date, endDate, pIdentifier);
					t.setResourceIdentifier(identifier);
				} else {
					double balanceAfter = d.getDouble("AccountBalanceAfter");
					t = new SingleIncome(amount, name, category, date, balanceAfter, pIdentifier);
					t.setResourceIdentifier(identifier);
				}
			} else if(type.equals("Expense")) {
				if(recurring) {
					String endDateString = d.getString("RecurringUntil");
					int endYear = Integer.parseInt(endDateString.substring(0,4));
					int endMonth = Integer.parseInt(endDateString.substring(5,7));
					int endDay = Integer.parseInt(endDateString.substring(8, 10));
					Date endDate = DateFactory.getDate(endDay, endMonth, endYear);
					int recurFrequency = d.getInteger("RecurringFrequency");
					Period period;
					if(recurFrequency == 30) {
						period = Period.MONTHLY;
					} else if(recurFrequency == 365) {
						period = Period.YEARLY;
					} else {
						period = Period.DAILY;
					}
					t = new RecurringExpense(amount, name, category, period, date, endDate, pIdentifier);
					t.setResourceIdentifier(identifier);
				} else {
					double balanceAfter = d.getDouble("AccountBalanceAfter");
					t = new SingleExpense(amount, name, category, date, balanceAfter, pIdentifier);
					t.setResourceIdentifier(identifier);
				}
			} else if(type.equals("Transfer")) {
				String fromRI = d.getString("From");
				String toRI = d.getString("To");
				double fromBalanceAfter = d.getDouble("FromAccountBalanceAfter");
				double toBalanceAfter = d.getDouble("ToAccountBalanceAfter");
				t = new Transfer(amount, name, category, date, fromRI, toRI, fromBalanceAfter, toBalanceAfter);
				t.setResourceIdentifier(identifier);
			}
			
			if(t != null) {
				Parser.addResource(identifier, t);
				transactionsList.put(identifier, t);
			}
		}
		
		return transactionsList;
	}
	
	public static void insertTransaction(Transaction t, String pIdentifier) {
		//Connect to the transactions collection, where the new Document will be stored
		MongoCollection<Document> transactions = db.getCollection("transactions");
		
		//Create the new Document and fill in the appropriate values for each key based on the
		//given Transaction object.
		Document newTransaction = new Document("ResourceIdentifier", t.getResourceIdentifier());
		newTransaction.append("ParentIdentifier", pIdentifier);
		if(t instanceof Income) {
			newTransaction.append("TransactionType", "Income");
		}
		else if(t instanceof Expense) {
			newTransaction.append("TransactionType", "Expense");
		}
		else if (t instanceof Transfer) {
			newTransaction.append("TransactionType", "Transfer");
			newTransaction.append("From", ((Transfer) t).getFromResourceIdentifier());
			newTransaction.append("To", ((Transfer) t).getToResourceIdentifier());
			newTransaction.append("FromAccountBalanceAfter", ((Transfer) t).getFromBalanceAfter());
			newTransaction.append("ToAccountBalanceAfter", ((Transfer) t).getToBalanceAfter());
		}
		newTransaction.append("Amount", t.getAmount());
		newTransaction.append("Description", t.getName());
		newTransaction.append("DateTime", t.getDate().format());
		newTransaction.append("Category", t.getCategory());
		if(t instanceof RecurringIncome) {
			newTransaction.append("Recurring", true);
			newTransaction.append("RecurringUntil", ((RecurringIncome) t).getEndDate().format());
			if(((RecurringIncome) t).getPeriod().equals(Period.DAILY)) {
				newTransaction.append("RecurringFrequency", 1);
			}
			else if(((RecurringIncome) t).getPeriod().equals(Period.MONTHLY)) {
				newTransaction.append("RecurringFrequency", 30);
			}
			else if(((RecurringIncome) t).getPeriod().equals(Period.YEARLY)) {
				newTransaction.append("RecurringFrequency", 365);
			}
		}
		else if(t instanceof RecurringExpense) {
			newTransaction.append("Recurring", true);
			newTransaction.append("RecurringUntil", ((RecurringExpense) t).getEndDate().format());
			if(((RecurringExpense) t).getPeriod().equals(Period.DAILY)) {
				newTransaction.append("RecurringFrequency", 1);
			}
			else if(((RecurringExpense) t).getPeriod().equals(Period.MONTHLY)) {
				newTransaction.append("RecurringFrequency", 30);
			}
			else if(((RecurringExpense) t).getPeriod().equals(Period.YEARLY)) {
				newTransaction.append("RecurringFrequency", 365);
			}
		}
		else if(t instanceof SingleIncome) {
			newTransaction.append("Recurring", false);
			newTransaction.append("AccountBalanceAfter", ((SingleIncome)t).getBalanceAfter());
		}
		else if(t instanceof SingleExpense) {
			newTransaction.append("Recurring", false);
			newTransaction.append("AccountBalanceAfter", ((SingleExpense)t).getBalanceAfter());
		}
		else if(t instanceof Transfer) {
			newTransaction.append("Recurring", false);
		}
		
		try {
			transactions.insertOne(newTransaction);
		} catch(Exception e) {
		}
	}
	
	public static void insertAccount(Account a, String pIdentifier) {
		//Connect to the accounts collection, where the new Document will be stored
		MongoCollection<Document> accounts = db.getCollection("accounts");
		
		//Create the new Document and fill in the appropriate values for each key based on the
		//given Account object.
		Document newAccount = new Document("ResourceIdentifier", a.getResourceIdentifier());
		newAccount.append("ParentIdentifier", pIdentifier);
		newAccount.append("AccountName", a.getName());
		newAccount.append("AccountType", a.getType());
		newAccount.append("LatestBalance", a.getBalance());
		
		accounts.insertOne(newAccount);
	}
	
	public static void insertSubBalance(SubBalance sb, String pIdentifier) {
		//Connect to the accounts collection, where the new Document will be stored
		MongoCollection<Document> accounts = db.getCollection("accounts");
		
		//Create the new Document and fill in the appropriate values for each key based on the
		//given SubBalance object.
		Document newSubBalance = new Document("ResourceIdentifier", sb.getResourceIdentifier());
		newSubBalance.append("ParentIdentifier", pIdentifier);
		newSubBalance.append("AccountName", sb.getName());
		newSubBalance.append("LatestBalance", sb.getBalance());
		
		accounts.insertOne(newSubBalance);
	}
	
	public static void insertUser(User u, String salt, String pw) {
		//Connect to the users collection, where the new Document will be stored
		MongoCollection<Document> users = db.getCollection("users");
		
		//Create the new Document and fill in the appropriate values for each key based on the
		//given User object.
		Document newUser = new Document("ResourceIdentifier", u.getResourceIdentifier());
		newUser.append("UserIdentifier", u.getEmail());
		newUser.append("FirstName", u.getFirstName());
		newUser.append("LastName", u.getLastName());
		newUser.append("Password", pw);
		newUser.append("Salt", salt);
		
		users.insertOne(newUser);
	}
	
	public static String verifyLogin(String email, String pw) {
		MongoCollection<Document> users = db.getCollection("users");
		FindIterable<Document> iterDoc = users.find(Filters.eq("UserIdentifier", email));
		Iterator<Document> iter = iterDoc.iterator();
		
		if(iter.hasNext()) {
			Document u = iter.next();
			
			String salt = u.getString("Salt");
			String password = u.getString("Password");
			if(password.equals(salt + Parser.getSHA256Hash(pw))) {
				return u.getString("ResourceIdentifier");
			}
			else {
				return "";
			}
		}
		else {
			return "";
		}
	}
	
	public static void insertSavingsPlan(String identifier, SavingsPlan savingsPlan) {
		MongoCollection<Document> users = db.getCollection("users");
		users.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("SavingsPlan", true));
		users.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("SavingsPlanName", savingsPlan.getName()));
		users.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("SavingsPlanAmount", savingsPlan.getAmount()));
		users.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("SavingsPlanBalance", savingsPlan.getBalance()));
		users.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("SavingsPlan", savingsPlan.getDate().format()));
	}
	
	public static void insertBudget(String identifier, Budget budget) {
		MongoCollection<Document> users = db.getCollection("users");
		users.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("Budget", true));
		users.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("BudgetLimit", budget.getLimit()));
		users.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("BudgetDescription", budget.getDescription()));
		users.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("BudgetBalance", budget.getBalance()));
		users.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("BudgetDuration", budget.getDuration()));
		users.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("BudgetStartDate", budget.getStartDate().format()));
		users.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("BudgetEndDate", budget.getEndDate().format()));
	}
	
	public static void updateUser(String identifier, String key, String value) {
		MongoCollection<Document> accounts = db.getCollection("users");
		accounts.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set(key, value));
	}
	
	public static void updateAccount(String identifier, String key, String value) {
		MongoCollection<Document> accounts = db.getCollection("accounts");
		accounts.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set(key, value));
	}
	
	public static void updateBalance(String identifier, double balance) {
		MongoCollection<Document> accounts = db.getCollection("accounts");
		accounts.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set("LatestBalance", balance));
	}
	
	public static void updateTransaction(String identifier, String key, String value) {
		MongoCollection<Document> accounts = db.getCollection("transactions");
		accounts.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set(key, value));
	}
	
	public static void updateTransaction(String identifier, String key, double value) {
		MongoCollection<Document> accounts = db.getCollection("transactions");
		accounts.updateOne(Filters.eq("ResourceIdentifier", identifier), Updates.set(key, value));
	}
}