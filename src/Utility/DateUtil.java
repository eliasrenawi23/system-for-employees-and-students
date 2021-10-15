package Utility;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * This is a utility class used for date calculations with the class Timestamp
 * 
 * @author Bshara
 * */
public class DateUtil {

	public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public static final Timestamp NA = Timestamp.valueOf(LocalDateTime.of(1999, 1, 1, 1, 0));
	// public static final Timestamp ONE_DAY = Timestamp.;

	static {
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
	}

	public static Timestamp now() {
		return Timestamp.valueOf(LocalDateTime.now());
	}

	public static String toString(Timestamp ts) {
		return sdf.format(ts);
	}

	/**
	 * Returns the difference between a and b, meaning a - b.
	 */
	public static String difference(Timestamp a, Timestamp b) {
		Timestamp diff = new Timestamp(a.getTime() - b.getTime());
		return sdf.format(diff);
	}

	
	public static Timestamp add(Timestamp a, int days, int hours) {

		LocalDateTime date = a.toLocalDateTime();
		date = date.plusDays(days);
		date = date.plusHours(hours);

		return Timestamp.valueOf(date);
	}
	
	public static Timestamp add(Timestamp a, int days, int hours, int minutes) {
		LocalDateTime date = a.toLocalDateTime();
		date = date.plusDays(days);
		date = date.plusHours(hours);
		date = date.plusMinutes(minutes);

		return Timestamp.valueOf(date);
	}

	public static void main(String[] args) {

		String str = DateUtil.differenceInDaysHours(DateUtil.add(DateUtil.now(),  3, 7), DateUtil.now());
		System.out.println(str);

	}
	
	public static Timestamp get(LocalDate date) {
		return Timestamp.valueOf(date.atStartOfDay());
	}

	public static Timestamp daysFromNow(int days) {
		LocalDateTime now = LocalDateTime.now();
		now = now.plusDays(days);
		return Timestamp.valueOf(now);
	}
	
	public static Timestamp minus(Timestamp ts, int secs) {
		LocalDateTime now = ts.toLocalDateTime();
		now = now.minusSeconds(secs);
		return Timestamp.valueOf(now);
	}

	public static String differenceInDaysHours(Timestamp a, Timestamp b) {
		long diff = a.getTime() - b.getTime();
		double hours = diff * 2.77778e-7;
		double days = hours * 0.0416667;
		hours -= (int)days / 0.0416667;
		return "Days: " + (int)days + ", Hours: " + (int)hours;
	}
	
	public static int diffInDays(Timestamp a, Timestamp b) {
		long diff = a.getTime() - b.getTime();
		double hours = diff * 2.77778e-7;
		double days = hours * 0.0416667;
		return (int)days;
	}

	

}
