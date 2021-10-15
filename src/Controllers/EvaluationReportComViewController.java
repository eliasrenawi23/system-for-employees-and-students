package Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.ControllerSwapper;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.EvaluationReport;
import Entities.Phase;
import Utility.DateUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


/**
 * This class is used by other controllers to display the evaluation report, it shows the information about the evaluation report.
 * 
 * @author Bshara
 * */
public class EvaluationReportComViewController implements Initializable {

	@FXML
	private Label lblPlace;

	@FXML
	private Label lblDescriptionOfRequiredChange;

	@FXML
	private Label lblResults;

	@FXML
	private Label lblConstraint;

	@FXML
	private Label lblRisks;

	@FXML
	private Label lblEstimatedExecTime;

	@FXML
	private HBox hbBack;

	private EvaluationReport evaluationReport;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		hbBack.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbBack, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbBack, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		hbBack.setOnMousePressed(event -> {
			NavigationBar.reload();
		});
	
	}

	public void setEvaluationReport(EvaluationReport evaluationReport) {

		this.evaluationReport = evaluationReport;
		lblConstraint.setText(evaluationReport.getConstraints());
		lblDescriptionOfRequiredChange.setText(evaluationReport.getContentLT());
		lblEstimatedExecTime.setText(DateUtil.toString(evaluationReport.getEstimatedExecutionTime()));
		lblPlace.setText(evaluationReport.getPlace());
		lblResults.setText(evaluationReport.getResult());
		lblRisks.setText(evaluationReport.getRisks());

	}

}
