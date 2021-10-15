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
 * This class contains the tables of the supervisor, examiner, executer, evaluator and decision maker.
 * it provides the ability to traverse between jobs and select a phase from the table to begin the request treatment.
 * 
 * @author Bshara
 * */
public class ListOfRequestsForTreatmentController implements Initializable {

	private static final String GET_SYSTEM_USER_BY_REQUEST_LIST_OF_REQUESTS = "getSystemUserByRequestListOfRequests";

	private static final String GET_COUNT_OF_PHASES_TYPES = "GetCountOfPhasesTypes";

	private static final String GET_MY_REQUESTS_AS_SUPERVISOR = "GetMyRequestsAsSupervisor";

	private static final String GET_COUNT_OF_REQUESTS = "GET_COUINT_RESQUEST234256354";

	private static final String GET_REQS_LIST_CTRL = "dwad2414r2rr";

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private AnchorPane mainAnchor;

	@FXML
	private TableView<XTableDataX> tblSupervisorOnly;

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
	private TableView<TableDataRequests> tblSupervisorRequests;

	@FXML
	private TableColumn<TableDataRequests, String> tcIssuedBy;

	@FXML
	private TableColumn<TableDataRequests, String> tcPhaseStatus;

	@FXML
	private TableColumn<TableDataRequests, String> tcPhaseStartingDate;

	@FXML
	private TableColumn<TableDataRequests, String> tcPhaseDeadline;

	@FXML
	private TableColumn<TableDataRequests, String> tcPhaseTimeLeft;

	@FXML
	private TableColumn<TableDataRequests, String> tcHasBeenTimeExtended;

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
	private Line lineBottomJobs;

	@FXML
	private HBox hbRequestsType;

	@FXML
	private HBox apSupervise;

	@FXML
	private HBox apAnalyze;

	@FXML
	private HBox apDecide;

	@FXML
	private HBox apExecute;

	@FXML
	private HBox apExamine;

	@FXML
	private TextField tfSeachByReqId;

	@FXML
	private Line lineTableJob;

	private ArrayList<Node> tableButtons, jobs;

	public static boolean disableAllJobs = false;
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
		System.out.println("Init: ListOfRequestsForTreatmentController");

		ClientGUI.addOnMenuBtnClickedEvent(getClass().getName() + "3232145125", () -> {
			System.out.println("Finalize: ListOfRequestsForTreatmentController");

			Client.removeMessageRecievedFromServer(GET_COUNT_OF_REQUESTS + 2);
			Client.removeMessageRecievedFromServer(GET_MY_REQUESTS_AS_SUPERVISOR);
			Client.removeMessageRecievedFromServer(GET_COUNT_OF_PHASES_TYPES);
		});

		initTable();
		initXTableX();
		
		imgRefresh.setOnMousePressed(event->{
			NavigationBar.reload();
		});

		ControllerManager.addListener(tfSeachByReqId, str -> {
			long id = Integer.parseInt(str);
			ArrayList<ChangeRequest> temp = (ArrayList<ChangeRequest>) myRequests.clone();
			temp.removeIf(p -> !(p.getRequestID() + "").startsWith(id + ""));
			
			
			if (tblSupervisorOnly.isDisabled()) {
				loadIntoTable(temp);

			}else {
				loadIntoXTableX(temp);

			}
		});

		requestTypesAPs = ControllerManager.getAllNodes(hbRequestsType);

		tableButtons = new ArrayList<Node>();
		jobs = new ArrayList<Node>();

		jobs.addAll(hbRequestsType.getChildren());

		tableButtons.add(imgSearch);
		tableButtons.add(imgSettings);
		tableButtons.add(imgRefresh);
		tableButtons.add(imgMenu);

		tableButtons.add(imgBack);
		tableButtons.add(imgForward);

		// Set the on mouse pressed event for the jobs
		for (Node node : jobs) {
			node.setOnMousePressed(event -> {
				selectNode(node);
			});
			node.setOnMouseEntered(event -> {
				ControllerManager.setEffectConditioned(node, CommonEffects.REQUESTS_TABLE_ELEMENT_BLACK,
						CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE);
			});
			node.setOnMouseExited(event -> {
				ControllerManager.setEffectConditioned(node, CommonEffects.REQUESTS_TABLE_ELEMENT_GRAY,
						CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE);
			});
		}

		ControllerManager.setEffect(lineTableJob, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE);

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

		ControllerManager.setEffect(jobs, CommonEffects.REQUESTS_TABLE_ELEMENT_GRAY);
		ControllerManager.setEffect(tableButtons, CommonEffects.REQUESTS_TABLE_ELEMENT_GRAY);

		Client.addMessageRecievedFromServer(GET_REQS_LIST_CTRL, srMsg -> {

			if (srMsg.getCommand() == Command.GetMyRequests) {

				PhaseType requestType = (PhaseType) srMsg.getAttachedData()[0];
				myRequests = (ArrayList<ChangeRequest>) srMsg.getAttachedData()[1];

				int size = myRequests.size();
				txtRequestsCount.setText("Size: " + size);

				boolean isSupervision = requestType == PhaseType.Supervision;

				tblSupervisorOnly.setVisible(isSupervision);
				tblSupervisorOnly.setDisable(!isSupervision);

				tblSupervisorRequests.setVisible(!isSupervision);
				tblSupervisorRequests.setDisable(isSupervision);

				switch (requestType) {

				case Supervision:

					loadIntoXTableX(myRequests);
					break;

				case Examination:
					loadIntoTable(myRequests);

					break;

				case Execution:
					loadIntoTable(myRequests);

					break;

				case Decision:
					loadIntoTable(myRequests);

					break;

				case Evaluation:
					loadIntoTable(myRequests);

					break;

				default:
					break;
				}
			}
		});

		ArrayList<Node> nodes = ControllerManager.getAllNodes(hbRequestsType);
		nodes.add(lineTableJob);
		nodes.add(lineBottomJobs);
		for (Node node : nodes) {
			node.setDisable(disableAllJobs);
			node.setOpacity(disableAllJobs ? 0 : 1);
		}

		if (disableAllJobs) {
			NavigationBar.setCurrentPage("Request Details", FxmlNames.REQUEST_DETAILS_MY);
		}

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

		ControllerManager.installTooltip(apAnalyze,
				"This apprease because you are responsible on a number of requests for evaluation");

		allowExistingTabsOnly();

	}

	private void allowExistingTabsOnly() {

		Client.addMessageRecievedFromServer(GET_COUNT_OF_PHASES_TYPES, srMsg -> {

			if (srMsg.getCommand() == Command.getCountOfPhasesTypes) {

				int cntSupervision = (int) srMsg.getAttachedData()[0];
				int cntEvaluation = (int) srMsg.getAttachedData()[1];
				int cntDecision = (int) srMsg.getAttachedData()[2];
				int cntExecution = (int) srMsg.getAttachedData()[3];
				int cntExamination = (int) srMsg.getAttachedData()[4];

				ArrayList<Node> newNodesForRequestTypes = new ArrayList<Node>();

				for (Node node : requestTypesAPs) {
					if (node instanceof HBox) {
						Node textNode = ((HBox) node).getChildren().get(1);
						String text = ((Text) textNode).getText();
						switch (text) {
						case "Supervise":

							if (cntSupervision > 0 && ClientGUI.isSupervisor) {
								newNodesForRequestTypes.add(node);
							}

							break;
						case "Evaluate":

							if (cntEvaluation > 0 && ClientGUI.empNumber != -1) {
								newNodesForRequestTypes.add(node);
							}

							break;
						case "Decide":

							if (cntDecision > 0 && ClientGUI.isCommitteeMember) {
								newNodesForRequestTypes.add(node);
							}

							break;
						case "Execute":

							if (cntExecution > 0 && ClientGUI.empNumber != -1) {
								newNodesForRequestTypes.add(node);
							}

							break;
						case "Examine":

							if (cntExamination > 0 && ClientGUI.empNumber != -1) {
								newNodesForRequestTypes.add(node);
							}

							break;
						default:
							System.err.println("Error, case not defined! [allowExistingTabsOnly]");
							break;
						}
					}
				}

				hbRequestsType.getChildren().clear();
				hbRequestsType.getChildren().setAll(FXCollections.observableArrayList(newNodesForRequestTypes));

				int numOfReq = hbRequestsType.getChildren().size();
				if (numOfReq == 0) {
					lineTableJob.setVisible(false);
				} else {
					lineTableJob.setVisible(true);

				}
				if (newNodesForRequestTypes.size() > 0) {
					selectNode(newNodesForRequestTypes.get(0));
				}
			}

		});

		Client.getInstance().request(Command.getCountOfPhasesTypes, ClientGUI.empNumber);

	}

	private void loadIntoTable(ArrayList<ChangeRequest> myRequests) {
		ArrayList<TableDataRequests> tableContent = new ArrayList<TableDataRequests>();

		for (ChangeRequest cr : myRequests) {

			Phase ph = cr.getPhases().get(0);

			String issuedBy = cr.getRequestID() + "";
			String phaseStatus = ph.getStatus();
			String phaseStartingDate = ControllerManager.getDateTime(ph.getStartingDate());// DateUtil.toString(ph.getStartingDate());
			String phaseDeadline = ph.getDeadline().equals(DateUtil.NA) ? "N/A" : DateUtil.toString(ph.getDeadline());
			String phaseTimeLeft = ph.getDeadline().equals(DateUtil.NA) ? "N/A"
					: DateUtil.differenceInDaysHours(ph.getDeadline(), DateUtil.now());
			String hasBeenTimeExtended = ph.isHasBeenTimeExtended() ? "Yes" : "No";

			TableDataRequests tableRow = new TableDataRequests(issuedBy, phaseStatus, phaseStartingDate, phaseDeadline,
					phaseTimeLeft, hasBeenTimeExtended);
			tableContent.add(tableRow);
		}

		tblSupervisorRequests.setItems(FXCollections.observableArrayList(tableContent));

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

		tblSupervisorOnly.setItems(FXCollections.observableArrayList(tableContent));

	}

	private void selectNode(Node node) {

		ControllerManager.setEffect(jobs, CommonEffects.REQUESTS_TABLE_ELEMENT_GRAY);
		ControllerManager.setEffect(node, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE);
		lineTableJob.setStartX(node.getBoundsInParent().getMinX() - 76);
		lineTableJob.setEndX(node.getBoundsInParent().getMaxX() - 76);

		updateRequestsTableByJob(node);
	}

	private void updateRequestsTableByJob(Node node) {
		Node textNode = ((HBox) node).getChildren().get(1);
		String text = ((Text) textNode).getText();

		switch (text) {
		case "Supervise":
			Client.getInstance().request(Command.GetMyRequests, PhaseType.Supervision, ClientGUI.empNumber);
			// This only prepares the next page in case that the user has double clicked on
			// one of the requests from the table
			NavigationBar.setCurrentPage("Request Details (Supervisor View)", FxmlNames.REQUEST_DETAILS_SUPERVISOR);
			break;
		case "Evaluate":
			Client.getInstance().request(Command.GetMyRequests, PhaseType.Evaluation, ClientGUI.empNumber);
			NavigationBar.setCurrentPage("Request Details (Evaluator View)", FxmlNames.REQUEST_DETAILS_EVALUATE);

			break;
		case "Decide":
			Client.getInstance().request(Command.GetMyRequests, PhaseType.Decision, ClientGUI.empNumber);
			NavigationBar.setCurrentPage("Request Details (Committee Members View)",
					FxmlNames.REQUEST_DETAILS_DECISION);

			break;
		case "Execute":
			Client.getInstance().request(Command.GetMyRequests, PhaseType.Execution, ClientGUI.empNumber);
			NavigationBar.setCurrentPage("Request Details (Executer View)", FxmlNames.REQUEST_DETAILS_EXECUTER);

			break;
		case "Examine":
			Client.getInstance().request(Command.GetMyRequests, PhaseType.Examination, ClientGUI.empNumber);
			NavigationBar.setCurrentPage("Request Details (Examiner View)", FxmlNames.REQUEST_DETAILS_EXAMINER);

			break;
		default:
			System.err.println("Error, case not defined! [updateRequestsTableByJob]");
			break;
		}

	}

	private void initTable() {

		tcHasBeenTimeExtended
				.setCellValueFactory(new PropertyValueFactory<TableDataRequests, String>("hasBeenTimeExtended"));
		tcIssuedBy.setCellValueFactory(new PropertyValueFactory<TableDataRequests, String>("issuedBy"));
		tcPhaseDeadline.setCellValueFactory(new PropertyValueFactory<TableDataRequests, String>("phaseDeadline"));
		tcPhaseStartingDate
				.setCellValueFactory(new PropertyValueFactory<TableDataRequests, String>("phaseStartingDate"));
		tcPhaseStatus.setCellValueFactory(new PropertyValueFactory<TableDataRequests, String>("phaseStatus"));
		tcPhaseTimeLeft.setCellValueFactory(new PropertyValueFactory<TableDataRequests, String>("phaseTimeLeft"));

		tcHasBeenTimeExtended.setSortable(false);
		tcIssuedBy.setSortable(false);
		tcPhaseDeadline.setSortable(false);
		tcPhaseStartingDate.setSortable(false);
		tcPhaseStatus.setSortable(false);
		tcPhaseTimeLeft.setSortable(false);

	}

	public class TableDataRequests {
		public String issuedBy, phaseStatus, phaseStartingDate, phaseDeadline, phaseTimeLeft, hasBeenTimeExtended;

		public TableDataRequests(String issuedBy, String phaseStatus, String phaseStartingDate, String phaseDeadline,
				String phaseTimeLeft, String hasBeenTimeExtended) {
			super();
			this.issuedBy = issuedBy;
			this.phaseStatus = phaseStatus;
			this.phaseStartingDate = phaseStartingDate;
			this.phaseDeadline = phaseDeadline;
			this.phaseTimeLeft = phaseTimeLeft;
			this.hasBeenTimeExtended = hasBeenTimeExtended;
		}

		public String getIssuedBy() {
			return issuedBy;
		}

		public void setIssuedBy(String issuedBy) {
			this.issuedBy = issuedBy;
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

		public String getPhaseTimeLeft() {
			return phaseTimeLeft;
		}

		public void setPhaseTimeLeft(String phaseTimeLeft) {
			this.phaseTimeLeft = phaseTimeLeft;
		}

		public String getHasBeenTimeExtended() {
			return hasBeenTimeExtended;
		}

		public void setHasBeenTimeExtended(String hasBeenTimeExtended) {
			this.hasBeenTimeExtended = hasBeenTimeExtended;
		}

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
	public void clickItem(MouseEvent event) {
		if (event.getClickCount() == 2) // Checking double click
		{
			int selectedIndex;
			if (tblSupervisorRequests.isDisabled()) {
				selectedIndex = tblSupervisorOnly.getSelectionModel().getSelectedIndex();

			} else {
				selectedIndex = tblSupervisorRequests.getSelectionModel().getSelectedIndex();

			}

			if (selectedIndex != -1) {
				lastSelectedRequest = myRequests.get(selectedIndex);

				// Set a listener for the requests list from the server.

				Client.addMessageRecievedFromServer(GET_SYSTEM_USER_BY_REQUEST_LIST_OF_REQUESTS, srMsg -> {
					if (srMsg.getCommand() == Command.getSystemUserByRequest) {
						lastSelectedRequestOwner = (SystemUser) srMsg.getAttachedData()[0];
						NavigationBar.next(NavigationBar.getNextPage().getPageTitle(),
								NavigationBar.getNextPage().getPageLocation());
						Client.removeMessageRecievedFromServer(GET_SYSTEM_USER_BY_REQUEST_LIST_OF_REQUESTS);
					}
				});

				Client.getInstance().request(Command.getSystemUserByRequest, lastSelectedRequest.getRequestID());
				// go to next page after receiving the request id from the server.

			}
		}
	}

	@FXML
	void onMouseEntered(MouseEvent event) {

	}

}
