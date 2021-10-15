package Utility;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


/**
 * This class is used by the settings controller in the gui and provides a set of functions to implement a shortcuts system.
 * 
 * @author Bshara
 * */
public class ShortcutManager {
	final KeyCombination R = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
	final KeyCombination N = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
	final KeyCombination M = new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN);
	final KeyCombination O = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
	final KeyCombination T = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
	final KeyCombination A = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
	final KeyCombination B = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
	final KeyCombination BACK_SPACE = new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCombination.CONTROL_DOWN);

	// final KeyCombination Unassigned = new KeyCodeCombination(null,
	// KeyCombination.CONTROL_DOWN);

	public static Func CTRL_R;
	public static Func CTRL_N;
	public static Func CTRL_M;
	public static Func CTRL_O;
	public static Func CTRL_T;
	public static Func CTRL_A;
	public static Func CTRL_B;
	public static Func CTRL_BACK_SPACE;

	public ShortcutManager(Stage stage) {

		stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			public void handle(KeyEvent ke) {
				if (R.match(ke)) {
					if (CTRL_R != null)
						CTRL_R.execute();

					ke.consume(); // <-- stops passing the event to next node
				} else if (N.match(ke)) {
					if (CTRL_N != null)
						CTRL_N.execute();

					ke.consume(); // <-- stops passing the event to next node
				} else if (M.match(ke)) {
					if (CTRL_M != null)
						CTRL_M.execute();

					ke.consume(); // <-- stops passing the event to next node
				} else if (O.match(ke)) {
					if (CTRL_O != null)
						CTRL_O.execute();
					ke.consume(); // <-- stops passing the event to next node
				} else if (T.match(ke)) {
					if (CTRL_T != null)
						CTRL_T.execute();
					ke.consume(); // <-- stops passing the event to next node
				} else if (A.match(ke)) {
					if (CTRL_A != null)
						CTRL_A.execute();

					ke.consume(); // <-- stops passing the event to next node
				} else if (BACK_SPACE.match(ke)) {
					if (CTRL_BACK_SPACE != null)
						CTRL_BACK_SPACE.execute();

					ke.consume(); // <-- stops passing the event to next node
				}
			}

		});
	}

}
