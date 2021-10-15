package Controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Protocol.Command;
import Utility.AppManager;
import Utility.Func;
import Utility.ShortcutManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


/**
 * This page provides the user the ability to select and configure the shortcuts of the application, it also provides the user the ability
 * to disable graphics if he wishes to.
 * 
 * @author Bshara
 * */
public class SettingsController implements Initializable {
	private static final String GET_MY_SHORTCUTS = "getMyShortcuts";
	private Stage stage;
	private static final String UpdateShortcuts = "UpdateShortcuts";

	@FXML
	private AnchorPane anchorpane;

	public static SettingsController instance;
	static {

		instance = new SettingsController();

	}

	public static SettingsController getInstance() {

		return instance;

	}

	public enum KeyComb {
		Unassigned, R, N, M, O, A, T, B, BACK_SPACE;
	}

	@FXML
	private ComboBox<KeyComb> IssReqShortcut;

	@FXML
	private ComboBox<KeyComb> ONotifShortcut;

	@FXML
	private ComboBox<KeyComb> OMessShortcut;

	@FXML
	private ComboBox<KeyComb> OMyReqShortcut;

	@FXML
	private ComboBox<KeyComb> SignOutShortcut;

	@FXML
	private ComboBox<KeyComb> OpenEmpShortcut;

	@FXML
	private ComboBox<KeyComb> OpenAnalyticsShortcut;

	@FXML
	private ComboBox<KeyComb> OpReqTreatShortcut;
	@FXML
	private ComboBox<KeyComb> gobackcomb;

	@FXML
	private HBox hbApplyChanges;

	@FXML
	private Canvas canvasRight;

	@FXML
	private Canvas canvasLeft;

	@FXML
	private CheckBox cbDisableSideGrapgics;

	@FXML
	private Button btnDebug;

	@FXML
	private TextField tfDebug;

	public static Func IssueRequest;
	public static Func NotifShortcut;
	public static Func MessShortcut;
	public static Func MyReqShortcut;
	public static Func SignOutShort;
	public static Func EmpShortcut;
	public static Func AnalyticsShortcut;
	public static Func ReqTreatShortcut;
	public static Func GOBACKFUNC;

	private static boolean disableGraphics = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		cbDisableSideGrapgics.setSelected(disableGraphics);
		// Apply the effects for the canvas
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);

		hbApplyChanges.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbApplyChanges, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbApplyChanges, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		Client.getInstance().requestWithListener(Command.getMyShortcuts, srMsg -> {

			if (srMsg.getCommand() == Command.getMyShortcuts) {

				ArrayList<String> strs = (ArrayList<String>) srMsg.getAttachedData()[0];

				gobackcomb.setItems(FXCollections.observableArrayList(KeyComb.values()));
				gobackcomb.setValue(KeyComb.valueOf(strs.get(0)));

				IssReqShortcut.setItems(FXCollections.observableArrayList(KeyComb.values()));
				IssReqShortcut.setValue(KeyComb.valueOf(strs.get(2)));

				OMessShortcut.setItems(FXCollections.observableArrayList(KeyComb.values()));
				OMessShortcut.setValue(KeyComb.valueOf(strs.get(4)));

				OMyReqShortcut.setItems(FXCollections.observableArrayList(KeyComb.values()));
				OMyReqShortcut.setValue(KeyComb.valueOf(strs.get(6)));

				ONotifShortcut.setItems(FXCollections.observableArrayList(KeyComb.values()));
				ONotifShortcut.setValue(KeyComb.valueOf(strs.get(8)));

				OpenAnalyticsShortcut.setItems(FXCollections.observableArrayList(KeyComb.values()));
				OpenAnalyticsShortcut.setValue(KeyComb.valueOf(strs.get(10)));

				OpenEmpShortcut.setItems(FXCollections.observableArrayList(KeyComb.values()));
				OpenEmpShortcut.setValue(KeyComb.valueOf(strs.get(12)));

				OpReqTreatShortcut.setItems(FXCollections.observableArrayList(KeyComb.values()));
				OpReqTreatShortcut.setValue(KeyComb.valueOf(strs.get(14)));

				SignOutShortcut.setItems(FXCollections.observableArrayList(KeyComb.values()));
				SignOutShortcut.setValue(KeyComb.valueOf(strs.get(16)));

				Client.removeMessageRecievedFromServer(GET_MY_SHORTCUTS);
			}

		}, GET_MY_SHORTCUTS, ClientGUI.systemUser.getUserName());

		hbApplyChanges.setOnMousePressed(event -> {

			apply();

		});

		cbDisableSideGrapgics.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				disableGraphics = newValue;
				if (newValue) {
					AppManager.timeline.stop();
				} else {
					AppManager.timeline.play();
				}

			}
		});

	}

	public void apply() {

		setBehavior(IssReqShortcut.getValue(), IssueRequest);
		setBehavior(ONotifShortcut.getValue(), NotifShortcut);
		setBehavior(OMessShortcut.getValue(), MessShortcut);
		setBehavior(OMyReqShortcut.getValue(), MyReqShortcut);
		setBehavior(SignOutShortcut.getValue(), SignOutShort);
		setBehavior(OpenEmpShortcut.getValue(), EmpShortcut);
		setBehavior(OpenAnalyticsShortcut.getValue(), AnalyticsShortcut);
		setBehavior(OpReqTreatShortcut.getValue(), ReqTreatShortcut);
		setBehavior(gobackcomb.getValue(), GOBACKFUNC);

		Client.getInstance().request(Command.UpdateShortcuts, "IssReqShortcut", IssReqShortcut.getValue().name(),
				"ONotifShortcut", ONotifShortcut.getValue().name(), "OMessShortcut", OMessShortcut.getValue().name(),
				"OMyReqShortcut", OMyReqShortcut.getValue().name(), "SignOutShortcut",
				SignOutShortcut.getValue().name(), "OpenEmpShortcut", OpenEmpShortcut.getValue().name(),
				"OpenAnalyticsShortcut", OpenAnalyticsShortcut.getValue().name(), "OpReqTreatShortcut",
				OpReqTreatShortcut.getValue().name(), "gobackcomb", gobackcomb.getValue().name(),
				ClientGUI.systemUser.getUserName());

		Client.addMessageRecievedFromServer(UpdateShortcuts, srMsg -> {

			int contChange = (int) srMsg.getAttachedData()[0];

			System.out.println(contChange);
			if (contChange != 9) {
				ControllerManager.showErrorMessage("Error", "Update Shortcuts Erorr", "Please try again", null);
			} else {

				ControllerManager.showInformationMessage("Success", "Update Shortcuts successfully", "successfully",
						null);
				Client.removeMessageRecievedFromServer(UpdateShortcuts);
			}
		});

	}

	public void setShortcutsOnStart() {
		setBehavior(KeyComb.R, IssueRequest);
		setBehavior(KeyComb.N, NotifShortcut);
		setBehavior(KeyComb.M, MessShortcut);
		setBehavior(KeyComb.Unassigned, MyReqShortcut);
		setBehavior(KeyComb.O, SignOutShort);
		setBehavior(KeyComb.Unassigned, EmpShortcut);
		setBehavior(KeyComb.A, AnalyticsShortcut);
		setBehavior(KeyComb.T, ReqTreatShortcut);
		setBehavior(KeyComb.BACK_SPACE, GOBACKFUNC);

	}

	private void setBehavior(KeyComb keyComb, Func f) {

		switch (keyComb) {
		case R:

			ShortcutManager.CTRL_R = () -> {

				f.execute();
			};
			break;

		case N:
			ShortcutManager.CTRL_N = () -> {
				f.execute();

			};
			break;

		case M:
			ShortcutManager.CTRL_M = () -> {
				f.execute();

			};
			break;
		case O:
			ShortcutManager.CTRL_O = () -> {
				f.execute();

			};
			break;
		case A:
			ShortcutManager.CTRL_A = () -> {
				f.execute();

			};
			break;
		case T:
			ShortcutManager.CTRL_T = () -> {
				f.execute();

			};
			break;

		case B:
			ShortcutManager.CTRL_B = () -> {
				f.execute();

			};
		case BACK_SPACE:
			ShortcutManager.CTRL_BACK_SPACE = () -> {
				f.execute();

			};
			break;
		default:
			break;
		}
	}

	@FXML
	void onDebug(MouseEvent event) {
		ArrayList<String> sss = new ArrayList<String>();
		sss.add(AppManager.getRnd().nextInt(100) + "");
		System.out.println("Debugging with " + sss.get(0));
		Client.getInstance().request(Command.debug_simulateBigCalculations, sss);

	}

}