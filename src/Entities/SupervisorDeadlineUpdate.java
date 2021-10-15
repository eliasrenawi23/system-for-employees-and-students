package Entities;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * This class contains all the needed information about the supervisor deadline update as represented in the database.
 * 
 * @author Bshara
 * */
public class SupervisorDeadlineUpdate implements Serializable {

	private long id, phaseId, superEmpNum;
	private Timestamp dateOfChange, oldDeadline, newDeadline;

	public SupervisorDeadlineUpdate(long id, long phaseId, long superEmpNum, Timestamp dateOfChange,
			Timestamp oldDeadline, Timestamp newDeadline) {
		super();
		this.id = id;
		this.phaseId = phaseId;
		this.superEmpNum = superEmpNum;
		this.dateOfChange = dateOfChange;
		this.oldDeadline = oldDeadline;
		this.newDeadline = newDeadline;
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

	public long getSuperEmpNum() {
		return superEmpNum;
	}

	public void setSuperEmpNum(long superEmpNum) {
		this.superEmpNum = superEmpNum;
	}

	public Timestamp getDateOfChange() {
		return dateOfChange;
	}

	public void setDateOfChange(Timestamp dateOfChange) {
		this.dateOfChange = dateOfChange;
	}

	public Timestamp getOldDeadline() {
		return oldDeadline;
	}

	public void setOldDeadline(Timestamp oldDeadline) {
		this.oldDeadline = oldDeadline;
	}

	public Timestamp getNewDeadline() {
		return newDeadline;
	}

	public void setNewDeadline(Timestamp newDeadline) {
		this.newDeadline = newDeadline;
	}

}
