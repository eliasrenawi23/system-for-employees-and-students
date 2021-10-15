package Entities;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;


/**
 * This class contains all the needed information about the supervisor updates with comments as represented in the database.
 * 
 * @author Bshara
 * */
public class SupervisorUpdateCR extends SqlObject implements Serializable {

	// TODO: this should also be linked to the request or phase that got updated!!
	public long updateID;
	public long supervisorID;
	public String updateDescription;
	public LocalDate updateDate;

	private static SupervisorUpdateCR emptyInstance = new SupervisorUpdateCR(0, 0, null, null);

	public static SupervisorUpdateCR getEmptyInstance() {
		return emptyInstance;
	}

	public SupervisorUpdateCR(long updateID, long supervisorID, String updateDescription, LocalDate updateDate) {
		super();
		this.updateID = updateID;
		this.supervisorID = supervisorID;
		this.updateDescription = updateDescription;
		this.updateDate = updateDate;
	}

	public LocalDate getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(LocalDate updateDate) {
		this.updateDate = updateDate;
	}

	public long getUpdateID() {
		return updateID;
	}

	public void setUpdateID(long updateID) {
		this.updateID = updateID;
	}

	public long getSupervisorID() {
		return supervisorID;
	}

	public void setSupervisorID(long supervisorID) {
		this.supervisorID = supervisorID;
	}

	public String getUpdateDescription() {
		return updateDescription;
	}

	public void setUpdateDescription(String updateDescription) {
		this.updateDescription = updateDescription;
	}


	@Override
	public int getPrimaryKeyIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getForeignKeyIndex() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String getReferenceTableName() {
		// TODO Auto-generated method stub
		return "Employee";
	}

	@Override
	public boolean hasForeignKey() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getReferenceTableForeignKeyName() {
		// TODO Auto-generated method stub
		return "empNumber";
	}

	@Override
	public int fieldsLastIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

}
