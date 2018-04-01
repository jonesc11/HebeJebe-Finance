package finance;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import finance.FinanceUtilities.Period;

import java.util.Iterator;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;

import java.util.Map;
import java.util.HashMap;

public class dbParser {
	
	private static MongoClient mongo = new MongoClient("localhost", 27017);
	private static MongoCredential credentials = MongoCredential.createCredential("", "finance", "".toCharArray());
	private static MongoDatabase db = mongo.getDatabase("finance");
	
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
			
			Parser.addUser(identifier, u);
		}
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
		MongoCollection<Document> transactions = db.getCollection("transaction");
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
					}
					else if(recurFrequency == 365) {
						period = Period.YEARLY;
					}
					else {
						period = Period.DAILY;
					}
					t = new RecurringIncome(amount, name, category, period, date, endDate);
					t.setResourceIdentifier(identifier);
				}
				else {
					t = new SingleIncome(amount, name, category, date);
					t.setResourceIdentifier(identifier);
				}
			}
			else if(type.equals("Expense")) {
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
					}
					else if(recurFrequency == 365) {
						period = Period.YEARLY;
					}
					else {
						period = Period.DAILY;
					}
					t = new RecurringExpense(amount, name, category, period, date, endDate);
					t.setResourceIdentifier(identifier);
				}
				else {
					t = new SingleExpense(amount, name, category, date);
					t.setResourceIdentifier(identifier);
				}
			}
			else if(type.equals("Transfer")) {
				//Unimplemented
			}
			
			if(t == null) {
				Parser.addResource(identifier, t);
				transactionsList.put(identifier, t);
			}
		}
		
		return transactionsList;
	}
}