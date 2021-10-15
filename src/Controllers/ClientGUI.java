package Controllers;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.scene.shape.Circle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.ControllerSwapper;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.Employee;
import Entities.PhaseType;
import Entities.SystemUser;
import Protocol.Command;
import Protocol.SRMessageFunc;
import Utility.Func;
import Utility.VoidFunc;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * This class contains the main menu of the GUI, where we have all of the menu buttons on the right side.
 * this class acts as the main class, it stays alive unless the user logs off, this class changes only it's center content when loading other classes.
 * this is used as a median between classes, where it saves all of the needed info of the user once he/she logs in.
 * 
 * @author Bshara
 * */
public class ClientGUI implements Initializable {

	private static final String LOG_OUT = "LogOut";

	private static final String ALLOW_EXISTING_TABS_ONLY_F_MAIN_MENU = "allowExistingTabsOnlyFMainMenu";

	@FXML
	private AnchorPane mainAnchor;

	@FXML
	private AnchorPane apMainContent;

	@FXML
	private VBox vbMenu;

	@FXML
	private AnchorPane apBtnLogoMain;

	@FXML
	private AnchorPane apBtnIssueRequest;

	@FXML
	private AnchorPane apBtnMyRequests;

	@FXML
	private AnchorPane apBtnAnalytics;

	@FXML
	private AnchorPane apBtnRequestsTreatment;

	@FXML
	private AnchorPane apBtnRequestsManager;

	@FXML
	private AnchorPane apBtnEmployees;

	@FXML
	private AnchorPane apBtnMessages;

	@FXML
	private AnchorPane apBtnSettings;

	@FXML
	private AnchorPane apHeader;

	@FXML
	private ImageView imgNavigationBarArrow;

	@FXML
	private HBox hbNavigator;

	@FXML
	private Text txtHiName;

	@FXML
	private Circle cNewMessageMark;

	@FXML
	private Circle cNewOrUpdateRequestsMark;

	@FXML
	private VBox DropDownMenu;

	@FXML
	private HBox hboxHelp;

	@FXML
	private HBox hboxSignout;

	@FXML
	private HBox hboxExit;

	@FXML
	private HBox hbDropMenu;

	@FXML
	private AnchorPane apDragger;

	private AnchorPane selectedMenuElement;

	private ArrayList<Node> apList;

	private static Stage stage;

	public static Stage getStage() {
		if (LogInController.username != null)
			return LogInController.getStage();
		return stage;
	}

	private static double xOffset = 0;
	private static double yOffset = 0;

	// TODO: make this dynamic
	public static long myID = 5;

	public static SystemUser systemUser = new SystemUser("userName10", "123", "eee", "Dwayne", "Johnson", "052-2862671",
			true);

	public static long empNumber = -1;
	public static boolean isManager = false;
	public static boolean isSupervisor = true;
	public static boolean isCommitteeMember = true;
	public static boolean isComitteeHead = true;

	private static HashMap<String, VoidFunc> onMenuBtnClickedEvents;
	static {
		onMenuBtnClickedEvents = new HashMap<String, VoidFunc>();
	}

	/**
	 * Adds a function that executes whenever a button from the main menu is clicked
	 * on. This function runs only once and then delete itself from the map.
	 */
	public static void addOnMenuBtnClickedEvent(String key, VoidFunc func) {
		onMenuBtnClickedEvents.remove(key);
		onMenuBtnClickedEvents.put(key, func);
	}

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		txtHiName.setText("Hi, " + systemUser.getFirstName());
		getStage().setX(200);
		getStage().setY(35);

		System.out.println("Init: ClientGUI");
		NavigationBar.imgNavigationBarArrow = imgNavigationBarArrow;
		NavigationBar.navigationBar = hbNavigator;
		NavigationBar.apMainContent = apMainContent;

		apList = new ArrayList<Node>();

		apList.add(apBtnLogoMain);

		apList.add(apBtnIssueRequest);
		apList.add(apBtnMessages);
		apList.add(apBtnMyRequests);
		apList.add(apBtnSettings);
		apList.add(apBtnAnalytics);
		apList.add(apBtnEmployees);
		apList.add(apBtnRequestsTreatment);
		apList.add(apBtnRequestsManager);

		dropDownMenuConfigurations();

		for (Node node : apList) {

			ControllerManager.setMouseHoverPressEffects(node, CommonEffects.MENU_ELEMENT_ON_HOVER,
					CommonEffects.MENU_ELEMENT_IDLE, CommonEffects.MENU_ELEMENT_PRESSED, apList, Cursor.HAND);

		}
		ControllerManager.setMouseHoverPressEffects(apBtnLogoMain, CommonEffects.MENU_ELEMENT_ON_HOVER,
				CommonEffects.MENU_ELEMENT_IDLE, CommonEffects.LOGO_SELECT, apList, Cursor.HAND);
		selectedMenuElement = null;

		commondMenuBehavior(apBtnLogoMain, "Home", FxmlNames.HOME);
		ControllerManager.setEffect(apBtnLogoMain, CommonEffects.LOGO_SELECT);

		apDragger.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = getStage().getX() - event.getScreenX();
				yOffset = getStage().getY() - event.getScreenY();
				apDragger.setCursor(Cursor.CLOSED_HAND);
				// TODO: add the opacity to the settings
				getStage().setOpacity(0.8);

			}
		});
		apDragger.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				getStage().setX(event.getScreenX() + xOffset);
				getStage().setY(event.getScreenY() + yOffset);
			}
		});
		apDragger.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				apDragger.setCursor(Cursor.OPEN_HAND);
				getStage().setOpacity(1);
			}
		});

		apDragger.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				getStage().setIconified(true);
			}
		});

		Client.addMessageRecievedFromServer("NewMessageReceived", srMsg -> {
			if (srMsg.getCommand() == Command.receivedNewMessage) {
				String username = (String) srMsg.getAttachedData()[0];

				// If this user got this message then
				if (username.toLowerCase().compareTo(systemUser.getUserName().toLowerCase()) == 0) {
					addNewMessageMark();
				}
			}
		});

		Client.addMessageRecievedFromServer("receivedNewOrUpdateRequests", srMsg -> {
			if (srMsg.getCommand() == Command.receivedNewOrUpdateRequests) {
				long empNum = (long) srMsg.getAttachedData()[0];

				// If this user got this message then
				if (empNum == empNumber) {
					addNewOrUpdateRequestsMark();
				}
			}
		});

		Client.addServerConnectionClosedEvent("connClosed2", () -> {

			serverErrorLogOut();

		});

		Client.addServerExceptionEvent("connClosed2", () -> {

			serverErrorLogOut();
		});

		removeNewOrUpdateRequestsMark();

		removeNewMessageMark();

		allowMenuButtonsByPermissions();

		shortcuts();
		SettingsController.getInstance().setShortcutsOnStart();

	}

	private void serverErrorLogOut() {

		ControllerManager.showErrorMessage("Connection Error", "Connection Error",
				"The connection is from the server is down!\nExiting back to the log in page.", () -> {
					Parent s = ControllerSwapper.loadContentWithLoader(FxmlNames.LOG_IN);
					LogInController.getStage().setScene(new Scene(s));
					LogInController.getStage().show();
				});

	}

	private void shortcuts() {

		SettingsController.IssueRequest = () -> {
			commondMenuBehavior(apBtnIssueRequest, "Issue Request", FxmlNames.ISSUE_REQUEST);
			lightBtnOnAndOff();
			ControllerManager.setEffect(apBtnIssueRequest, CommonEffects.MENU_ELEMENT_ON_HOVER);

		};
	
		SettingsController.MessShortcut = () -> {

			commondMenuBehavior(apBtnMessages, "Messages", FxmlNames.MESSAGES);
			lightBtnOnAndOff();
			ControllerManager.setEffect(apBtnMessages, CommonEffects.MENU_ELEMENT_ON_HOVER);

		};
		SettingsController.MyReqShortcut = () -> {
			commondMenuBehavior(apBtnMyRequests, "My Requests", FxmlNames.REQUESTS_LIST);
			lightBtnOnAndOff();
			ControllerManager.setEffect(apBtnMyRequests, CommonEffects.MENU_ELEMENT_ON_HOVER);

		};
		SettingsController.SignOutShort = () -> {
			ControllerManager.showYesNoMessage("Exit", "Exit", "Are you sure you want to sign out?", () -> {

				logOut(() -> {
					Parent s = ControllerSwapper.loadContentWithLoader(FxmlNames.LOG_IN);
					LogInController.getStage().setScene(new Scene(s));
					LogInController.getStage().show();
				});

			}, null);

		};
		SettingsController.EmpShortcut = () -> {

			if (isManager) {
				commondMenuBehavior(apBtnEmployees, "Employees", FxmlNames.EMPLOYEES);
				lightBtnOnAndOff();
				ControllerManager.setEffect(apBtnEmployees, CommonEffects.MENU_ELEMENT_ON_HOVER);
			}

		};
		SettingsController.AnalyticsShortcut = () -> {

			if (isManager) {
				commondMenuBehavior(apBtnAnalytics, "Analytics", FxmlNames.ANALYTICS);
				lightBtnOnAndOff();
				ControllerManager.setEffect(apBtnAnalytics, CommonEffects.MENU_ELEMENT_ON_HOVER);
			}

		};
		SettingsController.ReqTreatShortcut = () -> {
			if (empNumber != -1) {
				commondMenuBehavior(apBtnMyRequests, "Requests Treatment", FxmlNames.LIST_OF_REQUESTS_FOR_TREATMENT);
				lightBtnOnAndOff();
				ControllerManager.setEffect(apBtnMyRequests, CommonEffects.MENU_ELEMENT_ON_HOVER);
			}

		};
		SettingsController.GOBACKFUNC = () -> {

			NavigationBar.back(true);

		};

	}

	private void lightBtnOnAndOff() {

		apList = new ArrayList<Node>();

		apList.add(apBtnLogoMain);

		apList.add(apBtnIssueRequest);
		apList.add(apBtnMessages);
		apList.add(apBtnMyRequests);
		apList.add(apBtnSettings);
		apList.add(apBtnAnalytics);
		apList.add(apBtnEmployees);
		apList.add(apBtnRequestsTreatment);

		for (Node node : apList) {

			ControllerManager.setEffect(node, CommonEffects.MENU_ELEMENT_IDLE);
		}

	}

	private void dropDownMenuConfigurations() {

		DropDownMenu.setVisible(false);

		hbDropMenu.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbDropMenu, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbDropMenu, CommonEffects.REQUESTS_TABLE_ELEMENT_BLACK,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbDropMenu.setOnMouseClicked(event -> {
			DropDownMenu.setVisible(true);
		});
		DropDownMenu.setOnMouseExited(event -> {

			DropDownMenu.setVisible(false);
		});

		hboxHelp.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hboxHelp, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hboxHelp, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hboxHelp.setOnMousePressed(event -> {
			NavigationBar.next("List Of Employees", FxmlNames.LIST_OF_EMPLOYEES_SIMPLE);
		});

		hboxSignout.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hboxSignout, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hboxSignout, CommonEffects.REQUEST_DETAILS_BUTTON_RED,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hboxSignout.setOnMousePressed(event -> {
			ControllerManager.showYesNoMessage("Exit", "Exit", "Are you sure you want to sign out?", () -> {

				logOut(() -> {
					Parent s = ControllerSwapper.loadContentWithLoader(FxmlNames.LOG_IN);
					LogInController.getStage().setScene(new Scene(s));
					LogInController.getStage().show();
				});

			}, null);
		});

		hboxExit.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hboxExit, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hboxExit, CommonEffects.REQUEST_DETAILS_BUTTON_RED,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hboxExit.setOnMousePressed(event -> {
			ControllerManager.showYesNoMessage("Exit", "Exit", "Are you sure you want to exit?", () -> {

				logOut(() -> {
					try {
						Client.getInstance().closeConnection();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.exit(1);
				});

			}, null);

		});
	}

	private void logOut(VoidFunc f) {

		Client.getInstance().requestWithListener(Command.logOut, srMsg -> {
			if (srMsg.getCommand() == Command.logOut) {

				boolean logOutSucces = (boolean) srMsg.getAttachedData()[0];
				if (logOutSucces && f != null) {
					f.call();
				} else if (!logOutSucces) {
					System.out.println(
							"Error, how did this happend? looks like we are getting wrong data from the system user\n username = "
									+ systemUser.getUserName());
				}

				Client.removeMessageRecievedFromServer(LOG_OUT);

			}
		}, LOG_OUT, systemUser.getUserName());

	}

	private void addNewOrUpdateRequestsMark() {
		cNewOrUpdateRequestsMark.setOpacity(1);
	}

	private void removeNewOrUpdateRequestsMark() {
		cNewOrUpdateRequestsMark.setOpacity(0);

	}

	private void addNewMessageMark() {
		cNewMessageMark.setOpacity(1);
	}

	private void removeNewMessageMark() {
		cNewMessageMark.setOpacity(0);
	}

	private void allowMenuButtonsByPermissions() {

		SRMessageFunc allowExistingTabsOnlyF = srMsg -> {

			if (srMsg.getCommand() == Command.getPermissionsData) {

				// get data from server
				boolean isManager = (boolean) srMsg.getAttachedData()[0];
				ClientGUI.isManager = isManager;
				boolean hasAtleastOnePhaseToManage = (boolean) srMsg.getAttachedData()[1];

				ClientGUI.empNumber = (long) srMsg.getAttachedData()[2];
				ClientGUI.isSupervisor = (boolean) srMsg.getAttachedData()[3];
				ClientGUI.isCommitteeMember = (boolean) srMsg.getAttachedData()[4];
				ClientGUI.isComitteeHead = (boolean) srMsg.getAttachedData()[5];

				ArrayList<Node> newNodesForMenu = new ArrayList<Node>();

				for (Node node : vbMenu.getChildren()) {
					if (node instanceof AnchorPane) {
						if (((AnchorPane) node).getChildren().size() == 2) {
							Node textNode = ((AnchorPane) node).getChildren().get(1);
							String text = ((Label) textNode).getText();
							switch (text) {
							case "Issue a Request":
								// Always add
								newNodesForMenu.add(node);

								break;
							case "My Requests":
								// Always add
								newNodesForMenu.add(node);

								break;
							case "Requests  Treatment":
								// Add only if he has a phase to manage
								if (hasAtleastOnePhaseToManage) {
									newNodesForMenu.add(node);
								}

								break;
							case "Analytics":
								// Add only if the user is the manager
								if (isManager) {
									newNodesForMenu.add(node);
								}

								break;
							case "Employees":
								// Add only if the user is the manager
								if (isManager) {
									newNodesForMenu.add(node);
								}

								break;
							case "Messages":
								newNodesForMenu.add(node);

								break;
							case "Settings":
								newNodesForMenu.add(node);

								break;
							case "Requests":
								// Add only if the user is the manager
								if (isManager) {
									newNodesForMenu.add(node);
								}

								break;
							default:
								System.err.println("Error, case not defined! [allowMenuButtonsByPermissions] at "
										+ getClass().getName());
								break;
							}
						} else {
							// Add the home button to all users
							newNodesForMenu.add(node);
						}
					}
				}

				vbMenu.getChildren().clear();
				vbMenu.getChildren().setAll(FXCollections.observableArrayList(newNodesForMenu));

				// Auto delete after first use
				Client.removeMessageRecievedFromServer(ALLOW_EXISTING_TABS_ONLY_F_MAIN_MENU);
			}
		};

		Client.getInstance().requestWithListener(Command.getPermissionsData, allowExistingTabsOnlyF,
				ALLOW_EXISTING_TABS_ONLY_F_MAIN_MENU, ClientGUI.systemUser.getUserName());

	}

	private int index = 0;
	private int size = BlendMode.values().length;

	@FXML
	void onIssueRequestPress(MouseEvent event) {
		commondMenuBehavior(apBtnIssueRequest, "Issue Request", FxmlNames.ISSUE_REQUEST);

	}

	@FXML
	void onMessagesPress(MouseEvent event) {
		removeNewMessageMark();
		commondMenuBehavior(apBtnMessages, "Messages", FxmlNames.MESSAGES);
	}

	@FXML
	void onMyRequestsPress(MouseEvent event) {
		commondMenuBehavior(apBtnMyRequests, "My Requests", FxmlNames.REQUESTS_LIST);
	}

	@FXML
	void onSettingsPress(MouseEvent event) {
		commondMenuBehavior(apBtnSettings, "Settings", FxmlNames.SETTINGS);

	}

	@FXML
	void onAnalyticsPress(MouseEvent event) {
		commondMenuBehavior(apBtnAnalytics, "Analytics", FxmlNames.ANALYTICS);
	}

	@FXML
	void onEmployeesPress(MouseEvent event) {
		commondMenuBehavior(apBtnEmployees, "Employees", FxmlNames.EMPLOYEES);
	}

	@FXML
	void onLogoMainPress(MouseEvent event) {
		commondMenuBehavior(apBtnLogoMain, "Home", FxmlNames.HOME);
	}

	@FXML
	void onRequestTreatmentPress(MouseEvent event) {
		removeNewOrUpdateRequestsMark();
		commondMenuBehavior(apBtnMyRequests, "Requests Treatment", FxmlNames.LIST_OF_REQUESTS_FOR_TREATMENT);

	}

	@FXML
	void onRequestPress(MouseEvent event) {
		commondMenuBehavior(apBtnRequestsManager, "Requests", FxmlNames.LIST_OF_REQUESTS_FOR_MANAGER);

	}

	private void commondMenuBehavior(AnchorPane ap, String pageName, String fxmlName) {

		for (Entry<String, VoidFunc> func : onMenuBtnClickedEvents.entrySet()) {
			if (func.getValue() != null)
				func.getValue().call();
			// Self delete after running once
			onMenuBtnClickedEvents.remove(func.getKey());
		}
		selectedMenuElement = ap;
		NavigationBar.clear();
		NavigationBar.next(pageName, fxmlName);
	}

	@FXML
	void MenuExit(MouseEvent event) {

	}

	@FXML
	void MenuHelp(MouseEvent event) {

	}

	@FXML
	void MenuSignOut(MouseEvent event) {

	}

}
