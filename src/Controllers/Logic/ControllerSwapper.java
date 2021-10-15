package Controllers.Logic;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import Controllers.ClientGUI;
import Controllers.MessageEntryController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This class is used to load the FXML data from one FXML to the other
 * also it can load a controller of an FXML
 * this class is used as a utility class, and is used by many controllers.
 * 
 * @author Bshara
 * */
public class ControllerSwapper {

	public static Stage Show(Stage stage, URL location, String title) {

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(location);

			Parent root1 = null;
			try {
				root1 = (Parent) fxmlLoader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stage = new Stage();
			stage.setTitle(title);
			stage.setScene(new Scene(root1));

			ArrayList<Stage> s = new ArrayList<>();

			// Since the we can't use stage.show() directly
			// encapsulate it in an array list instead.
			s.add(stage);
			Platform.runLater(() -> {
				s.get(0).show();
			});
			return stage;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ObservableList<Node> getChildrenOf(String fxmlFileName) {

		// Reference a class that exist in the same folder as the FXML files
		URL url = ClientGUI.class.getResource(fxmlFileName);

		AnchorPane loader = null;
		try {
			loader = FXMLLoader.load(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return loader.getChildren();
	}

	public static void loadAnchorContent(Pane destination, String fxmlFileName) {

		// Reference a class that exist in the same folder as the FXML files
		URL url = ClientGUI.class.getResource(fxmlFileName);

		AnchorPane loader = null;
		try {
			loader = FXMLLoader.load(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		destination.getChildren().setAll(loader);
	}

	public static void loadContent(Pane destination, String fxmlFileName) {

		// Reference a class that exist in the same folder as the FXML files
		URL url = ClientGUI.class.getResource(fxmlFileName);

		Pane loader = null;
		try {
			loader = FXMLLoader.load(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		destination.getChildren().setAll(loader);
	}

	public static Initializable loadContentWithController(Pane destination, String fxmlFileName) {

		URL url = ClientGUI.class.getResource(fxmlFileName);

		FXMLLoader loader = new FXMLLoader(url);

		Pane pane = null;
		try {
			pane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Initializable controller = (Initializable) loader.getController();
		destination.getChildren().setAll(pane);

		return controller;
	}

	public static Parent loadContentWithLoader(String fxmlFileName) {
		// Reference a class that exist in the same folder as the FXML files
		URL url = ClientGUI.class.getResource(fxmlFileName);

		Pane loader = null;
		try {
			loader = FXMLLoader.load(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return loader;
	}

}
