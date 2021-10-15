package Controllers.Logic;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import Utility.Func;
import Utility.StringFunc;
import Utility.VoidFunc;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;

/**
 * This class provides utility functions that are used by almost all of the controllers, things like changing the color of the nodes (can be icons, images or text)
 * setting mouse icons, hover effects and display information messages to the user.
 * 
 * @author Bshara
 * */
public class ControllerManager {

	public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	static {
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
	}

	public static void installTooltip(Node node, String content) {
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().set(content);
		Tooltip.install(node, tooltip);
	}

	/**
	 * Displays an alert message
	 * 
	 * @param func The function to call after the "OK" button has been pressed.
	 */
	public static void showOkCancelMessage(String title, String header, String content, VoidFunc okFunc,
			VoidFunc cancelFunc) {
		Alert alertSuccess = new Alert(AlertType.CONFIRMATION);
		alertSuccess.setTitle(title);
		alertSuccess.setHeaderText(header);
		alertSuccess.setContentText(content);
		alertSuccess.showAndWait().ifPresent(rs -> {
			if (rs == ButtonType.OK) {
				Platform.runLater(() -> {
					if (okFunc != null)
						okFunc.call();
				});
			}
			if (rs == ButtonType.CANCEL) {
				Platform.runLater(() -> {
					if (cancelFunc != null)
						cancelFunc.call();
				});
			}
		});
	}

	public static void showYesNoMessage(String title, String header, String content, VoidFunc yesFunc,
			VoidFunc noFunc) {
		Alert alertSuccess = new Alert(AlertType.CONFIRMATION);
		alertSuccess.setTitle(title);
		alertSuccess.setHeaderText(header);
		alertSuccess.setContentText(content);
		((Button) alertSuccess.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
		((Button) alertSuccess.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");

		alertSuccess.showAndWait().ifPresent(rs -> {
			if (rs == ButtonType.OK) {
				Platform.runLater(() -> {
					if (yesFunc != null)
						yesFunc.call();
				});
			}
			if (rs == ButtonType.CANCEL) {
				Platform.runLater(() -> {
					if (noFunc != null)
						noFunc.call();
				});
			}
		});

	}

	public static void showInformationMessage(String title, String header, String content, VoidFunc okFunc) {
		showMessage(title, header, content, okFunc, AlertType.INFORMATION);

	}

	public static void showErrorMessage(String title, String header, String content, VoidFunc okFunc) {
		showMessage(title, header, content, okFunc, AlertType.ERROR);
	}

	public static void showMessage(String title, String header, String content, VoidFunc okFunc, AlertType alertType) {
		Alert alertSuccess = new Alert(alertType);
		alertSuccess.setTitle(title);
		alertSuccess.setHeaderText(header);
		alertSuccess.setContentText(content);
		alertSuccess.showAndWait().ifPresent(rs -> {
			if (rs == ButtonType.OK) {
				Platform.runLater(() -> {

					if (okFunc != null)
						okFunc.call();

				});
			}

		});
	}

	public static void setEffect(ArrayList<Node> nodes, Effect effect) {
		for (Node node : nodes) {
			node.setEffect(effect);
		}
	}

	public static void setEffect(Node node, Effect effect) {
		node.setEffect(effect);
	}

	// TODO: bug found, were if i were to use the regular
	public static void setEffectConditioned(ArrayList<Node> nodes, Effect effect, Effect exceptThisEffect) {
		for (Node node : nodes) {
			if (!exceptThisEffect.equals(node.getEffect()))
				node.setEffect(effect);
		}
	}

	public static void setEffectConditioned(Node node, Effect effect, Effect exceptThisEffect) {
		if (!exceptThisEffect.equals(node.getEffect()))
			node.setEffect(effect);
	}

	public static ArrayList<Node> getAllNodes(Pane root) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		addAllDescendents(root, nodes);
		return nodes;
	}

	private static void addAllDescendents(Pane parent, ArrayList<Node> nodes) {
		for (Node node : parent.getChildrenUnmodifiable()) {
			nodes.add(node);
			if (node instanceof Pane)
				addAllDescendents((Pane) node, nodes);
		}
	}

	public static void setOnHoverEffect(Node node, Effect onEntered, Effect onExited) {
		node.setOnMouseEntered(event -> {
			ControllerManager.setEffect(node, onEntered);
		});
		node.setOnMouseExited(event -> {
			ControllerManager.setEffect(node, onExited);
		});
	}

	public static void setOnHoverEffectConditioned(Node node, Effect onEntered, Effect onExited, Effect condition) {
		node.setOnMouseEntered(event -> {
			ControllerManager.setEffectConditioned(node, onEntered, condition);
		});
		node.setOnMouseExited(event -> {
			ControllerManager.setEffectConditioned(node, onExited, condition);
		});
	}

	public static void setMouseHoverEffects(Node node, Effect onEntered, Effect onExited, Effect condition,
			Cursor value) {

		node.setCursor(Cursor.HAND);
		setEffect(node, onExited);
		setOnHoverEffectConditioned(node, onEntered, onExited, condition);
	}

	public static void setMouseHoverEffects(Node node, Effect onEntered, Effect onExited, Effect condition) {
		setMouseHoverEffects(node, onEntered, onExited, condition, Cursor.HAND);
	}

	public static void setMouseHoverPressEffects(Node node, Effect onEntered, Effect onExited, Effect onPressed,
			ArrayList<Node> nodesToReset, Cursor value) {

		node.setCursor(Cursor.HAND);
		setEffect(node, onExited);
		setOnHoverEffectConditioned(node, onEntered, onExited, onPressed);
		node.setOnMousePressed(event -> {
			ControllerManager.setEffect(nodesToReset, onExited);
			ControllerManager.setEffect(node, onPressed);
		});
	}

	public static void setMouseHoverPressEffects(Node node, Effect onEntered, Effect onExited, Effect onPressed,
			Cursor value) {

		node.setCursor(Cursor.HAND);
		setEffect(node, onExited);
		setOnHoverEffectConditioned(node, onEntered, onExited, onPressed);
		node.setOnMousePressed(event -> {
			ControllerManager.setEffect(node, onPressed);
		});
	}

	public static boolean areAllStringsNotEmpty(String... strings) {
		for (String str : strings) {
			if (str.compareTo("") == 0)
				return false;
		}
		return true;
	}

	public static String getDateTime(Timestamp ts) {
		return sdf.format(ts);
	}

	/**
	 * Returns the difference between a and b, meaning a - b.
	 */
	public static String getDateTimeDiff(Timestamp a, Timestamp b) {
		Timestamp diff = new Timestamp(a.getTime() - b.getTime());
		return sdf.format(diff);
	}

	public static void setTextFieldToNumbersOnly(TextField textField) {
		// force the field to be numeric only
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					textField.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});

	}

	public static void setFreezeBehavior(ArrayList<Node> btns) {
		for (Node node : btns) {
			node.setOnMousePressed(event -> {
				ControllerManager.showInformationMessage("Request Suspended", "Request suspended",
						"You can't make any changes to this request until the suspention is left off", null);
			});

		}
	}

	public static void addListener(TextField textField, StringFunc f) {

		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (f != null) {
				f.call(newValue);
			}
		});
	}

}
