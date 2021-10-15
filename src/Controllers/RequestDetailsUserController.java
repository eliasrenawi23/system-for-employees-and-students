package Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import Utility.AppManager;
import Utility.Curve;
import Utility.Particle;
import Utility.Graphics.ParticlePlexus;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;


/**
 * This page loads once the user double clicks on a request from the my requests page, with this page provides the user
 * with all of the needed information about the request and it's current state, the user can also view and open the attached files
 * that has been attached with the request.
 * 
 * @author Bshara
 * */
public class RequestDetailsUserController implements Initializable {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Canvas canvasRight;

	@FXML
	private Canvas canvasLeft;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		applyCanvasEffects(canvasRight, canvasLeft);

	}

	
	
	public static void applyCanvasEffects(Canvas right, Canvas left) {
		
		ParticlePlexus ppRight = new ParticlePlexus(210, 150, 50, right.getGraphicsContext2D());
		ParticlePlexus ppLeft = new ParticlePlexus(210, 150, 50, left.getGraphicsContext2D());

		AppManager.removeUnique("drawCallbackLoop");
		AppManager.addTimeTrigger(() -> {
			AppManager.updateUnique("drawCallbackLoop", () -> {
				ppRight.drawCallback();
				ppLeft.drawCallback();
			});	
		}, 0.2, "dd");
		

		
		
		AppManager.removeUnique("fffs");
		AppManager.addTimeTrigger(() -> {
			AppManager.updateUnique("fffs", () -> {
				Particle.globalSpeedFactor = Curve.cubic((Math.cos(AppManager.time * 0)
						+ Curve.easeInOut(Math.cos(AppManager.time * 2.54), -0.61803398875)));
			});
		}, 0.2, "ddf");
	}
	
}
