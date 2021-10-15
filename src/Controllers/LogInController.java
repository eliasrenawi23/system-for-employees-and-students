package Controllers;

import java.applet.Applet;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.ControllerSwapper;
import Controllers.Logic.FxmlNames;
import Entities.SystemUser;
import Protocol.Command;
import Utility.ShortcutManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


/**
 * This class provides the user with the ability to connect to the server by given it the ip address of the server
 * */
public class LogInController extends Application implements Initializable {

	private static final String CHECK_LOG_IN = "CheckLogIn";

	public static long empNumber = -1;
	public static boolean isManager = false;
	public static boolean isSupervisor = false;
	public static boolean isCommitteeMember = false;
	public static boolean isCommitteeHead = false;
	public static String username = null;
	private int numOfLogintimes = 0;

	private static Stage stage;

	@FXML
	private HBox apMainAnch;

	@FXML
	private VBox apHeader;

	@FXML
	private ImageView exitbtn;

	@FXML
	private TextField tfIpAddr;

	@FXML
	private TextField tfPort;

	@FXML
	private Button connectBtn;

	@FXML
	private Text txtConnectionStatus;

	@FXML
	private Circle cConnectionStatus;

	@FXML
	private TextField usernamefield;

	@FXML
	private PasswordField passwordfield;

	@FXML
	private HBox loginbtn;

	@FXML
	private Text iFrogotMyPassword;

	private ShortcutManager sm;

	private static boolean connectedToServer;

	@FXML
	void exitbtnclick(MouseEvent event) {

		System.exit(1);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		if (connectedToServer) {
			txtConnectionStatus.setText("Connection Status: Connected");
			cConnectionStatus.setFill(Color.GREEN);
			connectBtn.setText("Disconnect");
		} else {

			txtConnectionStatus.setText("Connection Status: Disconnected");
			cConnectionStatus.setFill(Color.RED);
		}

		// TODO Auto-generated method stub
		exitbtn.setCursor(Cursor.HAND);
		ControllerManager.setEffect(exitbtn, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(exitbtn, CommonEffects.REQUEST_DETAILS_BUTTON_RED,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		connectBtn.setOnMouseClicked(event -> {

			if (connectedToServer) {

				try {
					Client.getInstance().closeConnection();

					connectBtn.setText("Connect");
				} catch (IOException e) {

				}

			} else {
				boolean ipFill = ControllerManager.areAllStringsNotEmpty(tfIpAddr.getText(), tfPort.getText());
				if (!ipFill) {
					connectedToServer = false;
					ControllerManager.showErrorMessage("Error", "IP Address or Port Fields Are Missing",
							"Please fill the missing fields", null);

				} else {

					txtConnectionStatus.setText("Connection Status: Connecting...");
					cConnectionStatus.setFill(Color.YELLOW);
					Client.getInstance().initialize(tfIpAddr.getText(), Integer.parseInt(tfPort.getText()));

				}
			}

		});

		Client.connExceptionFromClient = () -> {
			Platform.runLater(() -> {
				connectedToServer = false;

				txtConnectionStatus.setText("Connection Status: Disconnected");
				cConnectionStatus.setFill(Color.RED);

				ControllerManager.showErrorMessage("Error", "Connection Error", "Connection could not be established!",
						null);
			});

		};

		Client.addServerConnectionEstablishedEvent("ConnEstab", () -> {

			connectedToServer = true;

			txtConnectionStatus.setText("Connection Status: Connected");
			cConnectionStatus.setFill(Color.GREEN);
			connectBtn.setText("Disconnect");

		});

		Client.addServerConnectionClosedEvent("connClosed", () -> {

			connectedToServer = false;

			txtConnectionStatus.setText("Connection Status: Disconnected");
			cConnectionStatus.setFill(Color.RED);
		});

		Client.addServerExceptionEvent("connClosed", () -> {

			connectedToServer = false;

			txtConnectionStatus.setText("Connection Status: Disconnected");
			cConnectionStatus.setFill(Color.RED);
		});

		loginbtn.setOnMouseClicked(event -> {
			if (!connectedToServer) {
				ControllerManager.showErrorMessage("Error", "No connection to server",
						"Please make sure that you are connected to the server before logging in!", null);
				return;
			}

			if (numOfLogintimes < 3) {
				numOfLogintimes++;
				username = usernamefield.getText();
				String password = passwordfield.getText();

				boolean isAllFilled = ControllerManager.areAllStringsNotEmpty(username, password);
				if (isAllFilled) {

					Client.getInstance().request(Command.checkLogIn, username, password);

				} else {
					ControllerManager.showInformationMessage("Error", "Username or Password Fields Are Missing",
							"Please fill the missing fields", null);
				}
			} else {
				Client.removeMessageRecievedFromServer(CHECK_LOG_IN);
				ControllerManager.showErrorMessage("Error",
						"You Have Entered WRONG Username or Password " + numOfLogintimes + " times  ",
						" No MORE TRIES FOR YOU ... BYE BYE", null);
				System.exit(1);

			}
		});

		Client.addMessageRecievedFromServer(CHECK_LOG_IN, srMsg -> {

			if (connectedToServer == false) {
				ControllerManager.showErrorMessage("Error", "Connect to server first",
						"You have to connect to server first in order to log in!", null);
			}

			if (srMsg.getCommand() == Command.checkLogIn) {

				boolean canLogIn = (boolean) srMsg.getAttachedData()[0];
				boolean isAlreadyLoggedIn = (boolean) srMsg.getAttachedData()[1];
				if (canLogIn && !isAlreadyLoggedIn) {

					ClientGUI.systemUser = (SystemUser) srMsg.getAttachedData()[2];
					numOfLogintimes = 0;
					showClientGUI();
				}else {
					if (!canLogIn) {
						ControllerManager.showErrorMessage("Error", "The username or password is incorrect",
								"Please Enter the correct username or password", null);
					} else if (isAlreadyLoggedIn){
						ControllerManager.showErrorMessage("Error", "You are already logged in",
								"It seems that you are already logged in to this account from another computer!", null);
					}
				}
				

			}

		});
	}

	private void showClientGUI() {

		Parent s = ControllerSwapper.loadContentWithLoader("GlobalMenusGUI.fxml");
		LogInController.stage.setScene(new Scene(s));
		LogInController.stage.show();
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.initStyle(StageStyle.UNDECORATED);

		FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlNames.LOG_IN));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {

			e.printStackTrace();
		}
		sm = new ShortcutManager(stage);

		stage.setScene(new Scene(root));
		stage.setTitle("ICM System");
		stage.show();

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				System.exit(1);
			}
		});
		LogInController.stage = stage;

	}

	public static Stage getStage() {
		return stage;
	}

	public static void main(String[] args) {
		launch();
	}

}
