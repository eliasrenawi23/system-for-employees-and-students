package Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Controllers.Logic.ControllerManager;
import ServerLogic.Server;
import Utility.AppManager;
import Utility.Curve;
import Utility.FXUtility;
import Utility.Func;
import Utility.MathUtil;
import Utility.Util;
import Utility.VoidFunc;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * This class provides the user with the needed information to start and run the server, the user can also stop the server,
 * the user can define the database parameters, like user and password.
 * it also shows the ip address of the server so that clients on other computers can connect to it.
 * 
 * @author Bshara
 * */
public class ServerGUIController extends Application implements Initializable {
	private static final int WIDTH = 600;
	private static final int HEIGHT = (int) (WIDTH / MathUtil.goldenRatio);

	@FXML
	private Canvas canvas;

	@FXML
	private Label lbAddressIP;

	@FXML
	private TextField ifPort;

	@FXML
	private Button btnStartServer;

	@FXML
	private Label lblStatus;

	@FXML
	private Circle cStatus;

	@FXML
	private Label lblHostName;

	@FXML
	private TextField ifDbUsername;

	@FXML
	private TextField ifDbPassword;

	@FXML
	private TextField ifDbSchemaName;

	@FXML
	private TextField tfThreadPoolSize;

	private GraphicsContext gc;
	private ArrayList<Utility.Particle> particles;
	private int cnt = 0;
	private final int maxParticlesCount = 20;

	public static void main(String[] args) {

		System.out.println("main");
		Application.launch(args);
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("Start");

		FXMLLoader loader = new FXMLLoader(getClass().getResource("serverGUI.fxml"));
		Parent root = loader.load();

		Platform.setImplicitExit(false);
		primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
		primaryStage.setTitle("Server GUI");
		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {

				System.exit(1);
			}
		});

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		gc = canvas.getGraphicsContext2D();

		particles = new ArrayList<Utility.Particle>();

		AppManager.addInitialFunction(() -> {
			gc.clearRect(0, 0, WIDTH, HEIGHT);
		});
		AppManager.updateUnique("drawCallbackLoop", () -> {
			drawCallbackLoop();
		});

		AppManager.updateUnique("particle speed factor", () -> {
			Utility.Particle.globalSpeedFactor = Curve.cubic((Math.cos(AppManager.time * 0)
					+ Curve.easeInOut(Math.cos(AppManager.time * 2.54), -0.61803398875)));
		});

		AppManager.updateUnique("add particles", () -> {
			if (particles.size() > maxParticlesCount)
				return;
			cnt++;
			if (cnt == 4) {
				cnt = 0;
				createParticle();
			}
		});

		VoidFunc onServerStart = () -> {
			lblStatus.setText("Running");
			cStatus.setFill(Color.GREEN);
			btnStartServer.setText("Stop Server");
			lbAddressIP.setText(Server.getInstance().getHostAddress().toString());
			lblHostName.setText(Server.getInstance().getHostName().toString());
			Server.checkForTimeExceptions();

			
		};
		Server.addServerStartedEvent(onServerStart);

		VoidFunc onServerStop = () -> {
			
			Server.onShutDown();
			
			lblStatus.setText("Stopped");
			cStatus.setFill(Color.RED);
			Server.stopCheckingForTimeExceptions();
			lbAddressIP.setText("-");
			lblHostName.setText("-");

			btnStartServer.setText("Start Server");

		};

		Server.addServerStoppedEvent(onServerStop);

		FXUtility.addNumbersOnlyListner(tfThreadPoolSize);

	}

	@FXML
	private void onStartServerClick() {
		if (btnStartServer.getText().compareTo("Start Server") == 0) {
			System.out.println("Starting server");
			int port = Integer.parseInt(ifPort.getText());
			port = port < 0 ? Server.DEFAULT_PORT : port;

			String username = ifDbUsername.getText();
			String password = ifDbPassword.getText();
			String schemaName = ifDbSchemaName.getText();

			if (username == "" || password == "" || schemaName == "") {
				ControllerManager.showInformationMessage("Input error", "Missing fields",
						"Please fill the missing fields", null);
			} else {

				lblStatus.setText("Starting server...");
				cStatus.setFill(Color.YELLOW);

				int poolSize = Integer.parseInt(tfThreadPoolSize.getText());

				Server.getInstance().initialize(port, username, password, schemaName, poolSize);

				System.out.println("Server started!");

				System.out.println("max thread pool size = " + poolSize);

			}

		} else if (btnStartServer.getText().compareTo("Stop Server") == 0) {

			try {
				Server.getInstance().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private double maxDist = 210;
	private double minDist = 150;
	private double midDist = (maxDist + minDist) / 2.0;

	private void drawCallbackLoop() {
		particles.removeIf(p -> {
			return !p.isActive();
		});
		for (Utility.Particle p1 : particles) {
			p1.update();
			p1.draw(gc);
			for (Utility.Particle p2 : particles) {
				double distance = Utility.Vector2D.ManhattanDistance(p1.getPosition(), p2.getPosition());
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
		int x = AppManager.getRnd().nextInt(WIDTH);
		int y = AppManager.getRnd().nextInt(HEIGHT);
		int red = AppManager.getRnd().nextInt(200) + 55;
		double opacity = AppManager.getRnd().nextDouble();
		Color color = Color.rgb(0, 0, 0, opacity);
		// TODO
		// color = Color.TRANSPARENT;
		double particleLife = AppManager.getRnd().nextDouble() * 5 + 3;// AppManager.getRnd().nextDouble() * 4 + 3; //
																		// life = [3, 7]
		Utility.Particle p = new Utility.Particle(x, y, color, particleLife);
		double velX = AppManager.getRnd().nextDouble() * 2 - 1;
		double velY = AppManager.getRnd().nextDouble() * 2 - 1;

		p.setVelocity(new Utility.Vector2D(velX / 3, velY / 3));
		particles.add(p);
	}

}
