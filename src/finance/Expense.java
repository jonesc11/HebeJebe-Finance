package finance;

public class Expense extends Transaction{
	
	public Expense(Account a, boolean isRecurring, Period period)
	{
		if(isRecurring)
		{
			new RecurringExpense();
		}
		else
		{
			new SingleExpense();
		}
	}
	
}
