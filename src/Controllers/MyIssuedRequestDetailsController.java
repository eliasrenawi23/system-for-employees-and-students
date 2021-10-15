package Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.ChangeRequest;
import Protocol.Command;
import Utility.DateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * This class provides the user of all of the requests that he issued. the user can click on a table of the request and select a row
 * which represents a request, by double clicking on it the user can see the request in a different page and view it's details and see it's attached files.
 * 
 * @author Bshara
 * */
public class MyIssuedRequestDetailsController implements Initializable {
	private static final String REQUEST_OWNER_DECLINE = "requestOwnerDecline";
	private static final String REQUEST_OWNER_CONFIRM = "requestOwnerConfirm";
	private static final String GET_FULL_NAME_OF_SYSTEM_USER = "GetFullNameOfSystemUser";
	@FXML
	private Canvas canvasRight;

	@FXML
	private HBox hbInformationContainer;

	@FXML
	private Label lblRequestID;

	@FXML
	private Label lblStatus;

	@FXML
	private Label lblRelatedInfoSystem;

	@FXML
	private Label lblIssueDate;

	@FXML
	private Label lblRequestDescription;

	@FXML
	private Text lblDescriptionOfCurrentState;

	@FXML
	private Text lblDescriptionOfRequestedChange;

	@FXML
	private Text lblComments;

	@FXML
	private HBox hbAttachedFilesList;

	@FXML
	private HBox hbConfirm;

	@FXML
	private HBox hbDecline;

	@FXML
	private Canvas canvasLeft;

	@FXML
	private VBox vbRequestEndedConfirmation;

	private ObservableList<Node> nodes;
	private ChangeRequest changeRequest;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);
		vbRequestEndedConfirmation.setVisible(false);

		nodes = FXCollections.observableArrayList(hbInformationContainer.getChildren());
		changeRequest = ListOfRequestsController.lastSelectedRequest;

		hbConfirm.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbConfirm, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbConfirm, CommonEffects.REQUEST_DETAILS_BUTTON_GREEN,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		hbConfirm.setOnMousePressed(event -> {
			ControllerManager.showYesNoMessage("Confirmation", "Confirm Request",
					"Are you sure you want to confirm the execution of this request?", () -> {
						Client.getInstance().requestWithListener(Command.requestOwnerConfirm, srMsg -> {

							if (srMsg.getCommand() == Command.requestOwnerConfirm) {

								ControllerManager.showInformationMessage("Success", "Request confirmed",
										"The request has been confirmed!", null);

								Client.removeMessageRecievedFromServer(REQUEST_OWNER_CONFIRM);

								NavigationBar.reload();

							}

						}, REQUEST_OWNER_CONFIRM, changeRequest.getRequestID());

					}, null);
		});

		hbDecline.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbDecline, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbDecline, CommonEffects.REQUEST_DETAILS_BUTTON_RED,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		hbDecline.setOnMousePressed(event -> {

			ControllerManager.showYesNoMessage("Rejection", "Reject Request",
					"Are you sure you want to reject the execution of this request?", () -> {
						Client.getInstance().requestWithListener(Command.requestOwnerDecline, srMsg -> {

							if (srMsg.getCommand() == Command.requestOwnerDecline) {

								ControllerManager.showInformationMessage("Success", "Request declined",
										"The request has been declined!", null);

								Client.removeMessageRecievedFromServer(REQUEST_OWNER_DECLINE);

								NavigationBar.reload();

							}

						}, REQUEST_OWNER_DECLINE, changeRequest.getRequestID());
					}, null);
		});

		hbAttachedFilesList.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbAttachedFilesList, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbAttachedFilesList, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		hbAttachedFilesList.setOnMousePressed(event -> {

			// Set the request id for the files table
			filesListController.requestId = ListOfRequestsController.lastSelectedRequest.getRequestID();
			NavigationBar.next("Files List", FxmlNames.FILES_LIST);

		});

		loadFields();

		checkIfRequestHasEnded();

	}

	private void checkIfRequestHasEnded() {

		Client.getInstance().requestWithListener(Command.isMyRequestWaitingForMyConfirmation, srMsg -> {

			if (srMsg.getCommand() == Command.isMyRequestWaitingForMyConfirmation) {

				boolean correct = (boolean) srMsg.getAttachedData()[0];

				if (correct) {
					vbRequestEndedConfirmation.setVisible(true);
				} else {
					vbRequestEndedConfirmation.setVisible(false);
				}

				Client.removeMessageRecievedFromServer(REQUEST_OWNER_DECLINE);
			}

		}, REQUEST_OWNER_DECLINE, changeRequest.getRequestID());
	}

	private void loadFields() {
		if (changeRequest != null) {

			lblRequestID.setText(changeRequest.getRequestID() + "");
			lblComments.setText(changeRequest.getCommentsLT());
			lblDescriptionOfCurrentState.setText(changeRequest.getDescriptionOfCurrentStateLT());
			lblIssueDate.setText(DateUtil.toString(changeRequest.getDateOfRequest()));
			lblDescriptionOfRequestedChange.setText(changeRequest.getDescriptionOfRequestedChangeLT());
			lblRelatedInfoSystem.setText(changeRequest.getRelatedInformationSystem());
			lblRequestDescription.setText(changeRequest.getRequestDescriptionLT());

		}
	}
}
