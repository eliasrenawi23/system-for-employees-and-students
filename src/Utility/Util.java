package Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;


/**
 * This class provides some basic utility functions for images and buttons
 * 
 * @author Bshara
 * */
public class Util {

	public static void setBtnFont(Button btn, String fontName, int size) {
		Font f = null;
		try {
			f = Font.loadFont(new FileInputStream(new File("src/fonts/" + fontName + ".TTF")), size);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		btn.setFont(f); // use this font with our label
	}

	public static MediaPlayer getAudioSource(String src) {

		Media sound = new Media(new File(src).toURI().toString());
		return new MediaPlayer(sound);

	}
	
	public static Image getImage(String src) {
		Image img = null;
		try {
			img = new Image(new FileInputStream(src));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
	}

	public static String generateRandomString() {
		byte[] array = new byte[256]; // length is bounded by 256
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));
		return generatedString;
	}

	public static void ExecuteThreadAfterDelay(Func func, long delay) {
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(delay), event -> {
				
			if (func != null)
				func.execute();

		}));
		timeline.play();
		
		
		
	}

}
