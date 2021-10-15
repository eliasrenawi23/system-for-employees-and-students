package Utility.Graphics;

import java.util.ArrayList;

import Utility.AppManager;
import Utility.Particle;
import Utility.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This class is used to create the particle animation on the sides that appears in most of the GUI of this project.
 * 
 * @author Bshara
 * */
public class ParticlePlexus {

	private ArrayList<Particle> particles;

	private double maxDist = 210;
	private double minDist = 150;
	private double midDist;
	private GraphicsContext gc;
	private int width, height;
	private int particlesCount;

	public ParticlePlexus(double maxDist, double minDist, int particlesCount, GraphicsContext gc) {

		this.particles = new ArrayList<Particle>();
		this.maxDist = maxDist;
		this.minDist = minDist;
		this.midDist = (maxDist + minDist) / 2.0;
		this.gc = gc;
		this.width = (int) gc.getCanvas().getWidth();
		this.height = (int) gc.getCanvas().getHeight();
		this.particlesCount = particlesCount;
	}

	public void drawCallback() {
		gc.clearRect(0, 0, width, height);
		createParticle();
		particles.removeIf(p -> {
			return !p.isActive();
		});
		for (Particle p1 : particles) {
			p1.update();
			p1.draw(gc);
			for (Particle p2 : particles) {
				double distance = Vector2D.ManhattanDistance(p1.getPosition(), p2.getPosition());
				if (distance < minDist) {
					p1.addNeighbour(p2);
				}
				if (distance > maxDist) {
					p1.removeNeighbour(p2);
				} else if (p1.isNeighbour(p2)) {

					double alpha = 1;
					if (distance < minDist + midDist / 6)
						alpha = 1 - distance / midDist;
					else if (distance > maxDist - midDist / 6)
						alpha = 1 - distance / maxDist;

					gc.setGlobalAlpha(alpha);
					Color color = Color.rgb(194, 195, 196);
					gc.setStroke(color);

					gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());

					/*
					 * if (p1.numberOfNeighbours() == 3) { gc.setFill(Color.rgb(0, 102, 255, 0.1));
					 * Utility.Vector2D pos1 = p1.getNeighbours().get(0).getPosition();
					 * Utility.Vector2D pos2 = p1.getNeighbours().get(1).getPosition();
					 * 
					 * double x1 = pos1.getX(); double y1 = pos1.getY();
					 * 
					 * double x2 = pos2.getX(); double y2 = pos2.getY();
					 * 
					 * double x3 = p1.getX(); double y3 = p1.getY(); gc.fillPolygon(new double[] {
					 * x1, x2, x3 }, new double[] { y1, y2, y3 }, 3); }
					 */
				}
			}
		}
	}

	private void createParticle() {
		if (particles.size() > particlesCount)
			return;
		int x = AppManager.getRnd().nextInt(width);
		int y = AppManager.getRnd().nextInt(height);
		int red = AppManager.getRnd().nextInt(200) + 55;
		double opacity = AppManager.getRnd().nextDouble();
		Color color = Color.rgb(177, 218, 254, opacity);

		// TODO
		// color = Color.TRANSPARENT;
		double particleLife = AppManager.getRnd().nextDouble() * 5 + 3;// AppManager.getRnd().nextDouble() * 4 + 3; //
																		// life = [3, 7]
		Particle p = new Particle(x, y, color, particleLife);
		double velX = AppManager.getRnd().nextDouble() * 2 - 1;
		double velY = AppManager.getRnd().nextDouble() * 2 - 1;

		p.setVelocity(new Vector2D(velX / 3, velY / 3));
		particles.add(p);
	}
}
