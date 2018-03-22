package finance;

/**
 * A date class represents a date. This object is immutable, and you will be able to get the formatted date
 * as well as the individual integer values of the date.
 * 
 * @author jonesc11
 *
 */

public class Date {
	/**
	 * Representation Invariant:
	 *   this.day > 0
	 *   this.month > 0
	 *   this.year > 0
	 */
	private int day, month, year;
	
	/**
	 * @param day is the day of the date we are creating.
	 * @param month is the month of the date we are creating.
	 * @param year is the year of the date we are creating.
	 * @modifies this
	 * @effects creates a new Date object.
	 * @throws IllegalArgumentException if day, month, or year is not possibly part of the calendar.
	 */
	public Date(int day, int month, int year) {
		if (month < 1 || month > 12)
			throw new IllegalArgumentException("Date: month is an illegal value: " + month);
		if (year < 1)
			throw new IllegalArgumentException("Date: year is an illegal value: " + year);
		if (dayIsIllegal(day, month, year))
			throw new IllegalArgumentException("Date: day is an illegal value for the month & year: " + day);
		
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	/**
	 * @param d is the date we are copying
	 * @modifies this
	 * @effects creates a new Date object
	 * @throws NullPointerException if d is null
	 */
	public Date(Date d) {
		if (d == null)
			throw new NullPointerException("Date: d cannot be null");
		
		this.day = d.day;
		this.month = d.month;
		this.year = d.year;
	}
	
	/**
	 * @param day is the day of the date we are creating
	 * @param month is the month of the date we are creating
	 * @param year is the year of the date we are creating
	 * @requires 0 < month < 13, year > 0
	 * @returns true if the day is legal based on the month and year, false otherwise.
	 */
	private boolean dayIsIllegal(int day, int month, int year) {
		//- Months with 31 days
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)
			return day < 1 || day > 31;
		
		//- Months with 30 days
		if (month == 4 || month == 6 || month == 9 || month == 11)
			return day < 1 || day > 30;
		
		//- This determines when it's February, calculates leap years and returns accordingly.
		if (year % 4 == 0 && !(year % 100 == 0 && year % 400 != 0))
			return day < 1 || day > 29;
		else
			return day < 1 || day > 28;
	}
	
	/**
	 * @returns this.day
	 */
	public int getDay() {
		return this.day;
	}
	
	/**
	 * @returns this.month
	 */
	public int getMonth() {
		return this.month;
	}
	
	/**
	 * @returns this.year
	 */
	public int getYear() {
		return this.year;
	}
	
	/**
	 * @param d is the date we are comparing this date to.
	 * @throws NullPointerException if d is null
	 * @returns true if this is before d, false if this is equal to or after d.
	 */
	public boolean isBefore(Date d) {
		if (d == null)
			throw new NullPointerException("Date: d cannot be null");
		
		//- Year is earlier
		if (this.year < d.year)
			return true;
		
		//- Year is equal but earlier month
		if (this.year == d.year && this.month < d.month)
			return true;
		
		//- Year and month is equal but day is earlier
		if (this.year == d.year && this.month == d.month && this.day < d.day)
			return true;
		
		//- Everything else means it's either the same date or later
		return false;
	}
	
	/**
	 * @returns a string formatted in dd/mm/yyyy
	 */
	public String format() {
		return String.format("%02d", this.day) + "/" + String.format("%02d", this.month) + "/" + String.format("%04d", this.year);
	}
	
	/**
	 * @param formatOptions is a string saying how to format the date in a format with "m"s, "d"s, and "y"s.
	 *          -> "y"s can be in the form of "yy" or "yyyy"
	 *          -> "m"s can be in the form of "m" or "mm"
	 *          -> "d"s can be in the form of "d" or "dd"
	 * @throws IllegalArgumentException if format string is illegal.
	 * @returns a String formatted to the specification of formatOptions
	 */
	public String format(String formatOptions) {
		String ret = new String();
		
		for (int i = 0; i < formatOptions.length(); ++i) {
			if (formatOptions.substring(i, i + 1) == "y") { //- String is a year
				//- yyyy
				if ((i + 3) < formatOptions.length() &&
					formatOptions.substring(i + 1, i + 2).equals("y") &&
					formatOptions.substring(i + 2, i + 3).equals("y") &&
					formatOptions.substring(i + 3, i + 4).equals("y")) {
					
					//- Append the new part of the string
					ret += String.format("%04d", this.year);
					
					//- Continue the loop after the "y"s
					i += 3;
					continue;
				} else if (i + 1 < formatOptions.length() &&
						   formatOptions.substring(i + 1, i + 2).equals("y")) {
					
					//- Append the new part of the string
					ret += String.format("%02d", this.year % 100);
					
					//- Continue the loop after the "y"s
					i++;
					continue;
				} else {
					throw new IllegalArgumentException("Legal options for \"year\" is \"yy\" or \"yyyy\"");
				}
			} else if (formatOptions.substring(i, i + 1) == "m") { //- String is a month
				if (i + 1 < formatOptions.length() &&
					formatOptions.substring(i + 1, i + 2).equals("m")) {
					
					//- Append the new part of the string
					ret += String.format("%02d", this.month);
					
					//- Continue the loop after the "m"s
					i++;
					continue;
				} else {
					//- Append the new part of the string
					ret += this.month;
					
					//- Continue the loop after the "m"
					continue;
				}
			} else if (formatOptions.substring(i, i + 1) == "d") { //- String is a day
				if (i + 1 < formatOptions.length() &&
					formatOptions.substring(i + 1, i + 2).equals("d")) {
					
					//- Append the new part of the string
					ret += String.format("%02d", this.day);
					
					//- Continue the loop after the "d"s
					i++;
					continue;
				} else {
					//- Append the new part of the string
					ret += this.day;
					
					//- Continue the loop after the "d"
					continue;
				}
			} else {
				ret += formatOptions.substring(i, i + 1);
			}
		}
		
		return ret;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		
		if (o.getClass() == getClass()) {
			Date d = (Date) o;
			return this.year == d.year && this.month == d.month && this.day == d.day;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.year * this.month ^ (this.day * 47);
	}
}
