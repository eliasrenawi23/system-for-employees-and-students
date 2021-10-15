package Controllers;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.ControllerManager;
import Entities.ChangeRequest;
import Entities.Phase;
import Entities.SupervisorDeadlineUpdate;
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
 * This class displays a list the contains all of the deadline changes that the supervisor did in the past,
 * it shows the date of the change and the change it self, also the supervisor who did that change at that time.
 * 
 * @author Bshara
 * */
public class ChangesBySupervisorController implements Initializable {

	private static final String GET_SUPERVISOR_UPDATES = "getSupervisorUpdates";

	@FXML
	private Canvas canvasRight;

	@FXML
	private TableView<TblSup> tblSupervisorUpdates;

	@FXML
	private TableColumn<TblSup, String> tcPhaseId;

	@FXML
	private TableColumn<TblSup, String> tcSupervisorEmpNum;

	@FXML
	private TableColumn<TblSup, String> tcDateOfChange;

	@FXML
	private TableColumn<TblSup, String> tcOldDeadline;

	@FXML
	private TableColumn<TblSup, String> tcNeadDeadline;

	@FXML
	private Canvas canvasLeft;

	ArrayList<SupervisorDeadlineUpdate> svrs;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// Apply the effects for the canvas
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);

		Client.addMessageRecievedFromServer(GET_SUPERVISOR_UPDATES, srMsg -> {

			if (srMsg.getCommand() == Command.getSupervisorDeadlineUpdate) {

				svrs = (ArrayList<SupervisorDeadlineUpdate>) srMsg.getAttachedData()[0];

				loadDataIntoTable(svrs);

				Client.removeMessageRecievedFromServer(GET_SUPERVISOR_UPDATES);

			}
		});

		Client.getInstance().request(Command.getSupervisorDeadlineUpdate);

	}

	private void loadDataIntoTable(ArrayList<SupervisorDeadlineUpdate> data) {
		initTable();

		ArrayList<TblSup> tableContent = new ArrayList<TblSup>();

		for (SupervisorDeadlineUpdate s : data) {

			TblSup tableRow = new TblSup(s.getPhaseId() + "", s.getSuperEmpNum() + "",
					DateUtil.toString(s.getDateOfChange()), DateUtil.toString(s.getOldDeadline()),
					DateUtil.toString(s.getNewDeadline()));

			tableContent.add(tableRow);

		}

		tblSupervisorUpdates.setItems(FXCollections.observableArrayList(tableContent));

	}

	private void initTable() {

		tcPhaseId.setCellValueFactory(new PropertyValueFactory<TblSup, String>("phaseId"));
		tcSupervisorEmpNum.setCellValueFactory(new PropertyValueFactory<TblSup, String>("supervisorNum"));
		tcDateOfChange.setCellValueFactory(new PropertyValueFactory<TblSup, String>("dateOfChange"));
		tcOldDeadline.setCellValueFactory(new PropertyValueFactory<TblSup, String>("oldDeadline"));
		tcNeadDeadline.setCellValueFactory(new PropertyValueFactory<TblSup, String>("newDeadline"));

	}

	public class TblSup {

		public String phaseId, supervisorNum, dateOfChange, oldDeadline, newDeadline;

		public TblSup(String phaseId, String supervisorNum, String dateOfChange, String oldDeadline,
				String newDeadline) {
			super();
			this.phaseId = phaseId;
			this.supervisorNum = supervisorNum;
			this.dateOfChange = dateOfChange;
			this.oldDeadline = oldDeadline;
			this.newDeadline = newDeadline;
		}

		public String getPhaseId() {
			return phaseId;
		}

		public void setPhaseId(String phaseId) {
			this.phaseId = phaseId;
		}

		public String getSupervisorNum() {
			return supervisorNum;
		}

		public void setSupervisorNum(String supervisorNum) {
			this.supervisorNum = supervisorNum;
		}

		public String getDateOfChange() {
			return dateOfChange;
		}

		public void setDateOfChange(String dateOfChange) {
			this.dateOfChange = dateOfChange;
		}

		public String getOldDeadline() {
			return oldDeadline;
		}

		public void setOldDeadline(String oldDeadline) {
			this.oldDeadline = oldDeadline;
		}

		public String getNewDeadline() {
			return newDeadline;
		}

		public void setNewDeadline(String newDeadline) {
			this.newDeadline = newDeadline;
		}

	}

}
