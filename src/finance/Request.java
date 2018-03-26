package finance;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.lang.String;
import java.util.List;
import java.util.ArrayList;

public class Request {
	
	public List<String> JSONArrayToList(JSONArray a) throws JSONException {
		List<String> l = new ArrayList<String>();
		
		for (int i = 0; i < a.length(); i++) {
			l.add(a.getString(i));
		}
		return l;
	}
	
	public JSONObject processRequest(String r) throws JSONException {
		//Take the input string and turn it into a JSONObject. Create an empty JSONObject that will be
		//returned by the function
		JSONObject request = new JSONObject(r);
		JSONObject response = new JSONObject();
		
		//Get the User object that specified by the request, along with the actionType, which will 
		//specify what the response JSONObject will look like, and the action JSONObject that will
		//specify important information regarding the action to be performed.
		User user = getUser(request.getString("AccountID"));
		String actionType = request.getString("ActionType");
		JSONObject action = request.getJSONObject("Action");
		
		// If else cases for the different action types.
		
		// First case: GetTransactions
		// Returns a JSONObject containing a list of Transaction JSONObjects, and a next token based on
		// the provided limit.
		// Issues: Currently ignores NextTokens.
		if(actionType == "GetTransactions") {
			// Define the JSONArray of transactions, which will be the first item in the response JSONObject.
			// Also defines list of all Transactions for the specified User, and gets the types of transactions
			// that will be selected (Incomes, Expenses, Transfers).
			JSONArray transactions = new JSONArray();
			List<Transaction> allTransactions = user.getTransactionHistory();
			List<String> types = JSONArrayToList(action.getJSONArray("TransactionType"));
			
			// If no limit is specified, default to 25.
			int limit;
			if(action.get("Limit") != null) {
				limit = 25;
			}
			else {
				limit = action.getInt("Limit");
			}
			
			// Loops through all transactions, and adds a Transaction JSONObject to the JSONArray of 
			// transactions if the current transaction meets the requirements
			// type == null means no transaction types have been specified.
			for(int i = 0; i < allTransactions.size(); i++) {
				if(transactions.length() >= limit) {
					break;
				}
				
				JSONObject o = new JSONObject();
				if(allTransactions.get(i) instanceof Income && (types.contains("Income") || types == null)) { 
					o.put("TransactionType", "Income");
					o.put("Amount", allTransactions.get(i).getAmount());
					o.put("Description", allTransactions.get(i).getName());
					o.put("DateTime", allTransactions.get(i).getDate());
					if (allTransactions.get(i) instanceof RecurringExpense) {
						o.put("Recurring", true);
					}
					else {
						o.put("Recurring", false);
					}
					transactions.put(o);
				}
				else if(allTransactions.get(i) instanceof Expense && (types.contains("Expense") || types == null)) {
					o.put("TransactionType", "Expense");
					o.put("Amount", allTransactions.get(i).getAmount());
					o.put("Description", allTransactions.get(i).getName());
					o.put("DateTime", allTransactions.get(i).getDate());
					if (allTransactions.get(i) instanceof RecurringExpense) {
						o.put("Recurring", true);
					}
					else {
						o.put("Recurring", false);
					}
					transactions.put(o);
				}
				else if(allTransactions.get(i) instanceof Transfer && (types.contains("Transfer") || types == null)) {
					o.put("TransactionType", "Transfer");
					o.put("Amount", allTransactions.get(i).getAmount());
					o.put("Description", allTransactions.get(i).getName());
					o.put("DateTime", allTransactions.get(i).getDate());
					o.put("Recurring",  false);
					transactions.put(o);
				}
			}
			
			response.put("Transactions", transactions);
		}
		// Second case: GetAccount
		// Returns a JSONObject containing a JSONObject that includes information about the specified Account.
		// Issues: Currently ignores NextTokens.
		else if(actionType == "GetAccount") {
			// Gets the Account object specified by the ResourceIdentifier, and creates a new JSONObject
			// which will contain information about the Account. Creates a JSONArray transactions to contain
			// the most recent 25 transactions associated with the Account.
			Account account = user.getAccounts().get(Integer.parseInt(action.getString("ResourceIdentifier")));
			JSONObject o = new JSONObject();
			JSONArray transactions = new JSONArray();
			List<Transaction> allTransactions = account.getTransactionHistory();
			
			//Puts the most recent 25 transactions from the specified accounts into the transactions JSONArray.
			for(int i = 0; i < allTransactions.size(); i++) {
				if(transactions.length() >= 25) {
					break;
				}
				
				transactions.put(allTransactions.get(i));
			}
			
			//Creates the appropriate Account JSONObject based on information about the specified Account.
			o.put("ResourceIdentifier", user.getAccounts().indexOf(account));
			o.put("AccountType", account.getType());
			o.put("LatestBalance", account.getBalance());
			o.put("LatestTransactions", transactions);
			
			response.put("Account", o);
		}
		else if(actionType == "CreateUser") {
			
		}
		else if(actionType == "CreateAccount") {
			
		}
		else if(actionType == "CreateTransaction") {
			
		}
					 
		return response;
	}
}