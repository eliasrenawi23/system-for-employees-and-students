package Controllers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javax.imageio.ImageIO;

import ClientLogic.Client;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.Message;
import Protocol.Command;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


/**
 * This class is an message entry that is used by the message list, it provides the ability to star message and check mark them.
 * on double click the user can enter to the message details page.
 * 
 * @author Bshara
 * */
public class MessageEntryController implements Initializable {

	@FXML
	private ImageView imgCheckBox;

	@FXML
	private ImageView imgStar;

	@FXML
	private HBox hbContainer;

	@FXML
	private Text txtSubject;

	@FXML
	private Text txtContent;

	@FXML
	private Text txtDate;
	
	@FXML
    private HBox hbMainContainer;
	
	public boolean starred, checked;
	
	private Message message;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		
		
		imgStar.setCursor(Cursor.HAND);
		imgCheckBox.setCursor(Cursor.HAND);
		hbContainer.setCursor(Cursor.HAND);


		imgStar.setOnMousePressed(event -> {
			try {
				setStarredImage(!starred);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			updateInDatabase();
		});
		
		imgCheckBox.setOnMousePressed(event -> {
			setCheckedImage(!checked);
		});
		
		
		hbContainer.setOnMousePressed(event -> {

			ListOfMessagesController.selectedMessage = message;
			NavigationBar.next("Message Details", FxmlNames.MESSAGE_SINGLE_PAGE);
			
		});
		
		
	}
	
	public void setFields(Message msg) {
		
		message = msg;
		
		// TODO: shorten it
		txtContent.setText(msg.getMessageContentLT());
		
		txtSubject.setText(msg.getSubject());
		
		txtDate.setText(ControllerManager.getDateTime(msg.getSentAt()));

		try {
			setStarredImage(msg.isStarred());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//setStarredImage(msg.isStarred());
		setCheckedImage(false);
	}
	
	
	
	private Image getImage(String img) throws IOException {
		URL url = getClass().getResource("/manikin.png");
		BufferedImage awtImg = ImageIO.read(url);
		Image fxImg = SwingFXUtils.toFXImage(awtImg, new WritableImage(50, 50));
		Image fxImgDirect = new Image(url.openStream());
		return fxImgDirect;
	}
	private void setStarredImage(boolean value) throws IOException {
		this.starred = value;
		message.setStarred(value);
		if(value) {
			imgStar.setImage(getImage("Images/Messages/icons8_star_50px_2.png"));
		}else {
			imgStar.setImage(new Image("Images/Messages/icons8_star_50px_1.png"));
		}
	}
	
	private void setCheckedImage(boolean value) {
		this.checked = value;
		if(value) {
			imgCheckBox.setImage(new Image("Images/Messages/icons8_checked_checkbox_50px_3.png"));
		}else {
			imgCheckBox.setImage(new Image("Images/Messages/icons8_unchecked_checkbox_50px_1.png"));
		}
	}
	
	public void deleteSelf() {
		p.getChildren().clear();
		checked = false;
		message = null;
		
	}
	
	public void updateInDatabase() {
		Client.getInstance().request(Command.updateMessage, message);
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}



	private AnchorPane p;
	public void setAttachedPane(AnchorPane containerPane) {
		this.p = containerPane;
	}

}
