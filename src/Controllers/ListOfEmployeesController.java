package Controllers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import ClientLogic.Client;
import Controllers.ListOfRequestsController.TableDataRequests;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.ChangeRequest;
import Entities.SystemUser;
import Protocol.Command;
import Utility.AppManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;


/**
 * This class provides the manager with a table of the users and also with the ability to assign and change the supervisor, the execution changes committee
 * and the maintenance and support managers of the different departments.
 * 
 * @author Bshara
 * */
public class ListOfEmployeesController implements Initializable {

	private static final String GET_ALL_USERS = "GetAllUsers";

	@FXML
	private Text txtPageHeader;

	@FXML
	private TableView<TableUsers> tblUsers;

	@FXML
	private TableColumn<TableUsers, String> tcUsername;

	@FXML
	private TableColumn<TableUsers, String> tcFirstName;

	@FXML
	private TableColumn<TableUsers, String> tcLastName;

	@FXML
	private TableColumn<TableUsers, String> tcPhoneNumber;

	@FXML
	private TableColumn<TableUsers, String> tcEmail;

	@FXML
	private TableColumn<TableUsers, String> tcIsOnline;

	@FXML
	private HBox hbAssignMaintenance;

	@FXML
	private HBox hbAssingSupervisor;

	@FXML
	private HBox hbAssignCommitteeMembers;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		hbAssignMaintenance.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbAssignMaintenance, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbAssignMaintenance, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbAssignMaintenance.setOnMousePressed(event -> {
			NavigationBar.next("Departments Maintenance Managers", FxmlNames.MAINTAINANCE_MANAGERS);
		});

		hbAssingSupervisor.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbAssingSupervisor, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbAssingSupervisor, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbAssingSupervisor.setOnMousePressed(event -> {
			NavigationBar.next("Supervisor Details", FxmlNames.CURRENT_SUPERVISOR);

		});

		hbAssignCommitteeMembers.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbAssignCommitteeMembers, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbAssignCommitteeMembers, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbAssignCommitteeMembers.setOnMousePressed(event -> {
			NavigationBar.next("Supervisor Details", FxmlNames.CURRENT_COMMITTEE);
		});

		initTable();

		Client.getInstance().requestWithListener(Command.getAllUsers, srMsg -> {

			if (srMsg.getCommand() == Command.getAllUsers) {

				ArrayList<SystemUser> data = (ArrayList<SystemUser>) srMsg.getAttachedData()[0];

				loadRequestToTable(data);

				Client.removeStringRecievedFromServer(GET_ALL_USERS);
			}

		}, GET_ALL_USERS);

	}

	private void loadRequestToTable(ArrayList<SystemUser> users) {

		ArrayList<TableUsers> data = new ArrayList<TableUsers>();

		for (SystemUser u : users) {

			String isOnline = u.isOnline() ? "Yes" : "No";
			TableUsers tu = new TableUsers(u.getUserName(), u.getFirstName(), u.getLastName(), u.getPhoneNo(),
					u.getEmail(), isOnline);

			data.add(tu);
		}

		addContentToTable(data);
	}

	private void initTable() {

		tcUsername.setCellValueFactory(new PropertyValueFactory<TableUsers, String>("username"));
		tcFirstName.setCellValueFactory(new PropertyValueFactory<TableUsers, String>("firstName"));
		tcLastName.setCellValueFactory(new PropertyValueFactory<TableUsers, String>("lastName"));
		tcPhoneNumber.setCellValueFactory(new PropertyValueFactory<TableUsers, String>("phoneNo"));
		tcEmail.setCellValueFactory(new PropertyValueFactory<TableUsers, String>("email"));
		tcIsOnline.setCellValueFactory(new PropertyValueFactory<TableUsers, String>("isOnline"));

	}

	private void addContentToTable(ArrayList<TableUsers> strs) {

		tblUsers.setItems(FXCollections.observableArrayList(strs));

	}

	public class TableUsers {
		private String username, firstName, lastName, phoneNo, email, isOnline;

		public TableUsers(String username, String firstName, String lastName, String phoneNo, String email,
				String isOnline) {
			super();
			this.username = username;
			this.firstName = firstName;
			this.lastName = lastName;
			this.phoneNo = phoneNo;
			this.email = email;
			this.isOnline = isOnline;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getPhoneNo() {
			return phoneNo;
		}

		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getIsOnline() {
			return isOnline;
		}

		public void setIsOnline(String isOnline) {
			this.isOnline = isOnline;
		}

	}

}
