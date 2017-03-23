package carvalhorr.cs654.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	public static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	public static Date convertStringToDate(String format, String date) throws ParseException {
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.parse(date);
	}
}
