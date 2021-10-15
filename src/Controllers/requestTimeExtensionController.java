package Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.ControllerSwapper;
import Controllers.Logic.NavigationBar;
import Entities.ChangeRequest;
import Entities.Phase;
import Entities.PhaseStatus;
import Entities.PhaseTimeExtensionRequest;
import Protocol.Command;
import Utility.DateUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


/**
 * This class is used by other classes where the evaluator or committee member or executer can request a time extension, this class provides the needed
 * fields to request a time extension for a phase.
 * 
 * @author Bshara
 * */
public class requestTimeExtensionController implements Initializable {

	private static final String INSERT_TIME_EXTENSION324 = "insertTimeExtension324";

	@FXML
	private VBox vbEvaluationReport;

	@FXML
	private Text txtRequestID;

	@FXML
	private TextField tfNumberOfDays;

	@FXML
	private TextField tfNumberOfHours;

	@FXML
	private TextArea txtDescription;

	@FXML
	private Text txtOldDeadline;

	@FXML
	private Text txtNewDeadline1;

	@FXML
	private HBox hbCancle;

	@FXML
	private HBox hbSendTimeExtensionRequest1;

	private Phase phase;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		hbSendTimeExtensionRequest1.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbSendTimeExtensionRequest1, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbSendTimeExtensionRequest1, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbCancle.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbCancle, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbCancle, CommonEffects.REQUEST_DETAILS_BUTTON_RED,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		hbCancle.setOnMousePressed(event -> {
			NavigationBar.reload();
		});

		hbSendTimeExtensionRequest1.setOnMousePressed(event -> {

			if (tfNumberOfDays.getText().length() == 0) {
				ControllerManager.showErrorMessage("Error", "Number of days not set", "Please select a number of days",
						null);
				return;
			}

			if (tfNumberOfHours.getText().length() == 0) {
				ControllerManager.showErrorMessage("Error", "Number of hours not set",
						"Please select a number of hours", null);
				return;
			}

			int requestedDays = Integer.parseInt(tfNumberOfDays.getText());
			int requestedHours = Integer.parseInt(tfNumberOfHours.getText());

			if (requestedDays < 0) {
				ControllerManager.showErrorMessage("Error", "Negative number of days",
						"Please select a positive number of days", null);
				return;
			}

			if (requestedHours < 0) {
				ControllerManager.showErrorMessage("Error", "Negative number of hours",
						"Please select a positive number of hours", null);
				return;
			}

			if (requestedDays + requestedHours == 0) {
				ControllerManager.showErrorMessage("Error", "Request time is 0",
						"Please select a valid number of hours and/or days", null);
				return;
			}

			String description = txtDescription.getText();

			PhaseTimeExtensionRequest phaseTimeExtensionRequest = new PhaseTimeExtensionRequest(phase.getPhaseID(),
					requestedDays, requestedHours, description);

			Client.getInstance().requestWithListener(Command.insertTimeExtension, srMsg -> {

				if (srMsg.getCommand() == Command.insertTimeExtension) {

					if ((boolean) srMsg.getAttachedData()[0]) {

						int days = (int) srMsg.getAttachedData()[1];
						int hours = (int) srMsg.getAttachedData()[2];

						ControllerManager.showInformationMessage("Success", "Time Extension",
								"Your request time extension of " + days + " days and " + hours
										+ " hours has been send to the supervisor, please wait for a resonse in the coming days",
								null);
						
						phase.setStatus(PhaseStatus.Active_And_Waiting_For_Time_Extension.nameNo_());

						NavigationBar.reload();
					} else {
						System.err.println("returned false from Command.insertTimeExtension");
					}

					Client.removeMessageRecievedFromServer(INSERT_TIME_EXTENSION324);
				}

			}, INSERT_TIME_EXTENSION324, phaseTimeExtensionRequest);
		});

		ControllerManager.setTextFieldToNumbersOnly(tfNumberOfDays);
		ControllerManager.setTextFieldToNumbersOnly(tfNumberOfHours);

	}

	public void setPhase(Phase lastPhase) {
		phase = lastPhase;
		txtRequestID.setText("[" + phase.getRequestID() + "]");
		txtOldDeadline.setText(DateUtil.toString(phase.getDeadline()));
		txtNewDeadline1.setText("Please select a date");
	}

}
