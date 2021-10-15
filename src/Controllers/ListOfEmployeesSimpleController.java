package Controllers;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.ControllerManager;
import Entities.ChangeRequest;
import Entities.Phase;
import Protocol.Command;
import Utility.DateUtil;
import Utility.Func;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import Entities.Employee;


/**
 * This class is used as a simple list of employees to pick an employee to assign for various classes that might require the user to select an employee
 * the class contains an event that can be registered to, which invokes the event on double clicking a row from the employee table.
 * 
 * @author Bshara
 * */
public class ListOfEmployeesSimpleController implements Initializable {

	private static final String GET_E_MPLOYEES_SIMPLE = "GetEMployeesSimple";

	@FXML
	private Canvas canvasRight;

	@FXML
	private TableView<TableEmps> tblEmployees;

	@FXML
	private TableColumn<TableEmps, String> tcEmpNumber;

	@FXML
	private TableColumn<TableEmps, String> tcUsername;

	@FXML
	private TableColumn<TableEmps, String> tcFirstName;

	@FXML
	private TableColumn<TableEmps, String> tcLastName;

	@FXML
	private Canvas canvasLeft;
	ArrayList<Employee> employees;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Client.addMessageRecievedFromServer(GET_E_MPLOYEES_SIMPLE, srMsg -> {

			if (srMsg.getCommand() == Command.getEmployeesListSimple) {

				employees = (ArrayList<Employee>) srMsg.getAttachedData()[0];

				loadDataIntoTable(employees);

				Client.removeMessageRecievedFromServer(GET_E_MPLOYEES_SIMPLE);

			}
		});

		Client.getInstance().request(Command.getEmployeesListSimple);

	}

	private void loadDataIntoTable(ArrayList<Employee> data) {
		initTable();

		ArrayList<TableEmps> tableContent = new ArrayList<TableEmps>();

		for (Employee emp : data) {

			TableEmps tableRow = new TableEmps(emp.getEmpNumber() + "", emp.getUserName(), emp.getFirstName(),
					emp.getLastName());
			tableContent.add(tableRow);

		}

		tblEmployees.setItems(FXCollections.observableArrayList(tableContent));

	}

	private static EmpFunc f;

	public interface EmpFunc {
		public void call(Employee emp);
	}

	public static void setOnRowDoubleClicked(EmpFunc func) {
		f = func;
	};

	@FXML
	public void clickItem(MouseEvent event) {
		if (event.getClickCount() == 2) // Checking double click
		{
			int selectedIndex = tblEmployees.getSelectionModel().getSelectedIndex();
			if (selectedIndex != -1) {
				if (f != null)
					f.call(employees.get(selectedIndex));
			}

		}
	}

	private void initTable() {

		tcEmpNumber.setCellValueFactory(new PropertyValueFactory<TableEmps, String>("employeeNumber"));
		tcFirstName.setCellValueFactory(new PropertyValueFactory<TableEmps, String>("firstName"));
		tcLastName.setCellValueFactory(new PropertyValueFactory<TableEmps, String>("lastName"));
		tcUsername.setCellValueFactory(new PropertyValueFactory<TableEmps, String>("username"));

	}

	public class TableEmps {

		public String employeeNumber, username, firstName, lastName;

		public TableEmps(String employeeNumber, String username, String firstName, String lastName) {
			super();
			this.employeeNumber = employeeNumber;
			this.username = username;
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public String getEmployeeNumber() {
			return employeeNumber;
		}

		public void setEmployeeNumber(String employeeNumber) {
			this.employeeNumber = employeeNumber;
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

		@Override
		public String toString() {
			return "TableDataRequests [employeeNumber=" + employeeNumber + ", username=" + username + ", firstName="
					+ firstName + ", lastName=" + lastName + "]";
		}

	}

}
