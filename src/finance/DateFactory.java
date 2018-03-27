package finance;

import java.util.Map;
import java.util.HashMap;

public class DateFactory {
	public static final Map<String, Date> dates = new HashMap<String, Date>();
	
	public static Date getDate(int day, int month, int year) {
		String dateString = Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
		Date d = dates.get(dateString);
		
		if(d == null) {
			d = new Date(day, month, year);
			dates.put(dateString, d);
		}
		
		return d;
	}
}