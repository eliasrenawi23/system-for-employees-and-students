package Controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Protocol.Command;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


/**
 * This class provides the ability to change the maintenance and support managers of the departments.
 * 
 * @author Bshara
 * */
public class DepartmentsManagersController implements Initializable {

	private static final String UPDATE_DEPARTMENT_MANAGER = "updateDepartmentManager";

	private static final String GET_DEPARTMENTS_MANAGERS = "getDepartmentsManagers";

	@FXML
	private Canvas canvasRight;

	@FXML
	private HBox hbInformationContainer;

	@FXML
	private Text txtBraudeWebsite;

	@FXML
	private Text txtClassroomComputr;

	@FXML
	private Text txtInfoSystem;

	@FXML
	private Text txtLabsAndComputers;

	@FXML
	private Text txtLibrarySystem;

	@FXML
	private Text txtMoodle;

	@FXML
	private ImageView imgMoodle;

	@FXML
	private ImageView imgBraudeWebsite;

	@FXML
	private ImageView imgClassroomComputers;

	@FXML
	private ImageView imgInfoSystem;

	@FXML
	private ImageView imgLabsAndComputers;

	@FXML
	private ImageView imgLibrarySystem;

	@FXML
	private Canvas canvasLeft;

	private ArrayList<String> data;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Apply the effects for the canvas
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);
		
		Client.getInstance().requestWithListener(Command.getDepartmentsManagers, srMsg -> {

			if (srMsg.getCommand() == Command.getDepartmentsManagers) {

				data = (ArrayList<String>) srMsg.getAttachedData()[0];

				txtBraudeWebsite.setText(data.get(1));
				txtClassroomComputr.setText(data.get(3));
				txtInfoSystem.setText(data.get(5));
				txtLabsAndComputers.setText(data.get(7));
				txtLibrarySystem.setText(data.get(9));
				txtMoodle.setText(data.get(11));

				Client.removeStringRecievedFromServer(GET_DEPARTMENTS_MANAGERS);
			}

		}, GET_DEPARTMENTS_MANAGERS);

		buttons();
	}

	private void buttons() {
		imgBraudeWebsite.setCursor(Cursor.HAND);
		ControllerManager.setEffect(imgBraudeWebsite, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(imgBraudeWebsite, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		imgBraudeWebsite.setOnMousePressed(event -> {

			registerListBehavior(data.get(0), data.get(1));
			NavigationBar.next("List Of Employees", FxmlNames.LIST_OF_EMPLOYEES_SIMPLE);

		});

		imgClassroomComputers.setCursor(Cursor.HAND);
		ControllerManager.setEffect(imgClassroomComputers, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(imgClassroomComputers, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		imgClassroomComputers.setOnMousePressed(event -> {

			registerListBehavior(data.get(2), data.get(3));

			NavigationBar.next("List Of Employees", FxmlNames.LIST_OF_EMPLOYEES_SIMPLE);

		});

		imgInfoSystem.setCursor(Cursor.HAND);
		ControllerManager.setEffect(imgInfoSystem, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(imgInfoSystem, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		imgInfoSystem.setOnMousePressed(event -> {

			registerListBehavior(data.get(4), data.get(5));

			NavigationBar.next("List Of Employees", FxmlNames.LIST_OF_EMPLOYEES_SIMPLE);

		});

		imgLabsAndComputers.setCursor(Cursor.HAND);
		ControllerManager.setEffect(imgLabsAndComputers, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(imgLabsAndComputers, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		imgLabsAndComputers.setOnMousePressed(event -> {

			registerListBehavior(data.get(6), data.get(7));

			NavigationBar.next("List Of Employees", FxmlNames.LIST_OF_EMPLOYEES_SIMPLE);

		});

		imgLibrarySystem.setCursor(Cursor.HAND);
		ControllerManager.setEffect(imgLibrarySystem, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(imgLibrarySystem, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		imgLibrarySystem.setOnMousePressed(event -> {

			registerListBehavior(data.get(8), data.get(9));

			NavigationBar.next("List Of Employees", FxmlNames.LIST_OF_EMPLOYEES_SIMPLE);

		});

		imgMoodle.setCursor(Cursor.HAND);
		ControllerManager.setEffect(imgMoodle, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(imgMoodle, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		imgMoodle.setOnMousePressed(event -> {

			registerListBehavior(data.get(10), data.get(11));

			NavigationBar.next("List Of Employees", FxmlNames.LIST_OF_EMPLOYEES_SIMPLE);

		});
	}

	private void registerListBehavior(String department, String fullName) {
		ListOfEmployeesSimpleController.setOnRowDoubleClicked(emp -> {
			ControllerManager.showYesNoMessage("Update", "Update Department Manager",
					"Are you sure you want to update the departmet of " + department
							+ " maintenance and support manager to employee " + emp.getEmpNumber(),
					() -> {
						Client.getInstance().requestWithListener(Command.updateDepartmentManager, srMsg -> {
							if (srMsg.getCommand() == Command.updateDepartmentManager) {

								NavigationBar.back(true);

								ControllerManager.showInformationMessage("Success", "Manager Updated",
										"The department " + department + " has been assigned to a new manager!", null);

								Client.removeMessageRecievedFromServer(UPDATE_DEPARTMENT_MANAGER);
							}
						}, UPDATE_DEPARTMENT_MANAGER, department, emp.getEmpNumber());
					}, null);
		});
	}

}
