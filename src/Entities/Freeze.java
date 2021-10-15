package Entities;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * This class contains all the needed information about the Freeze of a phase as represented in the database.
 * 
 * @author Bshara
 * */
public class Freeze implements Serializable{

	private long id;
	private long phaseId;
	private String prevPhaseStatus;
	private Timestamp initDate, endDate;

	public Freeze(long id, long phaseId, String prevPhaseStatus, Timestamp initDate, Timestamp endDate) {
		super();
		this.id = id;
		this.phaseId = phaseId;
		this.prevPhaseStatus = prevPhaseStatus;
		this.initDate = initDate;
		this.endDate = endDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(long phaseId) {
		this.phaseId = phaseId;
	}

	public String getPrevPhaseStatus() {
		return prevPhaseStatus;
	}

	public void setPrevPhaseStatus(String prevPhaseStatus) {
		this.prevPhaseStatus = prevPhaseStatus;
	}

	public Timestamp getInitDate() {
		return initDate;
	}

	public void setInitDate(Timestamp initDate) {
		this.initDate = initDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "Freeze [id=" + id + ", phaseId=" + phaseId + ", prevPhaseStatus=" + prevPhaseStatus + ", initDate="
				+ initDate + ", endDate=" + endDate + "]";
	}

	
}
