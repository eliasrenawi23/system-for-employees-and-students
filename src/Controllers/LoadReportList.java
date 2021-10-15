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
 * This class is used as a simple list of employees to pick an employee to
 * assign for various classes that might require the user to select an employee
 * the class contains an event that can be registered to, which invokes the
 * event on double clicking a row from the employee table.
 * 
 * @author Bshara
 */
public class LoadReportList implements Initializable {

	@FXML
	private Canvas canvasRight;

	@FXML
	private TableView<TableReport> tblReport;

	@FXML
	private TableColumn<TableReport, String> tcReportId;

	@FXML
	private TableColumn<TableReport, String> tcDateOfCreation;

	@FXML
	private TableColumn<TableReport, String> tcRange;

	@FXML
	private Canvas canvasLeft;

	private ArrayList<ArrayList<String>> reports;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Client.addMessageRecievedFromServer("dawdawf465646486111188113818318", srMsg -> {

			if (srMsg.getCommand() == Command.getReportsSimpleData) {

				reports = (ArrayList<ArrayList<String>>) srMsg.getAttachedData()[0];

				loadDataIntoTable(reports);

				Client.removeMessageRecievedFromServer("dawdawf465646486111188113818318");

			}
		});

		Client.getInstance().request(Command.getReportsSimpleData);

	}

	private void loadDataIntoTable(ArrayList<ArrayList<String>> data) {
		initTable();

		ArrayList<TableReport> tableContent = new ArrayList<TableReport>();

		for (ArrayList<String> emp : data) {

			TableReport tableRow = new TableReport(emp.get(0), emp.get(1), emp.get(2));
			tableContent.add(tableRow);

		}

		tblReport.setItems(FXCollections.observableArrayList(tableContent));

	}

	private static ReportFunc f;

	public interface ReportFunc {
		public void call(int id);
	}

	public static void setOnRowDoubleClicked(ReportFunc func) {
		f = func;
	};

	@FXML
	public void clickItem(MouseEvent event) {
		if (event.getClickCount() == 2) // Checking double click
		{
			int selectedIndex = tblReport.getSelectionModel().getSelectedIndex();
			if (selectedIndex != -1) {
				if (f != null)
					f.call(Integer.parseInt(reports.get(selectedIndex).get(0)));
			}

		}
	}

	private void initTable() {

		tcReportId.setCellValueFactory(new PropertyValueFactory<TableReport, String>("id"));
		tcDateOfCreation.setCellValueFactory(new PropertyValueFactory<TableReport, String>("datecreation"));
		tcRange.setCellValueFactory(new PropertyValueFactory<TableReport, String>("range"));

	}

	public class TableReport {

		public String id, datecreation, range;

		public TableReport(String id, String datecreation, String range) {
			super();
			this.id = id;
			this.datecreation = datecreation;
			this.range = range;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDatecreation() {
			return datecreation;
		}

		public void setDatecreation(String datecreation) {
			this.datecreation = datecreation;
		}

		public String getRange() {
			return range;
		}

		public void setRange(String range) {
			this.range = range;
		}

	}

}
