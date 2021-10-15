package Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.ControllerSwapper;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.Message;
import Protocol.Command;
import Protocol.MsgReturnType;
import Protocol.SRMessageFunc;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * This class provides the user with a list of messages that were received from
 * the system or from other user, this class also provides the user with the ability to delete messages and star them.
 * 
 * @author Bshara
 */
public class ListOfMessagesController implements Initializable {

	private static final String GET_COUNT_OF_MESSAGES = "GetCountOfMessages";

	private static final String MESSAGES_DELETED_LIST_OF_MESSAGES_RESPONSE = "messagesDeletedListOfMessagesResponse";

	private static final String GET_MESSAGES_PRIMARY_LIST_OF_MESSAGES = "getMessagesPrimaryListOfMessages";

	public static Message selectedMessage;

	@FXML
	private ImageView imgSearch;

	@FXML
	private Text txtPageHeader;

	@FXML
	private ImageView imgBack;

	@FXML
	private ImageView imgForward;

	@FXML
	private ImageView imgSettings;

	@FXML
	private ImageView imgRefresh;

	@FXML
	private ImageView imgArchive;

	@FXML
	private ImageView imgTrashBin;

	@FXML
	private ImageView imgMarkAsRead;

	@FXML
	private ImageView imgThreeDots;

	@FXML
	private HBox hbPrimary;

	@FXML
	private HBox hbUpdates;

	@FXML
	private HBox hbStaff;

	@FXML
	private HBox hbWork;

	@FXML
	private Line lineTableJob;

	@FXML
	private VBox hbMessagesContainer;

	@FXML
	private Text txtMessagesCount;

	private ArrayList<Node> buttons, messageTypes;

	private ArrayList<MessageEntryController> msgEntryControllers;

	private int currentRowIndex = 0;
	private int countOfMessages;
	private int rowCountLimit = 10;
	private int selectedMessagesCount = 0;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Init: ListOfMessagesController");

		msgEntryControllers = new ArrayList<MessageEntryController>();
		buttons = new ArrayList<Node>();
		messageTypes = new ArrayList<Node>();

		buttons.add(imgArchive);
		buttons.add(imgBack);
		buttons.add(imgForward);
		buttons.add(imgMarkAsRead);
		buttons.add(imgRefresh);
		buttons.add(imgSearch);
		buttons.add(imgSettings);
		buttons.add(imgThreeDots);
		buttons.add(imgTrashBin);

		messageTypes.add(hbStaff);
		messageTypes.add(hbPrimary);

		messageTypes.add(hbUpdates);
		messageTypes.add(hbWork);

		imgRefresh.setOnMousePressed(event -> {

			NavigationBar.reload();
		});

		for (Node node : buttons) {
			ControllerManager.setMouseHoverPressEffects(node, CommonEffects.REQUEST_DETAILS_BUTTON_BLACK,
					CommonEffects.REQUEST_DETAILS_BUTTON_GRAY, CommonEffects.REQUEST_DETAILS_BUTTON_BLUE, Cursor.HAND);
		}

		for (Node node : messageTypes) {
			ControllerManager.setMouseHoverPressEffects(node, CommonEffects.REQUEST_DETAILS_BUTTON_BLACK,
					CommonEffects.REQUEST_DETAILS_BUTTON_GRAY, CommonEffects.REQUEST_DETAILS_BUTTON_BLUE, messageTypes,
					Cursor.HAND);
		}

		ControllerManager.setMouseHoverPressEffects(imgTrashBin, CommonEffects.REQUEST_DETAILS_BUTTON_RED,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY, CommonEffects.REQUEST_DETAILS_BUTTON_BLACK, messageTypes,
				Cursor.HAND);

		imgTrashBin.setOnMousePressed(event -> {
			boolean hasAtleastOneSelected = false;
			selectedMessagesCount = 0;
			for (MessageEntryController messageEntryController : msgEntryControllers) {
				if (messageEntryController.checked) {
					hasAtleastOneSelected = true;
					selectedMessagesCount++;
				}
			}

			if (hasAtleastOneSelected) {
				ControllerManager.showOkCancelMessage("Delete", "Delete messages",
						"Are you sure you want to delete " + selectedMessagesCount + " messages?", () -> {
							ArrayList<Message> messagesToDelete = new ArrayList<Message>();

							for (int i = 0; i < msgEntryControllers.size(); i++) {
								MessageEntryController cc = msgEntryControllers.get(i);

								if (cc.checked) {
									messagesToDelete.add(cc.getMessage());
									cc.deleteSelf();
								}
							}
							selectedMessagesCount = 0;
							Client.getInstance().requestWithListener(Command.deleteObjects, countOfObjectsFunc,
									GET_COUNT_OF_MESSAGES, messagesToDelete);
						}, null);
			}

		});

		ControllerManager.setEffect(lineTableJob, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE);
		ControllerManager.setEffect(hbPrimary, CommonEffects.REQUEST_DETAILS_BUTTON_BLUE);

//		 Client.getInstance().request(Command.getMessagesPrimary, new
//		 SeriObject(ClientGUI.userName));

		imgForward.setOnMousePressed(event -> {

			if (currentRowIndex + rowCountLimit < countOfMessages) {
				currentRowIndex += rowCountLimit;
				Client.getInstance().requestWithListener(Command.getMessagesPrimary, getMessagesPrimaryFunc,
						GET_MESSAGES_PRIMARY_LIST_OF_MESSAGES, ClientGUI.systemUser.getUserName(), currentRowIndex,
						rowCountLimit);
				txtMessagesCount.setText(
						(currentRowIndex + 1) + "-" + (currentRowIndex + rowCountLimit) + " of " + countOfMessages);

			}
		});

		imgBack.setOnMousePressed(event -> {

			if (currentRowIndex - rowCountLimit >= 0) {
				currentRowIndex -= rowCountLimit;
				Client.getInstance().requestWithListener(Command.getMessagesPrimary, getMessagesPrimaryFunc,
						GET_MESSAGES_PRIMARY_LIST_OF_MESSAGES, ClientGUI.systemUser.getUserName(), currentRowIndex,
						rowCountLimit);

				txtMessagesCount.setText(
						(currentRowIndex + 1) + "-" + (currentRowIndex + rowCountLimit) + " of " + countOfMessages);
			}

		});

		Client.getInstance().requestWithListener(Command.countOfObjects, countOfObjectsFunc, GET_COUNT_OF_MESSAGES,
				"`to`='" + ClientGUI.systemUser.getUserName() + "'", Message.getEmptyInstance());

	}

	private SRMessageFunc getMessagesPrimaryFunc = rsMsg -> {

		if (rsMsg.getCommand() == Command.getMessagesPrimary) {
			ArrayList<Message> msgs = (ArrayList<Message>) rsMsg.getAttachedData()[0];

			int size = msgs.size();

			loadMessages(msgs);

			txtMessagesCount.setText((currentRowIndex + 1) + "-" + (currentRowIndex + size) + " of " + countOfMessages);

			Client.removeMessageRecievedFromServer(GET_MESSAGES_PRIMARY_LIST_OF_MESSAGES);
		}

	};

	private SRMessageFunc countOfObjectsFunc = srMsg -> {

		if (srMsg.getCommand() == Command.countOfObjects) {

			countOfMessages = (int) srMsg.getAttachedData()[0];

			if (countOfMessages > 0) {
				Client.getInstance().requestWithListener(Command.getMessagesPrimary, getMessagesPrimaryFunc,
						GET_MESSAGES_PRIMARY_LIST_OF_MESSAGES, ClientGUI.systemUser.getUserName(), currentRowIndex,
						rowCountLimit);
			} else {
				loadEmptyMessagesWindow();
			}

			Client.removeMessageRecievedFromServer(GET_COUNT_OF_MESSAGES);
		}
	};

	private SRMessageFunc deleteObjectsFunc = rsMsg -> {

		if (rsMsg.getCommand() == Command.deleteObjects) {
			String responseId = "";
			if (responseId instanceof String)
				responseId = (String) rsMsg.getAttachedData()[0];

			if (responseId.compareTo(MESSAGES_DELETED_LIST_OF_MESSAGES_RESPONSE) == 0) {
				// We don't care if the delete was successful; show error message on failure of
				// deletion.
				if ((MsgReturnType) rsMsg.getAttachedData()[1] == MsgReturnType.Failure) {
					ControllerManager.showErrorMessage("Error", "Deletion Error",
							"Server was not able to delete the message!", null);
				}
				Client.getInstance().requestWithListener(Command.countOfObjects, countOfObjectsFunc,
						GET_COUNT_OF_MESSAGES, "`to`='" + ClientGUI.systemUser.getUserName() + "'",
						Message.getEmptyInstance());

				Client.removeMessageRecievedFromServer(MESSAGES_DELETED_LIST_OF_MESSAGES_RESPONSE);
			}

		}

	};

	@Override
	protected void finalize() throws Throwable {
		Client.removeMessageRecievedFromServer(GET_MESSAGES_PRIMARY_LIST_OF_MESSAGES);
		Client.removeMessageRecievedFromServer(MESSAGES_DELETED_LIST_OF_MESSAGES_RESPONSE);
		super.finalize();
	}

	private void loadEmptyMessagesWindow() {

		hbMessagesContainer.getChildren()
				.setAll(ControllerSwapper.getChildrenOf(FxmlNames.MESSAGE_NO_MESSAGES_AVAILABLE_FXML));
		txtMessagesCount.setText("");
	}

	private void loadMessages(ArrayList<Message> msgs) {

		ObservableList<Node> nodes = FXCollections.observableArrayList();

		for (Message message : msgs) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlNames.MESSAGE_ENTRY_FXML));
				AnchorPane pane = loader.load();
				MessageEntryController msgController = (MessageEntryController) loader.getController();
				msgEntryControllers.add(msgController);
				nodes.addAll(pane);

				msgController.setFields(message);
				msgController.setAttachedPane(pane);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		hbMessagesContainer.getChildren().setAll(nodes);

	}

}
