package Controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.NavigationBar;
import Entities.ChangeRequest;
import Entities.PhaseType;
import Protocol.Command;
import Protocol.SRMessageFunc;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;


/**
 * This class provides the user of the list of requests that he/she issued, with the ability to view information about the request and it's current status.
 * 
 * @author Bshara
 * */
public class ListOfRequestsController implements Initializable {

	private static final String MY_ISSUED_REQUEST_DETAILS_FXML = "MyIssuedRequestDetails.fxml";

	private static final String GET_MY_ISSUED_REQUESTS = "GetMyIssuedRequests";

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
	private TableView<TableDataRequests> tblSupervisorRequests;

	@FXML
	private TableColumn<TableDataRequests, String> tcRequestID;

	@FXML
	private TableColumn<TableDataRequests, String> tcIssueDate;

	@FXML
	private TableColumn<TableDataRequests, String> tcCurrentPhase;

	@FXML
	private TableColumn<TableDataRequests, String> tcStatus;

	@FXML
	private TableColumn<TableDataRequests, String> tcDeadline;
	
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
	private ArrayList<ChangeRequest> myIssuedRequests;
	public static ChangeRequest lastSelectedRequest;

	private int currentRowIndex = 0;
	private int countOfRequests;
	private int rowCountLimit = 16;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initTable();
		imgRefresh.setOnMousePressed(event->{
			NavigationBar.reload();
		});
		tableButtons = new ArrayList<Node>();

		ControllerManager.addListener(tfSeachByReqId, str -> {
			long id = Integer.parseInt(str);
			ArrayList<ChangeRequest> temp = (ArrayList<ChangeRequest>) myIssuedRequests.clone();
			temp.removeIf(p -> !(p.getRequestID() + "").startsWith(id + ""));
			loadRequestToTable(temp);
		});
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


		imgForward.setOnMousePressed(event -> {

			if (currentRowIndex + rowCountLimit < countOfRequests) {
				currentRowIndex += rowCountLimit;
				getMyIssuedRequests();
			}
		});

		imgBack.setOnMousePressed(event -> {

			if (currentRowIndex - rowCountLimit >= 0) {
				currentRowIndex -= rowCountLimit;
				getMyIssuedRequests();
			}

		});

		getMyIssuedRequestsCount();
	}

	private void getMyIssuedRequestsCount() {
		Client.getInstance().requestWithListener(Command.GetMyIssuedRequestsCount, onGettingMyIssuedRequestsCount,
				GET_COUNT_OF_REQUESTS, "`username`='" + ClientGUI.systemUser.getUserName() + "'");
	}

	private SRMessageFunc onGettingMyIssuedRequestsCount = srMsg -> {

		if (srMsg.getCommand() == Command.GetMyIssuedRequestsCount) {

			countOfRequests = (int) srMsg.getAttachedData()[0];
			System.out.println("Count of requests is " + countOfRequests);

			if (countOfRequests > 0)
				getMyIssuedRequests();
			else
				loadEmptyRequestsMessage();

			Client.removeMessageRecievedFromServer(GET_COUNT_OF_REQUESTS);

		}

	};

	private void loadEmptyRequestsMessage() {
		// TODO Auto-generated method stub

	}

	private void getMyIssuedRequests() {
		Client.getInstance().requestWithListener(Command.GetMyIssuedRequests, onGettingMyRequests,
				GET_MY_ISSUED_REQUESTS, ClientGUI.systemUser.getUserName(), currentRowIndex, rowCountLimit);
	}

	private SRMessageFunc onGettingMyRequests = srMsg -> {

		if (srMsg.getCommand() == Command.GetMyIssuedRequests) {

			myIssuedRequests = (ArrayList<ChangeRequest>) srMsg.getAttachedData()[0];

			int size = myIssuedRequests.size();

			loadRequestToTable(myIssuedRequests);

			if (myIssuedRequests.size() < rowCountLimit) {
				txtRequestsCount
						.setText((currentRowIndex + 1) + "-" + (currentRowIndex + size) + " of " + countOfRequests);
			} else {
				txtRequestsCount.setText(
						(currentRowIndex + 1) + "-" + (currentRowIndex + rowCountLimit) + " of " + countOfRequests);

			}

			Client.removeMessageRecievedFromServer(GET_MY_ISSUED_REQUESTS);
		}
	};

	private void loadRequestToTable(ArrayList<ChangeRequest> changeRequests) {

		ArrayList<TableDataRequests> strs = new ArrayList<TableDataRequests>();

		for (ChangeRequest changeRequest : changeRequests) {

			strs.add(new TableDataRequests(changeRequest.getRequestID() + "",

					ControllerManager.getDateTime(changeRequest.getDateOfRequest()),
					ControllerManager.getDateTimeDiff(changeRequest.getEndDateOfRequest(),
							changeRequest.getDateOfRequest()) + "",
					"Active", ControllerManager.getDateTime(changeRequest.getEndDateOfRequest())));
		}

		addContentToTable(strs);
	}

	private void initTable() {

		tcRequestID.setCellValueFactory(new PropertyValueFactory<TableDataRequests, String>("S1"));
		tcIssueDate.setCellValueFactory(new PropertyValueFactory<TableDataRequests, String>("S2"));
		tcCurrentPhase.setCellValueFactory(new PropertyValueFactory<TableDataRequests, String>("S3"));
		tcStatus.setCellValueFactory(new PropertyValueFactory<TableDataRequests, String>("S4"));
		tcDeadline.setCellValueFactory(new PropertyValueFactory<TableDataRequests, String>("S5"));

	}

	private void addContentToTable(ArrayList<TableDataRequests> strs) {

		tblSupervisorRequests.setItems(FXCollections.observableArrayList(strs));

	}

	public class TableDataRequests {
		public String s1, s2, s3, s4, s5;

		public TableDataRequests(String s1, String s2, String s3, String s4, String s5) {
			this.s1 = s1;
			this.s2 = s2;
			this.s3 = s3;
			this.s4 = s4;
			this.s5 = s5;
		}

		public String getS1() {
			return s1;
		}

		public String getS2() {
			return s2;
		}

		public String getS3() {
			return s3;
		}

		public String getS4() {
			return s4;
		}

		public String getS5() {
			return s5;
		}

		@Override
		public String toString() {
			return "TableDataRequests [s1=" + s1 + ", s2=" + s2 + ", s3=" + s3 + ", s4=" + s4 + ", s5=" + s5 + "]";
		}

	}

	@FXML
	public void clickItem(MouseEvent event) {
		if (event.getClickCount() == 2) // Checking double click
		{
			int selectedIndex = tblSupervisorRequests.getSelectionModel().getSelectedIndex();
			if (selectedIndex != -1) {
				lastSelectedRequest = myIssuedRequests.get(selectedIndex);

				NavigationBar.next("Request Details", MY_ISSUED_REQUEST_DETAILS_FXML);

			}
		}
	}

	@FXML
	void onMouseEntered(MouseEvent event) {

	}

}
