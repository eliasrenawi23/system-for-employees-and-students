package ServerLogic;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import Entities.*;
import ServerLogic.UtilityInterfaces.IPreparedStatement;
import ServerLogic.UtilityInterfaces.IStatement;
import ServerLogic.UtilityInterfaces.UpdateFunc;
import Utility.DateUtil;
import Utility.VoidFunc;

/**
 * A MySql class that is built for fast development of Client/Server
 * application. the class contains various methods that are required for this
 * project. methods like insertion, update and select. the class extends the
 * MySqlConnBase class which contains an sql connection class.
 * 
 * @author Bshara
 * 
 */
public class MySQL extends MySqlConnBase {

	private QueryBuilder qb;

	public MySQL(String username, String password, String schemaName, VoidFunc connectionErrorEvent) {
		super(username, password, schemaName, connectionErrorEvent);
		qb = new QueryBuilder();
	}

	/* print Table columns */

	/**
	 * Prints the table columns with their indexes.
	 * 
	 * @param tableName the name of the table to print columns from.
	 */
	public void printTableColumns(String tableName) {
		IStatement statment = f -> {
			try {
				// Get columns count
				int colCnt = f.getMetaData().getColumnCount();

				for (int i = 0; i < colCnt; i++) {
					System.out.println("Column index [" + (i + 1) + "], Column name ["
							+ f.getMetaData().getColumnLabel(i + 1) + "]");
				}

			} catch (SQLException e) {
			}
		};

		executeStatement("SELECT * FROM " + tableName, statment);
	}

	/* get table column name by index */

	/**
	 * Returns the table column name by the given index.
	 * 
	 * @param index     the index of the column.
	 * @param tableName name of the table to get the column name from.
	 */
	public String getTableColumnName(int index, String tableName) {
		ArrayList<String> result = new ArrayList<String>(1);
		IStatement statment = f -> {
			try {

				result.add(f.getMetaData().getColumnLabel(index));

			} catch (SQLException e) {
			}
		};

		executeStatement("SELECT * FROM " + tableName, statment);
		return result.get(0);
	}

	/* update Table Data */

	public void updateTableData(String tableName, String Set, String Where) {
		String updateQuery = "UPDATE " + tableName + " SET " + Set + " WHERE " + Where;
		executePreparedStatement(updateQuery, null);
	}

	public void updateByObject(SqlObject obj, UpdateFunc uFunc) {

		QueryBuilder qb = new QueryBuilder();

		String updateQuery = qb.update(obj.getTableName()).set(obj.getFieldsAndValues()).where(obj.getPrimaryKeyName())
				.eq(obj.getPrimaryKeyValue()).toString();

		System.out.println(updateQuery);
		int numOfRowsChanged = executePreparedStatement(updateQuery, null);

		// Execute after getting the number of changed rows
		// uFunc.execute(numOfRowsChanged);
	}

	public void updateByObject(SqlObject obj) {

		updateByObject(obj, null);
	}

	public <E> String getEveryFieldWithValue(Class<E> ctype, Object obj) {

		Field[] fields = ctype.getFields();
		String result = "";

		for (int i = 0; i < fields.length; i++) {
			try {
				result += "`" + fields[i].getName() + "` = '";
				result += fields[i].get(obj).toString() + "'";
				if (i != fields.length - 1)
					result += ", ";
			} catch (IllegalArgumentException | IllegalAccessException e) {

				e.printStackTrace();
			}
		}

		return result;
	}

	/* insert using object */

	/**
	 * Check if an object exist by return true if it does exist, otherwise false.
	 */
	public boolean doesObjectExist(SqlObject obj) {

		String query = qb.select(qb.count(obj.getPrimaryKeyName())).from(obj.getTableName())
				.where(obj.getPrimaryKeyName()).eq(obj.getPrimaryKeyValue()).toString();

		ArrayList<Integer> res = new ArrayList<Integer>();
		executeStatement(query, rs -> {
			try {
				rs.next();
				res.add(rs.getInt(1));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		// if result was not added or the result is 0 then return false
		// otherwise return true.
		return res.size() == 0 ? false : res.get(0) > 0 ? true : false;
	}

	/**
	 * Returns a new max id by the parameter object table.
	 */
	public long getNewMaxID(SqlObject obj) {

		String query = qb.select(qb.max(obj.getPrimaryKeyName())).from(obj.getTableName()).toString();

		ArrayList<Long> res = new ArrayList<Long>();
		executeStatement(query, rs -> {
			try {
				rs.next();
				res.add(rs.getLong(1));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		// Return the max id + 1 to get a new max id.
		return res.get(0) + 1;
	}

	public int insertObject(SqlObject obj) {

		String query = qb.insertInto(obj.getTableName()).forColumns(obj.getFieldsNames())
				.theValues(obj.getFieldsValues()).toString();

		System.out.println(query);

		return executePreparedStatement(query, null) > 0 ? 1 : 0;

	}

	public int deleteObject(SqlObject obj) {

		String query = qb.deleteFrom(obj.getTableName()).where(obj.getPrimaryKeyName()).eq(obj.getPrimaryKeyValue())
				.toString();

		System.out.println(query);
		return executePreparedStatement(query, null) > 0 ? 1 : 0;

	}

	public void createTable(SqlObject obj) {

		StringBuilder query = new StringBuilder();
		query.append("CREATE TABLE IF NOT EXISTS ");
		query.append(obj.tableInfo());

		query.append(";");

		executePreparedStatement(query.toString(), null);

	}

	public int insertFile(File file) {

		String query = "INSERT INTO `icm`.`file` (`requestID`, `data`, `fileName`, `type`) VALUES (?, ?, ?, ?);";
		try {

			PreparedStatement ps = conn.prepareStatement(query);

			ps.setLong(1, file.getRequestID());
			ps.setBlob(2, file.getBinaryStream());
			ps.setString(3, file.getFileName());
			ps.setString(4, file.getType());

			return ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public File getFile(long fileID) {
		String query = "SELECT * FROM `icm`.`file` WHERE `icm`.`file`.ID = ?;";
		try {
			PreparedStatement ps = conn.prepareStatement(query);

			ps.setLong(1, fileID);
			ps.execute();

			ResultSet rs = ps.getResultSet();
			if (rs.next()) {

				File file = new File(fileID, rs.getLong(2), rs.getString(4), rs.getString(5));

				file.setBytes(rs.getBlob(3).getBinaryStream(), (int) rs.getBlob(3).length());

				return file;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<ChangeRequest> getChangeRequests(int options) {

		return getChangeRequests(null, -1, -1);
	}

	public ArrayList<Phase> getPhases() {
		return getPhases(0);
	}

	public ArrayList<Phase> getPhases(long forRequestID) {
		String query = "SELECT icm.phase.* FROM icm.changerequest "
				+ "INNER JOIN icm.phase ON icm.phase.requestID=icm.changerequest.requestID ";

		if (forRequestID != 0) {
			query += "WHERE icm.changerequest.requestID = " + forRequestID;
		}

		query += " ORDER BY icm.changerequest.requestID ASC ,icm.phase.requestID ASC ";

		ArrayList<Phase> results = new ArrayList<Phase>();

		IStatement prepS = rs -> {
			try {
				while (rs.next()) {

					Phase phase = new Phase(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4),
							rs.getLong(5), rs.getTimestamp(6), rs.getTimestamp(7), rs.getTimestamp(8),
							rs.getTimestamp(9), rs.getBoolean(10));

					// add at the last change request
					results.add(phase);

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executeStatement(query, prepS);

		return results;

	}

	public ArrayList<ExecutionReport> getExecutionReports() {
		return getExecutionReports(0);
	}

	public ArrayList<ExecutionReport> getExecutionReports(long forRequestID) {
		String query = "SELECT icm.executionreport.* FROM icm.changerequest "
				+ "INNER JOIN icm.executionreport ON icm.executionreport.requestID=icm.changerequest.requestID ";

		if (forRequestID != 0) {
			query += "WHERE icm.changerequest.username = " + forRequestID;
		}

		query += " ORDER BY icm.changerequest.requestID ASC ,icm.executionreport.requestID ASC ";

		ArrayList<ExecutionReport> results = new ArrayList<ExecutionReport>();

		IStatement prepS = rs -> {
			try {
				while (rs.next()) {

					ExecutionReport exeRep = new ExecutionReport(rs.getLong(1), rs.getLong(2), rs.getString(3),
							rs.getString(4));

					results.add(exeRep);

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executeStatement(query, prepS);

		return results;

	}

	public ArrayList<EvaluationReport> getEvaluationReports() {
		return getEvaluationReports(0);
	}

	public ArrayList<EvaluationReport> getEvaluationReports(long forRequestID) {
		String query = "SELECT icm.evaluationreport.* FROM icm.changerequest "
				+ "INNER JOIN icm.evaluationreport ON icm.evaluationreport.requestID=icm.changerequest.requestID ";

		if (forRequestID != 0) {
			query += "WHERE icm.changerequest.username = " + forRequestID;
		}
		query += " ORDER BY icm.changerequest.requestID ASC ,icm.evaluationreport.requestID ASC ";

		ArrayList<EvaluationReport> results = new ArrayList<EvaluationReport>();

		IStatement prepS = rs -> {
			try {
				while (rs.next()) {

					EvaluationReport evalRep = new EvaluationReport(rs.getLong(5), rs.getLong(6), rs.getString(7),
							rs.getString(8), rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4));

					results.add(evalRep);

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executeStatement(query, prepS);

		return results;

	}

	public ArrayList<File> getFiles() {
		return getFiles(0);
	}

	public ArrayList<File> getFiles(long forRequestID) {
		String query = "SELECT f.* FROM icm.file as f\r\n"
				+ "inner join icm.changerequest as c on c.requestID = f.requestID\r\n" + "where f.requestID = '"
				+ forRequestID + "';";

		ArrayList<File> results = new ArrayList<File>();

		IStatement prepS = rs -> {
			try {
				while (rs.next()) {

					File file = new File(rs.getLong(1), rs.getLong(2), rs.getString(4), rs.getString(5));

					file.setBytes(rs.getBlob(3).getBinaryStream(), (int) rs.getBlob(3).length());

					results.add(file);

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executeStatement(query, prepS);

		return results;

	}

	public ArrayList<ChangeRequest> getChangeRequests() {
		return getChangeRequests(0);
	}

	// SELECT COUNT(*) FROM icm.changerequest WHERE icm.changerequest.username =
	// 'username2'

	public int getCountOf(SqlObject obj, String whereCondition) {

		String query = qb.select(qb.count("*")).from(obj.getTableName()).where(whereCondition).toString();

		return executeStatement(query);

	}

	public ArrayList<ChangeRequest> getChangeRequests(String forUsername, int startingRow, int size) {

		String query = "SELECT * FROM icm.changerequest ";

		// This is added so we get the requests for all of the users, used for the
		// overridden function.
		if (forUsername != null) {
			query += "WHERE icm.changerequest.username = '" + forUsername + "' ";
		}

		query += "ORDER BY icm.changerequest.requestID ASC ";

		if (size > 0)
			query += "LIMIT " + startingRow + ", " + size;

		ArrayList<ChangeRequest> results = new ArrayList<ChangeRequest>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {

					ChangeRequest cr = new ChangeRequest(rs.getLong(1), rs.getString(2), rs.getTimestamp(3),
							rs.getTimestamp(4), rs.getTimestamp(5), rs.getString(6), rs.getString(7), rs.getString(8),
							rs.getString(9), rs.getString(10));

					results.add(cr);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executeStatement(query, prepS);

		return results;
	}

	public ArrayList<ChangeRequest> getChangeRequestPhaseByEmployee(long empNum, PhaseType phaseType) {

		String query = "SELECT * FROM icm.employee as emp "
				+ "INNER JOIN icm.phase as ph ON ph.empNumber = emp.empNumber "
				+ "INNER JOIN icm.changerequest as cr ON cr.requestID = ph.requestID " + "WHERE ph.empNumber = '"
				+ empNum
				+ "' AND ph.status != 'Waiting To Set Evaluator' AND ph.status != 'closed' AND ph.phaseName = '"
				+ phaseType.name() + "' ORDER BY ph.deadline ASC";

		ArrayList<ChangeRequest> results = new ArrayList<ChangeRequest>();

		IStatement prepS = rs -> {

			// o = offset
			try {

				while (rs.next()) {
					int o = 4;

					Phase phase = new Phase(rs.getLong(o + 1), rs.getLong(o + 2), rs.getString(o + 3),
							rs.getString(o + 4), rs.getLong(o + 5), rs.getTimestamp(o + 6), rs.getTimestamp(o + 7),
							rs.getTimestamp(o + 8), rs.getTimestamp(o + 9), rs.getBoolean(o + 10));

					o += 10;

					ChangeRequest changeRequest = new ChangeRequest(rs.getLong(o + 1), rs.getString(o + 2),
							rs.getTimestamp(o + 3), rs.getTimestamp(o + 4), rs.getTimestamp(o + 5), rs.getString(o + 6),
							rs.getString(o + 7), rs.getString(o + 8), rs.getString(o + 9), rs.getString(o + 10));

					changeRequest.getPhases().add(phase);

					results.add(changeRequest);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executeStatement(query, prepS);

		return results;
	}

	public int updateMessage(Message msg) {

		String query = "UPDATE `icm`.`message` SET `subject` = ?, `from` = ?, `to` = ?,"
				+ " `messageContentLT` = ?, `hasBeenViewed` = ?, `sentAt` = ?,"
				+ " `isStarred` = ?, `isRead` = ?, `isArchived` = ?, `requestId` = ?, `phaseId` = ? WHERE (`messageID` = ?);";

		IPreparedStatement prepS = ps -> {
			try {

				ps.setString(1, msg.getSubject());
				ps.setString(2, msg.getFrom());
				ps.setString(3, msg.getTo());
				ps.setString(4, msg.getMessageContentLT());
				ps.setBoolean(5, msg.isHasBeenViewed());
				ps.setTimestamp(6, msg.getSentAt());
				ps.setBoolean(7, msg.isStarred());
				ps.setBoolean(8, msg.isRead());
				ps.setBoolean(9, msg.isArchived());
				ps.setLong(10, msg.getRequestId());
				ps.setLong(11, msg.getPhaseId());
				ps.setLong(12, msg.getMessageID());

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		return executePreparedStatement(query, prepS);

	}

	public Message getMessage(long msgID) {
		String query = "SELECT * FROM icm.message where icm.message.messageID = '" + msgID + "';";

		ArrayList<Message> res = new ArrayList<Message>(1);
		IStatement prepS = rs -> {
			try {

				if (rs.next()) {

					Message msg = new Message(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
							rs.getString(5), rs.getBoolean(6), rs.getTimestamp(7), rs.getBoolean(8), rs.getBoolean(9),
							rs.getBoolean(10), rs.getLong(11), rs.getLong(12));

					res.add(msg);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executeStatement(query, prepS);

		return res.get(0);
	}

	public ArrayList<Message> getMessages(String forUsername, int startingRow, int size) {
		String query = "SELECT * FROM icm.message WHERE icm.message.to = '" + forUsername + "' ";

		query += "ORDER BY icm.message.sentAt DESC ";

		if (size > 0)
			query += "LIMIT " + startingRow + ", " + size;

		ArrayList<Message> results = new ArrayList<Message>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {

					Message cr = new Message(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
							rs.getString(5), rs.getBoolean(6), rs.getTimestamp(7), rs.getBoolean(8), rs.getBoolean(9),
							rs.getBoolean(10), rs.getLong(11), rs.getLong(12));

					results.add(cr);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executeStatement(query, prepS);

		return results;
	}

	public int getCountOfPhasesByType(long empNumberForPhases, PhaseType phaseType) {
		String query = "SELECT COUNT(*) FROM icm.phase as ph WHERE ph.empNumber = '" + empNumberForPhases
				+ "' AND ph.phaseName = '" + phaseType.name() + "' AND ph.status != 'closed'"
				+ " AND ph.status != 'Waiting To Set Executer' AND ph.status != 'Waiting To Set Evaluator'";

		ArrayList<Integer> results = new ArrayList<Integer>();
		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getInt(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.get(0);
	}

	public int getCountOfPhasesByType(PhaseType phaseType) {
		String query = "SELECT COUNT(*) FROM icm.phase as ph where ph.phaseName = '" + phaseType.name()
				+ "' AND ph.status != 'closed' AND ph.status != 'Rejected'";

		ArrayList<Integer> results = new ArrayList<Integer>();
		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getInt(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.get(0);
	}

	public ArrayList<Phase> getPhasesOfRequest(long requestID) {
		String query = "SELECT * " + "FROM icm.phase as ph "
				+ "INNER JOIN icm.changerequest as cr ON cr.requestID = ph.requestID "
				+ "LEFT JOIN icm.phasetimeextensionrequest as pte ON pte.phaseID = ph.phaseID "
				+ "WHERE ph.requestID = '" + requestID + "';";

		ArrayList<Phase> results = new ArrayList<Phase>();
		IStatement prepS = rs -> {
			try {

				while (rs.next()) {

					int offset = 0;

					Phase phase = new Phase(rs.getLong(offset + 1), rs.getLong(offset + 2), rs.getString(offset + 3),
							rs.getString(offset + 4), rs.getLong(offset + 5), rs.getTimestamp(offset + 6),
							rs.getTimestamp(offset + 7), rs.getTimestamp(offset + 8), rs.getTimestamp(offset + 9),
							rs.getBoolean(offset + 10));

					offset = 10 + 10;

					if (rs.getLong(offset + 1) == 0) {
						System.out.println("Null is found");
					} else {

						System.out.println(
								rs.getLong(offset + 1) + " is found, adding PhaseTimeExtensionRequest to the phase");
						PhaseTimeExtensionRequest pter = new PhaseTimeExtensionRequest(rs.getLong(offset + 1),
								rs.getInt(offset + 2), rs.getInt(offset + 3), rs.getString(offset + 4));
						phase.setPhaseTimeExtensionRequest(pter);
					}

					results.add(phase);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results;

	}

	public SystemUser getSystemUserByRequestID(long requestId) {
		String query = "SELECT *  FROM icm.systemUser as su "
				+ "INNER JOIN icm.changerequest as cr ON cr.username = su.userName WHERE cr.requestID = '" + requestId
				+ "';";

		ArrayList<SystemUser> results = new ArrayList<SystemUser>();
		IStatement prepS = rs -> {
			try {

				if (rs.next()) {

					SystemUser systemUser = new SystemUser(rs.getString(1), rs.getString(2), rs.getString(3),
							rs.getString(4), rs.getString(5), rs.getString(6), rs.getBoolean(7));
					results.add(systemUser);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		if (results.size() == 1) {
			return results.get(0);
		} else {
			System.err.println("Error, user not found for the request id " + requestId);
			return SystemUser.getEmptyInstance();
		}
	}

	public Employee getEmployeeByEmpNumber(long empId) {
		String query = "SELECT * FROM icm.systemuser as su  "
				+ "INNER JOIN icm.employee as emp ON su.username = emp.userName WHERE emp.empNumber = '" + empId + "';";

		ArrayList<Employee> results = new ArrayList<Employee>();
		IStatement prepS = rs -> {
			try {

				if (rs.next()) {

					Employee employee = new Employee(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
							rs.getString(5), rs.getString(6), rs.getBoolean(7), rs.getLong(8), rs.getString(9),
							rs.getString(10));

					results.add(employee);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		if (results.size() == 1) {
			return results.get(0);
		} else {
			System.err.println("Error, user not found for the request id " + empId);
			return Employee.getEmptyInstance();
		}
	}

	public String getFullNameByUsername(String username45) {
		String query = "SELECT su.firstName, su.lastName FROM icm.systemuser as su WHERE su.userName = '" + username45
				+ "';";

		ArrayList<String> results = new ArrayList<String>();
		IStatement prepS = rs -> {
			try {

				if (rs.next()) {

					results.add(rs.getString(1));
					results.add(rs.getString(2));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results.get(0) + " " + results.get(1);

	}

	public int getCountOfPhaseForEmpByUsername(String username23) {

		String query = "SELECT COUNT(ph.phaseName) FROM icm.employee as emp "
				+ "INNER JOIN icm.systemUser as su ON su.userName = emp.userName "
				+ "INNER JOIN icm.phase as ph ON ph.empNumber = emp.empNumber "
				+ "WHERE ph.status != 'Closed' AND su.userName = '" + username23 + "'";

		ArrayList<Integer> results = new ArrayList<Integer>();
		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getInt(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results.get(0);

	}

	public boolean isUserManager(String username23) {

		String query = "SELECT COUNT(*) FROM icm.systemUser as su "
				+ "INNER JOIN icm.manager as m ON m.userName = su.userName WHERE su.userName = '" + username23 + "'";

		ArrayList<Integer> results = new ArrayList<Integer>();
		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getInt(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results.get(0) == 1;
	}

	public boolean isEmployeeIsSupervisor(long empNumber) {
		String query = "SELECT COUNT(*) FROM icm.supervisor as sv WHERE sv.empNumber = '" + empNumber + "'";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getInt(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.get(0) == 1;
	}

	public ArrayList<ChangeRequest> getChangeRequestWithCurrentPhase() {
		String query = "SELECT * FROM icm.phase AS ph "
				+ "INNER JOIN icm.changeRequest as cr ON cr.requestID = ph.requestID order by cr.startDateOfRequest;";

		ArrayList<ChangeRequest> results = new ArrayList<ChangeRequest>();

		IStatement prepS = rs -> {

			// o = offset
			try {

				while (rs.next()) {
					int o = 0;

					Phase phase = new Phase(rs.getLong(o + 1), rs.getLong(o + 2), rs.getString(o + 3),
							rs.getString(o + 4), rs.getLong(o + 5), rs.getTimestamp(o + 6), rs.getTimestamp(o + 7),
							rs.getTimestamp(o + 8), rs.getTimestamp(o + 9), rs.getBoolean(o + 10));

					o += 10;

					ChangeRequest changeRequest = new ChangeRequest(rs.getLong(o + 1), rs.getString(o + 2),
							rs.getTimestamp(o + 3), rs.getTimestamp(o + 4), rs.getTimestamp(o + 5), rs.getString(o + 6),
							rs.getString(o + 7), rs.getString(o + 8), rs.getString(o + 9), rs.getString(o + 10));

					changeRequest.getPhases().add(phase);

					results.add(changeRequest);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executeStatement(query, prepS);

		return results;
	}

	public void insertMessage(Message msg) {

		String query = "INSERT INTO `icm`.`message` (`subject`, `from`, `to`, `messageContentLT`, "
				+ "`hasBeenViewed`, `sentAt`, `isStarred`, `isRead`, `isArchived`, `requestId`, `phaseId`)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);\r\n";
		try {

			PreparedStatement ps = conn.prepareStatement(query);

			ps.setString(1, msg.getSubject());
			ps.setString(2, msg.getFrom());
			ps.setString(3, msg.getTo());
			ps.setString(4, msg.getMessageContentLT());

			ps.setBoolean(5, msg.isHasBeenViewed());

			ps.setTimestamp(6, msg.getSentAt());

			ps.setBoolean(7, msg.isStarred());
			ps.setBoolean(8, msg.isRead());
			ps.setBoolean(9, msg.isArchived());
			ps.setLong(10, msg.getRequestId());
			ps.setLong(11, msg.getPhaseId());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getUsernameOfSupervisor() {
		String query = "SELECT su.userName FROM icm.supervisor as s\r\n"
				+ "inner join icm.employee as e on e.empNumber = s.empNumber\r\n"
				+ "inner join icm.systemuser as su on su.userName = e.userName;";

		ArrayList<String> results = new ArrayList<String>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getString(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1 ? results.get(0) : "Supervisor not assigned";
	}

	public boolean isPhaseStatus(long phaseId, PhaseStatus status) {
		String query = "SELECT COUNT(*) FROM icm.phase as ph where ph.phaseID = '" + phaseId + "' and ph.status = '"
				+ status.name() + "';";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getInt(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1 ? results.get(0) == 1 : false;
	}

	public ChangeRequest getChangeRequestById(long requestID) {

		String query = "SELECT * FROM icm.changerequest as cr where cr.requestID = '" + requestID + "';";

		ArrayList<ChangeRequest> results = new ArrayList<ChangeRequest>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {

					int o = 0;
					ChangeRequest changeRequest = new ChangeRequest(rs.getLong(o + 1), rs.getString(o + 2),
							rs.getTimestamp(o + 3), rs.getTimestamp(o + 4), rs.getTimestamp(o + 5), rs.getString(o + 6),
							rs.getString(o + 7), rs.getString(o + 8), rs.getString(o + 9), rs.getString(o + 10));

					results.add(changeRequest);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1 ? results.get(0) : null;
	}

	public ArrayList<Employee> getEmployees() {

		String query = "SELECT * FROM icm.systemUser as su\r\n"
				+ "inner join icm.employee as e on e.userName=su.userName;";

		ArrayList<Employee> results = new ArrayList<Employee>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {

					Employee emp = new Employee(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
							rs.getString(5), rs.getString(6), rs.getBoolean(7), rs.getLong(8), rs.getString(9),
							rs.getString(10));

					results.add(emp);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results;
	}

	public String getUsernameByEmpNumber(long empNumber) {

		String query = "SELECT su.userName FROM icm.systemUser as su "
				+ "inner join icm.employee as e on e.userName=su.userName where e.empNumber = '" + empNumber + "';";

		ArrayList<String> results = new ArrayList<String>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {

					results.add(rs.getString(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1 ? results.get(0) : "";

	}

	public boolean insertTimeExtension(PhaseTimeExtensionRequest pter) {

		String query = "INSERT INTO `icm`.`phasetimeextensionrequest` (`phaseID`, `requestedTimeInDays`, `requestedTimeInHours`, `description`) VALUES (?, ?, ?, ?);";

		IPreparedStatement prepS = ps -> {
			try {

				ps.setLong(1, pter.getPhaseID());
				ps.setInt(2, pter.getRequestedTimeInDays());
				ps.setInt(3, pter.getRequestedTimeInHours());
				ps.setString(4, pter.getDescription());

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		return executePreparedStatement(query, prepS) == 1;

	}

	public boolean updatePhaseStatus(long phaseId, PhaseStatus status) {

		String statusName = status.nameNo_();

		String query = "UPDATE `icm`.`phase` SET `status` = ? WHERE (`phaseID` = ?);";

		IPreparedStatement prepS = ps -> {
			try {

				ps.setString(1, statusName);
				ps.setLong(2, phaseId);

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		return executePreparedStatement(query, prepS) == 1;

	}

	public boolean updatePhaseTimeOfCompletion(long phaseId, Timestamp dateTime) {

		String query = "UPDATE `icm`.`phase` SET `timeOfCompletion` = ? WHERE (`phaseID` = ?);";

		IPreparedStatement prepS = ps -> {
			try {

				ps.setTimestamp(1, dateTime);
				ps.setLong(2, phaseId);

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		return executePreparedStatement(query, prepS) == 1;

	}

	public long getRequestIdByPhaseId(long phId) {

		String query = "SELECT p.requestID FROM icm.phase as p where p.phaseID = '" + phId + "';";

		ArrayList<Long> results = new ArrayList<Long>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getLong(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1 ? results.get(0) : -1;

	}

	public String getFullNameByPhaseId(long phId) {

		String query = "SELECT s.firstName, s.lastName FROM icm.phase as p\r\n"
				+ "inner join icm.employee as e on p.empNumber = e.empNumber\r\n"
				+ "inner join icm.systemUser as s on s.userName = e.userName where p.phaseID = '" + phId + "';";

		ArrayList<String> results = new ArrayList<String>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getString(1));
					results.add(rs.getString(2));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executeStatement(query, prepS);
		return results.size() == 0 ? "" : results.get(0) + " " + results.get(1);
	}

	public long getComHeadEmpNum() {

		String query = "SELECT e.empNumber FROM icm.employee as e\r\n"
				+ "inner join icm.executionchangescommitteemember as c on c.empNumber = e.empNumber\r\n"
				+ "where c.isManager = '1'";

		ArrayList<Long> results = new ArrayList<Long>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getLong(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1 ? results.get(0) : -1;

	}

	public ArrayList<String> getComsUsernames() {
		String query = "SELECT su.userName FROM icm.systemUser as su\r\n"
				+ "inner join icm.employee as e on e.userName=su.userName\r\n"
				+ "inner join icm.executionchangescommitteemember as c on c.empNumber = e.empNumber";

		ArrayList<String> results = new ArrayList<String>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {
					results.add(rs.getString(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() > 0 ? results : null;
	}

	public ArrayList<Long> getComsEmpNums() {
		String query = "SELECT e.empNumber FROM icm.systemUser as su\r\n"
				+ "inner join icm.employee as e on e.userName=su.userName\r\n"
				+ "inner join icm.executionchangescommitteemember as c on c.empNumber = e.empNumber";

		ArrayList<Long> results = new ArrayList<Long>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {
					results.add(rs.getLong(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() > 0 ? results : null;
	}

	public ArrayList<Long> getComsEmpNumsWithoutManager() {
		String query = "SELECT e.empNumber FROM icm.systemUser as su\r\n"
				+ "inner join icm.employee as e on e.userName=su.userName\r\n"
				+ "inner join icm.executionchangescommitteemember as c on c.empNumber = e.empNumber"
				+ " where c.isManager = '0'";

		ArrayList<Long> results = new ArrayList<Long>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {
					results.add(rs.getLong(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() > 0 ? results : null;
	}

	public ArrayList<ChangeRequest> getChangeRequestPhaseForCom() {
		String query = "SELECT * FROM icm.phase p\r\n"
				+ "inner join icm.changerequest as c on c.requestID = p.requestID\r\n"
				+ "left join icm.evaluationreport as e on e.phaseID = p.phaseID\r\n"
				+ "where p.phaseName = 'decision' and p.status != 'Closed' and p.status != 'Rejected' ORDER BY p.startingDate ASC";

		ArrayList<ChangeRequest> results = new ArrayList<ChangeRequest>();

		IStatement prepS = rs -> {

			// o = offset
			try {

				while (rs.next()) {
					int o = 0;

					Phase phase = new Phase(rs.getLong(o + 1), rs.getLong(o + 2), rs.getString(o + 3),
							rs.getString(o + 4), rs.getLong(o + 5), rs.getTimestamp(o + 6), rs.getTimestamp(o + 7),
							rs.getTimestamp(o + 8), rs.getTimestamp(o + 9), rs.getBoolean(o + 10));

					o += 10;

					ChangeRequest changeRequest = new ChangeRequest(rs.getLong(o + 1), rs.getString(o + 2),
							rs.getTimestamp(o + 3), rs.getTimestamp(o + 4), rs.getTimestamp(o + 5), rs.getString(o + 6),
							rs.getString(o + 7), rs.getString(o + 8), rs.getString(o + 9), rs.getString(o + 10));

					o += 10;

					if (rs.getLong(o + 1) != 0) {
						EvaluationReport evalRep = new EvaluationReport(rs.getLong(o + 5), rs.getLong(o + 6),
								rs.getString(o + 7), rs.getString(o + 8), rs.getString(o + 1), rs.getString(o + 2),
								rs.getString(o + 3), rs.getTimestamp(o + 4));

						phase.setEvaluationReport(evalRep);
					}

					changeRequest.getPhases().add(phase);

					results.add(changeRequest);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executeStatement(query, prepS);

		return results;
	}

	public String getRequestOwnerUsername(long requestID) {

		String query = "SELECT s.userName FROM icm.systemUser as s\r\n"
				+ "inner join icm.changerequest as c on c.username = s.username where c.requestID = '" + requestID
				+ "';";

		ArrayList<String> results = new ArrayList<String>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {
					results.add(rs.getString(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() > 0 ? results.get(0) : "";
	}

	public Long getLatestEvaluatorEmpNumber(long requestID) {
		String query = "SELECT p.empNumber FROM icm.systemUser as s\r\n"
				+ "inner join icm.changerequest as c on c.username = s.username\r\n"
				+ "inner join icm.phase as p on p.requestID = c.requestID\r\n" + "where c.requestID = '" + requestID
				+ "' and p.phaseName = 'evaluation' and p.status = 'Closed' \r\n" + "ORDER BY p.startingDate DESC;";

		ArrayList<Long> results = new ArrayList<Long>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {
					results.add(rs.getLong(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1 ? results.get(0) : -1;
	}

	public EvaluationReport getLatestEvaluationReport(long requestId6663) {

		String query = "SELECT * FROM icm.evaluationreport as e\r\n"
				+ "inner join icm.phase as p on p.phaseID = e.phaseID\r\n"
				+ "where p.phaseName = 'Evaluation' and p.status = 'Closed' and p.requestId = '" + requestId6663
				+ "'\r\n" + "order by p.startingDate desc;";

		ArrayList<EvaluationReport> results = new ArrayList<EvaluationReport>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					EvaluationReport evalRep = new EvaluationReport(rs.getLong(5), rs.getLong(6), rs.getString(7),
							rs.getString(8), rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4));

					results.add(evalRep);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1 ? results.get(0) : null;
	}

	public long getSupervisorEmpNum() {
		String query = "SELECT * FROM icm.supervisor;";

		ArrayList<Long> results = new ArrayList<Long>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getLong(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1 ? results.get(0) : -1;
	}

	public String getUsernameOfComHead() {

		String query = "SELECT su.userName FROM icm.executionchangescommitteemember as s\r\n"
				+ "inner join icm.employee as e on e.empNumber = s.empNumber\r\n"
				+ "inner join icm.systemuser as su on su.userName = e.userName\r\n" + "where s.isManager = '1';";

		ArrayList<String> results = new ArrayList<String>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getString(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1 ? results.get(0) : "Supervisor not assigned";
	}

	public ExecutionReport getLatestExecutionReport(long requestId) {

		String query = "SELECT e.reportID, e.phaseID, e.contentLT, e.place FROM icm.executionreport as e\r\n"
				+ "inner join icm.phase as p on p.phaseID = e.phaseID\r\n"
				+ "inner join icm.changerequest as c on c.requestID = p.requestID\r\n"
				+ "where p.status = 'Closed' and c.requestID = '" + requestId + "'\r\n"
				+ "order by p.startingDate desc";

		ArrayList<ExecutionReport> results = new ArrayList<ExecutionReport>();

		IStatement prepS = rs -> {
			try {
				if (rs.next()) {

					ExecutionReport exeRep = new ExecutionReport(rs.getLong(1), rs.getLong(2), rs.getString(3),
							rs.getString(4));

					results.add(exeRep);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executeStatement(query, prepS);

		return results.size() == 1 ? results.get(0) : null;
	}

	public ArrayList<Object> getRegularCommitteeMemebersNamesAndNumbers() {

		String query = "SELECT u.firstName, u.lastName, e.empNumber FROM icm.executionchangescommitteemember as t\r\n"
				+ "inner join icm.employee as e on e.empNumber = t.empNumber\r\n"
				+ "inner join icm.systemuser as u on u.username = e.userName\r\n" + "where t.isManager = '0'";

		ArrayList<Object> results = new ArrayList<Object>();

		IStatement prepS = rs -> {

			try {
				while (rs.next()) {

					results.add(rs.getString(1) + " " + rs.getString(2));
					results.add(rs.getLong(3));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executeStatement(query, prepS);

		return results;

	}

	public boolean isRequestStatusEql(PhaseType type, PhaseStatus status) {

		String query = "SELECT COUNT(*) FROM icm.phase as p where p.phaseName = '" + type.name() + "' and p.status = '"
				+ status.nameNo_() + "';";

		ArrayList<Boolean> results = new ArrayList<Boolean>();

		IStatement prepS = rs -> {

			try {
				if (rs.next()) {

					results.add(rs.getBoolean(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executeStatement(query, prepS);

		return results.size() == 1 ? results.get(0) : false;

	}

	public Phase getClosingPhase(long reqId12) {

		String query = "SELECT * FROM icm.phase as p inner join icm.changerequest as c on c.requestID=p.requestID "
				+ "where p.phaseName = 'Closing' and c.requestID = '" + reqId12 + "' order by p.startingDate;";

		ArrayList<Phase> results = new ArrayList<Phase>();

		IStatement prepS = rs -> {

			try {
				if (rs.next()) {

					Phase phase = new Phase(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4),
							rs.getLong(5), rs.getTimestamp(6), rs.getTimestamp(7), rs.getTimestamp(8),
							rs.getTimestamp(9), rs.getBoolean(10));

					results.add(phase);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executeStatement(query, prepS);

		return results.size() == 1 ? results.get(0) : null;

	}

	public void setCompletionTimeOfRequestToNow(long requestID) {

		String query = "UPDATE `icm`.`changerequest` SET `endDateOfRequest` = ? WHERE (`requestID` = ?);";

		IPreparedStatement prepS = ps -> {
			try {

				ps.setTimestamp(1, DateUtil.now());
				ps.setLong(2, requestID);

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executePreparedStatement(query, prepS);
	}

	public void setRequestEstimateTimeOfExecution(long requestID, Timestamp estimatedExecutionTime) {

		String query = "UPDATE `icm`.`changerequest` SET `estimatedTimeForExecution` = ? WHERE (`requestID` = ?);";

		IPreparedStatement prepS = ps -> {
			try {

				ps.setTimestamp(1, estimatedExecutionTime);
				ps.setLong(2, requestID);

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executePreparedStatement(query, prepS);

	}

	public String getUsernameOfEmployee(long phaseId) {

		String query = "SELECT e.userName FROM icm.phase as p "
				+ "inner join icm.employee as e on e.empNumber = p.empNumber where p.phaseID = '" + phaseId + "'";

		ArrayList<String> results = new ArrayList<String>();

		IStatement prepS = rs -> {

			try {
				if (rs.next()) {

					results.add(rs.getString(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executeStatement(query, prepS);

		return results.size() == 1 ? results.get(0) : "";
	}

	public String getUsernameOfManager() {

		String query = "SELECT * FROM icm.manager;";

		ArrayList<String> results = new ArrayList<String>();

		IStatement prepS = rs -> {

			try {
				if (rs.next()) {

					results.add(rs.getString(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executeStatement(query, prepS);

		return results.size() == 1 ? results.get(0) : "";
	}

	public void insertFreeze(long phaseID, String status, Timestamp initDate, Timestamp endDate) {

		String query = "INSERT INTO `icm`.`freeze` (`phaseId`, `prevPhaseStatus`, `initDate`, `endDate`) VALUES (?, ?, ?, ?);";

		IPreparedStatement prepS = ps -> {
			try {
				ps.setLong(1, phaseID);
				ps.setString(2, status);
				ps.setTimestamp(3, initDate);
				ps.setTimestamp(4, endDate);

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executePreparedStatement(query, prepS);
	}

	public PhaseStatus getPreviousStatusBeforeFreeze(long phaseID) {

		String query = "SELECT f.prevPhaseStatus FROM icm.freeze as f where f.phaseId = '" + phaseID
				+ "' order by f.initDate desc;";

		ArrayList<PhaseStatus> results = new ArrayList<PhaseStatus>();

		IStatement prepS = rs -> {

			try {
				if (rs.next()) {

					results.add(PhaseStatus.valueOfAdvanced(rs.getString(1)));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executeStatement(query, prepS);

		return results.size() == 1 ? results.get(0) : null;
	}

	public long getLatestFreezeId(long phaseID) {

		String query = "SELECT f.id FROM icm.freeze as f where f.phaseId = '" + phaseID + "' order by f.initDate desc;";

		ArrayList<Long> results = new ArrayList<Long>();

		IStatement prepS = rs -> {

			try {
				if (rs.next()) {

					results.add(rs.getLong(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executeStatement(query, prepS);

		return results.size() == 1 ? results.get(0) : null;

	}

	public void setLatestFreezeEndDate(long phaseID, Timestamp dateTime) {
		long freezeId = getLatestFreezeId(phaseID);
		String query = "UPDATE `icm`.`freeze` SET `endDate` = ? WHERE (`id` = ?);";

		IPreparedStatement prepS = ps -> {
			try {

				ps.setTimestamp(1, dateTime);
				ps.setLong(2, freezeId);

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executePreparedStatement(query, prepS);

	}

	public long getEmpNumByUsername(String username23) {

		String query = "SELECT e.empNumber FROM icm.employee as e where e.username = '" + username23 + "';";

		ArrayList<Long> results = new ArrayList<Long>();

		IStatement prepS = rs -> {

			try {
				if (rs.next()) {

					results.add(rs.getLong(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executeStatement(query, prepS);

		return results.size() == 1 ? results.get(0) : -1;
	}

	public boolean isEmployeeComMember(long empNum) {
		String query = "SELECT * FROM icm.employee as e\r\n"
				+ "inner join icm.executionchangescommitteemember t on t.empNumber = e.empNumber\r\n"
				+ "where e.empNumber = '" + empNum + "'";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(10); // just add, doesn't matter what...
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1;
	}

	public boolean isEmployeeComHead(long empNum) {
		String query = "SELECT * FROM icm.employee as e\r\n"
				+ "inner join icm.executionchangescommitteemember t on t.empNumber = e.empNumber\r\n"
				+ "where e.empNumber = '" + empNum + "' AND t.isManager = '1'";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(10); // just add, doesn't matter what...
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1;
	}

	public boolean canLogIn(String username24, String password) {
		String query = "SELECT COUNT(*) FROM icm.systemuser as s where s.userName = '" + username24
				+ "' AND s.password = '" + password + "';";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getInt(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1 ? results.get(0) == 1 : false;
	}

	public SystemUser getSystemUserByUsername(String username24) {
		String query = "SELECT * FROM icm.systemUser as su " + "WHERE su.username = '" + username24 + "';";

		ArrayList<SystemUser> results = new ArrayList<SystemUser>();
		IStatement prepS = rs -> {
			try {

				if (rs.next()) {

					SystemUser systemUser = new SystemUser(rs.getString(1), rs.getString(2), rs.getString(3),
							rs.getString(4), rs.getString(5), rs.getString(6), rs.getBoolean(7));
					results.add(systemUser);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results.size() == 1 ? results.get(0) : null;
	}

	public void insertTimeException(TimeException te) {
		String query = "INSERT INTO `icm`.`timeexception` (`phaseId`, `from`, `to`) VALUES (?, ?, ?);";

		IPreparedStatement prepS = ps -> {
			try {
				ps.setLong(1, te.getPhaseId());
				ps.setTimestamp(2, te.getFrom());
				ps.setTimestamp(3, te.getTo());

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executePreparedStatement(query, prepS);

	}

	public ArrayList<Long> phasesForTimeException() {
		String query = "SELECT p.phaseID FROM icm.phase as p\r\n"
				+ "where timediff(now(), p.deadline) > 0 and p.deadline != '1999-01-01 04:30:00'\r\n"
				+ "and p.phaseID not in (SELECT ph.phaseID FROM icm.phase as ph\r\n"
				+ "inner join icm.timeexception as te on ph.phaseID = te.phaseID);";

		ArrayList<Long> results = new ArrayList<Long>();
		IStatement prepS = rs -> {
			try {

				while (rs.next()) {

					results.add(rs.getLong(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results;

	}

	public boolean phaseHasTimeException(long phaseId) {
		String query = "SELECT COUNT(*) FROM icm.timeexception as t "
				+ "inner join icm.phase as p on t.phaseId = p.phaseId where p.phaseId = '" + phaseId
				+ "' and t.to = '1999-01-01 04:30:00';";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(10);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1;
	}

	public void setTimeExceptionTimeOfCompletion(long phaseId, Timestamp ts) {
		String query = "UPDATE `icm`.`timeexception` SET `to` = ? WHERE (`phaseId` = ?);";

		IPreparedStatement prepS = ps -> {
			try {
				ps.setTimestamp(1, ts);
				ps.setLong(2, phaseId);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executePreparedStatement(query, prepS);

	}

	public ArrayList<String> getMaintainanceManagers() {
		String query = "SELECT m.Department, u.firstName, u.lastName, m.empNum FROM icm.maintainancemanagers as m\r\n"
				+ "inner join icm.employee as e on e.empNumber = m.empNum\r\n"
				+ "inner join icm.systemuser as u on e.username = u.username " + "order by m.Department;";

		ArrayList<String> results = new ArrayList<String>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {
					results.add(rs.getString(1));
					results.add(rs.getString(2) + " " + rs.getString(3) + " (" + rs.getString(4) + ")");

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results;
	}

	public void updateDepartmentManager(String department, long empNum090989) {
		String query = "UPDATE `icm`.`maintainancemanagers` SET `empNum` = ? WHERE (`Department` = ?);";

		IPreparedStatement prepS = ps -> {
			try {

				ps.setLong(1, empNum090989);
				ps.setString(2, department);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executePreparedStatement(query, prepS);
	}

	public ArrayList<SystemUser> getAllUsers() {
		String query = "SELECT *  FROM icm.systemUser as u where u.username != 'system';";

		ArrayList<SystemUser> results = new ArrayList<SystemUser>();
		IStatement prepS = rs -> {
			try {

				while (rs.next()) {

					SystemUser systemUser = new SystemUser(rs.getString(1), rs.getString(2), rs.getString(3),
							rs.getString(4), rs.getString(5), rs.getString(6), rs.getBoolean(7));
					results.add(systemUser);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results;
	}

	public void updateSupervisor(long oldSupEmpNum, long newSupEmpNum) {
		String query = "UPDATE `icm`.`supervisor` SET `empNumber` = ? WHERE (`empNumber` = ?);";

		IPreparedStatement prepS = ps -> {
			try {

				ps.setLong(1, newSupEmpNum);
				ps.setLong(2, oldSupEmpNum);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executePreparedStatement(query, prepS);

	}

	public void updateCommitteeMember(long oldComMemEmpNum, long newComMemEmpNum) {
		String query = "UPDATE `icm`.`executionchangescommitteemember` SET `empNumber` = ? WHERE (`empNumber` = ?);";

		IPreparedStatement prepS = ps -> {
			try {

				ps.setLong(1, newComMemEmpNum);
				ps.setLong(2, oldComMemEmpNum);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		executePreparedStatement(query, prepS);

	}

	public boolean isLoggedIn(String username242) {
		String query = "SELECT COUNT(*) FROM icm.systemuser as s where s.userName = '" + username242
				+ "' AND s.isOnline = '1';";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getInt(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results.size() == 1 ? results.get(0) == 1 : false;
	}

	public boolean logIn(String usernameToLogIn) {
		String query = "UPDATE `icm`.`systemuser` SET `isOnline` = '1' WHERE (`userName` = ?);";

		IPreparedStatement prepS = ps -> {
			try {

				ps.setString(1, usernameToLogIn);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		return executePreparedStatement(query, prepS) == 1;
	}

	public boolean logOut(String usernameToLogOut) {
		String query = "UPDATE `icm`.`systemuser` SET `isOnline` = '0' WHERE (`userName` = ?);";

		IPreparedStatement prepS = ps -> {
			try {

				ps.setString(1, usernameToLogOut);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		return executePreparedStatement(query, prepS) == 1;
	}

	public int UpdateShortcuts(String username, String shortcutsbtn, String shortcutsname) {

		String query = "UPDATE `icm`.`shortcuts` SET `shortcutsbtn`= ? "
				+ "  WHERE (`shortcutsname` = ? AND `userName` = ?);";
		IPreparedStatement prepS = ps -> {
			try {

				ps.setString(1, shortcutsbtn);
				ps.setString(2, shortcutsname);
				ps.setString(3, username);

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		int res = executePreparedStatement(query, prepS);

		if (res == 0) {
			System.out.println(shortcutsbtn + " : " + shortcutsname);
		}

		return res;

	}

	public void insertSupervisorDeadlineUpdate(SupervisorDeadlineUpdate sdu) {
		String query = "INSERT INTO `icm`.`supervisordeadlineupdate` (`phaseId`, `superEmpNum`, `dateOfChange`, `oldDeadline`, `newDeadline`) VALUES (?, ?, ?, ?, ?);";
		IPreparedStatement prepS = ps -> {
			try {

				ps.setLong(1, sdu.getPhaseId());
				ps.setLong(2, sdu.getSuperEmpNum());

				ps.setTimestamp(3, sdu.getDateOfChange());
				ps.setTimestamp(4, sdu.getOldDeadline());
				ps.setTimestamp(5, sdu.getNewDeadline());

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};

		executePreparedStatement(query, prepS);
	}

	public ArrayList<SupervisorDeadlineUpdate> getSupervisorDeadlineUpdate() {
		String query = "SELECT * FROM icm.supervisordeadlineupdate;";

		ArrayList<SupervisorDeadlineUpdate> results = new ArrayList<SupervisorDeadlineUpdate>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {
					SupervisorDeadlineUpdate sdu = new SupervisorDeadlineUpdate(rs.getLong(1), rs.getLong(2),
							rs.getLong(3), rs.getTimestamp(4), rs.getTimestamp(5), rs.getTimestamp(6));

					results.add(sdu);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);
		return results;
	}

	public int getNewAddr() {
		String query = "SELECT MAX(a.addr) + 1 FROM icm.array as a;";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getInt(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results.get(0);
	}

	public int newReportId() {
		String query = "SELECT MAX(t.id) + 1 FROM icm.activityreport as t;";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {
					results.add(rs.getInt(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results.size() == 1 ? results.get(0) : 1;
	}

	public int insertArrayList(ArrayList<Integer> arr) {

		String str;

		int addr = getNewAddr();

		int d = 0;
		for (int i = 0; i < arr.size(); i++) {

			IPreparedStatement prepS = ps -> {

			};

			d = arr.get(i);
			str = "INSERT INTO `icm`.`array` (`index`, `addr`, `data`) VALUES ('" + i + "', '" + addr + "', '" + d
					+ "');";

			executePreparedStatement(str, prepS);

		}

		return addr;

	}

	public ArrayList<Integer> getArrayList(int addr) {

		String query = "SELECT a.data FROM icm.array as a\r\n" + "where a.addr = '" + addr + "'\r\n"
				+ "order by a.index;";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {
					results.add(rs.getInt(1));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results;

	}

	public void insertActivityReport(ActivityReport ar) {

		String query = "INSERT INTO `icm`.`activityreport` (`name`, `date`, `active`, `frozen`, `closed`, `rejected`, `numOfWorkDays`,"
				+ " `totalActive`, `totalFrozen`, `totalClosed`, `totalRejected`, `totalNumOfWorkDays`) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

		int activeAddr = insertArrayList(ar.getActive());
		int frozenAddr = insertArrayList(ar.getFrozen());
		int closedAddr = insertArrayList(ar.getClosed());
		int rejectedAddr = insertArrayList(ar.getRejected());
		int numOfWorkDaysAddr = insertArrayList(ar.getNumOfWorkDays());

		IPreparedStatement prepS = ps -> {

			try {
				ps.setString(1, ar.getName());
				ps.setTimestamp(2, ar.getDate());

				ps.setInt(3, activeAddr);
				ps.setInt(4, frozenAddr);
				ps.setInt(5, closedAddr);
				ps.setInt(6, rejectedAddr);
				ps.setInt(7, numOfWorkDaysAddr);

				ps.setInt(8, ar.getTotalActive());
				ps.setInt(9, ar.getTotalFrozen());
				ps.setInt(10, ar.getTotalClosed());
				ps.setInt(11, ar.getTotalRejected());
				ps.setInt(12, ar.getTotalNumOfWorkDays());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		};

		executePreparedStatement(query, prepS);
	}

	public int countOfActiveReqests(Timestamp dFrom, Timestamp dTo) {

		String query = "SELECT c.startDateOfRequest, c.endDateOfRequest FROM icm.changerequest as c;"; // todo

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {
				int sum = 0;
				while (rs.next()) {
					Timestamp a = rs.getTimestamp(1);
					Timestamp b = rs.getTimestamp(2);
					sum += SQLUtil.isActiveInInterval(dFrom, dTo, a, b);
				}

				results.add(sum);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results.get(0);

	}

	public int countOfFreezeReqests(Timestamp dFrom, Timestamp dTo) {
		String from = SQLUtil.toString(dFrom);
		String to = SQLUtil.toString(dTo);

		String query = "SELECT COUNT(*) FROM icm.freeze as f where f.initDate > '" + from + "' and f.initDate < '" + to
				+ "';";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {
				if (rs.next()) {
					results.add(rs.getInt(1));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results.size() > 0 ? results.get(0) : 0;
	}

	public int countOfClosedRequests(Timestamp dFrom, Timestamp dTo) {

		String from = SQLUtil.toString(dFrom);
		String to = SQLUtil.toString(dTo);

		String query = "SELECT COUNT(*) FROM icm.phase as p\r\n"
				+ "where p.status = 'Closed' and p.timeOfCompletion > '" + from + "' and p.timeOfCompletion < '" + to
				+ "';";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {
				if (rs.next()) {
					results.add(rs.getInt(1));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results.size() > 0 ? results.get(0) : 0;

	}

	public int countOfDeniedRequests(Timestamp dFrom, Timestamp dTo) {
		String from = SQLUtil.toString(dFrom);
		String to = SQLUtil.toString(dTo);

		String query = "SELECT COUNT(*) FROM icm.phase as p\r\n"
				+ "where p.status = 'Rejected' and p.timeOfCompletion > '" + from + "' and p.timeOfCompletion < '" + to
				+ "';";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {
				if (rs.next()) {
					results.add(rs.getInt(1));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results.size() > 0 ? results.get(0) : 0;
	}

	public int countOfTotalWorkingDays(Timestamp dFrom, Timestamp dTo) {

		String query = "SELECT p.startingDate, p.timeOfCompletion FROM icm.phase as p;";

		ArrayList<Integer> results = new ArrayList<Integer>();

		IStatement prepS = rs -> {
			try {
				int sum = 0;
				while (rs.next()) {
					Timestamp a = rs.getTimestamp(1);
					Timestamp b = rs.getTimestamp(2);
					sum += SQLUtil.getNumOfDaysInInterval(dFrom, dTo, a, b);
				}

				results.add(sum);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results.get(0);
	}

	public ArrayList<String> getShortcuts(String username) {
		String query = "SELECT shortcutsbtn , shortcutsname FROM icm.shortcuts where username = '" + username
				+ "' order by shortcutsname;";

		ArrayList<String> results = new ArrayList<String>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {

					results.add(rs.getString(1));
					results.add(rs.getString(2));

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		System.out.println(results);

		return results;

	}

	public ActivityReport getActivityReport(int id, Timestamp dFrom, Timestamp dTo) {

		ArrayList<Integer> activeCnt = new ArrayList<Integer>();
		ArrayList<Integer> freezeCnt = new ArrayList<Integer>();
		ArrayList<Integer> closedCnt = new ArrayList<Integer>();
		ArrayList<Integer> rejectedCnt = new ArrayList<Integer>();
		ArrayList<Integer> workingDaysCnt = new ArrayList<Integer>();

		int diff = SQLUtil.diff(dTo, dFrom);

		int interval = 10;
		if (diff % interval == 0) {
			interval = diff / interval;
		} else
			interval = diff / interval + 1;

		for (Timestamp i = dFrom; !i.equals(dTo);) {

			Timestamp to = DateUtil.add(i, interval - 1, 0);

			if (!to.after(dTo)) {

				activeCnt.add(countOfActiveReqests(i, to));
				freezeCnt.add(countOfFreezeReqests(i, to));
				closedCnt.add(countOfClosedRequests(i, to));
				rejectedCnt.add(countOfDeniedRequests(i, to));
				workingDaysCnt.add(countOfTotalWorkingDays(i, to));

				i = DateUtil.add(to, 1, 0);

			} else {

				activeCnt.add(countOfActiveReqests(i, dTo));
				freezeCnt.add(countOfFreezeReqests(i, dTo));
				closedCnt.add(countOfClosedRequests(i, dTo));
				rejectedCnt.add(countOfDeniedRequests(i, dTo));
				workingDaysCnt.add(countOfTotalWorkingDays(i, dTo));

				i = dTo;
			}

		}

		int totalActiveCnt = countOfActiveReqests(dFrom, dTo);
		int totalFreezeCnt = countOfFreezeReqests(dFrom, dTo);
		int totalClosedCnt = countOfClosedRequests(dFrom, dTo);
		int totalRejectedCnt = countOfDeniedRequests(dFrom, dTo);
		int totalWorkingCnt = countOfTotalWorkingDays(dFrom, dTo);

		ActivityReport ac = new ActivityReport(id, "ActivityReport" + id, DateUtil.NA, activeCnt, freezeCnt, closedCnt,
				rejectedCnt, workingDaysCnt, totalActiveCnt, totalFreezeCnt, totalClosedCnt, totalRejectedCnt,
				totalWorkingCnt);

		return ac;

	}

	public ActivityReport getActivityReportById(int repId) {
		String query = "SELECT * FROM icm.activityreport as a where a.id = '" + repId + "';";

		ArrayList<ActivityReport> results = new ArrayList<ActivityReport>();

		IStatement prepS = rs -> {
			try {

				if (rs.next()) {

					long id = rs.getLong(1);
					String name = rs.getString(2);
					Timestamp date = rs.getTimestamp(3);
					ArrayList<Integer> active = getArrayList(rs.getInt(4));
					ArrayList<Integer> frozen = getArrayList(rs.getInt(5));
					ArrayList<Integer> closed = getArrayList(rs.getInt(6));
					ArrayList<Integer> rejected = getArrayList(rs.getInt(7));
					ArrayList<Integer> numOfWorkDays = getArrayList(rs.getInt(8));
					int totalActive = rs.getInt(9);
					int totalFrozen = rs.getInt(10);
					int totalClosed = rs.getInt(11);
					int totalRejected = rs.getInt(12);
					int totalNumOfWorkDays = rs.getInt(13);
					ActivityReport ac = new ActivityReport(id, name, date, active, frozen, closed, rejected,
							numOfWorkDays, totalActive, totalFrozen, totalClosed, totalRejected, totalNumOfWorkDays);

					results.add(ac);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results.size() == 1 ? results.get(0) : null;
	}

	public ArrayList<ArrayList<String>> getSimpleReportsData() {
		String query = "SELECT a.id, a.date, a.name FROM icm.activityreport as a;";

		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();

		IStatement prepS = rs -> {
			try {

				while (rs.next()) {

					long id = rs.getLong(1);
					Timestamp date = rs.getTimestamp(2);
					String name = rs.getString(3);

					ArrayList<String> res = new ArrayList<String>();
					res.add(id + "");
					res.add(DateUtil.toString(date));
					res.add(name);
					
					results.add(res);
					
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		};
		executeStatement(query, prepS);

		return results;
	}
}
