package finance;

import java.io.IOException;
import java.util.*;
import java.lang.String;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import finance.FinanceUtilities.Period;

/*
Main function for initializing the server client and site.
 */


public class Runner {
	
	private static Map<String, User> users = new HashMap<String, User>();
	private static Map<String, Object> resources = new HashMap<String, Object>();
	private static DateFactory dateFactory = new DateFactory();
	
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
		User user = users.get(request.getString("AccountId"));
		String actionType = request.getString("ActionType");
		JSONObject action = request.getJSONObject("Action");
		
		// If else cases for the different action types.
		
		// First case: GetTransactions
		// Returns a JSONObject containing a list of Transaction JSONObjects, and a next token based on
		// the provided limit.
		// Issues: Currently ignores NextTokens and ResourceIdentifier
		if(actionType.equals("GetTransactions")) {
			JSONArray transactions = null;
			List<Transaction> allTransactions = null;
			List<String> types = null;
			String category = null;
			String getFrom = null;
			
			// Define the JSONArray of transactions, which will be the first item in the response JSONObject.
			// Also defines list of all Transactions for the specified User, and gets the types of transactions
			// that will be selected (Incomes, Expenses, Transfers).
			transactions = new JSONArray();
			allTransactions = user.getTransactionHistory();
			getFrom = action.getString("GetFrom");
			if (!action.isNull("TransactionType"))
				types = JSONArrayToList(action.getJSONArray("TransactionType"));
			
			if (getFrom.substring(0, 1).equals("u"))
				allTransactions = Runner.getUser(getFrom).getTransactionHistory();
			else if (getFrom.substring(0, 1).equals("a"))
				allTransactions = Runner.getAccount(getFrom).getTransactionHistory();
			
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
				System.out.println("i: " + i + " category " + category + " types " +types);
				JSONObject o = new JSONObject();
				if(allTransactions.get(i) instanceof Income && (category == null || allTransactions.get(i).getCategory().equals(category)) && (types == null || types.contains("Income"))) {
					System.out.println("Income");
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
				} else if(allTransactions.get(i) instanceof Expense && (allTransactions.get(i).getCategory() == category || category == null) && (types.contains("Expense") || types == null)) {
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
				} else if(allTransactions.get(i) instanceof Transfer && (allTransactions.get(i).getCategory() == category || category == null) && (types.contains("Transfer") || types == null)) {
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
			
			response.put("Transactions", transactions);
		}
		// Second case: GetAccount
		// Returns a JSONObject containing a JSONObject that includes information about the specified Account.
		// Issues: Currently ignores NextTokens.
		else if(actionType.equals("GetAccounts")) {
			//If a Resource Identifier is specified, returns a single Account Object. Otherwise, returns
			//a list of all account objects.
			if(action.getString("ResourceIdentifier") != null) {
				List<Account> accountsList = user.getAccounts();
				JSONArray accounts = new JSONArray();
				
				for(int i = 0; i < accountsList.size(); i++) {
					JSONObject o = new JSONObject();
					JSONArray transactions = new JSONArray();
					List<Transaction> allTransactions = accountsList.get(i).getTransactionHistory();
					
					//Puts the most recent 25 transactions from the specified accounts into the JSONArray transactions.
					for(int j = 0; j < allTransactions.size(); j++) {
						if(transactions.length() >= 25) {
							break;
						}
						
						JSONObject t = new JSONObject();
						
						t.put("ResourceIdentifier", allTransactions.get(j).getResourceIdentifier());
						if(allTransactions.get(j) instanceof Income) {
							t.put("TransactionType", "Income");
						}
						else if(allTransactions.get(j) instanceof Expense) {
							t.put("TransactionType", "Expense");
						}
						else if(allTransactions.get(j) instanceof Transfer) {
							t.put("TransactionType", "Transfer");
						}
						t.put("Amount", allTransactions.get(j).getAmount());
						t.put("Description", allTransactions.get(j).getName());
						t.put("DateTime", allTransactions.get(j).getDate().format());
						t.put("Category", allTransactions.get(j).getCategory());
						if (allTransactions.get(j) instanceof RecurringIncome) {
							o.put("Recurring", true);
							o.put("RecurringUntil", ((RecurringIncome)allTransactions.get(j)).getEndDate().format());
							if(((RecurringIncome)allTransactions.get(j)).getPeriod() == Period.DAILY) {
								o.put("RecurringInterval", 1);
							}
							else if(((RecurringIncome)allTransactions.get(j)).getPeriod() == Period.MONTHLY) {
								o.put("RecurringInterval", 30);
							}
							else if(((RecurringIncome)allTransactions.get(j)).getPeriod() == Period.YEARLY) {
								o.put("RecurringInterval", 365);
							}
						}
						else if (allTransactions.get(j) instanceof RecurringExpense) {
							o.put("Recurring", true);
							o.put("RecurringUntil", ((RecurringExpense)allTransactions.get(j)).getEndDate().format());
							if(((RecurringExpense)allTransactions.get(j)).getPeriod() == Period.DAILY) {
								o.put("RecurringInterval", 1);
							}
							else if(((RecurringExpense)allTransactions.get(j)).getPeriod() == Period.MONTHLY) {
								o.put("RecurringInterval", 30);
							}
							else if(((RecurringExpense)allTransactions.get(j)).getPeriod() == Period.YEARLY) {
								o.put("RecurringInterval", 365);
							}
						}
						else {
							t.put("Recurring", false);
						}
						
						transactions.put(t);
					}
					
					//Creates the appropriate Account JSONObject based on information about the specified Account.
					o.put("ResourceIdentifier", accountsList.get(i).getResourceIdentifier());
					o.put("AccountName", accountsList.get(i).getName());
					o.put("AccountType", accountsList.get(i).getType());
					o.put("LatestBalance", accountsList.get(i).getBalance());
					o.put("LatestTransactions", transactions);
					
					accounts.put(o);
				}
				
				response.put("Account", accounts);
			}
			else {
				// Gets the Account object specified by the ResourceIdentifier, and creates a new JSONObject
				// which will contain information about the Account. Creates a JSONArray transactions to contain
				// the most recent 25 transactions associated with the Account.
				Account account = user.getAccount(action.getString("ResourceIdentifier"));
				JSONObject o = new JSONObject();
				JSONArray transactions = new JSONArray();
				List<Transaction> allTransactions = account.getTransactionHistory();
				
				//Puts the most recent 25 transactions from the specified accounts into the JSONArray transactions.
				for(int i = 0; i < allTransactions.size(); i++) {
					if(transactions.length() >= 25) {
						break;
					}
					
					transactions.put(allTransactions.get(i));
				}
				
				//Creates the appropriate Account JSONObject based on information about the specified Account.
				o.put("ResourceIdentifier", account.getResourceIdentifier());
				o.put("AccountName", account.getName());
				o.put("AccountType", account.getType());
				o.put("LatestBalance", account.getBalance());
				o.put("LatestTransactions", transactions);
				
				response.put("Account", o);
			}
		}
		else if(actionType.equals("GetUser")) {
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
		}
		else if(actionType.equals("CreateUser")) {
			String email = action.getString("UserIdentifier");
			String password = action.getString("Password");
			String firstName = action.getString("FirstName");
			String lastName = action.getString("LastName");
			
			User newUser = new User(email, password, firstName, lastName);
			
			//A very poor way of creating a new Resource Identifier for the new User
			int i = 1;
			while(users.get("u" + i) != null) {
				i++;
			}
			users.put("u" + i, newUser);
			
			response.put("ResourceIdentifier", "u" + i);
			response.put("UserIdentifier", email);
			response.put("Password", password);
			response.put("FirstName", firstName);
			response.put("LastName", lastName);
		}
		else if(actionType.equals("CreateAccount")) {
			String name = action.getString("AccountName");
			String type = action.getString("AccountType");
			double balance = action.getDouble("AccountBalance");
			
			String resourceIdentifier = user.createAccount(name, type, balance);
			
			response.put("ResourceIdentifier", resourceIdentifier);
			response.put("UserResourceIdentifier", action.getString("UserResourceIdentifier"));
			response.put("AccountName", name);
			response.put("AccountType", type);
			response.put("AccountBalance", balance);
		}
		//Creates a new transaction, associates it with the appropriate user and account/sub-balance
		//Issues: Currently doesn't work with Transfers.
		else if(actionType.equals("CreateTransaction")) {
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
			Date date = dateFactory.getDate(day, month, year);
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
						int endYear = Integer.parseInt(dateString.substring(0,4));
						int endMonth = Integer.parseInt(dateString.substring(5,7));
						int endDay = Integer.parseInt(dateString.substring(8, 10));
						endDate = dateFactory.getDate(endDay, endMonth, endYear);
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
						int endYear = Integer.parseInt(dateString.substring(0,4));
						int endMonth = Integer.parseInt(dateString.substring(5,7));
						int endDay = Integer.parseInt(dateString.substring(8, 10));
						endDate = dateFactory.getDate(endDay, endMonth, endYear);
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
		}
					 
		return response.toString();
	}

    public static void main(String[] args) throws IOException{
    	User u = new User ("test@example.com", "butts", "Liam", "Neeson");
    	u.setResourceIdentifier("u0");
    	users.put("u0", u);
        startServer();
    }

    public static void startServer(){
        (new Thread() {
            @Override
            public void run() {
 
                new Server().run();
            }
        }).start();
    }
}