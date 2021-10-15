package Utility;

import javafx.scene.control.TextField;

public class FXUtility {

	
	public static void addNumbersOnlyListner(TextField tf) {
		tf.textProperty().addListener((observable, oldValue, newValue) -> {

			if (!newValue.matches("\\d*")) {
				tf.setText(newValue.replaceAll("[^\\d]", ""));
			}
		});
	}
}
