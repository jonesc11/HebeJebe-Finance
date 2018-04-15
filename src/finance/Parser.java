package finance;

import java.util.*;

import javax.xml.bind.DatatypeConverter;

import java.lang.String;
import java.security.MessageDigest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import finance.Date;
import finance.FinanceUtilities.Period;

public class Parser {
	private static Map<String, User> users = new HashMap<String, User>();
	private static Map<String, Object> resources = new HashMap<String, Object>();
	
	//Helper function to make a JSONArray into a Java List object.
	public static List<String> JSONArrayToList(JSONArray a) throws JSONException {
		List<String> l = new ArrayList<String>();
		
		for (int i = 0; i < a.length(); i++) {
			l.add(a.getString(i));
		}
		return l;
	}
	
	public static User getUser (String identifier) {
		return users.get(identifier);
	}
	
	public static Object getResource (String identifier) {
		return resources.get(identifier);
	}
	
	public static Transaction getTransaction (String identifier) {
		if (!identifier.substring(0, 1).equals("t"))
			return null;
		return (Transaction) resources.get(identifier);
	}
	
	public static Account getAccount (String identifier) {
		if (!identifier.substring(0, 1).equals("a"))
			return null;
		return (Account) resources.get(identifier);
	}
	
	public static void addResource (String identifier, Object resource) {
		resources.put(identifier, resource);
	}
	
	/**
	 * @param identifier 
	 * @param user
	 */
	public static void addUser (String identifier, User user) {
		users.put(identifier, user);
	}
	
	//JSON Parser for Requests. Returns a String that is an appropriately formatted JSON string.
	public static String processRequest(String r) throws JSONException {
		//Take the input string and turn it into a JSONObject. Create an empty JSONObject that will be
		//returned by the function
		JSONObject request = new JSONObject(r);
		JSONObject response = new JSONObject();
		
		//Gets the User object from users based on the specified resource identifier, the actionType, 
		//which will specify what the response JSONObject will look like, and the action 
		//JSONObject that will specify important information regarding the action to be performed.
		String actionType = request.getString("ActionType");
		JSONObject action = request.getJSONObject("Action");
		
		// If else cases for the different action types.
		
		// First case: GetTransactions
		// Returns a JSONObject containing a list of Transaction JSONObjects, and a next token based on
		// the provided limit.
		// Issues: Currently ignores NextTokens and ResourceIdentifier
		if(actionType.equals("GetTransactions")) {
			response = parseGetTransactions(action);
		}
		// Second case: GetAccount
		// Returns a JSONObject containing a JSONObject that includes information about the specified Account.
		// Issues: Currently ignores NextTokens.
		else if(actionType.equals("GetAccounts")) {
			response = parseGetAccounts(action);
		}
		else if(actionType.equals("GetSubBalance")) {
			response = parseGetSubBalance(action);
		}
		else if(actionType.equals("GetUser")) {
			response = parseGetUsers(action);
		}
		else if(actionType.equals("CreateUser")) {
			response = parseCreateUser (action);
		}
		else if(actionType.equals("CreateAccount")) {
			response = parseCreateAccount (action);
		}
		else if(actionType.equals("CreateSubBalance")) {
			response = parseCreateSubBalance (action);
		}
		//Creates a new transaction, associates it with the appropriate user and account/sub-balance
		//Issues: Currently doesn't work with Transfers.
		else if(actionType.equals("CreateTransaction")) {
			User user = users.get(request.getString("AccountID"));
			response = parseCreateTransaction (action, user);
		}
		else if(actionType.equals("Login")) {
			response = parseLogin (action);
		}
					 
		return response.toString();
	}
	
	public static JSONObject parseCreateTransaction (JSONObject action, User user) throws JSONException {
		JSONObject response = new JSONObject();
		
		//Grabs information about the new Transaction from the Action Object. This includes:
		//The transaction type, transaction amount, transaction name,
		//transaction category, resourceIdentifier of the account/sub-balance, date of the
		//transaction, and whether the transaction is recurring or not.
		String type = action.getString("TransactionType");
		double amount = action.getDouble("Amount");
		String name = action.getString("Description");
		String category = action.getString("Category");
		String parentRI = action.getString("AssociatedWith");
		//Takes the input string of the transaction's date, and formats it into a Date object
		//using an abstract factory and the Flyweight design pattern.
		String dateString = action.getString("DateTime");
		int year = Integer.parseInt(dateString.substring(0,4));
		int month = Integer.parseInt(dateString.substring(5,7));
		int day = Integer.parseInt(dateString.substring(8, 10));
		Date date = DateFactory.getDate(day, month, year);
		Date endDate = null;
		boolean recurring = action.getBoolean("Recurring");
		String transactionRI = "";
		
		//If the first character is a, then the transaction is associated with an Account.
		//Uses the transaction type and whether it is recurring to create the appropriate
		//kind of transaction. 
		if(parentRI.charAt(0) == 'a') {
			if(type.equals("Income")) {
				if(recurring) {
					String endDateString = action.getString("RecurringUntil");
					int endYear = Integer.parseInt(endDateString.substring(0,4));
					int endMonth = Integer.parseInt(endDateString.substring(5,7));
					int endDay = Integer.parseInt(endDateString.substring(8, 10));
					endDate = DateFactory.getDate(endDay, endMonth, endYear);
					Period period;
					if(action.getInt("RecurringFrequency") == 30) {
						period = Period.MONTHLY;
					}
					else if(action.getInt("RecurringFrequency") == 365) {
						period = Period.YEARLY;
					}
					else {
						period = Period.DAILY;
					}
					transactionRI = user.createIncome(parentRI, amount, name, category, date, recurring, endDate, period);
				}
				else {
					transactionRI = user.createIncome(parentRI, amount, name, category, date, recurring, null, null);
				}
			}
			else if(type.equals("Expense")) {
				if(recurring) {
					String endDateString = action.getString("RecurringUntil");
					int endYear = Integer.parseInt(endDateString.substring(0,4));
					int endMonth = Integer.parseInt(endDateString.substring(5,7));
					int endDay = Integer.parseInt(endDateString.substring(8, 10));
					endDate = DateFactory.getDate(endDay, endMonth, endYear);
					Period period;
					if(action.getInt("RecurringFrequency") == 30) {
						period = Period.MONTHLY;
					}
					else if(action.getInt("RecurringFrequency") == 365) {
						period = Period.YEARLY;
					}
					else {
						period = Period.DAILY;
					}
					transactionRI = user.createExpense(parentRI, amount, name, category, date, recurring, endDate, period);
				}
				else {
					transactionRI = user.createExpense(parentRI, amount, name, category, date, recurring, null, null);
				}
			}
			else if(type.equals("Transfer")) {
				
			}
		}
		Transaction t = (Transaction)resources.get(transactionRI);
		dbParser.insertTransaction(t, parentRI);
		
		//Creates the appropriate response JSONObject for the CreateTransaction Request
		response.put("ResourceIdentifier", transactionRI);
		response.put("TransactionType", type);
		response.put("Amount", amount);
		response.put("Description", name);
		response.put("DateTime", date.format());
		response.put("Category", category);
		response.put("AssociatedWith", parentRI);
		response.put("Recurring", recurring);
		if(recurring) {
			response.put("RecurringUntil", endDate.format());
			response.put("RecurringInterval", action.getString("RecurringInterval"));
		}
		
		return response;
	}
	
	public static JSONObject parseCreateAccount (JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		
		User user = Parser.getUser(action.getString("UserResourceIdentifier"));
		String name = action.getString("AccountName");
		String type = action.getString("AccountType");
		double balance = action.getDouble("AccountBalance");
		
		String resourceIdentifier = user.createAccount(name, type, balance);
		Account a = (Account)resources.get(resourceIdentifier);
		dbParser.insertAccount(a, user.getResourceIdentifier());
		
		response.put("ResourceIdentifier", resourceIdentifier);
		response.put("UserResourceIdentifier", action.getString("UserResourceIdentifier"));
		response.put("AccountName", name);
		response.put("AccountType", type);
		response.put("AccountBalance", balance);
		
		return response;
	}
	
	public static JSONObject parseCreateSubBalance(JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		
		Account account = Parser.getAccount(action.getString("AccountResourceIdentifier"));
		String name = action.getString("SubBalanceName");
		Double balance = action.getDouble("SubBalanceBalance");
		
		String resourceIdentifier = account.createSubBalance(name, balance);
		SubBalance sb = (SubBalance)resources.get(resourceIdentifier);
		dbParser.insertSubBalance(sb, sb.getParentIdentifier());
		
		response.put("ResourceIdentifier", resourceIdentifier);
		response.put("AccountResourceIdentifier", account.getResourceIdentifier());
		response.put("SubBalanceName", name);
		response.put("SubBalanceBalance", balance);
		
		return response;
	}
	
	public static JSONObject parseCreateUser (JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		
		String email = action.getString("UserIdentifier");
		String salt = getSHA256Hash(getSaltString());
		String password = salt + getSHA256Hash(action.getString("Password"));
		String firstName = action.getString("FirstName");
		String lastName = action.getString("LastName");
		
		User newUser = new User(email, password, salt, firstName, lastName);
		
		//A very poor way of creating a new Resource Identifier for the new User
		int i = 1;
		while(users.get("u" + i) != null) {
			i++;
		}
		newUser.setResourceIdentifier("u" + i);
		users.put("u" + i, newUser);
		dbParser.insertUser(newUser, salt, password);
		
		response.put("ResourceIdentifier", "u" + i);
		response.put("UserIdentifier", email);
		response.put("Password", password);
		response.put("FirstName", firstName);
		response.put("LastName", lastName);
		
		return response;
	}
	
	public static JSONObject parseGetUsers (JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		
		User u = users.get(action.get("ResourceIdentifier"));
		JSONObject o = new JSONObject();
		JSONArray transactions = new JSONArray();
		List<Transaction> allTransactions = u.getTransactionHistory();
		
		//Creates a JSONArray of Account Resource Identifiers from the list of User accounts.
		List<String> accountRIsList = u.getAccountResourceIdentifiers();
		JSONArray accountRIs = new JSONArray();
		for (int i = 0; i < accountRIsList.size(); i++) {
			accountRIs.put(accountRIsList.get(i));
		}
		
		//Puts the User's 25 most recent Transactions into the JSONArray transactions.
		for(int i = 0; i < allTransactions.size(); i++) {
			if(transactions.length() >= 25) {
				break;
			}
			JSONObject t = new JSONObject();
			
			t.put("ResourceIdentifier", allTransactions.get(i).getResourceIdentifier());
			if(allTransactions.get(i) instanceof Income) {
				t.put("TransactionType", "Income");
			}
			else if(allTransactions.get(i) instanceof Expense) {
				t.put("TransactionType", "Expense");
			}
			else if(allTransactions.get(i) instanceof Transfer) {
				t.put("TransactionType", "Transfer");
			}
			t.put("Amount", allTransactions.get(i).getAmount());
			t.put("Description", allTransactions.get(i).getName());
			t.put("DateTime", allTransactions.get(i).getDate().format());
			t.put("Category", allTransactions.get(i).getCategory());
			if (allTransactions.get(i) instanceof RecurringIncome) {
				o.put("Recurring", true);
				o.put("RecurringUntil", ((RecurringIncome)allTransactions.get(i)).getEndDate().format());
				if(((RecurringIncome)allTransactions.get(i)).getPeriod() == Period.DAILY) {
					o.put("RecurringInterval", 1);
				}
				else if(((RecurringIncome)allTransactions.get(i)).getPeriod() == Period.MONTHLY) {
					o.put("RecurringInterval", 30);
				}
				else if(((RecurringIncome)allTransactions.get(i)).getPeriod() == Period.YEARLY) {
					o.put("RecurringInterval", 365);
				}
			}
			else if (allTransactions.get(i) instanceof RecurringExpense) {
				o.put("Recurring", true);
				o.put("RecurringUntil", ((RecurringExpense)allTransactions.get(i)).getEndDate().format());
				if(((RecurringExpense)allTransactions.get(i)).getPeriod() == Period.DAILY) {
					o.put("RecurringInterval", 1);
				}
				else if(((RecurringExpense)allTransactions.get(i)).getPeriod() == Period.MONTHLY) {
					o.put("RecurringInterval", 30);
				}
				else if(((RecurringExpense)allTransactions.get(i)).getPeriod() == Period.YEARLY) {
					o.put("RecurringInterval", 365);
				}
			}
			else {
				t.put("Recurring", false);
			}
			
			transactions.put(t);
		}
		
		o.put("ResourceIdentifier", action.get("ResourceIdentifier"));
		o.put("UserIdentifier", u.getEmail());
		o.put("FirstName", u.getFirstName());
		o.put("LastName", u.getLastName());
		o.put("AccountResourceIdentifiers", accountRIs);
		o.put("Balance", u.getBalance());
		o.put("LatestTransactions", transactions);
		
		return response;
	}
	
	public static JSONObject parseGetAccounts (JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		
		User user = Parser.getUser(action.getString("GetFrom"));
		//If a Resource Identifier is specified, returns a single Account Object. Otherwise, returns
		//a list of all account objects.
		if(action.getString("ResourceIdentifier") != null) {
			List<Account> accountsList = user.getAccounts();
			JSONArray accounts = new JSONArray();
			
			for(int i = 0; i < accountsList.size(); i++) {
				Account a = accountsList.get(i);
				JSONObject o = new JSONObject();
				
				//Creates the appropriate Account JSONObject based on information about the specified Account.
				o.put("ResourceIdentifier", a.getResourceIdentifier());
				o.put("AccountName", a.getName());
				o.put("AccountType", a.getType());
				o.put("LatestBalance", a.getTotal());
				o.put("LatestTransactions", Parser.getTransactionsJSONArray(a.getResourceIdentifier(), 25));
				
				accounts.put(o);
			}
			
			response.put("Account", accounts);
		}
		else {
			// Gets the Account object specified by the ResourceIdentifier, and creates a new JSONObject
			// which will contain information about the Account. Creates a JSONArray transactions to contain
			// the most recent 25 transactions associated with the Account.
			Account a = user.getAccount(action.getString("ResourceIdentifier"));
			JSONObject o = new JSONObject();
			
			//Creates the appropriate Account JSONObject based on information about the specified Account.
			o.put("ResourceIdentifier", a.getResourceIdentifier());
			o.put("AccountName", a.getName());
			o.put("AccountType", a.getType());
			o.put("LatestBalance", a.getTotal());
			o.put("LatestTransactions", Parser.getTransactionsJSONArray(a.getResourceIdentifier(), 25));
			
			response.put("Account", o);
		}
		
		return response;
	}
	
	public static JSONObject parseGetSubBalance (JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		JSONArray subBalances = new JSONArray();
		
		if(action.isNull("ResourceIdentifier")) {
			int limit;
			if(action.get("Limit").equals(null)) {
				limit = 25;
			}
			else {
				limit = action.getInt("Limit");
			}
			User user = Parser.getUser(action.getString("GetFrom"));
			List<String> subBalanceList = user.getSubBalanceResourceIdentifiers();
			
			for(int i = 0; i < limit; i++) {
				SubBalance sb = (SubBalance)Parser.getResource(subBalanceList.get(i));
				JSONObject sbObject = new JSONObject();
				
				sbObject.put("ResourceIdentifier", sb.getResourceIdentifier());
				sbObject.put("AccountResourceIdentifier", sb.getParentIdentifier());
				sbObject.put("SubBalanceName", sb.getName());
				sbObject.put("Balance", sb.getBalance());
				sbObject.put("LatestTransactions", Parser.getTransactionsJSONArray(sb.getResourceIdentifier(), 25));
				
				subBalances.put(sbObject);
			}
		}
		else {
			SubBalance sb = (SubBalance)Parser.getResource(action.getString("ResourceIdentifier"));
			JSONObject sbObject = new JSONObject();
			
			sbObject.put("ResourceIdentifier", sb.getResourceIdentifier());
			sbObject.put("AccountResourceIdentifier", sb.getParentIdentifier());
			sbObject.put("SubBalanceName", sb.getName());
			sbObject.put("Balance", sb.getBalance());
			sbObject.put("LatestTransactions", Parser.getTransactionsJSONArray(sb.getResourceIdentifier(), 25));
			
			subBalances.put(sbObject);
		}
		response.put("SubBalances", subBalances);
		
		return response;
	}
	
	public static JSONObject parseGetTransactions (JSONObject action) throws JSONException {
		JSONArray transactions = null;
		List<Transaction> allTransactions = null;
		List<String> types = null;
		String category = null;
		String getFrom = null;
		
		// Define the JSONArray of transactions, which will be the first item in the response JSONObject.
		// Also defines list of all Transactions for the specified User, and gets the types of transactions
		// that will be selected (Incomes, Expenses, Transfers).
		transactions = new JSONArray();
		getFrom = action.getString("GetFrom");
		if (!action.isNull("TransactionType"))
			types = JSONArrayToList(action.getJSONArray("TransactionType"));
		
		if (getFrom.substring(0, 1).equals("u"))
			allTransactions = Parser.getUser(getFrom).getTransactionHistory();
		else if (getFrom.substring(0, 1).equals("a"))
			allTransactions = Parser.getAccount(getFrom).getTransactionHistory();
		
		if (!action.isNull("Category"))
			category = action.getString("Category");
		
		// If no limit is specified, default to 25.
		int limit;
		if(action.get("Limit") != null)
			limit = action.getInt("Limit");
		else
			limit = 25;
		
		// Loops through all transactions, and adds a Transaction JSONObject to the JSONArray of 
		// transactions if the current transaction meets the requirements
		// type == null means no transaction types have been specified.
		for(int i = 0; i < allTransactions.size() && i < limit; i++) {
			JSONObject o = new JSONObject();
			if(allTransactions.get(i) instanceof Income && (category == null || allTransactions.get(i).getCategory().equals(category)) && (types == null || types.contains("Income"))) {
				o.put("ResourceIdentifier", allTransactions.get(i).getResourceIdentifier());
				o.put("TransactionType", "Income");
				o.put("Amount", allTransactions.get(i).getAmount());
				o.put("Description", allTransactions.get(i).getName());
				o.put("DateTime", allTransactions.get(i).getDate().format());
				o.put("Category", allTransactions.get(i).getCategory());
				if (allTransactions.get(i) instanceof RecurringIncome) {
					o.put("Recurring", true);
					o.put("RecurringUntil", ((RecurringIncome)allTransactions.get(i)).getEndDate().format());
					if(((RecurringIncome)allTransactions.get(i)).getPeriod() == Period.DAILY)
						o.put("RecurringInterval", 1);
					else if(((RecurringIncome)allTransactions.get(i)).getPeriod() == Period.MONTHLY)
						o.put("RecurringInterval", 30);
					else if(((RecurringIncome)allTransactions.get(i)).getPeriod() == Period.YEARLY)
						o.put("RecurringInterval", 365);
				} else {
					o.put("Recurring", false);
				}
				transactions.put(o);
			} else if(allTransactions.get(i) instanceof Expense && (category == null || allTransactions.get(i).getCategory().equals(category)) && (types == null || types.contains("Expense"))) {
				o.put("ResourceIdentifier", allTransactions.get(i).getResourceIdentifier());
				o.put("TransactionType", "Expense");
				o.put("Amount", allTransactions.get(i).getAmount());
				o.put("Description", allTransactions.get(i).getName());
				o.put("DateTime", allTransactions.get(i).getDate().format());
				o.put("Category", allTransactions.get(i).getCategory());
				if (allTransactions.get(i) instanceof RecurringExpense) {
					o.put("Recurring", true);
					o.put("RecurringUntil", ((RecurringExpense)allTransactions.get(i)).getEndDate().format());
					if(((RecurringExpense)allTransactions.get(i)).getPeriod() == Period.DAILY)
						o.put("RecurringInterval", 1);
					else if(((RecurringExpense)allTransactions.get(i)).getPeriod() == Period.MONTHLY)
						o.put("RecurringInterval", 30);
					else if(((RecurringExpense)allTransactions.get(i)).getPeriod() == Period.YEARLY)
						o.put("RecurringInterval", 365);
				} else {
					o.put("Recurring", false);
				}
				transactions.put(o);
			} else if(allTransactions.get(i) instanceof Transfer && (category == null || allTransactions.get(i).getCategory().equals(category)) && (types == null || types.contains("Transfer"))) {
				o.put("ResourceIdentifier", allTransactions.get(i).getResourceIdentifier());
				o.put("TransactionType", "Transfer");
				o.put("Amount", allTransactions.get(i).getAmount());
				o.put("Description", allTransactions.get(i).getName());
				o.put("DateTime", allTransactions.get(i).getDate().format());
				o.put("Category", allTransactions.get(i).getCategory());
				o.put("Recurring",  false);
				transactions.put(o);
			}
		}
		
		JSONObject response = new JSONObject();
		response.put("Transactions", transactions);
		return response;
	}
	
	public static  JSONObject parseLogin(JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		String resourceIdentifier = dbParser.verifyLogin(action.getString("UserIdentifier"), action.getString("Password"));
		
		if(resourceIdentifier.equals("")) {
			response.put("Verified", false);
		}
		else {
			User u = users.get(resourceIdentifier);
			response.put("Verified", true);
			response.put("ResourceIdentifier", resourceIdentifier);
			response.put("UserIdentifier", u.getEmail());
			response.put("FirstName", u.getFirstName());
			response.put("LastName", u.getLastName());
			
			u.checkRecurringTransactions();
		}
		
		return response;
	}
	
	public static JSONArray getTransactionsJSONArray(String identifier, int limit) throws JSONException {
		JSONArray transactions = new JSONArray();
		IAccount parent = (IAccount)Parser.getResource(identifier);
		List<Transaction> transactionList = parent.getTransactionHistory();
		
		for(int i = 0; i < limit; i++) {
			if(i >= transactionList.size()) {
				break;
			}
			Transaction t = transactionList.get(i);
			JSONObject transaction = new JSONObject();
			
			transaction.put("ResourceIdentifier", t.getResourceIdentifier());
			if(t instanceof Income) {
				transaction.put("TransactionType", "Income");
			}
			else if(t instanceof Expense) {
				transaction.put("TransactionType", "Expense");
			}
			else if(t instanceof Transfer) {
				transaction.put("TransactionType", "Transfer");
			}
			transaction.put("Amount", t.getAmount());
			transaction.put("Description", t.getName());
			transaction.put("DateTime", t.getDate().format());
			transaction.put("Category", t.getCategory());
			transaction.put("AssociatedWith", identifier);
			if(t instanceof SingleIncome || t instanceof SingleExpense || t instanceof Transfer) {
				transaction.put("Recurring", false);
			}
			else if(t instanceof RecurringIncome) {
				transaction.put("Recurring", true);
				transaction.put("RecurringUntil", ((RecurringIncome)t).getEndDate().format());
				if(((RecurringIncome)t).getPeriod() == Period.DAILY) {
					transaction.put("RecurringInterval", 1);
				}
				else if(((RecurringIncome)t).getPeriod() == Period.WEEKLY) {
					transaction.put("RecurringInterval", 7);
				}
				else if(((RecurringIncome)t).getPeriod() == Period.MONTHLY) {
					transaction.put("RecurringInterval", 30);
				}
				else if(((RecurringIncome)t).getPeriod() == Period.YEARLY) {
					transaction.put("RecurringInterval", 365);
				}
			}
			else if(t instanceof RecurringExpense) {
				transaction.put("Recurring", true);
				transaction.put("RecurringUntil", ((RecurringExpense)t).getEndDate().format());
				if(((RecurringExpense)t).getPeriod() == Period.DAILY) {
					transaction.put("RecurringInterval", 1);
				}
				else if(((RecurringExpense)t).getPeriod() == Period.WEEKLY) {
					transaction.put("RecurringInterval", 7);
				}
				else if(((RecurringExpense)t).getPeriod() == Period.MONTHLY) {
					transaction.put("RecurringInterval", 30);
				}
				else if(((RecurringExpense)t).getPeriod() == Period.YEARLY) {
					transaction.put("RecurringInterval", 365);
				}
			}
			
			transactions.put(transaction);
		}
		
		return transactions;
		
	}
	
	private static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
	
	public static String getSHA256Hash(String s) {
		String result = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(s.getBytes("UTF-8"));
			return bytesToHex(hash);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static String bytesToHex(byte[] hash) {
		return DatatypeConverter.printHexBinary(hash);
	}
}