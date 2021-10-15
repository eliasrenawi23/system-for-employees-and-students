package ServerLogic;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import Utility.DateUtil;

/**
 * This package is used with the MySQL class as a utility class that contains a
 * set of static methods. Most of these methods are used for dates calculations.
 */
public class SQLUtil {
	public static final Timestamp NA = Timestamp.valueOf(LocalDateTime.of(2002, 1, 1, 1, 0));

	public static String toString(Timestamp timeStamp) {

		timeStamp = DateUtil.add(timeStamp, 0, 3, 30);
		String res = timeStamp.toString();
		res = res.substring(0, res.length() - 2);

		return res;
	}

	private static boolean isAfterEq(Timestamp a, Timestamp b) {
		return a.after(b) || a.equals(b);
	}

	private static boolean isBeforeEq(Timestamp a, Timestamp b) {
		return a.before(b) || a.equals(b);
	}

	public static int getNumOfDaysInInterval(Timestamp limA, Timestamp limB, Timestamp a, Timestamp b) {

		b = b.before(SQLUtil.NA) ? DateUtil.minus(limB, 20) : b;

		if (a.after(b)) {
			return 0;
		}

		if (isAfterEq(a, limB) || isBeforeEq(b, limA)) {
			return 0;
		}

		if (isAfterEq(a, limA) && isBeforeEq(a, limB) && isAfterEq(b, limB)) {

			return diff(limB, a);
		}

		if (isBeforeEq(a, limA) && isBeforeEq(b, limB) && isAfterEq(b, limA)) {

			return diff(b, limA);

		}

		if (isAfterEq(a, limA) && isBeforeEq(b, limB)) {

			return diff(b, a);

		}

		return diff(limB, limA);

	}

	public static int isActiveInInterval(Timestamp limA, Timestamp limB, Timestamp a, Timestamp b) {

		b = b.before(SQLUtil.NA) ? DateUtil.minus(limB, 20) : b;

		if (a.after(b)) {
			return 0;
		}

		if (isAfterEq(a, limB) || isBeforeEq(b, limA)) {
			return 0;
		}

		if (isAfterEq(a, limA) && isBeforeEq(a, limB) && isAfterEq(b, limB)) {
			return 1;
		}

		if (isBeforeEq(a, limA) && isBeforeEq(b, limB) && isAfterEq(b, limA)) {
			return 1;
		}

		if (isAfterEq(a, limA) && isBeforeEq(b, limB)) {
			return 1;
		}

		return 0;

	}

	public static int diff(Timestamp a, Timestamp b) {

		long diff = a.getTime() - b.getTime();
		double hours = diff * 2.77778e-7;
		double days = hours * 0.0416667;
		return (int) days;
	}
}
