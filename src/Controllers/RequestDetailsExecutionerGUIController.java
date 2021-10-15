package Controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.ControllerSwapper;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.ChangeRequest;
import Entities.EvaluationReport;
import Entities.ExecutionReport;
import Entities.Phase;
import Entities.PhaseStatus;
import Protocol.Command;
import Utility.DateUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * This class provides the executioner with the needed information to execute the request, the executioner can view the request details with
 * a simple click in a button, and also can confirm his execution to the request with the ability to file an execution report.
 * 
 * @author Bshara
 * */
public class RequestDetailsExecutionerGUIController implements Initializable {

	private static final String INSERT_EXECUTION_REPORT = "InsertExecutionReport";

	@FXML
	private VBox vbContainerAll;

	@FXML
	private VBox vbEvaluationReport;

	@FXML
	private TextArea taLocation;

	@FXML
	private TextArea taDescription;

	@FXML
	private HBox hbSendExecutionDetails;

	@FXML
	private HBox hbFullRequestDetails;

	@FXML
	private HBox hbTimeExtension;

	@FXML
	private Canvas canvasRight;

	@FXML
	private Canvas canvasLeft;

	private ChangeRequest lastRequest;

	private Phase lastPhase;

	private ArrayList<Node> btnsAffectedBySuspension;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		btnsAffectedBySuspension = new ArrayList<Node>();
		btnsAffectedBySuspension.add(hbSendExecutionDetails);
		btnsAffectedBySuspension.add(hbTimeExtension);

		// Apply the effects for the canvas
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);

		lastRequest = ListOfRequestsForTreatmentController.lastSelectedRequest;
		lastPhase = lastRequest.getPhases().get(0);

		hbFullRequestDetails.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbFullRequestDetails, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbFullRequestDetails, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbFullRequestDetails.setOnMousePressed(event -> {

			NavigationBar.next("Request Full Details", FxmlNames.REQUEST_DETAILS);

		});

		hbTimeExtension.setVisible(false);

		PhaseStatus phaseStatus = PhaseStatus.valueOfAdvanced(lastPhase.getStatus());

		switch (phaseStatus) {

		case Waiting_To_Confirm_Time_Required_For_Phase:

			WaitingForPhaseEstimatedTimeConfirmationGUI controller2 = (WaitingForPhaseEstimatedTimeConfirmationGUI) ControllerSwapper
					.loadContentWithController(vbEvaluationReport,
							FxmlNames.WAITING_FOR_PHASE_ESTIMATED_TIME_CONFIRMATION);
			controller2.setPhase(lastPhase);

			break;

		case Waiting_To_Set_Time_Required_For_Phase:

			SetEstimatedTimeForPhaseController controller = (SetEstimatedTimeForPhaseController) ControllerSwapper
					.loadContentWithController(vbEvaluationReport, FxmlNames.SET_ESTIMATED_TIME_FOR_PHASE);
			controller.setPhase(lastPhase);

			break;

		case Active_And_Waiting_For_Time_Extension:
		case Active:

			hbSendExecutionDetails.setCursor(Cursor.HAND);
			ControllerManager.setEffect(hbSendExecutionDetails, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
			ControllerManager.setOnHoverEffect(hbSendExecutionDetails, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
					CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

			if (phaseStatus != PhaseStatus.Active_And_Waiting_For_Time_Extension
					&& !lastPhase.isHasBeenTimeExtended()) {
				hbTimeExtension.setVisible(true);
				hbTimeExtension.setCursor(Cursor.HAND);
				ControllerManager.setEffect(hbTimeExtension, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
				ControllerManager.setOnHoverEffect(hbTimeExtension, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
						CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

				hbTimeExtension.setOnMousePressed(event -> {

					requestTimeExtensionController r = (requestTimeExtensionController) ControllerSwapper
							.loadContentWithController(vbContainerAll, FxmlNames.REQUEST_TIME_EXTENSION);
					r.setPhase(lastPhase);

				});

			}

			hbSendExecutionDetails.setOnMousePressed(event -> {

				boolean areAllNotEmpty = ControllerManager.areAllStringsNotEmpty(taDescription.getText(),
						taLocation.getText());

				if (!areAllNotEmpty) {

					ControllerManager.showErrorMessage("Error", "Missing Fields", "Please fill all of fields!", null);
					return;
				}

				ControllerManager.showYesNoMessage("Execution", "Confirm Execution",
						"Are you sure you want to confirm the execution?", () -> {

							ExecutionReport exeRep = new ExecutionReport(-1, lastPhase.getPhaseID(),
									taDescription.getText(), taLocation.getText());

							Client.getInstance().requestWithListener(Command.insertExecutionReport, srMsg -> {

								if (srMsg.getCommand() == Command.insertExecutionReport) {

									ControllerManager.showInformationMessage("Success", "Execution Confirmed!",
											"The execution reported has been successfully puplished", null);

									Client.removeMessageRecievedFromServer(INSERT_EXECUTION_REPORT);
									NavigationBar.back(true);

								}

							}, INSERT_EXECUTION_REPORT, exeRep, lastPhase.getPhaseID());

						}, null);

			});

			break;

		default:

			System.err.println("Error, phase " + lastPhase.getStatus() + " is undefined!");
			break;
		}

		if (phaseStatus == PhaseStatus.Frozed) {
			ControllerManager.setFreezeBehavior(btnsAffectedBySuspension);
		}

	}

}
