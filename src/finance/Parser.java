package finance;

import java.util.*;

import javax.xml.bind.DatatypeConverter;

import java.lang.String;
import java.security.MessageDigest;
import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import finance.Date;
import finance.FinanceUtilities.Period;

public class Parser {
	private static Map<String, User> users = new HashMap<String, User>();
	private static Map<String, Object> resources = new HashMap<String, Object>();
	private static int nextUserRI;
	private static int nextAccountRI;
	private static int nextSubBalanceRI;
	private static int nextTransactionRI;
	private static int nextSavingsPlanRI;
	
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
	
	public static SubBalance getSubBalance (String identifier) {
		if (!identifier.substring(0, 1).equals("sb"))
			return null;
		return (SubBalance) resources.get(identifier);
	}
	
	public static SavingsPlan getSavingsPlan (String identifier) {
		if (!identifier.substring(0, 2).equals("sp"))
			return null;
		return (SavingsPlan) resources.get(identifier);
	}
	
	public static Budget getBudget (String identifier) {
		if (!identifier.substring(0, 1).equals("b"))
			return null;
		return (Budget) resources.get(identifier);
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
		} else if(actionType.equals("GetAccounts")) {
			response = parseGetAccounts(action);
		} else if(actionType.equals("GetSubBalance")) {
			response = parseGetSubBalance(action);
		} else if(actionType.equals("GetUser")) {
			response = parseGetUsers(action);
		} else if(actionType.equals("GetSavingsPlan")) {
			response = parseGetSavingsPlan(action);
		} else if(actionType.equals("GetProjection")) {
			response = parseGetProjection(action);
		} else if(actionType.equals("CreateUser")) {
			response = parseCreateUser (action);
		} else if(actionType.equals("CreateAccount")) {
			response = parseCreateAccount (action);
		} else if(actionType.equals("CreateSubBalance")) {
			response = parseCreateSubBalance (action);
		} else if(actionType.equals("CreateTransaction")) {
			User user = users.get(request.getString("AccountId"));
			response = parseCreateTransaction (action, user);
		} else if(actionType.equals("CreateBudget")) {
			response = parseCreateBudget(action);
		} else if(actionType.equals("CreateSavingsPlan")) {
			response = parseCreateSavingsPlan(action);
		} else if(actionType.equals("Login")) {
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
					if(action.isNull("RecurringUntil")) {
						String endDateString = action.getString("RecurringUntil");
						int endYear = Integer.parseInt(endDateString.substring(0,4));
						int endMonth = Integer.parseInt(endDateString.substring(5,7));
						int endDay = Integer.parseInt(endDateString.substring(8, 10));
						endDate = DateFactory.getDate(endDay, endMonth, endYear);
					}
					Period period;
					if(action.getInt("RecurringFrequency") == 30) {
						period = Period.MONTHLY;
					} else if(action.getInt("RecurringFrequency") == 365) {
						period = Period.YEARLY;
					} else {
						period = Period.DAILY;
					}
					transactionRI = user.createIncome(parentRI, amount, name, category, date, recurring, endDate, period);
				} else {
					transactionRI = user.createIncome(parentRI, amount, name, category, date, recurring, null, null);
				}
			} else if(type.equals("Expense")) {
				if(recurring) {
					String endDateString = action.getString("RecurringUntil");
					int endYear = Integer.parseInt(endDateString.substring(0,4));
					int endMonth = Integer.parseInt(endDateString.substring(5,7));
					int endDay = Integer.parseInt(endDateString.substring(8, 10));
					endDate = DateFactory.getDate(endDay, endMonth, endYear);
					Period period;
					if(action.getInt("RecurringFrequency") == 30) {
						period = Period.MONTHLY;
					} else if(action.getInt("RecurringFrequency") == 365) {
						period = Period.YEARLY;
					} else {
						period = Period.DAILY;
					}
					transactionRI = user.createExpense(parentRI, amount, name, category, date, recurring, endDate, period);
				} else {
					transactionRI = user.createExpense(parentRI, amount, name, category, date, recurring, null, null);
				}
			} else if(type.equals("Transfer")) {
				String fromRI = action.getString("From");
				String toRI = action.getString("To");
				transactionRI = user.createTransfer(amount, name, category, date, fromRI, toRI);
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
		response.put("AccountName", a.getName());
		response.put("AccountType", a.getType());
		response.put("AccountBalance", a.getBalance());
		
		return response;
	}
	
	public static JSONObject parseCreateSubBalance(JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		
		Account account = Parser.getAccount(action.getString("AccountResourceIdentifier"));
		String name = action.getString("SubBalanceName");
		Double balance = action.getDouble("SubBalanceBalance");
		
		String resourceIdentifier = account.createSubBalance(name, balance);
		SubBalance sb = (SubBalance)resources.get(resourceIdentifier);
		dbParser.insertSubBalance(sb, account.getResourceIdentifier());
		
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
		
		int ri = Parser.getNextUserRI();
		
		newUser.setResourceIdentifier("u" + ri);
		users.put("u" + ri, newUser);
		dbParser.insertUser(newUser, salt, password);
		Parser.setNextUserRI(ri+1);
		
		response.put("ResourceIdentifier", "u" + ri);
		response.put("UserIdentifier", email);
		response.put("FirstName", firstName);
		response.put("LastName", lastName);
		
		return response;
	}
	
	public static JSONObject parseCreateBudget(JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		
		User user = getUser(action.getString("UserResourceIdentifier"));
		double limit = action.getDouble("Limit");
		String description = action.getString("Description");
		int duration = action.getInt("Duration");
		LocalDateTime now = LocalDateTime.now();
		Date d1 = DateFactory.getDate(now.getDayOfMonth(), now.getMonthValue(), now.getYear());
		Date d2 = DateFactory.getDate(now.getDayOfMonth() + duration, now.getMonthValue(), now.getYear());
		
		String identifier = user.createBudget(description, limit, duration, d1, d2);
		Budget budget = getBudget(identifier);
		
		if(!budget.equals(null)) {
			response.put("ResourceIdentifier", budget.getResourceIdentifier());
			response.put("UserResourceIdentifier", user.getResourceIdentifier());
			response.put("Limit", budget.getLimit());
			response.put("Description", budget.getDescription());
			response.put("Duration", budget.getDuration());
		}
				
		return response;
	}
	
	public static JSONObject parseCreateSavingsPlan(JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		
		User user = getUser(action.getString("UserResourceIdentifier"));
		String name = action.getString("SavingsPlanName");
		double amount = action.getDouble("SavingsPlanAmount");
		String dateString = action.getString("SavingsPlanDate");
		int year = Integer.parseInt(dateString.substring(0,4));
		int month = Integer.parseInt(dateString.substring(5,7));
		int day = Integer.parseInt(dateString.substring(8, 10));
		Date date = DateFactory.getDate(day, month, year);
		
		String identifier = user.createSavingsPlan(name, amount, date);
		SavingsPlan savingsPlan = getSavingsPlan(identifier);
		
		if(!savingsPlan.equals(null)) {
			response.put("ResourceIdentifier", savingsPlan.getResourceIdentifier());
			response.put("UserResourceIdentifier", user.getResourceIdentifier());
			response.put("SavingsPlanName", savingsPlan.getName());
			response.put("SavingsPlanAmount", savingsPlan.getAmount());
			response.put("SavingsPlanDate", savingsPlan.getDate().format());
		}
				
		return response;
	}
	
	public static JSONObject parseGetUsers (JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		
		User u = users.get(action.get("ResourceIdentifier"));
		JSONObject user = new JSONObject();
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
			JSONObject transaction = new JSONObject();
			Transaction t = allTransactions.get(i);
			IAccount tParent = (IAccount)Parser.getResource(t.getParentIdentifier());
			
			transaction.put("ResourceIdentifier", t.getResourceIdentifier());
			if(t instanceof Income) {
				transaction.put("TransactionType", "Income");
			} else if(t instanceof Expense) {
				transaction.put("TransactionType", "Expense");
			} else if(t instanceof Transfer) {
				transaction.put("TransactionType", "Transfer");
			}
			transaction.put("Amount", t.getAmount());
			transaction.put("Description", t.getName());
			transaction.put("DateTime", t.getDate().format());
			transaction.put("Category", t.getCategory());
			if (allTransactions.get(i) instanceof RecurringIncome) {
				transaction.put("Recurring", true);
				transaction.put("RecurringUntil", ((RecurringIncome)t).getEndDate().format());
				if(((RecurringIncome)t).getPeriod() == Period.DAILY) {
					transaction.put("RecurringInterval", 1);
				} else if(((RecurringIncome)t).getPeriod() == Period.WEEKLY) {
					transaction.put("RecurringInterval", 7);
				} else if(((RecurringIncome)t).getPeriod() == Period.MONTHLY) {
					transaction.put("RecurringInterval", 30);
				} else if(((RecurringIncome)t).getPeriod() == Period.YEARLY) {
					transaction.put("RecurringInterval", 365);
				}
			} else if (t instanceof RecurringExpense) {
				transaction.put("Recurring", true);
				transaction.put("RecurringUntil", ((RecurringExpense)t).getEndDate().format());
				if(((RecurringExpense)t).getPeriod() == Period.DAILY) {
					transaction.put("RecurringInterval", 1);
				} else if(((RecurringExpense)t).getPeriod() == Period.WEEKLY) {
					transaction.put("RecurringInterval", 7);
				} else if(((RecurringExpense)t).getPeriod() == Period.MONTHLY) {
					transaction.put("RecurringInterval", 30);
				} else if(((RecurringExpense)t).getPeriod() == Period.YEARLY) {
					transaction.put("RecurringInterval", 365);
				}
			} else {
				transaction.put("Recurring", false);
			}
			
			if(tParent instanceof Account) {
				JSONObject a = new JSONObject();
				a.put("AccountResourceIdentifier", tParent.getResourceIdentifier());
				a.put("AccountName", tParent.getName());
				a.put("AccountBalance", ((Account) tParent).getTotal());
				
				transaction.put("Account", a);
			} else if(tParent instanceof SubBalance) {
				JSONObject sb = new JSONObject();
				sb.put("SubBalanceResourceIdentifier", tParent.getResourceIdentifier());
				sb.put("SubBalanceName", tParent.getName());
				sb.put("SubBalanceBalance", tParent.getBalance());
				
				Account sbParent = Parser.getAccount(((SubBalance) tParent).getParentIdentifier());
				JSONObject a = new JSONObject();
				a.put("AccountResourceIdentifier", sbParent.getResourceIdentifier());
				a.put("AccountName", sbParent.getName());
				a.put("AccountBalance", sbParent.getBalance());
				a.put("AccountTotal", sbParent.getTotal());
				
				transaction.put("Account", a);
				transaction.put("SubBalance", sb);
			}
			
			transactions.put(transaction);
		}
		
		user.put("ResourceIdentifier", action.get("ResourceIdentifier"));
		user.put("UserIdentifier", u.getEmail());
		user.put("FirstName", u.getFirstName());
		user.put("LastName", u.getLastName());
		user.put("AccountResourceIdentifiers", accountRIs);
		user.put("Balance", u.getBalance());
		user.put("LatestTransactions", transactions);
		
		response.put("User", user);
		
		return response;
	}
	
	public static JSONObject parseGetAccounts (JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		JSONArray accounts = new JSONArray();
		
		User user = Parser.getUser(action.getString("GetFrom"));
		//If a Resource Identifier is specified, returns a single Account Object. Otherwise, returns
		//a list of all account objects.
		if(action.getString("ResourceIdentifier") != null) {
			List<Account> accountsList = user.getAccounts();
			
			for(int i = 0; i < accountsList.size(); i++) {
				Account a = accountsList.get(i);
				JSONObject o = new JSONObject();
				
				//Creates the appropriate Account JSONObject based on information about the specified Account.
				o.put("ResourceIdentifier", a.getResourceIdentifier());
				o.put("AccountName", a.getName());
				o.put("AccountType", a.getType());
				o.put("LatestBalance", a.getBalance());
				o.put("LatestTotal", a.getTotal());
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
			o.put("LatestBalance", a.getBalance());
			o.put("LatestTotal", a.getTotal());
			o.put("LatestTransactions", Parser.getTransactionsJSONArray(a.getResourceIdentifier(), 25));
			
			accounts.put(o);
		}
		response.put("Accounts", accounts);
		
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
			Account account = Parser.getAccount(action.getString("GetFrom"));
			List<String> subBalanceList = account.getSubBalanceResourceIdentifiers();
			
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
			JSONObject transaction = new JSONObject();
			Transaction t = allTransactions.get(i);
			IAccount tParent = (IAccount)Parser.getResource(t.getParentIdentifier());
			
			if(t instanceof Income && (category == null || t.getCategory().equals(category)) && (types == null || types.contains("Income"))) {
				transaction.put("ResourceIdentifier", t.getResourceIdentifier());
				transaction.put("TransactionType", "Income");
				transaction.put("Amount", t.getAmount());
				transaction.put("Description", t.getName());
				transaction.put("DateTime", t.getDate().format());
				transaction.put("Category", t.getCategory());
				if (t instanceof RecurringIncome) {
					transaction.put("Recurring", true);
					transaction.put("RecurringUntil", ((RecurringIncome)t).getEndDate().format());
					if(((RecurringIncome)t).getPeriod() == Period.DAILY)
						transaction.put("RecurringInterval", 1);
					else if(((RecurringIncome)t).getPeriod() == Period.MONTHLY)
						transaction.put("RecurringInterval", 30);
					else if(((RecurringIncome)t).getPeriod() == Period.YEARLY)
						transaction.put("RecurringInterval", 365);
				} else {
					transaction.put("Recurring", false);
					transaction.put("AccountBalanceAfter", ((SingleIncome)t).getBalanceAfter());
				}
			} else if(t instanceof Expense && (category == null || t.getCategory().equals(category)) && (types == null || types.contains("Expense"))) {
				transaction.put("ResourceIdentifier", t.getResourceIdentifier());
				transaction.put("TransactionType", "Expense");
				transaction.put("Amount", t.getAmount());
				transaction.put("Description", t.getName());
				transaction.put("DateTime", t.getDate().format());
				transaction.put("Category", t.getCategory());
				if (t instanceof RecurringExpense) {
					transaction.put("Recurring", true);
					transaction.put("RecurringUntil", ((RecurringExpense)t).getEndDate().format());
					if(((RecurringExpense)t).getPeriod() == Period.DAILY)
						transaction.put("RecurringInterval", 1);
					else if(((RecurringExpense)t).getPeriod() == Period.MONTHLY)
						transaction.put("RecurringInterval", 30);
					else if(((RecurringExpense)t).getPeriod() == Period.YEARLY)
						transaction.put("RecurringInterval", 365);
				} else {
					transaction.put("Recurring", false);
					transaction.put("AccountBalanceAfter", ((SingleExpense)t).getBalanceAfter());
				}
			} else if(t instanceof Transfer && (category == null || t.getCategory().equals(category)) && (types == null || types.contains("Transfer"))) {
				transaction.put("ResourceIdentifier", t.getResourceIdentifier());
				transaction.put("TransactionType", "Transfer");
				transaction.put("Amount", t.getAmount());
				transaction.put("Description", t.getName());
				transaction.put("DateTime", t.getDate().format());
				transaction.put("Category", t.getCategory());
				transaction.put("Recurring",  false);
			}
			
			if(tParent instanceof Account) {
				JSONObject a = new JSONObject();
				a.put("AccountResourceIdentifier", tParent.getResourceIdentifier());
				a.put("AccountName", tParent.getName());
				a.put("AccountBalance", ((Account) tParent).getTotal());
				
				transaction.put("Account", a);
			} else if(tParent instanceof SubBalance) {
				JSONObject sb = new JSONObject();
				sb.put("SubBalanceResourceIdentifier", tParent.getResourceIdentifier());
				sb.put("SubBalanceName", tParent.getName());
				sb.put("SubBalanceBalance", tParent.getBalance());
				
				Account sbParent = Parser.getAccount(((SubBalance) tParent).getParentIdentifier());
				JSONObject a = new JSONObject();
				a.put("AccountResourceIdentifier", sbParent.getResourceIdentifier());
				a.put("AccountName", sbParent.getName());
				a.put("AccountBalance", sbParent.getBalance());
				a.put("AccountTotal", sbParent.getTotal());
				
				transaction.put("Account", a);
				transaction.put("SubBalance", sb);
			}
			
			transactions.put(transaction);
		}
		
		JSONObject response = new JSONObject();
		response.put("Transactions", transactions);
		return response;
	}
	
	public static JSONObject parseGetBudget(JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		JSONObject budgetObject = new JSONObject();
		
		User user = getUser(action.getString("UserResourceIdentifier"));
		Budget budget = user.getBudget();
		
		if(!budget.equals(null)) {
			budgetObject.put("ResourceIdentifier", budget.getResourceIdentifier());
			budgetObject.put("UserResourceIdentifier", user.getResourceIdentifier());
			budgetObject.put("Limit", budget.getLimit());
			budgetObject.put("Description", budget.getDescription());
			budgetObject.put("Balance", budget.getBalance());
			budgetObject.put("Duration", budget.getDuration());
			budgetObject.put("StartDate", budget.getStartDate().format());
			budgetObject.put("EndDate", budget.getEndDate().format());
		}
		
		response.put("Budget", budgetObject);
		
		return response;
	}
	
	public static JSONObject parseGetSavingsPlan(JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		JSONObject savingsPlanObject = new JSONObject();
		
		User user = getUser(action.getString("GetFrom"));
		SavingsPlan savingsPlan = user.getSavingsPlan();
		
		if(!savingsPlan.equals(null)) {
			savingsPlanObject.put("ResourceIdentifier", savingsPlan.getResourceIdentifier());
			savingsPlanObject.put("UserResourceIdentifier", user.getResourceIdentifier());
			savingsPlanObject.put("SavingsPlanName", savingsPlan.getName());
			savingsPlanObject.put("Amount", savingsPlan.getAmount());
			savingsPlanObject.put("Balance", savingsPlan.getBalance());
			savingsPlanObject.put("Date", savingsPlan.getDate().format());
		}
		
		response.put("SavingsPlan", savingsPlanObject);
		
		return response;
	}
	
	public static JSONObject parseGetProjection(JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		String resourceIdentifier = action.getString("GetFrom");
		double projection = 0;
		String dateString = action.getString("ProjectionDate");
		int year = Integer.parseInt(dateString.substring(0,4));
		int month = Integer.parseInt(dateString.substring(5,7));
		int day = Integer.parseInt(dateString.substring(8, 10));
		Date d = DateFactory.getDate(day, month, year);
		response.put("ResourceIdentifier", resourceIdentifier);
		response.put("ProjectionDate", d.format());
		
		if(resourceIdentifier.substring(0, 1).equals("u")) {
			projection = Parser.getUser(resourceIdentifier).getProjection(d);
		} else if(resourceIdentifier.substring(0, 1).equals("a")) {
			projection = Parser.getAccount(resourceIdentifier).getProjection(d);
			double projectedTotal = Parser.getAccount(resourceIdentifier).getTotalProjection(d);
			response.put("ProjectedTotal", projectedTotal);
		} else if(resourceIdentifier.substring(0, 2).equals("sb")) {
			projection = Parser.getSubBalance(resourceIdentifier).getProjection(d);
		}
		
		response.put("ProjectedBalance", projection);
		
		return response;
	}
	
	public static JSONObject parseLogin(JSONObject action) throws JSONException {
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
	
	public static JSONObject parseAddToSavingsPlan(JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		JSONObject savingsPlanObject = new JSONObject();
		User user = getUser(action.getString("UserResourceIdentifier"));
		SavingsPlan savingsPlan = user.getSavingsPlan();
		Account account = getAccount(action.getString("AccountResourceIdentifier"));
		double amount = action.getDouble("Amount");
		LocalDateTime now = LocalDateTime.now();
		Date d = DateFactory.getDate(now.getDayOfMonth(), now.getMonthValue(), now.getYear());
		
		savingsPlan.updateBalance(savingsPlan.getBalance() + amount);
		account.addSingleExpense(amount, "Savings", "Savings", d);
		
		savingsPlanObject.put("SavingsPlanResourceIdentifier", savingsPlan.getResourceIdentifier());
		savingsPlanObject.put("SavingsPlanName", savingsPlan.getName());
		savingsPlanObject.put("SavingsPlanAmount", savingsPlan.getAmount());
		savingsPlanObject.put("SavingsPlanBalance", savingsPlan.getBalance());
		savingsPlanObject.put("SavingsPlanDate", savingsPlan.getDate().format());
		
		response.put("UserResourceIdentifier", user.getResourceIdentifier());
		response.put("AccountResourceIdentifier", account.getResourceIdentifier());
		response.put("AmountAdded", amount);
		response.put("SavingsPlan", savingsPlanObject);
		
		return response;
	}
	
	public static JSONObject parseModify(JSONObject action) throws JSONException {
		JSONObject response = new JSONObject();
		String resourceIdentifier = action.getString("ResourceIdentifier");
		JSONArray changes = action.getJSONArray("Changes");
		
		if(resourceIdentifier.substring(0, 1).equals("u")) {
			modifyUser(resourceIdentifier, changes);
		} else if(resourceIdentifier.substring(0, 1).equals("a")) {
			modifyAccount(resourceIdentifier, changes);
		} else if(resourceIdentifier.substring(0, 1).equals("t")) {
			modifySubBalance(resourceIdentifier, changes);
		} else if(resourceIdentifier.substring(0, 2).equals("sb")) {
			modifyTransactions(resourceIdentifier, changes);
		}
		
		return response;
	}
	
	public static void modifyUser(String identifier, JSONArray changes) throws JSONException {
		User user = getUser(identifier);
		
		for(int i = 0; i < changes.length(); i++) {
			String key = changes.getJSONObject(i).getString("Key");
			String value = changes.getJSONObject(i).getString("Value");
			
			if(key.equals("UserIdentifier")) {
				user.updateEmail(value);
			} else if(key.equals("FirstName")) {
				user.updateFirstName(value);
			} else if(key.equals("LastName")) {
				user.updateLastName(value);
			} else if(key.equals("Password")) {
				String salt = getSaltString();
				user.updatePassword(value, salt);
			}
		}
	}
	
	public static void modifyAccount(String identifier, JSONArray changes) throws JSONException {
		Account account = getAccount(identifier);
		
		for(int i = 0; i < changes.length(); i++) {
			String key = changes.getJSONObject(i).getString("Key");
			
			if(key.equals("AccountName")) {
				String value = changes.getJSONObject(i).getString("Value");
				account.updateName(value);
			} else if(key.equals("AccountType")) {
				String value = changes.getJSONObject(i).getString("Value");
				account.updateType(value);
			} 
			else if(key.equals("AccountBalance")) {
				Double value = changes.getJSONObject(i).getDouble("Value");
				account.updateBalance(value);
			} 
		}
	}
	
	public static void modifySubBalance(String identifier, JSONArray changes) throws JSONException {
		SubBalance subbalance = getSubBalance(identifier);
		
		for(int i = 0; i < changes.length(); i++) {
			String key = changes.getJSONObject(i).getString("Key");
			
			if(key.equals("SubBalanceName")) {
				String value = changes.getJSONObject(i).getString("Value");
				subbalance.updateName(value);
			} else if(key.equals("SubBalanceBalance")) {
				double value = changes.getJSONObject(i).getDouble("Value");
				subbalance.updateBalance(value);
			} 
		}
	}
	
	public static void modifyTransactions(String identifier, JSONArray changes) throws JSONException {
		Transaction transaction = getTransaction(identifier);
		
		for(int i = 0; i < changes.length(); i++) {
			String key = changes.getJSONObject(i).getString("Key");
			
			if(key.equals("Amount")) {
				double value = changes.getJSONObject(i).getDouble("Value");
				transaction.updateAmount(value);
			} else if(key.equals("Description")) {
				String value = changes.getJSONObject(i).getString("Value");
				transaction.updateName(value);
			} else if(key.equals("Category")) {
				String value = changes.getJSONObject(i).getString("Value");
				transaction.updateCategory(value);
			} else if(key.equals("AssociatedWith")) {
				String value = changes.getJSONObject(i).getString("Value");
				transaction.updateParent(value);
			} else if(key.equals("Date")) {
				String value = changes.getJSONObject(i).getString("Value");
				int year = Integer.parseInt(value.substring(0,4));
				int month = Integer.parseInt(value.substring(5,7));
				int day = Integer.parseInt(value.substring(8, 10));
				Date d = DateFactory.getDate(day, month, year);
				transaction.updateDate(d);
			}
		}
	}
	public static void modifySavingsPlan(String identifier, JSONArray changes) throws JSONException {
		SavingsPlan savingsPlan = getSavingsPlan(identifier);
		
		for(int i = 0; i < changes.length(); i++) {
			String key = changes.getJSONObject(i).getString("Key");
			
			if(key.equals("SavingsPlanName")) {
				String value = changes.getJSONObject(i).getString("Value");
				savingsPlan.updateName(value);
			} else if(key.equals("Amount")) {
				double value = changes.getJSONObject(i).getDouble("Value");
				savingsPlan.updateAmount(value);
			} else if(key.equals("Balance")) {
				double value = changes.getJSONObject(i).getDouble("Value");
				savingsPlan.updateBalance(value);
			} else if(key.equals("Date")) {
				String value = changes.getJSONObject(i).getString("Value");
				int year = Integer.parseInt(value.substring(0,4));
				int month = Integer.parseInt(value.substring(5,7));
				int day = Integer.parseInt(value.substring(8, 10));
				Date d = DateFactory.getDate(day, month, year);
				savingsPlan.updateDate(d);
			}
		}
	}
	
	public static void modifySavingsPlan(String identifier, JSONArray changes) throws JSONException {
		SavingsPlan savingsPlan = getSavingsPlan(identifier);
		
		for(int i = 0; i < changes.length(); i++) {
			String key = changes.getJSONObject(i).getString("Key");
			
			if(key.equals("SavingsPlanName")) {
				String value = changes.getJSONObject(i).getString("Value");
				savingsPlan.updateName(value);
			} else if(key.equals("Amount")) {
				double value = changes.getJSONObject(i).getDouble("Value");
				savingsPlan.updateAmount(value);
			} else if(key.equals("Balance")) {
				double value = changes.getJSONObject(i).getDouble("Value");
				savingsPlan.updateBalance(value);
			} else if(key.equals("Date")) {
				String value = changes.getJSONObject(i).getString("Value");
				int year = Integer.parseInt(value.substring(0,4));
				int month = Integer.parseInt(value.substring(5,7));
				int day = Integer.parseInt(value.substring(8, 10));
				Date d = DateFactory.getDate(day, month, year);
				savingsPlan.updateDate(d);
			}
		}
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
			IAccount tParent = (IAccount)Parser.getResource(t.getParentIdentifier());
			
			transaction.put("ResourceIdentifier", t.getResourceIdentifier());
			if(t instanceof Income) {
				transaction.put("TransactionType", "Income");
			} else if(t instanceof Expense) {
				transaction.put("TransactionType", "Expense");
			} else if(t instanceof Transfer) {
				transaction.put("TransactionType", "Transfer");
			}
			transaction.put("Amount", t.getAmount());
			transaction.put("Description", t.getName());
			transaction.put("DateTime", t.getDate().format());
			transaction.put("Category", t.getCategory());
			transaction.put("AssociatedWith", identifier);
			if(t instanceof SingleIncome) {
				transaction.put("Recurring", false);
				transaction.put("AccountBalanceAfter", ((SingleIncome)t).getBalanceAfter());
			} else if(t instanceof SingleExpense) {
				transaction.put("Recurring", false);
				transaction.put("AccountBalanceAfter", ((SingleExpense)t).getBalanceAfter());
			} else if(t instanceof Transfer) {
				transaction.put("Recurring", false);
			} else if(t instanceof RecurringIncome) {
				transaction.put("Recurring", true);
				transaction.put("RecurringUntil", ((RecurringIncome)t).getEndDate().format());
				if(((RecurringIncome)t).getPeriod() == Period.DAILY) {
					transaction.put("RecurringInterval", 1);
				} else if(((RecurringIncome)t).getPeriod() == Period.WEEKLY) {
					transaction.put("RecurringInterval", 7);
				} else if(((RecurringIncome)t).getPeriod() == Period.MONTHLY) {
					transaction.put("RecurringInterval", 30);
				} else if(((RecurringIncome)t).getPeriod() == Period.YEARLY) {
					transaction.put("RecurringInterval", 365);
				}
			} else if(t instanceof RecurringExpense) {
				transaction.put("Recurring", true);
				transaction.put("RecurringUntil", ((RecurringExpense)t).getEndDate().format());
				if(((RecurringExpense)t).getPeriod() == Period.DAILY) {
					transaction.put("RecurringInterval", 1);
				} else if(((RecurringExpense)t).getPeriod() == Period.WEEKLY) {
					transaction.put("RecurringInterval", 7);
				} else if(((RecurringExpense)t).getPeriod() == Period.MONTHLY) {
					transaction.put("RecurringInterval", 30);
				} else if(((RecurringExpense)t).getPeriod() == Period.YEARLY) {
					transaction.put("RecurringInterval", 365);
				}
			}
			
			if(tParent instanceof Account) {
				JSONObject a = new JSONObject();
				a.put("AccountResourceIdentifier", tParent.getResourceIdentifier());
				a.put("AccountName", tParent.getName());
				a.put("AccountBalance", ((Account) tParent).getTotal());
				
				transaction.put("Account", a);
			} else if(tParent instanceof SubBalance) {
				JSONObject sb = new JSONObject();
				sb.put("SubBalanceResourceIdentifier", tParent.getResourceIdentifier());
				sb.put("SubBalanceName", tParent.getName());
				sb.put("SubBalanceBalance", tParent.getBalance());
				
				Account sbParent = Parser.getAccount(((SubBalance) tParent).getParentIdentifier());
				JSONObject a = new JSONObject();
				a.put("AccountResourceIdentifier", sbParent.getResourceIdentifier());
				a.put("AccountName", sbParent.getName());
				a.put("AccountBalance", sbParent.getBalance());
				a.put("AccountTotal", sbParent.getTotal());
				
				transaction.put("Account", a);
				transaction.put("SubBalance", sb);
			}
			
			transactions.put(transaction);
		}
		
		return transactions;
		
	}
	
	public static void setNextUserRI(int ri) {
		nextUserRI = ri;
	}
	
	public static void setNextAccountRI(int ri) {
		nextAccountRI = ri;
	}
	
	public static void setNextSubBalanceRI(int ri) {
		nextSubBalanceRI = ri;
	}
	
	public static void setNextTransactionRI(int ri) {
		nextTransactionRI = ri;
	}
	
	public static int getNextUserRI() {
		return nextUserRI;
	}
	
	public static int getNextAccountRI() {
		return nextAccountRI;
	}
	
	public static int getNextSubBalanceRI() {
		return nextSubBalanceRI;
	}
	
	public static int getNextTransactionRI() {
		return nextTransactionRI;
	}
	
	public static void updateNextRIs() {
		int numUsers = users.keySet().size();
		nextUserRI = numUsers + 1;
		
		int numAccounts = 0, numSubBalances = 0, numTransactions = 0;
		List<Object> resourceList = new ArrayList<Object>(resources.values());
		for(int i = 0; i < resourceList.size(); i++) {
			if(resourceList.get(i) instanceof Account) {
				numAccounts++;
			} else if(resourceList.get(i) instanceof SubBalance) {
				numSubBalances++;
			} else if(resourceList.get(i) instanceof Transaction) {
				numTransactions++;
			}
		}
		
		nextAccountRI = numAccounts + 1;
		nextSubBalanceRI = numSubBalances + 1;
		nextTransactionRI = numTransactions + 1;
		
		System.out.println("Account: " + nextAccountRI);
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
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static String bytesToHex(byte[] hash) {
		return DatatypeConverter.printHexBinary(hash);
	}
}
