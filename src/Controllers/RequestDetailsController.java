package Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.ControllerManager;
import Entities.ChangeRequest;
import Protocol.Command;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * This class is used with the user who issued a requests double clicked on the table of his issued request to view more details about the request
 * can view the attached files of the request and see all of the requests details, like request current phase.
 * 
 * @author Bshara
 * */
public class RequestDetailsController implements Initializable {

	private static final String GET_FULL_NAME_OF_SYSTEM_USER = "GetFullNameOfSystemUser";
	@FXML
	private Canvas canvasRight;

	@FXML
	private HBox hbInformationContainer;

	@FXML
	private Text txtRequestID;

	@FXML
	private Text txtRelatedInfoSystem;

	@FXML
	private Text txtIssuedBy;

	@FXML
	private Text txtIssueDate;

	@FXML
	private Text txtRequestDescription;

	@FXML
	private Text txtDescriptionOfCurrentChange;

	@FXML
	private Text txtDescriptionOfRequestedChange;

	@FXML
	private Text txtComments;

	@FXML
	private Canvas canvasLeft;

	private ObservableList<Node> nodes;
	private boolean swapper = false;
	private ChangeRequest changeRequest;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);

		nodes = FXCollections.observableArrayList(hbInformationContainer.getChildren());
		changeRequest = ListOfRequestsForTreatmentController.lastSelectedRequest;

		loadFields();

	}

	private void loadFields() {
		if (changeRequest != null) {

			txtRequestID.setText(changeRequest.getRequestID() + "");
			txtComments.setText(changeRequest.getCommentsLT());
			txtDescriptionOfCurrentChange.setText(changeRequest.getDescriptionOfCurrentStateLT());
			txtIssueDate.setText(ControllerManager.getDateTime(changeRequest.getDateOfRequest()));
			txtDescriptionOfRequestedChange.setText(changeRequest.getDescriptionOfRequestedChangeLT());
			txtRelatedInfoSystem.setText(changeRequest.getRelatedInformationSystem());
			txtRequestDescription.setText(changeRequest.getRequestDescriptionLT());

			Client.getInstance().requestWithListener(Command.getFullNameByUsername, srMsg -> {

				if (srMsg.getCommand() == Command.getFullNameByUsername) {

					String fullName = (String) srMsg.getAttachedData()[0];
					txtIssuedBy.setText(fullName);
					Client.removeMessageRecievedFromServer(GET_FULL_NAME_OF_SYSTEM_USER);

				}

			}, GET_FULL_NAME_OF_SYSTEM_USER, changeRequest.getUsername());
		}
	}

}