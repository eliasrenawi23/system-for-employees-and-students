package Controllers;

import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.NavigationBar;
import Entities.Phase;
import Entities.PhaseStatus;
import Protocol.Command;
import Utility.DateUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This class is used by other class to allow the employee to set the estimated time of completion for the phase that he manages.
 * 
 * @author Bshara
 * */
public class SetEstimatedTimeForPhaseController implements Initializable {

	private static final String UPDATE_PHASE_ESTIMATED_TIME = "UpdatePhaseEstimatedTime";

	@FXML
	private DatePicker dpEstimatedTime;

	@FXML
	private VBox vbEvaluationReport;

	@FXML
	private HBox hbSendEstimateTime;

	private Phase phase;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		hbSendEstimateTime.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbSendEstimateTime, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbSendEstimateTime, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		
	
		
		hbSendEstimateTime.setOnMousePressed(event -> {

			if (dpEstimatedTime.getValue() == null) {

				ControllerManager.showErrorMessage("Error", "Date not picked", "Please pick a date", null);
				return;
			}
			

			Timestamp pickedDate = Timestamp.valueOf(dpEstimatedTime.getValue().atStartOfDay());
			
			if(pickedDate.before(DateUtil.now())) {
				
				ControllerManager.showErrorMessage("Error", "Date is not valid", "Please pick a date that is after the date of today", null);
				return;
			}
			
			
			
			// Get selected date
			phase.setEstimatedTimeOfCompletion(Timestamp.valueOf(dpEstimatedTime.getValue().atStartOfDay()));
			phase.setStatus(PhaseStatus.Waiting_To_Confirm_Time_Required_For_Phase.nameNo_());

			Client.getInstance().requestWithListener(Command.updatePhaseEstimatedTime, srMsg -> {

				if (srMsg.getCommand() == Command.updatePhaseEstimatedTime) {

					ControllerManager.showInformationMessage("Success", "Evaluation Request Sent",
							"A message of the evaluated time for this phase has been sent to the supervisor, please wait for a response in the near future!",
							null);
					
					NavigationBar.back(true);

					Client.removeMessageRecievedFromServer(UPDATE_PHASE_ESTIMATED_TIME);
				}

			}, UPDATE_PHASE_ESTIMATED_TIME, phase);

		});

	}

	public void setPhase(Phase ph) {
		phase = ph;
	}

}
