package Controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.Employee;
import Entities.SystemUser;
import Protocol.Command;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


/**
 * This class provides the ability to view the current supervisor details and to change the current supervisor
 * by selecting another employee.
 * 
 * @author Bshara
 * */
public class CurrentSupervisor implements Initializable {

	private static final String UPDATE_SUPERVISOR = "updateSupervisor";

	private static final String GET_SUPERVIOSR_DETAILS = "getSuperviosrDetails";

	@FXML
	private Canvas canvasRight;

	@FXML
	private HBox hbInformationContainer;

	@FXML
	private Text txtFullName;

	@FXML
	private Text txtUsername;

	@FXML
	private Text txtEmployeeNumber;

	@FXML
	private Text txtPhoneNo;

	@FXML
	private Text txtEmail;

	@FXML
	private Text txtOrgRole;

	@FXML
	private HBox hbAssignSupervisor;

	@FXML
	private Canvas canvasLeft;

	private Employee sup;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Apply the effects for the canvas
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);

		Client.getInstance().requestWithListener(Command.getSuperviosrDetails, srMsg -> {

			if (srMsg.getCommand() == Command.getSuperviosrDetails) {

				sup = (Employee) srMsg.getAttachedData()[0];

				loadData(sup);

				Client.removeStringRecievedFromServer(GET_SUPERVIOSR_DETAILS);
			}

		}, GET_SUPERVIOSR_DETAILS);

		hbAssignSupervisor.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbAssignSupervisor, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbAssignSupervisor, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbAssignSupervisor.setOnMousePressed(event -> {
			registerListBehavior();
			NavigationBar.next("List Of Employees", FxmlNames.LIST_OF_EMPLOYEES_SIMPLE);
		});

	}

	private void loadData(Employee s) {

		txtEmail.setText(s.getEmail());
		txtEmployeeNumber.setText(s.getEmpNumber() + "");
		txtFullName.setText(s.getFirstName() + " " + s.getLastName());
		txtOrgRole.setText(s.getOrganizationalRole());
		txtPhoneNo.setText(s.getPhoneNo());
		txtUsername.setText(s.getUserName());

	}

	private void registerListBehavior() {

		ListOfEmployeesSimpleController.setOnRowDoubleClicked(emp -> {
			ControllerManager
					.showYesNoMessage(
							"Update", "Update Supervisor", "Are you sure you want to update the supervisor to "
									+ emp.getFirstName() + " " + emp.getLastName() + "(" + emp.getEmpNumber() + ")",
							() -> {
								Client.getInstance().requestWithListener(Command.updateSupervisor, srMsg -> {
									if (srMsg.getCommand() == Command.updateSupervisor) {

										NavigationBar.back(true);

										ControllerManager.showInformationMessage("Success", "Supervisor Updated",
												"Supervisor has been assigned successfully", null);

										Client.removeMessageRecievedFromServer(UPDATE_SUPERVISOR);
									}
								}, UPDATE_SUPERVISOR, emp.getEmpNumber());
							}, null);
		});
	}

}
