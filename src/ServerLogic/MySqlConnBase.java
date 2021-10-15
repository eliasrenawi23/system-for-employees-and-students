package ServerLogic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ServerLogic.UtilityInterfaces.IPreparedStatement;
import ServerLogic.UtilityInterfaces.IStatement;
import Utility.VoidFunc;

/**
 * This class acts as a layer between the sql connection class and MySql class
 * to provide more robust implementations of the sql methods.
 * 
 * @author Bshara
 * */
public abstract class MySqlConnBase {

	protected String username;
	protected String password;
	protected String schemaName;
	protected Connection conn;
	private VoidFunc connectionErrorEvent;

	public MySqlConnBase(String username, String password, String databaseName, VoidFunc connectionErrorEvent) {

		this.username = username;
		this.password = password;
		this.schemaName = databaseName;
		this.connectionErrorEvent = connectionErrorEvent;

		setDrive();
		connect();
	}

	private void setDrive() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			// System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
		}
	}

	private void connect() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/" + schemaName + "?serverTimezone=IST",
					username, password);

			//System.out.println("SQL connection succeed");

		} catch (SQLException ex) {/* handle any errors */

			if (connectionErrorEvent != null) {
				connectionErrorEvent.call();
			}
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	public void executeStatement(String query, IStatement func) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (func != null)
				func.executeQuery(rs);
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int executeStatement(String query) {
		int result = 0;
		try {
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				result = rs.getInt(1);
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int executePreparedStatement(String query, IPreparedStatement func) {
		try {
			PreparedStatement ps = conn.prepareStatement(query);
			if(func != null)
				func.executeChanges(ps);
			return ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void setConnectionErrorEvent(VoidFunc event) {
		connectionErrorEvent = event;
	}

}
