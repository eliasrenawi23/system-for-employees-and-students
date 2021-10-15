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
import Protocol.Command;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


/**
 * This class shows the current committee of execution changes, this class provides the ability to changes the members
 * of the committee and view their details.
 * 
 * @author Bshara
 * */
public class CurrentCommittee implements Initializable {

	private static final String UPDATE_COM_MEMBER = "updateComMember";

	private static final String GET_COMMITTEE_DETAILS = "getCommitteeDetails";

	@FXML
	private Canvas canvasRight;

	@FXML
	private HBox hbMember1;

	@FXML
	private HBox hbComChairman;

	@FXML
	private HBox hbMember2;

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

	ArrayList<Node> nodes;

	private ArrayList<Employee> coms;

	private int index = 0;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Apply the effects for the canvas
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);

		nodes = new ArrayList<Node>();

		nodes.add(hbMember1);
		nodes.add(hbMember2);
		nodes.add(hbComChairman);

		for (Node node : nodes) {
			node.setCursor(Cursor.HAND);
			ControllerManager.setEffect(node, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
			ControllerManager.setMouseHoverPressEffects(node, CommonEffects.REQUESTS_TABLE_ELEMENT_GRAY,
					CommonEffects.REQUESTS_TABLE_ELEMENT_BLACK, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE, nodes,
					Cursor.HAND);
		}

		hbMember1.setOnMouseClicked(event -> {
			loadData(coms.get(0));
			index = 0;
			registerListBehavior();

		});

		hbComChairman.setOnMouseClicked(event -> {
			loadData(coms.get(1));
			index = 1;
			registerListBehavior();

		});

		hbMember2.setOnMouseClicked(event -> {
			loadData(coms.get(2));
			index = 2;
			registerListBehavior();

		});

		Client.getInstance().requestWithListener(Command.getCommitteeDetails, srMsg -> {

			if (srMsg.getCommand() == Command.getCommitteeDetails) {

				coms = (ArrayList<Employee>) srMsg.getAttachedData()[0];

				loadData(coms.get(0));
				ControllerManager.setEffect(hbMember1, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE);

				Client.removeStringRecievedFromServer(GET_COMMITTEE_DETAILS);
			}

		}, GET_COMMITTEE_DETAILS);

		hbAssignSupervisor.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbAssignSupervisor, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbAssignSupervisor, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbAssignSupervisor.setOnMousePressed(event -> {
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
			ControllerManager.showYesNoMessage("Update", "Update Committee Member",
					"Are you sure you want to update the committee member to " + emp.getFirstName() + " "
							+ emp.getLastName() + "(" + emp.getEmpNumber() + ")",
					() -> {
						Client.getInstance().requestWithListener(Command.updateComMember, srMsg -> {
							if (srMsg.getCommand() == Command.updateComMember) {

								boolean alreadyComMem = (boolean) srMsg.getAttachedData()[0];
								if (alreadyComMem) {
									ControllerManager.showErrorMessage("Failure", "Committee member already exist",
											"Committee member has already a position in the committee, please select other employee",
											null);
								} else {
									NavigationBar.back(true);

									ControllerManager.showInformationMessage("Success", "Committee member Updated",
											"Committee member has been assigned successfully", null);

								}
								Client.removeMessageRecievedFromServer(UPDATE_COM_MEMBER);

							}
						}, UPDATE_COM_MEMBER, coms.get(index).getEmpNumber(), emp.getEmpNumber());
					}, null);
		});
	}

}
