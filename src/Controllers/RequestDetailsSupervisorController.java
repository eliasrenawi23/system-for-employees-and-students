package Controllers;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
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
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


/**
 * This class is what the supervisor sees once he double click on a phase, it provides details about the phase and the supervisor can change deadline,
 * assign evaluator, assign executer, approve or reject time extension and approve or reject estimate time of completion.
 * 
 * @author Bshara
 * */
public class RequestDetailsSupervisorController implements Initializable {

	private static final String UPDATE_DEADLINE = "UpdateDeadline";

	private static final String FREEZE_PHASE = "freezePhase";

	private static final String DECLINED_REQUEST_ENDED_BY_SUPERVISOR = "declinedRequestEndedBySupervisor";

	private static final String CONFIRM_REQUEST_ENDED_BY_SUPERVISOR = "confirmRequestEndedBySupervisor";

	private static final String ACCE = "dawdawfw53252365435435435435t5th5463453";

	private static final String REJECT_PHASE_DEADLINE = "RejectPhaseDeadline";

	private static final String CONFIRM_PHASE_DEADLINE = "ConfirmPhaseDeadline";

	private static final String ACTIVATE_EVALUATION_PHASE = "ActivateEvaluationPhase";

	private static final String UPDATE_PHASE_OWNER_FROM_REQUEST_DETAILS_SUPERVISOR = "UpdatePhaseOwnerFromRequestDetailsSupervisor9879";

	private static final String GET_EMPLOYEE_BY_EMPLOYEE_NUMBER = "GetEmployeeByEmployeeNumber";

	private static final String GET_PHASES_OF_REQUEST_WITH_TIME_EXTENSIONS_IF_POSSIBLE = "getPhasesOfRequestWithTimeExtensionsIfPossible";

	@FXML
	private Canvas canvasRight;

	@FXML
	private HBox hbEditPhaseDetails;

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
	private HBox hbAssignEvaluatiorContainer;

	@FXML
	private ImageView imgWarningAutoAssign;

	@FXML
	private HBox hbConfirmAutoAssign;

	@FXML
	private HBox hbAssignOtherEmp;

	@FXML
	private HBox hbDeadline;

	@FXML
	private ImageView imgWarningRequestedTimeExtension2;

	@FXML
	private HBox hbConfirmDeadline;

	@FXML
	private HBox hbRejectDeadline;

	@FXML
	private ImageView imgWarningRequestedTimeExtension1;

	@FXML
	private HBox hbConfirmTimeRequestExtension1;

	@FXML
	private HBox hbDeclineRequestTimeExtension1;

	@FXML
	private HBox hbRequestedTimeExtenContainer;

	@FXML
	private ImageView imgWarningRequestedTimeExtension;

	@FXML
	private HBox hbConfirmTimeRequestExtension;

	@FXML
	private HBox hbDeclineRequestTimeExtension;

	@FXML
	private HBox hbFullRequestDetails;

	@FXML
	private Canvas canvasLeft;

	@FXML
	private VBox vbRequestEndedConfirmation;

	@FXML
	private HBox hbConfirmRequestEnded;

	@FXML
	private HBox hbDeclineRequestEnded;

	@FXML
	private HBox hbFreezeProcess;

	@FXML
	private HBox hbChangeDeadline;

	@FXML
	private DatePicker dpChangeDeadline;

	@FXML
	private HBox hbChangeDeadlineDateContainer;

	@FXML
	private ImageView imgOnChangeDeadlineApproved;

	@FXML
	private ImageView imgOnChangeDeadlineReject;

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
		btnsAffectedBySuspension = new ArrayList<Node>();
		btnsAffectedBySuspension.add(hbConfirmTimeRequestExtension1);
		btnsAffectedBySuspension.add(hbConfirmTimeRequestExtension);
		btnsAffectedBySuspension.add(hbDeclineRequestTimeExtension);
		btnsAffectedBySuspension.add(hbConfirmAutoAssign);
		btnsAffectedBySuspension.add(hbConfirmDeadline);
		btnsAffectedBySuspension.add(hbAssignOtherEmp);
		btnsAffectedBySuspension.add(hbFreezeProcess);
		btnsAffectedBySuspension.add(hbRejectDeadline);

		changeRequest = ListOfRequestsForTreatmentController.lastSelectedRequest;
		selectedPhase = changeRequest.getPhases().get(0);
		vbRequestEndedConfirmation.setVisible(false);
		hbFreezeProcess.setVisible(false);
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);

		setButtonsBehaviors();

		setClientObservers();

		// loadPhase(changeRequest.getPhases().get(0));

	}

	private void setButtonsBehaviors() {

		hbChangeDeadline.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbChangeDeadline, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbChangeDeadline, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		hbChangeDeadlineDateContainer.setVisible(false);

		hbChangeDeadline.setOnMousePressed(event -> {

			hbChangeDeadlineDateContainer.setVisible(true);
		});

		imgOnChangeDeadlineApproved.setCursor(Cursor.HAND);
		imgOnChangeDeadlineReject.setCursor(Cursor.HAND);

		imgOnChangeDeadlineApproved.setOnMousePressed(event -> {

			if (dpChangeDeadline.getValue() == null) {

				ControllerManager.showErrorMessage("Missing Date", "Missing Date", "Please select a date first", null);
				return;
			}
			
			if (!dpChangeDeadline.getValue().isAfter(LocalDate.now())) {

				ControllerManager.showErrorMessage("Wrong Date", "Wrong Date", "Please select a date that is after today", null);
				return;
			}
			ControllerManager.showYesNoMessage("Confirm", "Change deadline",
					"Are you sure you want to change the deadline of this request to " + dpChangeDeadline.getValue(),
					() -> {
						Timestamp oldDeadline = requestedPhases.get(currentPhaseIndex).getDeadline();
						requestedPhases.get(currentPhaseIndex).setDeadline(DateUtil.get(dpChangeDeadline.getValue()));
						Client.getInstance().requestWithListener(Command.updateDeadline, srMsg -> {

							if (srMsg.getCommand() == Command.updateDeadline) {

								ControllerManager.showInformationMessage("Success", "Deadline changed",
										"The deadline has been successfully change!", () -> {
											hbChangeDeadlineDateContainer.setVisible(false);
											NavigationBar.reload();
										});

								Client.removeMessageRecievedFromServer(UPDATE_DEADLINE);
							}

						}, UPDATE_DEADLINE, requestedPhases.get(currentPhaseIndex), oldDeadline);

					}, null);
		});

		imgOnChangeDeadlineReject.setOnMousePressed(event -> {

			hbChangeDeadlineDateContainer.setVisible(false);

		});

		hbFreezeProcess.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbFreezeProcess, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbFreezeProcess, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbFreezeProcess.setOnMousePressed(event -> {

			onFreezeProcess();
		});

		hbFullRequestDetails.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbFullRequestDetails, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbFullRequestDetails, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbFullRequestDetails.setOnMousePressed(event -> {

			NavigationBar.next("Request Full Details", FxmlNames.REQUEST_DETAILS);

		});

		hbEditPhaseDetails.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbEditPhaseDetails, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbEditPhaseDetails, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbBrowsePhases.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbBrowsePhases, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbBrowsePhases, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbBrowsePhases.setOnMousePressed(event -> {

			currentPhaseIndex = (currentPhaseIndex + 1) % requestedPhases.size();
			loadPageDetails();

		});

		hbAssignOtherEmp.setOnMousePressed(event -> {
			NavigationBar.next("Select Employee", FxmlNames.LIST_OF_EMPLOYEES_SIMPLE);
		});

		ListOfEmployeesSimpleController.setOnRowDoubleClicked(emp -> {

			Client.getInstance().requestWithListener(Command.updatePhaseOwner, srMsg -> {
				if (srMsg.getCommand() == Command.updatePhaseOwner) {

					// requestedPhases.get(currentPhaseIndex).setEmpNumber(10);
					// currentEmployeeOfSelectedPhase.setEmpNumber(10);
					NavigationBar.back(true);

//					ControllerManager.showInformationMessage("Success", "Employee Selected",
//							"The employee has been set to the current phase, press the confirm button to confirm your selection",
//							null);

					// txtAssignedByName.setText(emp.getFirstName() + " " + emp.getLastName());

					// OK, so now the confirm button will change the phase status to active and send
					// the manager of the phase a message.
					Client.removeMessageRecievedFromServer(UPDATE_PHASE_OWNER_FROM_REQUEST_DETAILS_SUPERVISOR);
				}
			}, UPDATE_PHASE_OWNER_FROM_REQUEST_DETAILS_SUPERVISOR, requestedPhases.get(currentPhaseIndex),
					emp.getEmpNumber());

		});

		hbConfirmAutoAssign.setOnMousePressed(event -> {
			requestedPhases.get(currentPhaseIndex)
					.setStatus(PhaseStatus.Waiting_To_Set_Time_Required_For_Phase.nameNo_());

			Client.getInstance().requestWithListener(Command.setEvaluationPhaseToWaitingToSetTime, srMsg -> {
				if (srMsg.getCommand() == Command.setEvaluationPhaseToWaitingToSetTime) {

					hbAssignEvaluatiorContainer.setVisible(false);

					ControllerManager.showInformationMessage("Success", "Evaluator has been confirmed",
							"A message has been sent to the evaluator to choose a time to evaluate this request.",
							null);

					Client.removeMessageRecievedFromServer(ACTIVATE_EVALUATION_PHASE);
				}
			}, ACTIVATE_EVALUATION_PHASE, requestedPhases.get(currentPhaseIndex));
		});

		hbConfirmDeadline.setOnMousePressed(event -> {
			requestedPhases.get(currentPhaseIndex)
					.setDeadline(requestedPhases.get(currentPhaseIndex).getEstimatedTimeOfCompletion());

			requestedPhases.get(currentPhaseIndex).setStatus(PhaseStatus.Active.name());

			Client.getInstance().requestWithListener(Command.confirmPhaseDeadline, srMsg -> {
				if (srMsg.getCommand() == Command.confirmPhaseDeadline) {

					hbDeadline.setVisible(false);

					ControllerManager.showInformationMessage("Success", "Deadline confirmed",
							"The deadline for this request has been confirmed!", null);

					Client.removeMessageRecievedFromServer(CONFIRM_PHASE_DEADLINE);
				}
			}, CONFIRM_PHASE_DEADLINE, requestedPhases.get(currentPhaseIndex));
		});

		hbRejectDeadline.setOnMousePressed(event -> {
			requestedPhases.get(currentPhaseIndex).setEstimatedTimeOfCompletion(DateUtil.NA);
			requestedPhases.get(currentPhaseIndex)
					.setStatus(PhaseStatus.Waiting_To_Set_Time_Required_For_Phase.nameNo_());

			Client.getInstance().requestWithListener(Command.rejectPhaseDeadline, srMsg -> {
				if (srMsg.getCommand() == Command.rejectPhaseDeadline) {

					hbDeadline.setVisible(false);

					ControllerManager.showInformationMessage("Success", "Deadline rejected",
							"The deadline for this request has been rejected!", null);

					Client.removeMessageRecievedFromServer(REJECT_PHASE_DEADLINE);
				}
			}, REJECT_PHASE_DEADLINE, requestedPhases.get(currentPhaseIndex));
		});

	}

	private void setClientObservers() {
		Client.addMessageRecievedFromServer(ACCE, srMsg -> {
			if (srMsg.getCommand() == Command.acceptPhaseTimeExtensionSupervisor) {

				boolean isSuccess = (boolean) srMsg.getAttachedData()[0];

				if (isSuccess) {
					long requestId = (long) srMsg.getAttachedData()[1];
					ControllerManager.showInformationMessage("Success", "Time extension confirmed",
							"The time extension for request with id " + requestId + " has been successfuly applied!",
							null);

					// remove the phase time extension
					Phase currentPhase = requestedPhases.get(currentPhaseIndex);
					currentPhase.setPhaseTimeExtensionRequest(null);

					NavigationBar.reload();

				} else {
					ControllerManager.showErrorMessage("Error", "An error has occured",
							"Something has gone wrong when adding a time extension for this request", null);
				}

			}
		});

		Client.addMessageRecievedFromServer("dawdawfw5366664646th5463453", srMsg -> {
			if (srMsg.getCommand() == Command.rejectPhaseTimeExtensionSupervisor) {

				boolean isSuccess = (boolean) srMsg.getAttachedData()[0];

				if (isSuccess) {
					long requestId = (long) srMsg.getAttachedData()[1];
					ControllerManager.showInformationMessage("Success", "Time extension rejected",
							"The time extension for request with id " + requestId + " has been successfuly rejected!",
							null);
					hbRequestedTimeExtenContainer.setVisible(false);

					// remove the phase time extension
					Phase currentPhase = requestedPhases.get(currentPhaseIndex);
					currentPhase.setPhaseTimeExtensionRequest(null);
					currentPhase.setStatus(PhaseStatus.Active.name());

				} else {
					ControllerManager.showErrorMessage("Error", "An error has occured",
							"Something has gone wrong when adding a time extension for this request", null);
				}

			}
		});

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
		}, GET_PHASES_OF_REQUEST_WITH_TIME_EXTENSIONS_IF_POSSIBLE,

				changeRequest.getRequestID());
	}

	private void loadPageDetails() {
		Phase currentPhase = requestedPhases.get(currentPhaseIndex);
		loadPhase(currentPhase);
	}

	private void loadPhase(Phase currentPhase) {
		hbAssignEvaluatiorContainer.setVisible(false);

		txtPhaseName.setText(currentPhase.getPhaseName());

		PhaseStatus phStatus = PhaseStatus.valueOfAdvanced(currentPhase.getStatus());
		if (phStatus != PhaseStatus.Frozed && phStatus != PhaseStatus.Closed) {
			hbFreezeProcess.setVisible(true);
		}
		if (phStatus == PhaseStatus.Frozed) {
			ControllerManager.setFreezeBehavior(btnsAffectedBySuspension);
		}

		switch (phStatus) {
		case Active:
		case Active_And_Waiting_For_Time_Extension:
			hbChangeDeadline.setVisible(true);
			break;
		default:
			hbChangeDeadline.setVisible(false);

			break;
		}

		// if the estimated time of completion has been set
		if (!currentPhase.getEstimatedTimeOfCompletion().equals(DateUtil.NA)) {

			if (currentPhase.getStatus()
					.compareTo(PhaseStatus.Waiting_To_Confirm_Time_Required_For_Phase.nameNo_()) == 0) {
				hbDeadline.setVisible(true);

			} else {
				hbDeadline.setVisible(false);
			}

		} else {
			hbDeadline.setVisible(false);
		}

		Client.getInstance().requestWithListener(Command.getEmployeeByEmployeeNumber, srMsg -> {
			if (srMsg.getCommand() == Command.getEmployeeByEmployeeNumber) {
				// Casting error even tho it works and prints the content

				if (srMsg.getAttachedData()[0] instanceof Employee) {

					currentEmployeeOfSelectedPhase = (Employee) srMsg.getAttachedData()[0];

					System.out.println("Getting employee number " + currentEmployeeOfSelectedPhase);
					txtAssignedByName.setText(currentEmployeeOfSelectedPhase.getFirstName() + " "
							+ currentEmployeeOfSelectedPhase.getLastName());
					Client.removeMessageRecievedFromServer(GET_EMPLOYEE_BY_EMPLOYEE_NUMBER);
				}
			}
		}, GET_EMPLOYEE_BY_EMPLOYEE_NUMBER, currentPhase.getEmpNumber());

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

		if (currentPhase.phaseName.equals(PhaseType.Evaluation.name())
				|| currentPhase.phaseName.equals(PhaseType.Execution.name())) {

			if (currentPhase.getStatus().equals(PhaseStatus.Waiting_To_Set_Evaluator.nameNo_())
					|| currentPhase.getStatus().equals(PhaseStatus.Waiting_To_Set_Executer.nameNo_())) {

				hbAssignEvaluatiorContainer.setVisible(true);

			} else {
				hbAssignEvaluatiorContainer.setVisible(false);

			}

		}

		if (currentPhase.phaseName.equals(PhaseType.Closing.name())) {

			if (currentPhase.getStatus().equals(PhaseStatus.Waiting_For_Owner_And_Supervisor_Confirmation.nameNo_())
					|| currentPhase.getStatus().equals(PhaseStatus.Waiting_For_Supervisor_Confirmation.nameNo_())) {

				hbDeclineRequestEnded.setCursor(Cursor.HAND);
				ControllerManager.setEffect(hbDeclineRequestEnded, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
				ControllerManager.setOnHoverEffect(hbDeclineRequestEnded, CommonEffects.REQUEST_DETAILS_BUTTON_RED,
						CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
				hbDeclineRequestEnded.setOnMousePressed(event -> {
					onDeclineRequestEnded();
				});

				hbConfirmRequestEnded.setCursor(Cursor.HAND);
				ControllerManager.setEffect(hbConfirmRequestEnded, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
				ControllerManager.setOnHoverEffect(hbConfirmRequestEnded, CommonEffects.REQUEST_DETAILS_BUTTON_GREEN,
						CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
				hbConfirmRequestEnded.setOnMousePressed(event -> {
					onConfirmRequestEnded();
				});
				vbRequestEndedConfirmation.setVisible(true);

			}

		}

		if (currentPhase.isHasBeenTimeExtended() && currentPhase.getPhaseTimeExtensionRequest() != null) {
			int days1 = currentPhase.getPhaseTimeExtensionRequest().getRequestedTimeInDays();
			int hours1 = currentPhase.getPhaseTimeExtensionRequest().getRequestedTimeInHours();
			txtExtendedTime.setText("Days: " + days1 + " Hours: " + hours1);
		}

		if (!currentPhase.isHasBeenTimeExtended() && currentPhase.getPhaseTimeExtensionRequest() != null) {
			hbRequestedTimeExtenContainer.setVisible(true);

			if (currentPhase.getPhaseTimeExtensionRequest() != null) {
				int days1 = currentPhase.getPhaseTimeExtensionRequest().getRequestedTimeInDays();
				int hours1 = currentPhase.getPhaseTimeExtensionRequest().getRequestedTimeInHours();
				txtRequestedTimeExtension.setText("Days: " + days1 + " Hours: " + hours1);

			} else {
				txtRequestedTimeExtension.setText("N/A");

			}
			// if supervisor has accepted the time request
			hbConfirmTimeRequestExtension.setOnMousePressed(event -> {
				Client.getInstance().request(Command.acceptPhaseTimeExtensionSupervisor, currentPhase);
			});

			// if the supervisor has decline the time request
			hbDeclineRequestTimeExtension.setOnMousePressed(event -> {
				Client.getInstance().request(Command.rejectPhaseTimeExtensionSupervisor, currentPhase);
			});

		} else {
			hbRequestedTimeExtenContainer.setVisible(false);
			txtRequestedTimeExtension.setText("N/A");

		}

	}

	private void onFreezeProcess() {
		ControllerManager.showYesNoMessage("Confirmation", "Freeze Process",
				"Are you sure you want to freeze the process of this phase?", () -> {
					Client.getInstance().requestWithListener(Command.freezePhase, srMsg -> {
						if (srMsg.getCommand() == Command.freezePhase) {

							hbFreezeProcess.setVisible(false);

							ControllerManager.showInformationMessage("Success", "The process has been suspended",
									"The process is frozed and suspeded from any further changes!", null);

							Phase ph = requestedPhases.get(currentPhaseIndex);
							ph.setStatus(PhaseStatus.Frozed.name());

							Client.removeMessageRecievedFromServer(FREEZE_PHASE);

							NavigationBar.reload();

						}
					}, FREEZE_PHASE, requestedPhases.get(currentPhaseIndex));
				}, null);

	}

	private void onConfirmRequestEnded() {

		ControllerManager.showYesNoMessage("Confirmation", "Confirming Request",
				"Are you sure you want to confirm the execution of this request?", () -> {
					Client.getInstance().requestWithListener(Command.confirmRequestEndedBySupervisor, srMsg -> {
						if (srMsg.getCommand() == Command.confirmRequestEndedBySupervisor) {

							vbRequestEndedConfirmation.setVisible(false);

							ControllerManager.showInformationMessage("Success", "Request End Confirmed",
									"The request has been confirmed for ending!", null);

							Client.removeMessageRecievedFromServer(CONFIRM_REQUEST_ENDED_BY_SUPERVISOR);

							NavigationBar.reload();

						}
					}, CONFIRM_REQUEST_ENDED_BY_SUPERVISOR, requestedPhases.get(currentPhaseIndex));
				}, null);
	}

	private void onDeclineRequestEnded() {
		ControllerManager.showYesNoMessage("Rejection", "Rejecting Request",
				"Are you sure you want to reject the execution of this request?", () -> {
					Client.getInstance().requestWithListener(Command.declinedRequestEndedBySupervisor, srMsg -> {
						if (srMsg.getCommand() == Command.declinedRequestEndedBySupervisor) {

							vbRequestEndedConfirmation.setVisible(false);

							ControllerManager.showInformationMessage("Success", "Request End Rejected",
									"The request has been rejected for ending!", null);

							Client.removeMessageRecievedFromServer(DECLINED_REQUEST_ENDED_BY_SUPERVISOR);

							NavigationBar.reload();
						}
					}, DECLINED_REQUEST_ENDED_BY_SUPERVISOR, requestedPhases.get(currentPhaseIndex));
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
