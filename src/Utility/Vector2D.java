package Utility;

import javafx.scene.Node;
import javafx.scene.shape.Circle;

/**
 * This class defined a 2D vector.
 * 
 * @author Bshara
 * */
public class Vector2D {
	private double x, y;

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	


	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public static Vector2D Lerp(Vector2D from, Vector2D to, double t) {
		double x = MathUtil.Lerp(from.getX(), to.getX(), t);
		double y = MathUtil.Lerp(from.getY(), to.getY(), t);
		return new Vector2D(x, y);

	}

	public static double ManhattanDistance(Vector2D v1, Vector2D v2) {
		return Math.abs(v1.x - v2.x) + Math.abs(v1.y - v2.y);
	}
	
	public static double Distance(Vector2D v1, Vector2D v2) {
		return (double)Math.sqrt((v1.x - v2.x)*(v1.x - v2.x) + (v1.y - v2.y)*(v1.y - v2.y));
	}

	
	public static Vector2D getPosition(Node node) {
		if (node instanceof Circle) {
			Circle c = (Circle) node;
			double x =  c.getCenterX();
			double y = c.getCenterY();
			return new Vector2D(x, y);
		}
		
		double x =  node.getTranslateX();
		double y =  node.getTranslateY();
		return new Vector2D(x, y);
	}
	
	
	public static Vector2D getScale(Node node) {
		
		double x = node.getScaleX();
		double y = node.getScaleY();
		return new Vector2D(x, y);
	}
	
	public static void setPosition(Node node, Vector2D pos) {
		node.setTranslateX(pos.x);
		node.setTranslateY(pos.y);
	}
	public static void setScale(Node node, Vector2D scale) {
		node.setScaleX(scale.x);
		node.setScaleY(scale.y);
	}
	
	public static Vector2D add(Vector2D v1, Vector2D v2) {
		return new Vector2D(v1.getX() + v2.getX(), v1.getY() + v2.getY());
	}
	
	public static Vector2D add(Vector2D v, double x) {
		return new Vector2D(v.getX() + x, v.getY() + x);
	}
	
	public static Vector2D substract(Vector2D v1, Vector2D v2) {
		return new Vector2D(v1.getX() - v2.getX(), v1.getY() - v2.getY());
	}
	
	public static Vector2D scalar(Vector2D v, double scalar) {
		return new Vector2D(v.getX() * scalar, v.getY() * scalar);
	}
	
	public static double dotProduct(Vector2D v1, Vector2D v2) {
		return v1.getX() * v2.getX() + v1.getY() + v2.getY();
	}
	
	
	public static Vector2D Zero() {
		return new Vector2D(0f, 0f);
	}
	
	@Override
	public String toString() {
		return "Vector2D [" + x + ", " + y + "]";
	}
	
	
	
}
