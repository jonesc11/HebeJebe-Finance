package finance;

public class Transfer extends Transaction {
	
	private String fromResourceIdentifier;
	private String toResourceIdentifier;
	private double fromBalanceAfter;
	private double toBalanceAfter;
	
	/*
	 * @requires amount > 0, n.len() > 0, c.len() > 0, d != null, fRI.len() > 0, tRI.len() > 0, fb > 0, tb > 0
	 * @throws none
	 * @modifies none 
	 * @effects Provides a list of sub-balances from the specified account a
	 * @returns A list of transaction from the specified account a
	 * @param double a - amount of the transfer
	 * @param string n - name of transfer
	 * @param string c - catorgory
	 * @param Date d - date of transfer
	 * @param string fRI - from rescource identifier 
	 * @param string tRI - to rescource identifier 
	 * @param double fb - from balance total
	 * @param double tb - to balance total
	 */
	public Transfer(double a, String n, String c, Date d, String fRI, String tRI, double fb, double tb) {
		amount = a;
		name = n;
		category = c;
		date = d;
		fromResourceIdentifier = fRI;
		toResourceIdentifier = tRI;
		fromBalanceAfter = fb;
		toBalanceAfter = tb;
	}
	
	//getters and setters
	public String getFromResourceIdentifier() {
		return fromResourceIdentifier;
	}
	
	public String getToResourceIdentifier() {
		return toResourceIdentifier;
	}
	
	public double getFromBalanceAfter() {
		return fromBalanceAfter;
	}
	
	public double getToBalanceAfter() {
		return toBalanceAfter;
	}

}
