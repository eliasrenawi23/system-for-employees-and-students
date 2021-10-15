package Controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import com.mysql.cj.jdbc.jmx.LoadBalanceConnectionGroupManager;

import ClientLogic.Client;
import Controllers.Logic.CommonEffects;
import Controllers.Logic.ControllerManager;
import Controllers.Logic.ControllerSwapper;
import Controllers.Logic.FxmlNames;
import Controllers.Logic.NavigationBar;
import Entities.ActivityReport;
import Protocol.Command;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * This class is used to connect the FXML with the client, main functions are to
 * display statistical reports to the manager on demand to save reports and show
 * graphs.
 * 
 * @author Bshara
 * 
 */
public class AnalyticsGUIController implements Initializable {

	@FXML
	private Label hbLast30;

	@FXML
	private Label hbLastYear;

	@FXML
	private Label hbDateRange;

	@FXML
	private DatePicker dpFrom;

	@FXML
	private DatePicker dpTo;

	@FXML
	private Text txtActiveStd;

	@FXML
	private Text txtActiveMedian;

	@FXML
	private Text txtActiveAverage;

	@FXML
	private Text txtActiveTotal;

	@FXML
	private Text txtFrozenStd;

	@FXML
	private Text txtFrozenMedian;

	@FXML
	private Text txtFrozenAvg;

	@FXML
	private Text txtFrozenTotal;

	@FXML
	private Text txtClosedStd;

	@FXML
	private Text txtClosedMedian;

	@FXML
	private Text txtClosedAvg;

	@FXML
	private Text txtClosedTotal;

	@FXML
	private Text txtRejectedStd;

	@FXML
	private Text txtRejectedMedian;

	@FXML
	private Text txtRejectedAvg;

	@FXML
	private Text txtRejectedTotal;

	@FXML
	private Text txtWorkingStd;

	@FXML
	private Text txtWorkingMedian;

	@FXML
	private Text txtWorkingAvg;

	@FXML
	private Text txtWorkingTotal;

	@FXML
	private Text txtStatTitle;

	@FXML
	private BarChart<String, Number> bcActivity;

	@FXML
	private CategoryAxis caActivity;

	@FXML
	private NumberAxis naActivity;

	@FXML
	private BarChart<String, Number> bcTotalWorking;

	@FXML
	private CategoryAxis caTotalWorking;

	@FXML
	private NumberAxis naTotalWorking;

	@FXML
	private HBox hbSaveReport;

	@FXML
	private HBox hbLoadReport;

	private ArrayList<Node> buttons;

	private static ActivityReport ac;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		hbDateRange.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbDateRange, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbDateRange, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		
		hbSaveReport.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbSaveReport, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbSaveReport, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		
		
		hbLoadReport.setCursor(Cursor.HAND);
		ControllerManager.setEffect(hbLoadReport, CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);
		ControllerManager.setOnHoverEffect(hbLoadReport, CommonEffects.REQUESTS_TABLE_ELEMENT_BLUE,
				CommonEffects.REQUEST_DETAILS_BUTTON_GRAY);

		hbDateRange.setOnMousePressed(event -> {

			if (dpTo.getValue().isAfter(LocalDate.now())) {
				ControllerManager.showErrorMessage("Invalid Date", "Invalid Date",
						"Please pick a date that is not after today", null);
				return;
			}
			if (dpTo.getValue().isBefore(dpFrom.getValue())) {
				ControllerManager.showErrorMessage("Invalid Date", "Invalid Date", "Please pick a valid date", null);
				return;
			}

			Client.getInstance().requestWithListener(Command.getActivityReport, srMsg -> {
				if (srMsg.getCommand() == Command.getActivityReport) {

					ac = (ActivityReport) srMsg.getAttachedData()[0];

					ac.setName(dpFrom.getValue() + " : " + dpTo.getValue());
					loadActivityReport(ac);

				}
			}, "dawdawfawgwg24", dpFrom.getValue(), dpTo.getValue());
		});

		
		
		hbSaveReport.setOnMousePressed(event -> {
			
			if (dpTo.getValue() == null || dpFrom.getValue() == null) {
				ControllerManager.showErrorMessage("Invalid Date", "Invalid Date",
						"Please pick a date", null);
				return;
			}
			if (dpTo.getValue().isAfter(LocalDate.now())) {
				ControllerManager.showErrorMessage("Invalid Date", "Invalid Date",
						"Please pick a date that is not after today", null);
				return;
			}
			if (dpTo.getValue().isBefore(dpFrom.getValue())) {
				ControllerManager.showErrorMessage("Invalid Date", "Invalid Date", "Please pick a valid date", null);
				return;
			}
			if (ac == null) {
				ControllerManager.showErrorMessage("Error", "No report", "Please load a report before saving", null);
				return;
			}
			
			
			Client.getInstance().requestWithListener(Command.saveActivityReport, srMsg -> {
				if (srMsg.getCommand() == Command.saveActivityReport) {

					ControllerManager.showInformationMessage("Success", "Report Saved", "The report has been saved", null);

				}
			}, "wadawfwa35236236236632632342", ac);
		});

		
		hbLoadReport.setOnMousePressed(event -> {
			
			LoadReportList.setOnRowDoubleClicked(id -> {
				Client.getInstance().requestWithListener(Command.getActivityReportById, srMsg -> {
					if (srMsg.getCommand() == Command.getActivityReportById) {

						ac = (ActivityReport) srMsg.getAttachedData()[0];
						NavigationBar.back(true);

					}
				}, "dawdawfawgw235g24", id);
			});
			NavigationBar.next("Load Report", FxmlNames.loadReportList);
		});
		
		
		buttons = new ArrayList<Node>();

		for (Node node : buttons) {
			ControllerManager.setMouseHoverPressEffects(node, CommonEffects.REQUEST_DETAILS_BUTTON_BLACK,
					CommonEffects.REQUEST_DETAILS_BUTTON_GRAY, CommonEffects.REQUEST_DETAILS_BUTTON_BLUE, buttons,
					Cursor.HAND);
		}

		
		if (ac != null) {
			loadActivityReport(ac);
		}
		
	}

	private void loadActivityReport(ActivityReport ac) {

		Series<String, Number> s1 = new XYChart.Series<String, Number>();
		Series<String, Number> s2 = new XYChart.Series<String, Number>();
		Series<String, Number> s3 = new XYChart.Series<String, Number>();
		Series<String, Number> s4 = new XYChart.Series<String, Number>();
		Series<String, Number> s5 = new XYChart.Series<String, Number>();

		s1.setName("Active");
		s2.setName("Frozen");
		s3.setName("Closed");
		s4.setName("Rejected");
		s5.setName("Number Of Working Days");

		for (int i = 0; i < ac.getActive().size(); i++) {
			s1.getData().add(new XYChart.Data<String, Number>("Int " + i, ac.getActive().get(i)));
			s2.getData().add(new XYChart.Data<String, Number>("Day " + i, ac.getFrozen().get(i)));
			s3.getData().add(new XYChart.Data<String, Number>("Day " + i, ac.getClosed().get(i)));
			s4.getData().add(new XYChart.Data<String, Number>("Day " + i, ac.getRejected().get(i)));
			s5.getData().add(new XYChart.Data<String, Number>("Day " + i, ac.getNumOfWorkDays().get(i)));
		}

		bcActivity.getData().clear();
		bcActivity.getData().addAll(s1, s2, s3, s4);

		bcTotalWorking.getData().clear();
		bcTotalWorking.getData().addAll(s5);

		ArrayList<Double> avg = ac.getAverages();
		ArrayList<Double> std = ac.getSTD();
		ArrayList<Double> med = ac.getMedian();
//		

		txtActiveAverage.setText("Average: " + avg.get(0));
		txtActiveStd.setText("STD: " + std.get(0));
		txtActiveMedian.setText("Median: " + med.get(0));
		txtActiveTotal.setText("Total: " + ac.getTotalActive());
//		
		txtFrozenAvg.setText("Average: " + avg.get(1));
		txtFrozenStd.setText("STD: " + std.get(1));
		txtFrozenMedian.setText("Median: " + med.get(1));
		txtFrozenTotal.setText("Total: " + ac.getTotalFrozen());
//		
		txtClosedAvg.setText("Average: " + avg.get(2));
		txtClosedStd.setText("STD: " + std.get(2));
		txtClosedMedian.setText("Median: " + med.get(2));
		txtClosedTotal.setText("Total: " + ac.getTotalClosed());
//		
		txtRejectedAvg.setText("Average: " + avg.get(3));
		txtRejectedStd.setText("STD: " + std.get(3));
		txtRejectedMedian.setText("Median: " + med.get(3));
		txtRejectedTotal.setText("Total: " + ac.getTotalRejected());
//		
//		
		txtWorkingAvg.setText("Average: " + avg.get(4));
		txtWorkingStd.setText("STD: " + std.get(4));
		txtWorkingMedian.setText("Median: " + med.get(4));
		txtWorkingTotal.setText("Total: " + ac.getTotalNumOfWorkDays());

	}

	private ObservableList<Data> getChartData(int a, int b, int c, int d) {
		ObservableList<Data> list = FXCollections.observableArrayList();
		list.addAll(new PieChart.Data("Canceled", a), new PieChart.Data("Locked", b), new PieChart.Data("Active", c),
				new PieChart.Data("Closed", d));
		return list;
	}

	private String percentile(int num, int whole) {

		return ((100 * num / whole)) + "%";
	}

	private int percentile(int num, int whole, int dummy) {

		return ((100 * num / whole));
	}

	@FXML
	private void changeScene() {

	}

	String[] statTitles = new String[] { "Requests Executions", "Requests Delays", "Requests Delays222" };
	int statTitlesCurrIndex = 0;

	@FXML
	private void onMouseScrollStatType(ScrollEvent event) {
		double y = event.getDeltaY();

		// Scrolling up
		if (y > 0) {
			changeStatTitle(true);
		}
		// Scrolling down
		else if (y < 0) {
			changeStatTitle(false);

		}

		System.out.println(y);
	}

	private void changeStatTitle(boolean goNext) {
		if (goNext) {
			statTitlesCurrIndex = (statTitlesCurrIndex + 1) % statTitles.length;
		} else {
			statTitlesCurrIndex = (statTitlesCurrIndex + statTitles.length - 1) % statTitles.length;
		}

		String res = statTitles[statTitlesCurrIndex];

		txtStatTitle.setText(res);

	}

}
