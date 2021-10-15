package Entities;

import java.io.Serializable;


/**
 * This class contains all the needed information about the student as represented in the database.
 * 
 * @author Bshara
 * */
public class Student extends SystemUser implements Serializable {

	private static Student emptyInstance = new Student(null, null, null, null, null, null, false, 0, null);

	public static Student getEmptyInstance() {
		return emptyInstance;
	}

	public long stuNumber;
	public String stuDepartment;



	public Student(String userName, String password, String email, String firstName, String lastName, String phoneNo,
			boolean isOnline, long stuNumber, String stuDepartment) {
		super(userName, password, email, firstName, lastName, phoneNo, isOnline);
		this.stuNumber = stuNumber;
		this.stuDepartment = stuDepartment;
	}

	public long getStuNumber() {
		return stuNumber;
	}

	public void setStuNumber(long stuNumber) {
		this.stuNumber = stuNumber;
	}

	public String getStuDepartment() {
		return stuDepartment;
	}

	public void setStuDepartment(String stuDepartment) {
		this.stuDepartment = stuDepartment;
	}

	@Override
	public int getPrimaryKeyIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getForeignKeyIndex() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public String getReferenceTableName() {
		return "SystemUser";
	}

	@Override
	public boolean hasForeignKey() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getReferenceTableForeignKeyName() {
		// TODO Auto-generated method stub
		return "userName";
	}

	@Override
	public int fieldsLastIndex() {
		// TODO Auto-generated method stub
		return 2;
	}
}
