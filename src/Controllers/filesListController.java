package Controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ClientLogic.Client;
import Controllers.ListOfEmployeesSimpleController.TableEmps;
import Entities.Employee;
import Entities.File;
import Protocol.Command;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;


/**
 * This class is used by other classes to view a list of attached files with a request.
 * also provides the ability to select and open the files in the user pc.
 * 
 * @author Bshara
 * */
public class filesListController implements Initializable {

	private static final String GET_REQUEST_FILES = "getRequestFiles";

	@FXML
	private Canvas canvasRight;

	@FXML
	private Canvas canvasLeft;

	@FXML
	private TableView<FilesTable> tblFiles;

	@FXML
	private TableColumn<FilesTable, String> tcFileName;

	@FXML
	private TableColumn<FilesTable, String> tcFileType;

	public static long requestId;
	
	private String appData = System.getenv("LOCALAPPDATA") + "\\Temp\\";

	private ArrayList<File> files;
	
    Desktop desktop;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Apply the effects for the canvas
		RequestDetailsUserController.applyCanvasEffects(canvasRight, canvasLeft);
		
		desktop = Desktop.getDesktop();

		Client.addMessageRecievedFromServer(GET_REQUEST_FILES, srMsg -> {

			if (srMsg.getCommand() == Command.getRequestFiles) {

				files = (ArrayList<File>) srMsg.getAttachedData()[0];

				
				loadFilesIntoPC(files);
				loadDataIntoTable(files);

				Client.removeMessageRecievedFromServer(GET_REQUEST_FILES);

			}
		});

		Client.getInstance().request(Command.getRequestFiles, requestId);

	}

	private void loadFilesIntoPC(ArrayList<File> files) {

		for (File file : files) {
			file.writeData(appData);
		}
		
	}

	private void loadDataIntoTable(ArrayList<File> data) {
		initTable();

		ArrayList<FilesTable> tableContent = new ArrayList<FilesTable>();

		for (File f : data) {

			FilesTable tableRow = new FilesTable(f.getFileName(), f.getType());
			tableContent.add(tableRow);

		}

		tblFiles.setItems(FXCollections.observableArrayList(tableContent));

	}

	private void initTable() {

		tcFileName.setCellValueFactory(new PropertyValueFactory<FilesTable, String>("name"));
		tcFileType.setCellValueFactory(new PropertyValueFactory<FilesTable, String>("type"));

	}
	
	
	@FXML
	public void clickItem(MouseEvent event) {
		if (event.getClickCount() == 2) // Checking double click
		{
			int selectedIndex = tblFiles.getSelectionModel().getSelectedIndex();
			if (selectedIndex != -1) {
				openFile(files.get(selectedIndex).getFileName());
			}

		}
	}

	private void openFile(String fileName) {

	       try {
			desktop.open(new java.io.File(appData + fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public class FilesTable {
		String name, type;

		public FilesTable(String name, String type) {
			super();
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return "FilesTable [name=" + name + ", type=" + type + "]";
		}

	}

}
