package Entities;

import java.io.Serializable;

/**
 * This abstract class contains all the needed information for a basic report.
 * 
 * @author Bshara
 * */
public abstract class Report extends SqlObject implements Serializable {

	public long reportID;
	public long phaseID;
	public String contentLT, place;

	public Report(long reportID, long phaseID, String contentLT, String place) {
		super();
		this.reportID = reportID;
		this.phaseID = phaseID;
		this.contentLT = contentLT;
		this.place = place;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public long getReportID() {
		return reportID;
	}

	public void setReportID(long reportID) {
		this.reportID = reportID;
	}

	public long getPhaseID() {
		return phaseID;
	}

	public void setPhasetID(long phaseID) {
		this.phaseID = phaseID;
	}

	public String getContentLT() {
		return contentLT;
	}

	public void setContentLT(String contentLT) {
		this.contentLT = contentLT;
	}

	@Override
	public String toString() {
		return "Report [reportID=" + reportID + ", requestID=" + phaseID + ", contentLT=" + contentLT + ", place="
				+ place + "]";
	}

}
