package Controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.ControllerSwapper;
import Controllers.Logic.FxmlNames;
import Protocol.Command;
import Protocol.MsgReturnType;
import Utility.AppManager;
import Utility.DateUtil;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import Entities.*;


/**
 * This class provides the user with the necessary fields to issue a request, the class checks if all of the fields are valid and then issues
 * the request by sending a message via the client, the user can also attach files with the request.
 * 
 * @author Bshara
 * */
public class IssueRequestController implements Initializable {

	public static final int MB_4 = 4194303;

	private static final String TIME_OF_ISSUE_REQUEST = "timeOfIssueRequest";

	@FXML
	private Text txtCurrentDate;

	@FXML
	private Text txtNumberOfAttachedFiles;

	@FXML
	private ComboBox<String> cbInformationSystem;

	@FXML
	private TextArea taRequestDescription;

	@FXML
	private TextArea taDescriptionOfCurrentState;

	@FXML
	private TextArea taDescriptionOfRequestedChange;

	@FXML
	private TextArea taComments;

	@FXML
	private HBox hbBrowseFiles;

	@FXML
	private HBox hbIssueRequest;

	@FXML
	private Canvas canvasRight;

	@FXML
	private Canvas canvasLeft;

	private Timeline timeline;

	private static ArrayList<String> filesPaths;
	static {
		filesPaths = new ArrayList<String>();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		ClientGUI.addOnMenuBtnClickedEvent(getClass().getName() + "3232145125", () -> {
			System.out.println("Finalize: IssueRequestController");

			Client.removeMessageRecievedFromServer(TIME_OF_ISSUE_REQUEST);
		});

		// Apply the effects for the canvas
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);

		hbIssueRequest.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbIssueRequest, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbIssueRequest, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbBrowseFiles.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbBrowseFiles, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbBrowseFiles, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		if (timeline != null) {
			timeline.stop();
		}
		timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			txtCurrentDate.setText(dtf.format(now));

		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

		final FileChooser fileChooser = new FileChooser();

		txtNumberOfAttachedFiles.setText("(0 files)");

		hbBrowseFiles.setOnMousePressed(event -> {
			List<java.io.File> list = fileChooser.showOpenMultipleDialog(ClientGUI.getStage());
			filesPaths.clear();
			if (list != null) {

				for (java.io.File file : list) {
					String path = file.getPath();
					path = path.replace("\\", "/");
					filesPaths.add(path);
				}
				txtNumberOfAttachedFiles.setText("(" + filesPaths.size() + " files)");

			} else {
				txtNumberOfAttachedFiles.setText("(0 files)");
			}
		});

		// Set the behavior of the issue request button.
		hbIssueRequest.setOnMousePressed(event -> {

			String comments = taComments.getText();
			String reqDesc = taRequestDescription.getText();
			String descReqChange = taDescriptionOfRequestedChange.getText();
			String descCurrState = taDescriptionOfCurrentState.getText();
			String relateInfoSys = cbInformationSystem.getValue().toString();

			boolean areAllFieldsFilled = ControllerManager.areAllStringsNotEmpty(reqDesc, descReqChange, descCurrState);

			if (areAllFieldsFilled) {
				long reqestID = 9996; // TODO: if id = -1, the server should know that he has to find a fitting id

				ChangeRequest changeRequest = new ChangeRequest(reqestID, ClientGUI.systemUser.getUserName(),
						DateUtil.now(), DateUtil.NA, DateUtil.NA, comments, reqDesc, descReqChange, descCurrState,
						relateInfoSys);

				if (filesPaths.size() == 0) {
					Client.getInstance().request(Command.insertRequest, changeRequest);

				} else {
					ArrayList<File> files = new ArrayList<File>();
					for (String path : filesPaths) {

						File file = new File(0, reqestID, path, "");
						file.loadBytes();
						file.autoSetTypeAndNameFromPath();
						files.add(file);
						if (file.getStoredBytesSize() > MB_4) {
							ControllerManager.showInformationMessage("Error", "The attached file is too large",
									"Please attach files that are 4MB and below", null);
							return;
						}
					}
					Client.getInstance().request(Command.insertRequestWithFiles, changeRequest, files);

				}
			} else {
				ControllerManager.showInformationMessage("Error", "Required Fields Are Missing",
						"Please fill the missing fields", null);
			}

		});

		// Set the behavior of the controller after receiving a message back from the
		// server for
		// issuing a request.
		Client.addMessageRecievedFromServer("IssueRequestMessageReceieved", srMsg -> {
			if (srMsg.getCommand() == Command.insertRequest) {

				if (srMsg.getReturnType() == MsgReturnType.Success) {
					ControllerManager.showInformationMessage("Issue Request", "Success",
							"The request has been successfully issued!", null);
				} else if (srMsg.getReturnType() == MsgReturnType.Failure) {
					System.out.println("insertRequest");

					if ((String) srMsg.getAttachedData()[0] == null) {
						ControllerManager.showInformationMessage("Issue Request", "Failure", "Something went wrong!",
								null);

					} else {
						ControllerManager.showInformationMessage("Issue Request", "Failure",
								(String) srMsg.getAttachedData()[0], null);
					}
				}

			} else if (srMsg.getCommand() == Command.insertRequestWithFiles) {
				if (srMsg.getReturnType() == MsgReturnType.Success) {
					ControllerManager.showInformationMessage("Issue Request", "Success",
							"The request has been successfully issued!", null);
					resetFields();

				} else if (srMsg.getReturnType() == MsgReturnType.Failure) {
					System.out.println("insertRequestWithFiles");
					if ((String) srMsg.getAttachedData()[0] == null) {
						ControllerManager.showInformationMessage("Issue Request", "Failure", "Something went wrong!",
								null);

					} else {
						ControllerManager.showInformationMessage("Issue Request", "Failure",
								(String) srMsg.getAttachedData()[0], null);
					}
				}
			}

		});

		cbInformationSystem.setItems(FXCollections.observableArrayList("Moodle", "Information System", "Library System",
				"Classroom Computers", "Braude Website", "Labs and Computers Farms"));
		cbInformationSystem.setValue("Information System");

	}

	private void resetFields() {
		taComments.setText("");
		taDescriptionOfCurrentState.setText("");
		taDescriptionOfRequestedChange.setText("");
		taRequestDescription.setText("");
		txtNumberOfAttachedFiles.setText("(0 files)");
	}

}
