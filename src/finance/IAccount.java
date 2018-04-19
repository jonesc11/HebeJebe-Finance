package finance;

import finance.FinanceUtilities.Period;

import java.util.List;

public interface IAccount {
	
	public String getResourceIdentifier();
	public void setResourceIdentifier(String identifier);
	public String getName();
	public double getBalance();
	public void updateBalance(double b);
	public List<Transaction> getTransactionHistory();
	public String addSingleIncome(double a, String n, String c, Date d);
	public String addRecurringIncome(double a, String n, String c, Period p, Date d1, Date d2);
	public String addSingleExpense(double a, String n, String c, Date d);
	public String addRecurringExpense(double a, String n, String c, Period p, Date d1, Date d2);
	public void addTransfer(Transfer newTransfer);
	public void checkRecurringTransactions();
	
}