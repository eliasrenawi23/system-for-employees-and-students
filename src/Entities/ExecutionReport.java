package Entities;

import java.io.Serializable;

/**
 * This class contains all the needed data about the execution report as represented in the database.
 * 
 * @author Bshara
 * */
public class ExecutionReport extends Report implements Serializable {

	private static ExecutionReport emptyInstance = new ExecutionReport(0, 0, null, null);

	public static ExecutionReport getEmptyInstance() {
		return emptyInstance;
	}

	public ExecutionReport(long reportID, long phaseID, String contentLT, String place) {
		super(reportID, phaseID, contentLT, place);
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
		return "ChangeRequest";
	}

	@Override
	public boolean hasForeignKey() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getReferenceTableForeignKeyName() {
		// TODO Auto-generated method stub
		return "requestID";
	}

	@Override
	public int fieldsLastIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

}
