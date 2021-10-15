package Controllers;

import java.net.URL;
import java.sql.Timestamp;
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
import Entities.Phase;
import Entities.PhaseStatus;
import Protocol.Command;
import Utility.DateUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


/**
 * This class provides the evaluation with the needed needed fields in order to file an evaluation report for a request, also 
 * the evaluation can see the request details by clicking on a button.
 * 
 * @author Bshara
 * */
public class RequestDetailsEvaluatorGUIController implements Initializable {

	private static final String INSERT_EVALUATION_REPORT = "InsertEvaluationReport";

	@FXML
	private VBox vbEvaluationReport;

	@FXML
	private TextField tfPlace;

	@FXML
	private TextArea taDescriptionOfRequiredChange;

	@FXML
	private TextArea taAcceptedResults;

	@FXML
	private TextArea taConstraints;

	@FXML
	private TextArea taRisks;

	@FXML
	private DatePicker dpEstimatedExecTime;

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

	@FXML
	private VBox vbContainerAll;

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
		// ControllerSwapper.loadAnchorContent(vbEvaluationReport,

		// FxmlNames.REQUEST_DETAILS_EVALUATE);

		hbFullRequestDetails.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbFullRequestDetails, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbFullRequestDetails, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbFullRequestDetails.setOnMousePressed(event -> {

			NavigationBar.next("Request Full Details", FxmlNames.REQUEST_DETAILS);

		});

		lastRequest = ListOfRequestsForTreatmentController.lastSelectedRequest;
		lastPhase = lastRequest.getPhases().get(0);

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
				boolean areAllNotEmpty = ControllerManager.areAllStringsNotEmpty(tfPlace.getText(),
						taAcceptedResults.getText(), taConstraints.getText(), taDescriptionOfRequiredChange.getText(),
						taRisks.getText());

				if (!areAllNotEmpty) {

					ControllerManager.showErrorMessage("Error", "Missing Fields", "Please fill all of fields!", null);
					return;
				}

				if (dpEstimatedExecTime.getValue() == null) {
					ControllerManager.showErrorMessage("Error", "Missing Date",
							"Please set an estimated execution time", null);
					return;
				}

				if (dpEstimatedExecTime.getValue().isBefore(LocalDate.now())) {
					ControllerManager.showErrorMessage("Error", "Invalid Date",
							"Please set an estimated execution time that is after the date of today!", null);
					return;
				}

				ControllerManager.showYesNoMessage("Send Report", "Evaluation Report",
						"Are you sure you want to send the evaluation report?", () -> {

							EvaluationReport evaluationReport = new EvaluationReport(-1, lastPhase.getPhaseID(),
									taDescriptionOfRequiredChange.getText(), tfPlace.getText(),
									taAcceptedResults.getText(), taConstraints.getText(), taRisks.getText(),
									DateUtil.get(dpEstimatedExecTime.getValue()));

							Client.getInstance().requestWithListener(Command.insertEvaluationReport, srMsg -> {

								if (srMsg.getCommand() == Command.insertEvaluationReport) {

									ControllerManager.showInformationMessage("Success", "Evaluation Report",
											"The evaluation reported has been successfully puplished", null);

									Client.removeMessageRecievedFromServer(INSERT_EVALUATION_REPORT);
									NavigationBar.back(true);

								}

							}, INSERT_EVALUATION_REPORT, evaluationReport, lastPhase.getPhaseID());

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
