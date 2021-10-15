package Entities;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;


/**
 * This class contains all the needed information about the phase time extension request as represented in the database.
 * 
 * @author Bshara
 * */
public class PhaseTimeExtensionRequest extends SqlObject implements Serializable {

	private static PhaseTimeExtensionRequest emptyInstance = new PhaseTimeExtensionRequest(0, 0, 0, null);

	public static PhaseTimeExtensionRequest getEmptyInstance() {
		return emptyInstance;
	}

	public long phaseID;
	public int requestedTimeInDays, requestedTimeInHours;
	public String description;

	public PhaseTimeExtensionRequest(long phaseID, int requestedTimeInDays, int requestedTimeInHours,
			String description) {
		super();
		this.phaseID = phaseID;
		this.requestedTimeInDays = requestedTimeInDays;
		this.requestedTimeInHours = requestedTimeInHours;
		this.description = description;
	}

	public long getPhaseID() {
		return phaseID;
	}

	public void setPhaseID(long phaseID) {
		this.phaseID = phaseID;
	}

	public int getRequestedTimeInDays() {
		return requestedTimeInDays;
	}

	public void setRequestedTimeInDays(int requestedTimeInDays) {
		this.requestedTimeInDays = requestedTimeInDays;
	}

	public int getRequestedTimeInHours() {
		return requestedTimeInHours;
	}

	public void setRequestedTimeInHours(int requestedTimeInHours) {
		this.requestedTimeInHours = requestedTimeInHours;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static void setEmptyInstance(PhaseTimeExtensionRequest emptyInstance) {
		PhaseTimeExtensionRequest.emptyInstance = emptyInstance;
	}

	@Override
	public int getPrimaryKeyIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getForeignKeyIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getReferenceTableName() {
		// TODO Auto-generated method stub
		return "Phase";
	}

	@Override
	public boolean hasForeignKey() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getReferenceTableForeignKeyName() {
		// TODO Auto-generated method stub
		return "phaseID";
	}

	@Override
	public int fieldsLastIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return "PhaseTimeExtensionRequest [phaseID=" + phaseID + ", requestedTimeInDays=" + requestedTimeInDays
				+ ", requestedTimeInHours=" + requestedTimeInHours + ", description=" + description + "]";
	}




}
