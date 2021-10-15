package Entities;

import java.io.Serializable;

/**
 * This class contains all the needed information about the system user as represented in the database.
 * 
 * @author Bshara
 * */
public class SystemUser extends SqlObject implements Serializable {

	public String userName, password, email;
	public String firstName, lastName, phoneNo;
	public boolean isOnline;
	
	private static SystemUser emptyInstance = new SystemUser(null, null, null, null, null, null, false);
	public static SystemUser getEmptyInstance() {
		return emptyInstance;
	}
	
	public SystemUser(String userName, String password, String email, String firstName, String lastName, String phoneNo,
			boolean isOnline) {
		super();
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNo = phoneNo;
		this.isOnline = isOnline;
	}

	
	
	
	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
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
	public boolean hasForeignKey() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getReferenceTableForeignKeyName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReferenceTableName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int fieldsLastIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
	@Override
	public String toString() {
		return "SystemUser [userName=" + userName + ", password=" + password + ", email=" + email + ", firstName="
				+ firstName + ", lastName=" + lastName + ", phoneNo=" + phoneNo + "]";
	}
	
	
	
	
}
