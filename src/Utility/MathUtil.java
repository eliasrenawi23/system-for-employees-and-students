package Utility;

import javafx.scene.paint.Color;

public class MathUtil {

	public static final double goldenRatio = 1.61803398875;
	public static double Lerp(double a, double b, double t) {
		return a + t * (b - a);
	}
	
	public static Color LerpColor(Color a, Color b, double t) {
		int red = (int) (Lerp(a.getRed(), b.getRed(), t) * 255);
		int green = (int) (Lerp(a.getGreen(), b.getGreen(), t) * 255);
		int blue = (int) (Lerp(a.getBlue(), b.getBlue(), t) * 255);
		double opacity = Lerp(a.getOpacity(), b.getOpacity(), t);
		return Color.rgb(red, green, blue, opacity);
	}
	

}
