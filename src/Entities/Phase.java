package Entities;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;


/**
 * This class contains all the needed information about the phase as represented in the database.
 * 
 * @author Bshara
 * */
public class Phase extends SqlObject implements Serializable {

	private static Phase emptyInstance = new Phase(0, 0, null, null, 0, null, null, null, null, false);

	public static Phase getEmptyInstance() {
		return emptyInstance;
	}

	public long phaseID;
	public long requestID;
	public String phaseName, status;
	public long empNumber;

	public Timestamp deadline, estimatedTimeOfCompletion, timeOfCompletion, startingDate;
	public boolean hasBeenTimeExtended;

	private PhaseTimeExtensionRequest phaseTimeExtensionRequest;


	private ArrayList<File> files;
	private EvaluationReport evaluationReport;

	public Phase(long phaseID, long requestID, String phaseName, String status, long empNumber, Timestamp deadline,
			Timestamp estimatedTimeOfCompletion, Timestamp timeOfCompletion, Timestamp startingDate,
			boolean hasBeenTimeExtended) {
		super();
		this.phaseID = phaseID;
		this.requestID = requestID;
		this.phaseName = phaseName;
		this.status = status;
		this.empNumber = empNumber;
		this.deadline = deadline;
		this.estimatedTimeOfCompletion = estimatedTimeOfCompletion;
		this.timeOfCompletion = timeOfCompletion;
		this.startingDate = startingDate;
		this.hasBeenTimeExtended = hasBeenTimeExtended;
		
		files = new ArrayList<File>();

	}


	public boolean hasTimeException() {
		return deadline.getTime() - timeOfCompletion.getTime() < 0;
	}

	public Timestamp getStartingDate() {
		return startingDate;
	}




	public void setStartingDate(Timestamp startingDate) {
		this.startingDate = startingDate;
	}




	public long getRequestID() {
		return requestID;
	}

	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}

	public EvaluationReport getEvaluationReport() {
		return evaluationReport;
	}

	public void setEvaluationReport(EvaluationReport evaluationReport) {
		this.evaluationReport = evaluationReport;
	}

	public ArrayList<File> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}

	public PhaseTimeExtensionRequest getPhaseTimeExtensionRequest() {
		return phaseTimeExtensionRequest;
	}

	public void setPhaseTimeExtensionRequest(PhaseTimeExtensionRequest phaseTimeExtensionRequest) {
		this.phaseTimeExtensionRequest = phaseTimeExtensionRequest;
	}

	public long getPhaseID() {
		return phaseID;
	}

	public void setPhaseID(long phaseID) {
		this.phaseID = phaseID;
	}

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getEmpNumber() {
		return empNumber;
	}

	public void setEmpNumber(long empNumber) {
		this.empNumber = empNumber;
	}

	public Timestamp getDeadline() {
		return deadline;
	}

	public void setDeadline(Timestamp deadline) {
		this.deadline = deadline;
	}

	public Timestamp getEstimatedTimeOfCompletion() {
		return estimatedTimeOfCompletion;
	}

	public void setEstimatedTimeOfCompletion(Timestamp estimatedTimeOfCompletion) {
		this.estimatedTimeOfCompletion = estimatedTimeOfCompletion;
	}

	public Timestamp getTimeOfCompletion() {
		return timeOfCompletion;
	}

	public void setTimeOfCompletion(Timestamp timeOfCompletion) {
		this.timeOfCompletion = timeOfCompletion;
	}

	public boolean isHasBeenTimeExtended() {
		return hasBeenTimeExtended;
	}

	public void setHasBeenTimeExtended(boolean hasBeenTimeExtended) {
		this.hasBeenTimeExtended = hasBeenTimeExtended;
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

	@Override
	public String toString() {
		return "Phase [phaseID=" + phaseID + ", requestID=" + requestID + ", phaseName=" + phaseName + ", status="
				+ status + ", empNumber=" + empNumber + ", deadline=" + deadline + ", estimatedTimeOfCompletion="
				+ estimatedTimeOfCompletion + ", timeOfCompletion=" + timeOfCompletion + ", hasBeenTimeExtended="
				+ hasBeenTimeExtended + "]";
	}

}
