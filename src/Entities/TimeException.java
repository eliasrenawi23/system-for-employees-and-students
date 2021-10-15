package Entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * This class contains all the needed information about the time exception as represented in the database.
 * 
 * @author Bshara
 * */
public class TimeException implements Serializable {

	private long id;
	private long phaseId;
	private Timestamp from;
	private Timestamp to;

	public TimeException(long id, long phaseId, Timestamp from, Timestamp to) {
		this.id = id;
		this.phaseId = phaseId;
		this.from = from;
		this.to = to;
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


	public Timestamp getFrom() {
		return from;
	}

	public void setFrom(Timestamp from) {
		this.from = from;
	}

	public Timestamp getTo() {
		return to;
	}

	public void setTo(Timestamp to) {
		this.to = to;
	}

}
