package Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.ControllerSwapper;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.ExecutionReport;
import Protocol.Command;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


/**
 * This class provides a details for examiner where he can approve the execution of the request or report a failure.
 * This class is currently been used for testing purposes, ignore the confusing name.
 * @author Bshara
 * */
public class RequestDetailsGUIControllerV2 implements Initializable {

	@FXML
	private Canvas canvasRight;

	@FXML
	private VBox vbLoadRequestDetails;

	@FXML
	private HBox hbPressToConfirm1;

	@FXML
	private HBox hbPressRequestDetails;

	@FXML
	private HBox hbPressToConfirm;

	@FXML
	private TextArea taFailureDetails;

	@FXML
	private HBox hbPressToSendFailureDetails;

	@FXML
	private Canvas canvasLeft;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		System.out.println("RequestDetailsGUIControllerV2");
		// Apply the effects for the canvas
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);

		ControllerManager.setEffect(hbPressToConfirm, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setEffect(hbPressToSendFailureDetails, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbPressToConfirm.setCursor(Cursor.HAND);

		ControllerManager.setOnHoverEffect(hbPressToConfirm, CommonEffects.REQUEST_DETAILS_BUTTON_GREEN,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbPressToConfirm.setOnMousePressed(event -> {
			onPressConfirm();
		});

		hbPressToSendFailureDetails.setCursor(Cursor.HAND);

		ControllerManager.setOnHoverEffect(hbPressToSendFailureDetails, CommonEffects.REQUEST_DETAILS_BUTTON_RED,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbPressToSendFailureDetails.setOnMousePressed(event -> {
			onPressToSendFailure();
		});

		// ControllerSwapper.loadAnchorContent(vbLoadRequestDetails,
		// FxmlNames.REQUEST_DETAILS);
		
		
		hbPressRequestDetails.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbPressRequestDetails, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbPressRequestDetails, CommonEffects.REQUEST_DETAILS_BUTTON_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		hbPressRequestDetails.setOnMousePressed(event -> {

			System.out.println("hbPressRequestDetails");
			NavigationBar.next("Request Details", FxmlNames.EMPLOYEES);
		});

	}

	private void onPressToSendFailure() {

		long requestID = 0;
		String place = null;
		String contentLT = taFailureDetails.getText();

		if (contentLT.compareTo("") != 0) {
			ExecutionReport executionReport = new ExecutionReport(-1, requestID, contentLT, place);
			Client.getInstance().request(Command.SendExaminerReportOfFailures, executionReport);
		} else {

		}

	}

	private void onPressConfirm() {

	}

}
