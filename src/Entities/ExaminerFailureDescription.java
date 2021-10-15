package Entities;

import java.io.Serializable;

/**
 * This class contains all the needed information about the examiner failure report as represented in the database.
 * 
 * @author Bshara
 * */
public class ExaminerFailureDescription implements Serializable {

	private long requestID;
	private long ID;
	private String description;
	public ExaminerFailureDescription(long requestID, long iD, String description) {
		super();
		this.requestID = requestID;
		ID = iD;
		this.description = description;
	}
	public long getRequestID() {
		return requestID;
	}
	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}
	public long getID() {
		return ID;
	}
	public void setID(long iD) {
		ID = iD;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	@Override
	public String toString() {
		return "ExaminerFailureDescription [requestID=" + requestID + ", ID=" + ID + ", description=" + description
				+ "]";
	}
	
	
	
}
