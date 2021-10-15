package Controllers;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.ChangeRequest;
import Entities.Phase;
import Entities.PhaseType;
import Entities.SystemUser;
import Protocol.Command;
import Utility.AppManager;
import Utility.DateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;


/**
 * This class provides the manager with the ability to view the information about the requests with their current status and the ability to unfreeze a request.
 * also the manager can view the changes that were made by the supervisor, changes are things like changing the deadline of a request.
 * 
 * @author Bshara
 * */
public class ListOfRequestsForManagerController implements Initializable {

	private static final String GET_SYSTEM_USER_BY_REQUEST_LIST_OF_REQUESTS = "getSy21452stemUserByRequestListOfRequests";

	private static final String GET_COUNT_OF_PHASES_TYPES = "GetCountO3252352fPhasesTypes";

	private static final String GET_REQS_LIST_CTRL = "dwad24112321454r2rr";

	@FXML
	private TableView<XTableDataX> tblManagerOnly;

	@FXML
	private TableColumn<XTableDataX, String> tcXRequestId;

	@FXML
	private TableColumn<XTableDataX, String> tcXPhaseName;

	@FXML
	private TableColumn<XTableDataX, String> tcXPhaseStatus;

	@FXML
	private TableColumn<XTableDataX, String> tcXPhaseStartingDate;

	@FXML
	private TableColumn<XTableDataX, String> tcXPhaseDeadline;

	@FXML
	private TableColumn<XTableDataX, String> tcXPhaseTimeLeft;

	@FXML
	private TableColumn<XTableDataX, String> tcXHasBeenTimeExtended;

	@FXML
	private ImageView imgSearch;

	@FXML
	private Text txtPageHeader;

	@FXML
	private ImageView imgBack;

	@FXML
	private ImageView imgForward;

	@FXML
	private Text txtRequestsCount;

	@FXML
	private ImageView imgSettings;

	@FXML
	private ImageView imgRefresh;

	@FXML
	private ImageView imgMenu;

	@FXML
	private TextField tfSeachByReqId;
	private ArrayList<Node> tableButtons;

	@FXML
	private HBox hbChangesDoneBySupervisor;

	public static PhaseType phaseType;

	private int currentRowIndex = 0;
	private int countOfRequests;
	private int rowCountLimit = 16;
	private ArrayList<Node> requestTypesAPs;

	private ArrayList<ChangeRequest> myRequests;

	public static ChangeRequest lastSelectedRequest;
	public static SystemUser lastSelectedRequestOwner;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		imgRefresh.setOnMousePressed(event -> {
			NavigationBar.reload();
		});
		initXTableX();
		ControllerManager.addListener(tfSeachByReqId, str -> {
			long id = Integer.parseInt(str);
			ArrayList<ChangeRequest> temp = (ArrayList<ChangeRequest>) myRequests.clone();
			temp.removeIf(p -> !(p.getRequestID() + "").startsWith(id + ""));
			loadIntoXTableX(temp);
		});
		tableButtons = new ArrayList<Node>();

		tableButtons.add(imgSearch);
		tableButtons.add(imgSettings);
		tableButtons.add(imgRefresh);
		tableButtons.add(imgMenu);

		tableButtons.add(imgBack);
		tableButtons.add(imgForward);

		// Set the on mouse pressed even for the table buttons
		for (Node node : tableButtons) {
			node.setCursor(Cursor.HAND);
			node.setOnMouseEntered(event -> {
				ControllerManager.setEffect(node, CommonEffects.REQUESTS_TABLE_ELEMENT_BLACK);
			});
			node.setOnMouseExited(event -> {
				ControllerManager.setEffect(node, CommonEffects.REQUESTS_TABLE_ELEMENT_GRAY);
			});
		}

		ControllerManager.setEffect(tableButtons, CommonEffects.REQUESTS_TABLE_ELEMENT_GRAY);

		// Set a listener for the requests list from the server.

		Client.addMessageRecievedFromServer(GET_REQS_LIST_CTRL, srMsg -> {

			if (srMsg.getCommand() == Command.GetMyRequests) {

				PhaseType requestType = (PhaseType) srMsg.getAttachedData()[0];
				myRequests = (ArrayList<ChangeRequest>) srMsg.getAttachedData()[1];

				int size = myRequests.size();
				txtRequestsCount.setText("Size: " + size);

				boolean isSupervision = requestType == PhaseType.Supervision;

				tblManagerOnly.setVisible(isSupervision);
				tblManagerOnly.setDisable(!isSupervision);

				switch (requestType) {

				case Supervision:

					loadIntoXTableX(myRequests);
					break;

				default:
					System.err.println("Error at manager requests view, unkown requestType " + requestType.name());
					break;
				}
			}
		});

		imgForward.setOnMousePressed(event -> {

			if (currentRowIndex + rowCountLimit < countOfRequests) {
				currentRowIndex += rowCountLimit;
				Client.getInstance().request(Command.GetMyRequests, ClientGUI.systemUser.getUserName(),
						PhaseType.myRequests, currentRowIndex, rowCountLimit);
				txtRequestsCount.setText(
						(currentRowIndex + 1) + "-" + (currentRowIndex + rowCountLimit) + " of " + countOfRequests);

			}
		});

		imgBack.setOnMousePressed(event -> {

			if (currentRowIndex - rowCountLimit >= 0) {
				currentRowIndex -= rowCountLimit;
				Client.getInstance().request(Command.GetMyRequests, ClientGUI.systemUser.getUserName(),
						PhaseType.myRequests, currentRowIndex, rowCountLimit);

				txtRequestsCount.setText(
						(currentRowIndex + 1) + "-" + (currentRowIndex + rowCountLimit) + " of " + countOfRequests);
			}

		});

		hbChangesDoneBySupervisor.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbChangesDoneBySupervisor, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbChangesDoneBySupervisor, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		
		hbChangesDoneBySupervisor.setOnMousePressed(event -> {
			NavigationBar.next("Changes Done by Supervisor", FxmlNames.CHANGES_DONE_BY_SUPERVISOR);
		});

		checkForRequests();
	}

	private void checkForRequests() {

		Client.addMessageRecievedFromServer(GET_COUNT_OF_PHASES_TYPES, srMsg -> {

			if (srMsg.getCommand() == Command.getCountOfPhasesTypes) {

				int cntSupervision = (int) srMsg.getAttachedData()[0];
				if (cntSupervision > 0) {
					Client.getInstance().request(Command.GetMyRequests, PhaseType.Supervision, ClientGUI.empNumber);
					NavigationBar.setCurrentPage("Request Details (Supervisor View)",
							FxmlNames.REQUEST_DETAILS_SUPERVISOR);
				} else {

					ControllerManager.showInformationMessage("No requests", "You have no requests",
							"There are no requests in the system, going back to the previous page", () -> {
								NavigationBar.back(true);

							});
				}

			}

		});

		Client.getInstance().request(Command.getCountOfPhasesTypes, ClientGUI.empNumber);

	}

	private void loadIntoXTableX(ArrayList<ChangeRequest> myRequests) {
		ArrayList<XTableDataX> tableContent = new ArrayList<XTableDataX>();

		for (ChangeRequest cr : myRequests) {

			Phase ph = cr.getPhases().get(0);

			String phaseStatus = ph.getStatus();
			String phaseStartingDate = ControllerManager.getDateTime(ph.getStartingDate());// DateUtil.toString(ph.getStartingDate());
			String phaseDeadline = ph.getDeadline().equals(DateUtil.NA) ? "N/A" : DateUtil.toString(ph.getDeadline());
			String timeLeftForPhase = ph.getDeadline().equals(DateUtil.NA) ? "N/A"
					: DateUtil.differenceInDaysHours(ph.getDeadline(), DateUtil.now());
			String hasBeenTimeExtended = ph.isHasBeenTimeExtended() ? "Yes" : "No";

			String requestId = cr.getRequestID() + "";
			String phase = ph.getPhaseName() + "";

			XTableDataX tableRow = new XTableDataX(requestId, phase, phaseStatus, phaseStartingDate, phaseDeadline,
					timeLeftForPhase, hasBeenTimeExtended);
			tableContent.add(tableRow);
		}

		tblManagerOnly.setItems(FXCollections.observableArrayList(tableContent));

	}

	private void initXTableX() {

		tcXHasBeenTimeExtended
				.setCellValueFactory(new PropertyValueFactory<XTableDataX, String>("hasBeenTimeExtended"));
		tcXPhaseDeadline.setCellValueFactory(new PropertyValueFactory<XTableDataX, String>("phaseDeadline"));
		tcXPhaseName.setCellValueFactory(new PropertyValueFactory<XTableDataX, String>("phase"));
		tcXPhaseStartingDate.setCellValueFactory(new PropertyValueFactory<XTableDataX, String>("phaseStartingDate"));
		tcXPhaseStatus.setCellValueFactory(new PropertyValueFactory<XTableDataX, String>("phaseStatus"));
		tcXPhaseTimeLeft.setCellValueFactory(new PropertyValueFactory<XTableDataX, String>("timeLeftForPhase"));
		tcXRequestId.setCellValueFactory(new PropertyValueFactory<XTableDataX, String>("requestId"));

		tcXHasBeenTimeExtended.setSortable(false);
		tcXPhaseDeadline.setSortable(false);
		tcXPhaseName.setSortable(false);
		tcXPhaseStartingDate.setSortable(false);
		tcXPhaseStatus.setSortable(false);
		tcXPhaseTimeLeft.setSortable(false);
		tcXRequestId.setSortable(false);

	}

	public class XTableDataX {
		String requestId, phase, phaseStatus, phaseStartingDate, phaseDeadline, timeLeftForPhase, hasBeenTimeExtended;

		public XTableDataX(String requestId, String phase, String phaseStatus, String phaseStartingDate,
				String phaseDeadline, String timeLeftForPhase, String hasBeenTimeExtended) {
			super();
			this.requestId = requestId;
			this.phase = phase;
			this.phaseStatus = phaseStatus;
			this.phaseStartingDate = phaseStartingDate;
			this.phaseDeadline = phaseDeadline;
			this.timeLeftForPhase = timeLeftForPhase;
			this.hasBeenTimeExtended = hasBeenTimeExtended;
		}

		public String getRequestId() {
			return requestId;
		}

		public void setRequestId(String requestId) {
			this.requestId = requestId;
		}

		public String getPhase() {
			return phase;
		}

		public void setPhase(String phase) {
			this.phase = phase;
		}

		public String getPhaseStatus() {
			return phaseStatus;
		}

		public void setPhaseStatus(String phaseStatus) {
			this.phaseStatus = phaseStatus;
		}

		public String getPhaseStartingDate() {
			return phaseStartingDate;
		}

		public void setPhaseStartingDate(String phaseStartingDate) {
			this.phaseStartingDate = phaseStartingDate;
		}

		public String getPhaseDeadline() {
			return phaseDeadline;
		}

		public void setPhaseDeadline(String phaseDeadline) {
			this.phaseDeadline = phaseDeadline;
		}

		public String getTimeLeftForPhase() {
			return timeLeftForPhase;
		}

		public void setTimeLeftForPhase(String timeLeftForPhase) {
			this.timeLeftForPhase = timeLeftForPhase;
		}

		public String getHasBeenTimeExtended() {
			return hasBeenTimeExtended;
		}

		public void setHasBeenTimeExtended(String hasBeenTimeExtended) {
			this.hasBeenTimeExtended = hasBeenTimeExtended;
		}

	}

	@FXML
	public void clickItemManager(MouseEvent event) {
		if (event.getClickCount() == 2) // Checking double click
		{

			int selectedIndex = tblManagerOnly.getSelectionModel().getSelectedIndex();

			if (selectedIndex != -1) {
				lastSelectedRequest = myRequests.get(selectedIndex);

				Client.addMessageRecievedFromServer(GET_SYSTEM_USER_BY_REQUEST_LIST_OF_REQUESTS, srMsg -> {
					if (srMsg.getCommand() == Command.getSystemUserByRequest) {
						lastSelectedRequestOwner = (SystemUser) srMsg.getAttachedData()[0];
						NavigationBar.next("Requests (Manager View)", FxmlNames.REQUEST_DETAILS_MANAGER);

						Client.removeMessageRecievedFromServer(GET_SYSTEM_USER_BY_REQUEST_LIST_OF_REQUESTS);
					}
				});

				Client.getInstance().request(Command.getSystemUserByRequest, lastSelectedRequest.getRequestID());

			}
		}
	}

	@FXML
	void onMouseEntered(MouseEvent event) {

	}

}
