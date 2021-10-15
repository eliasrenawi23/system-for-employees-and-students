package Utility;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This class provides a particles object that can be define as a point on the 2D axis
 * it has a initial speed and life and can change size.
 * 
 * @author Bshara
 * */
public class Particle {
	public static double globalSpeedFactor = 1.0;
	private double x;
    private double y;
    
    private Vector2D velocity;
    private Color color;
    
    private double life;
    private boolean active = true;

    private double sizeX = 8;
    private double sizeY = 8;
    private ArrayList<Particle> neighbours;
    private int neighboursSize = 6;
    public Particle() {
    	new Particle(0, 0, Color.BLACK, Double.MAX_VALUE);
    }
    public Particle(double x, double y, Color color) {
    	velocity = Vector2D.Zero();
		this.x = x;
		this.y = y;
		this.color = color;
		this.life = 5.0;
		neighbours = new ArrayList<Particle>();
	}
    
    public Particle(double x, double y, Color color, double life) {
    	velocity = Vector2D.Zero();
		this.x = x;
		this.y = y;
		this.color = color;
		this.life = life;
		neighbours = new ArrayList<Particle>();
	}
    
	public void addNeighbour(Particle p) {
		if (neighbours.size() < neighboursSize)
			neighbours.add(p);
	}

	public void removeNeighbour(Particle p) {
		neighbours.remove(p);
	}
	
	public ArrayList<Particle> getNeighbours(){
		return neighbours;
	}
	
	public boolean isNeighbour(Particle p) {
		return neighbours.contains(p);
	}
	
	public int numberOfNeighbours() {
		return neighbours.size();
	}
	
    public Vector2D getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}

	public boolean isDead() {
        return life == 0;
    }

    public boolean isActive() {
        return active;
    }


    // TODO
    public double getX() {
		return x + sizeX / 2;
	}

	public void setX(double x) {
		this.x = x;
	}

	// TODO
	public double getY() {
		return y + sizeY / 2;
	}

	public void setY(double y) {
		this.y = y;
	}

	
	
	public double getLife() {
		return life;
	}

	public void setLife(double life) {
		this.life = life;
	}

	public void update() {
        if (!active)
            return;

        life -= AppManager.deltaTime;
        
        if (life < 0) {
            active = false;
            
        }

        x += velocity.getX() * globalSpeedFactor;
        y += velocity.getY() * globalSpeedFactor;
    }
	
	public void activate(Vector2D velocity) {
        active = true;
        this.velocity = velocity;
    }
	
    public void draw(GraphicsContext g) {
    	
    	
        g.setFill(color);
        g.setGlobalAlpha(life); 
        g.fillOval(x, y, sizeX, sizeY);
    }
    
    public Vector2D getPosition() {
    	return new Vector2D(getX(), getY());
    }
	
}
