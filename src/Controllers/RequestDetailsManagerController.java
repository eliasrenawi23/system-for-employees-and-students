package Controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.ControllerSwapper;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.ChangeRequest;
import Entities.Employee;
import Entities.Phase;
import Entities.PhaseStatus;
import Entities.PhaseType;
import Protocol.Command;
import Utility.AppManager;
import Utility.Curve;
import Utility.DateUtil;
import Utility.Particle;
import Utility.Graphics.ParticlePlexus;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


/**
 * This class provides the manager with a list of all the request with the ability to view details of a phase and unfreeze it if he wishes to.
 * also the manager can view the updates that the supervisor has made by click on a button.
 * 
 * @author Bshara
 * */
public class RequestDetailsManagerController implements Initializable {

	private static final String UNFREEZE_PHASE = "UnfreezePhase";

	@FXML
	private Canvas canvasRight;

	@FXML
	private HBox hbBrowsePhases;

	@FXML
	private Text txtPhaseOwner;

	@FXML
	private Text txtPhaseName;

	@FXML
	private Text txtPhaseStatus;

	@FXML
	private Text txtEstimateTimeOfCompletion;

	@FXML
	private Text txtAssignedByName;

	@FXML
	private Text txtDeadLine;

	@FXML
	private Text txtExtendedTime;

	@FXML
	private Text txtCompletedOnTime;

	@FXML
	private Text txtTimeException;

	@FXML
	private Text txtRequestedTimeExtension;

	@FXML
	private HBox hbFullRequestDetails;

	@FXML
	private HBox hbUnfreezeProcess;

	@FXML
	private Canvas canvasLeft;

	private ChangeRequest changeRequest;
	private Phase selectedPhase;

	private ArrayList<Phase> requestedPhases;
	private int currentPhaseIndex = 0;
	private Employee currentEmployeeOfSelectedPhase;

	private ArrayList<Node> btnsAffectedBySuspension;

	private boolean reload = false;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		changeRequest = ListOfRequestsForManagerController.lastSelectedRequest;
		selectedPhase = changeRequest.getPhases().get(0);

		hbUnfreezeProcess.setVisible(false);
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);

		setButtonsBehaviors();

		setClientObservers();

	}

	private void setButtonsBehaviors() {

		hbUnfreezeProcess.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbUnfreezeProcess, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbUnfreezeProcess, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbUnfreezeProcess.setOnMousePressed(event -> {

			onUnreezeProcess();
		});

		hbFullRequestDetails.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbFullRequestDetails, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbFullRequestDetails, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbFullRequestDetails.setOnMousePressed(event -> {

			NavigationBar.next("Request Full Details", FxmlNames.REQUEST_DETAILS);

		});

		hbBrowsePhases.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbBrowsePhases, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbBrowsePhases, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbBrowsePhases.setOnMousePressed(event -> {

			currentPhaseIndex = (currentPhaseIndex + 1) % requestedPhases.size();
			loadPageDetails();

		});

	}

	private void setClientObservers() {

		Client.getInstance().requestWithListener(Command.getPhasesOfRequestWithTimeExtensionsIfPossible, srMsg -> {
			if (srMsg.getCommand() == Command.getPhasesOfRequestWithTimeExtensionsIfPossible) {
				// Casting error even tho it works and prints the content

				if (srMsg.getAttachedData()[0] instanceof List) {

					requestedPhases = (ArrayList<Phase>) srMsg.getAttachedData()[0];

					long id = selectedPhase.getPhaseID();

					// set the current index to the selected id.
					for (int i = 0; i < requestedPhases.size(); i++) {
						if (id == requestedPhases.get(i).getPhaseID()) {
							currentPhaseIndex = i;
							break;
						}
					}

					loadPageDetails();

				}
			}
		}, "dwadwadaw23516372735df",

				changeRequest.getRequestID());
	}

	private void loadPageDetails() {
		Phase currentPhase = requestedPhases.get(currentPhaseIndex);
		loadPhase(currentPhase);
	}

	private void loadPhase(Phase currentPhase) {

		txtPhaseName.setText(currentPhase.getPhaseName());

		PhaseStatus phStatus = PhaseStatus.valueOfAdvanced(currentPhase.getStatus());

		if (phStatus == PhaseStatus.Frozed) {
			hbUnfreezeProcess.setVisible(true);
		} else {
			hbUnfreezeProcess.setVisible(false);
		}

		if (currentPhase.getEstimatedTimeOfCompletion().equals(DateUtil.NA)) {
			txtEstimateTimeOfCompletion.setText("Not set yet");

		} else {
			txtEstimateTimeOfCompletion.setText(DateUtil.toString(currentPhase.getEstimatedTimeOfCompletion()));
		}

		if (currentPhase.getDeadline().equals(DateUtil.NA)) {
			txtDeadLine.setText("Not set yet");

		} else {
			txtDeadLine.setText(DateUtil.toString(currentPhase.getDeadline()));
		}

		txtPhaseOwner.setText(getPhaseOwnerLabel(currentPhase.getPhaseName()) + ": ");

		txtPhaseStatus.setText(currentPhase.getStatus());

		if (currentPhase.getTimeOfCompletion().equals(DateUtil.NA)) {
			txtCompletedOnTime.setText("Not set yet");

		} else {
			txtCompletedOnTime.setText(DateUtil.toString(currentPhase.getTimeOfCompletion()));
		}

		txtTimeException.setText(currentPhase.hasTimeException() ? "True" : "False");

		if (currentPhase.getPhaseTimeExtensionRequest() != null) {
			int days = currentPhase.getPhaseTimeExtensionRequest().getRequestedTimeInDays();
			int hours = currentPhase.getPhaseTimeExtensionRequest().getRequestedTimeInHours();
			// Check if this has been time extended
			txtRequestedTimeExtension.setText("Days: " + days + " Hours: " + hours);
		} else {
			txtRequestedTimeExtension.setText("N/A");
		}

		if (currentPhase.isHasBeenTimeExtended() && currentPhase.getPhaseTimeExtensionRequest() != null) {
			int days1 = currentPhase.getPhaseTimeExtensionRequest().getRequestedTimeInDays();
			int hours1 = currentPhase.getPhaseTimeExtensionRequest().getRequestedTimeInHours();
			txtExtendedTime.setText("Days: " + days1 + " Hours: " + hours1);
		}

		if (!currentPhase.isHasBeenTimeExtended() && currentPhase.getPhaseTimeExtensionRequest() != null) {

			if (currentPhase.getPhaseTimeExtensionRequest() != null) {
				int days1 = currentPhase.getPhaseTimeExtensionRequest().getRequestedTimeInDays();
				int hours1 = currentPhase.getPhaseTimeExtensionRequest().getRequestedTimeInHours();
				txtRequestedTimeExtension.setText("Days: " + days1 + " Hours: " + hours1);

			} else {
				txtRequestedTimeExtension.setText("N/A");

			}
			// if supervisor has accepted the time request

		} else {
			txtRequestedTimeExtension.setText("N/A");

		}
	}

	private void onUnreezeProcess() {
		ControllerManager.showYesNoMessage("Confirmation", "Freeze Process",
				"Are you sure you want to freeze the process of this phase?", () -> {
					Client.getInstance().requestWithListener(Command.unfreezePhase, srMsg -> {
						if (srMsg.getCommand() == Command.unfreezePhase) {

							hbUnfreezeProcess.setVisible(false);

							ControllerManager.showInformationMessage("Success", "The process has been unsuspended",
									"The process is unfrozed and unsuspeded!", null);

							Client.removeMessageRecievedFromServer(UNFREEZE_PHASE);

							NavigationBar.reload();

						}
					}, UNFREEZE_PHASE, requestedPhases.get(currentPhaseIndex));
				}, null);

	}

	private String getPhaseOwnerLabel(String phaseName) {
		PhaseType phaseType = PhaseType.valueOf(phaseName);
		switch (phaseType) {

		case Evaluation:

			return "Evaluator";

		case Decision:

			return "The Committee Head";

		case Execution:

			return "Executer";

		case Examination:

			return "Examiner";

		case Closing:
		case Supervision:

			return "Supervisor";

		default:
			System.err.println("Error, phase type not recognize in function [getPhaseOwnerLabel] at class "
					+ getClass().getName());
			return "";
		}

	}

}
