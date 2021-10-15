package Utility;

/**
 * This class is used to define a speed curve for interpolation functions
 * 
 * @author Bshara
 * */
public class Curve {
	public static double easeInOutSin(double t) {
		double x = Math.sin(t * Math.PI - Math.PI / 2);
		return 0.5 * (x + 1);
	}

	// https://www.desmos.com/calculator/nc2qjdpkvd
	public static double easeInOut(double t, double k) {

		return (f1(2 * t - 1, k) / 2) + 0.5f;
	}

	public static double easeInOut(double t) {
		double k = 0.7f;
		return (f1(2 * t - 1, k) / 2) + 0.5f;
	}

	private static double f1(double x, double k) {
		double res = x * (1 - k);
		res = res / (k * (1 - 2 * Math.abs(x)) + 1);
		return res;
	}

	public static double quad(double x) {
		return x * x;
	}

	public static double cubic(double x) {
		return x * x * x;
	}
}
