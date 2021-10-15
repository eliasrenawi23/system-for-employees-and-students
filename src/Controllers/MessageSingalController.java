package Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.ChangeRequest;
import Entities.Message;
import Entities.SystemUser;
import Protocol.Command;
import Utility.DateUtil;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * This class provides the user with the details about the message, this class is used by the message entry for when
 * the user double clicks on a message to view more details
 * 
 * @author Bshara
 * */
public class MessageSingalController implements Initializable {

	@FXML
	private Canvas canvasRight;

	@FXML
	private HBox hbInformationContainer;

	@FXML
	private Text txtSubject;

	@FXML
	private Text txtFrom;

	@FXML
	private Text txtSentAt;

	@FXML
	private Text txtContent;

	@FXML
	private Text txtLinkedRequest;

	@FXML
	private Canvas canvasLeft;

	private Message msg;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		setMessage(ListOfMessagesController.selectedMessage);
		
		// Apply the effects for the canvas
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);
		
		
		
		
		Client.addMessageRecievedFromServer("MessageRequestingTheChangeRequest", srMsg -> {

			if (srMsg.getCommand() == Command.getChangeRequestFromMessagePage) {

				ChangeRequest cr = (ChangeRequest) srMsg.getAttachedData()[0];
				System.out.println(cr);
				ListOfRequestsForTreatmentController.lastSelectedRequest = cr;

				if (ClientGUI.isSupervisor) {
					NavigationBar.next("Request Details", FxmlNames.REQUEST_DETAILS_SUPERVISOR);
				}
				// TODO

			}
		});
	}

	private void setMessage(Message msg) {
		this.msg = msg;

		txtSubject.setText(msg.getSubject());
		txtContent.setText(msg.getMessageContentLT());
		txtSentAt.setText(DateUtil.toString(msg.getSentAt()));
		txtFrom.setText(msg.getFrom());

		if (msg.getRequestId() != -1) {
			txtLinkedRequest.setOnMousePressed(event -> {

				System.out.println(msg.getRequestId());
				checkIfMessageLinkExpired(msg.getRequestId(), msg.getPhaseId());

			});
		} else {
			txtLinkedRequest.setText("Not Available");
			txtLinkedRequest.setUnderline(false);
			txtLinkedRequest.setFill(Color.BLACK);
			txtLinkedRequest.setCursor(Cursor.NONE);
		}

	}

	private void checkIfMessageLinkExpired(long requestId, long phaseId) {
		String key = getClass().getName() + phaseId;
		Client.addMessageRecievedFromServer(key, srMsg -> {

			if (srMsg.getCommand() == Command.checkIfPhaseIsWaiting) {

				boolean isWaiting = (boolean) srMsg.getAttachedData()[0];

				if (!isWaiting) {
					ControllerManager.showInformationMessage("Message Expired", "Message has been expired",
							"This message link is no longer available", null);
				} else {
					Client.getInstance().request(Command.getChangeRequestFromMessagePage, requestId);
				}

				Client.removeMessageRecievedFromServer(key);
			}
		});

		Client.getInstance().request(Command.checkIfPhaseIsWaiting, phaseId);
	}
}
