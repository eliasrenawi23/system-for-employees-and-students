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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


/**
 * This class provides the needed information for the examiner to examine the request, with the ability to see the execution report if needed.
 * 
 * @author Bshara
 * */
public class RequestDetailsExaminerGUIController implements Initializable {

	private static final String ASSIGN_EXAMINER_FOR_REQUEST = "assignExaminerForRequest";

	private static final String GET_EXECUTION_REPORT_FOR_EXAMINATION = "getExecutionReportForExamination";

	private static final String REJECT_EXECUTION = "RejectExecution";

	private static final String CONFIRM_EXAMINATION = "ConfirmExamination";

	@FXML
    private VBox vbContainerAll;

    @FXML
    private VBox vbEvaluationReport;

    @FXML
    private Label lblExecutionLocation;

    @FXML
    private Label lblExecutionDescription;

    @FXML
    private VBox vbEvaluationReport1;

    @FXML
    private HBox hbConfirmExamination;

    @FXML
    private TextArea taDescriptionOfFailure;

    @FXML
    private HBox hbSendFaulureDescription;

    @FXML
    private HBox hbFullRequestDetails;

    @FXML
    private HBox hbTimeExtension;

    @FXML
    private HBox hbAssignAnExaminer;

    @FXML
    private HBox hbAssignComMem1;

    @FXML
    private Text txtComMem1Name;

    @FXML
    private HBox hbAssignComMem2;

    @FXML
    private Text txtComMem2Name;

    @FXML
    private Canvas canvasRight;

    @FXML
    private Canvas canvasLeft;

	private ChangeRequest lastRequest;

	private Phase lastPhase;

	private ExecutionReport exeRep;

	private long mem1empNum;

	private long mem2empNum;

	private ArrayList<Node> btnsAffectedBySuspension;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		
		btnsAffectedBySuspension = new ArrayList<Node>();
		btnsAffectedBySuspension.add(hbAssignComMem2);
		btnsAffectedBySuspension.add(hbAssignComMem1);
		btnsAffectedBySuspension.add(hbTimeExtension);
		btnsAffectedBySuspension.add(hbSendFaulureDescription);
		btnsAffectedBySuspension.add(hbConfirmExamination);

		
		// Apply the effects for the canvas
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);

		hbFullRequestDetails.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbFullRequestDetails, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbFullRequestDetails, CommonEffects.REQUEST_DETAILS_BUTTON_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbFullRequestDetails.setOnMousePressed(event -> {

			NavigationBar.next("Request Full Details", FxmlNames.REQUEST_DETAILS);

		});

		lastRequest = ListOfRequestsForTreatmentController.lastSelectedRequest;
		lastPhase = lastRequest.getPhases().get(0);

		hbTimeExtension.setVisible(false);

		hbSendFaulureDescription.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbSendFaulureDescription, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbSendFaulureDescription, CommonEffects.REQUEST_DETAILS_BUTTON_RED,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbSendFaulureDescription.setOnMousePressed(event -> {

			onSendFaulureDescriptionPressed();

		});

		hbConfirmExamination.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbConfirmExamination, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbConfirmExamination, CommonEffects.REQUEST_DETAILS_BUTTON_GREEN,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		
		hbAssignComMem1.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbAssignComMem1, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbAssignComMem1, CommonEffects.REQUEST_DETAILS_BUTTON_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		
		hbAssignComMem2.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbAssignComMem2, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbAssignComMem2, CommonEffects.REQUEST_DETAILS_BUTTON_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		
		
		
		hbConfirmExamination.setOnMousePressed(event -> {

			onConfirmExaminationPressed();

		});

		PhaseStatus phaseStatus = PhaseStatus.valueOfAdvanced(lastPhase.getStatus());

		if (phaseStatus != PhaseStatus.Active_And_Waiting_For_Time_Extension && !lastPhase.isHasBeenTimeExtended()) {
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
		

		loadPageDetails();

		
		if (phaseStatus == PhaseStatus.Frozed) {
			ControllerManager.setFreezeBehavior(btnsAffectedBySuspension);
		}

	}

	private void loadPageDetails() {

		Client.getInstance().requestWithListener(Command.getExecutionReportForExaminationAndComsNames, srMsg -> {

			if (srMsg.getCommand() == Command.getExecutionReportForExaminationAndComsNames) {

				exeRep = (ExecutionReport) srMsg.getAttachedData()[0];

				String mem1un = (String) srMsg.getAttachedData()[1];
				mem1empNum = (long) srMsg.getAttachedData()[2];
				String mem2un = (String) srMsg.getAttachedData()[3];
				mem2empNum = (long) srMsg.getAttachedData()[4];

				lblExecutionDescription.setText(exeRep.getContentLT());
				lblExecutionLocation.setText(exeRep.getPlace());

				txtComMem1Name.setText(mem1un + "(empNum: " + mem1empNum + ")");

				txtComMem2Name.setText(mem2un + "(empNum: " + mem2empNum + ")");

				Client.removeMessageRecievedFromServer(GET_EXECUTION_REPORT_FOR_EXAMINATION);
			}

		}, GET_EXECUTION_REPORT_FOR_EXAMINATION, lastPhase.getRequestID());

		hbAssignComMem1.setOnMousePressed(event -> {

			assignExaminer(txtComMem1Name.getText(), mem1empNum);
		});

		hbAssignComMem2.setOnMousePressed(event -> {
			assignExaminer(txtComMem2Name.getText(), mem2empNum);

		});

	}

	private void assignExaminer(String name, long empNum) {
		ControllerManager.showYesNoMessage("Confirmation", "Confirm the examiner", "Are you sure you want to assign "
				+ name + " as an examiner for request [" + lastRequest.getRequestID() + "].", () -> {

					lastPhase.setEmpNumber(empNum);
					
					Client.getInstance().requestWithListener(Command.assignExaminerForRequest, srMsg -> {

						if (srMsg.getCommand() == Command.assignExaminerForRequest) {

							ControllerManager.showInformationMessage("Assign", "Examiner has been assigned",
									"The committee member " + name + " has been assigned as an examiner for this request!", null);

							Client.removeMessageRecievedFromServer(ASSIGN_EXAMINER_FOR_REQUEST);

							NavigationBar.back(true);
						}

					}, ASSIGN_EXAMINER_FOR_REQUEST, lastPhase);

				}, null);
	}

	private void onConfirmExaminationPressed() {

		ControllerManager.showYesNoMessage("Confirmation", "Examinations results",
				"Are you sure you want to confirm the execution of this request?", () -> {

					Client.getInstance().requestWithListener(Command.confirmExamination, srMsg -> {

						if (srMsg.getCommand() == Command.confirmExamination) {

							ControllerManager.showInformationMessage("Success", "Execution Confirmed",
									"The execution has been successfully confirmed!", null);

							Client.removeMessageRecievedFromServer(CONFIRM_EXAMINATION);

							NavigationBar.back(true);

						}

					}, CONFIRM_EXAMINATION, lastPhase);

				}, null);

	}

	private void onSendFaulureDescriptionPressed() {

		if (taDescriptionOfFailure.getText().length() == 0) {

			ControllerManager.showErrorMessage("Error", "Missing Fields", "Please add a description of the failures!",
					null);
			return;
		}

		ControllerManager.showYesNoMessage("Confirmation", "Examinations results",
				"Are you sure you want to reject the execution of this request?", () -> {

					Client.getInstance().requestWithListener(Command.rejectExamination, srMsg -> {

						if (srMsg.getCommand() == Command.rejectExamination) {

							ControllerManager.showInformationMessage("Execution", "Execution Rejected",
									"The execution of this request has been rejected!", null);

							Client.removeMessageRecievedFromServer(REJECT_EXECUTION);

							NavigationBar.back(true);

						}

					}, REJECT_EXECUTION, lastPhase);

				}, null);

	}

}
