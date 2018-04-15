package finance;

public class Transfer extends Transaction {
	
	private String fromResourceIdentifier;
	private String toResourceIdentifier;
	private double fromBalanceAfter;
	private double toBalanceAfter;
	
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
