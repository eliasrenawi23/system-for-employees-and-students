package ServerLogic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Controllers.ClientGUI;
import Entities.ActivityReport;
import Entities.ChangeRequest;
import Entities.Employee;
import Entities.EvaluationReport;
import Entities.ExecutionReport;
import Entities.File;
import Entities.Message;
import Entities.SqlObject;
import Entities.SupervisorDeadlineUpdate;
import Entities.SystemUser;
import Entities.TimeException;
import Entities.Phase;
import Entities.PhaseStatus;
import Entities.PhaseTimeExtensionRequest;
import Entities.PhaseType;
import Protocol.Command;
import Protocol.MsgReturnType;
import Protocol.SRMessage;
import ServerLogic.UtilityInterfaces.ClientFunc;
import ServerLogic.UtilityInterfaces.ClientThrowableFunc;
import ServerLogic.UtilityInterfaces.ObjectClientFunc;
import ServerLogic.UtilityInterfaces.ThrowableFunc;
import Utility.DateUtil;
import Utility.VoidFunc;
import javafx.application.Platform;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

/**
 * This class extends the AbstractServer class and provides
 * The class contains a lists of events that can be registered to, for events such as server closed, server stopped, server started events.
 * This class is also a Singleton, and can not be initialized externally.
 * */
public class Server extends AbstractServer {

	private static final int SYSTEM_EMPLOYEE_NUMBER = -1;

	// TODO: change this to be dynamic and not constant
	private static int DEFAULT_EVALUATOR_EMP_NUMBER = 10;
	private static int DEFAULT_EXECUTER_EMP_NUMBER = 10;

	private boolean sqlException;
	public static final int DEFAULT_PORT = 5555;
	private static Server instance;
	private InetAddress inetAddress;
	private static ArrayList<ObjectClientFunc> objectRecievedFromClientsEvents;
	private static ArrayList<ClientFunc> clientConnectedEvents;
	private static ArrayList<ClientThrowableFunc> clientExceptionEvents;
	private static ArrayList<ClientFunc> clientDisconnectedEvents;
	private static ArrayList<ThrowableFunc> serverExceptionEvents;
	private static ArrayList<VoidFunc> serverClosedEvents;
	private static ArrayList<VoidFunc> serverStartedEvents;
	private static ArrayList<VoidFunc> serverStoppedEvents;
	private static MySQL db;
	// private static ExecutorService executorService;
	static {

		instance = new Server(5555);
		objectRecievedFromClientsEvents = new ArrayList<ObjectClientFunc>();
		clientConnectedEvents = new ArrayList<ClientFunc>();
		clientExceptionEvents = new ArrayList<ClientThrowableFunc>();
		clientDisconnectedEvents = new ArrayList<ClientFunc>();
		serverExceptionEvents = new ArrayList<ThrowableFunc>();
		serverClosedEvents = new ArrayList<VoidFunc>();
		serverStartedEvents = new ArrayList<VoidFunc>();
		serverStoppedEvents = new ArrayList<VoidFunc>();

		ServerConfigurations.InjectEvents();

	}

	public static Server getInstance() {
		return instance;
	}

	// TODO: is this valid or should just throw an exception?
	public MySQL getDB() {
		if (db == null)
			System.err.println("Database has not been initialized, please initialize!");
		return db;
	}

	private Server(int port) {
		super(port);
		try {
			inetAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {

			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		stopCheckingForTimeExceptions();
		executorService.shutdownNow();
		super.finalize();
	}

	// Initialize the client
	public void initialize(int port, String username, String password, String schemaName, int poolSize) {

		// executorService = Executors.newFixedThreadPool(poolSize);

		sqlException = false;
		instance.setPort(port);

		try {
			instance.listen();
		} catch (IOException e) {

			e.printStackTrace();
		}

		VoidFunc f = () -> {

			try {
				Server.getInstance().close();
				sqlException = true;
			} catch (IOException e) {

				e.printStackTrace();
			}
		};
		db = new MySQL(username, password, schemaName, f);
	}

	private static ExecutorService executorService = Executors.newSingleThreadExecutor();
	private static boolean stop;

	public static void stopCheckingForTimeExceptions() {
		stop = false;
	}

	public static void checkForTimeExceptions() {
		stop = true;
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				while (stop) {

					try {

						ArrayList<Long> phaseIds = db.phasesForTimeException();

						for (Long id : phaseIds) {
							TimeException te = new TimeException(-1, id, DateUtil.now(), DateUtil.NA);
							db.insertTimeException(te);

							String phaseOwner2 = db.getUsernameOfEmployee(id);

							String title = "Time exception";
							String contnet = "Time exception on phase [" + id + "]";

							getInstance().sendUserMessage(title, phaseOwner2, contnet, -1, -1);

							getInstance().sendUserMessage(title, db.getUsernameOfSupervisor(), contnet, -1, -1);

							getInstance().sendUserMessage(title, db.getUsernameOfManager(), contnet, -1, -1);

						}

						Thread.sleep(1000);
					} catch (InterruptedException e) {

					}
				}
			}
		});

	}

	private void timeExceptionValidation(long phaseId) {
		if (db.phaseHasTimeException(phaseId)) {
			db.setTimeExceptionTimeOfCompletion(phaseId, DateUtil.now());
		}
	}

	public boolean isSqlException() {
		return sqlException;
	}

	private Integer result;

	@SuppressWarnings("unchecked")
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		SRMessage srMsg = (SRMessage) msg;
		System.out.println("Message Received from client: " + srMsg.getCommand().toString());
		// Execute in a separate thread

		Command command = srMsg.getCommand();
		switch (command) {

		
		case getMyShortcuts:
			
			String username35235 = (String)srMsg.getAttachedData()[0];
			
			ArrayList<String> shortcuts = db.getShortcuts(username35235);
			
			
			sendMessageToClient(client, command, shortcuts);

			
			
			
			break;
			
			
		case getReportsSimpleData:
			
			
			ArrayList<ArrayList<String>> repsData = db.getSimpleReportsData();

			sendMessageToClient(client, command, repsData);
			
			break;
		case getActivityReportById:
			
			int repId = (int) srMsg.getAttachedData()[0];

			ActivityReport ac2 = db.getActivityReportById(repId);

			sendMessageToClient(client, command, ac2);

			
			break;
			
		case saveActivityReport:
			ActivityReport actReport = (ActivityReport) srMsg.getAttachedData()[0];

			actReport.setDate(DateUtil.now());
			
			db.insertActivityReport(actReport);
			
			sendMessageToClient(client, command, actReport);

			
			
			break;
		case getActivityReport:

			LocalDate dFrom = (LocalDate) srMsg.getAttachedData()[0];
			LocalDate dTo = (LocalDate) srMsg.getAttachedData()[1];

			ActivityReport ac = db.getActivityReport(-1, DateUtil.get(dFrom), DateUtil.get(dTo));

			sendMessageToClient(client, command, ac);

			break;

		case getSupervisorDeadlineUpdate:

			ArrayList<SupervisorDeadlineUpdate> sdus = db.getSupervisorDeadlineUpdate();

			sendMessageToClient(client, command, sdus);

			break;

		case updateDeadline:

			Phase p48 = (Phase) srMsg.getAttachedData()[0];

			Timestamp oldDeadline = (Timestamp) srMsg.getAttachedData()[1];

			sendUserMessage("Deadline update", db.getUsernameOfManager(),
					"Deadline changed from: " + DateUtil.toString(oldDeadline) + " to: "
							+ DateUtil.toString(p48.getDeadline()) + " by the supervisor",
					-1, -1);

			db.updateByObject(p48);

			SupervisorDeadlineUpdate sdu = new SupervisorDeadlineUpdate(-1, p48.getPhaseID(), db.getSupervisorEmpNum(),
					DateUtil.now(), oldDeadline, p48.getDeadline());

			db.insertSupervisorDeadlineUpdate(sdu);

			sendMessageToClient(client, command);

			break;

		case UpdateShortcuts:
			int count = 0;
			String IssReqShortcut = (String) srMsg.getAttachedData()[0];
			String IssReqShortcutbtn = (String) srMsg.getAttachedData()[1];

			String ONotifShortcut = (String) srMsg.getAttachedData()[2];
			String ONotifShortcutbtn = (String) srMsg.getAttachedData()[3];

			String OMessShortcut = (String) srMsg.getAttachedData()[4];
			String OMessShortcutbtn = (String) srMsg.getAttachedData()[5];

			String OMyReqShortcut = (String) srMsg.getAttachedData()[6];
			String OMyReqShortcutbtn = (String) srMsg.getAttachedData()[7];

			String SignOutShortcut = (String) srMsg.getAttachedData()[8];
			String SignOutShortcutbtn = (String) srMsg.getAttachedData()[9];

			String OpenEmpShortcut = (String) srMsg.getAttachedData()[10];
			String OpenEmpShortcutbtn = (String) srMsg.getAttachedData()[11];

			String OpenAnalyticsShortcut = (String) srMsg.getAttachedData()[12];
			String OpenAnalyticsShortcutbtn = (String) srMsg.getAttachedData()[13];

			String OpReqTreatShortcut = (String) srMsg.getAttachedData()[14];
			String OpReqTreatShortcutbtn = (String) srMsg.getAttachedData()[15];

			String gobackcomb = (String) srMsg.getAttachedData()[16];
			String gobackcombbtn = (String) srMsg.getAttachedData()[17];

			String username = (String) srMsg.getAttachedData()[18];

			count += db.UpdateShortcuts(username, IssReqShortcutbtn, IssReqShortcut);
			count += db.UpdateShortcuts(username, ONotifShortcutbtn, ONotifShortcut);
			count += db.UpdateShortcuts(username, OMessShortcutbtn, OMessShortcut);
			count += db.UpdateShortcuts(username, OMyReqShortcutbtn, OMyReqShortcut);
			
			count += db.UpdateShortcuts(username, SignOutShortcutbtn, SignOutShortcut);
			count += db.UpdateShortcuts(username, OpenEmpShortcutbtn, OpenEmpShortcut);
			count += db.UpdateShortcuts(username, OpenAnalyticsShortcutbtn, OpenAnalyticsShortcut);
			count += db.UpdateShortcuts(username, OpReqTreatShortcutbtn, OpReqTreatShortcut);
			
			count += db.UpdateShortcuts(username, gobackcombbtn, gobackcomb);

			sendMessageToClient(client, command, count);

			break;

		case logOut:

			String usernameToLogOut = (String) srMsg.getAttachedData()[0];

			boolean logOutSucces = db.logOut(usernameToLogOut);

			sendMessageToClient(client, command, logOutSucces);

			break;

		case getCommitteeDetails:

			ArrayList<Employee> comMems = new ArrayList<Employee>(3);

			ArrayList<Long> regComs = db.getComsEmpNumsWithoutManager();

			comMems.add(db.getEmployeeByEmpNumber(regComs.get(0)));
			comMems.add(db.getEmployeeByEmpNumber(db.getComHeadEmpNum()));
			comMems.add(db.getEmployeeByEmpNumber(regComs.get(1)));

			sendMessageToClient(client, command, comMems);

			break;

		case updateComMember:

			long oldComMemEmpNum = (long) srMsg.getAttachedData()[0];
			long newComMemEmpNum = (long) srMsg.getAttachedData()[1];

			boolean isComMem = db.isEmployeeComMember(newComMemEmpNum);

			if (!isComMem) {
				db.updateCommitteeMember(oldComMemEmpNum, newComMemEmpNum);
			}

			sendUserMessage("Role change", db.getUsernameByEmpNumber(oldComMemEmpNum),
					"You are not a committee member anymore.", -1, -1);
			sendUserMessage("Role change", db.getUsernameByEmpNumber(newComMemEmpNum),
					"You have been assigned as a committee member!", -1, -1);

			sendMessageToClient(client, command, isComMem);

			break;

		case updateSupervisor:
			long supEmpNum = (long) srMsg.getAttachedData()[0];

			sendUserMessage("Role change", db.getUsernameOfSupervisor(), "You are not the supervisor anymore.", -1, -1);
			sendUserMessage("Role change", db.getUsernameByEmpNumber(supEmpNum),
					"You have been assigned as the supervisor!", -1, -1);

			db.updateSupervisor(db.getSupervisorEmpNum(), supEmpNum);

			sendMessageToClient(client, command);

			break;

		case getSuperviosrDetails:

			Employee supEmp = db.getEmployeeByEmpNumber(db.getSupervisorEmpNum());

			sendMessageToClient(client, command, supEmp);

			break;

		case getAllUsers:

			ArrayList<SystemUser> users = db.getAllUsers();

			sendMessageToClient(client, command, users);

			break;

		case updateDepartmentManager:

			String department = (String) srMsg.getAttachedData()[0];
			long empNum090989 = (long) srMsg.getAttachedData()[1];

			db.updateDepartmentManager(department, empNum090989);

			sendUserMessage("New role: Department " + department + " maintenance manager",
					db.getUsernameByEmpNumber(empNum090989),
					"You have been assigned as the department maintenance manager of " + department, -1, -1);

			sendMessageToClient(client, command);

			break;

		case getDepartmentsManagers:

			ArrayList<String> depManagers = db.getMaintainanceManagers();
			sendMessageToClient(client, command, depManagers);

			break;

		case checkLogIn:
			String username242 = (String) srMsg.getAttachedData()[0];
			String password = (String) srMsg.getAttachedData()[1];

			SystemUser sysUser452 = null;

			boolean canLogIn = db.canLogIn(username242, password);
			boolean isAlreadyLoggedIn = false;
			if (canLogIn) {

				isAlreadyLoggedIn = db.isLoggedIn(username242);
				if (!isAlreadyLoggedIn) {
					db.logIn(username242);
					sysUser452 = db.getSystemUserByUsername(username242);

				}
			}

			sendMessageToClient(client, command, canLogIn, isAlreadyLoggedIn, sysUser452);

			break;

		case unfreezePhase:

			Phase p17 = (Phase) srMsg.getAttachedData()[0];
			db.updatePhaseStatus(p17.getPhaseID(), db.getPreviousStatusBeforeFreeze(p17.getPhaseID()));
			db.setLatestFreezeEndDate(p17.getPhaseID(), DateUtil.now());

			String phaseOwner2 = db.getUsernameOfEmployee(p17.getPhaseID());

			String title = "The request [" + p17.getRequestID() + "] has been unsuspended";
			String contnet = "The requests [" + p17.getRequestID() + "] has been unsuspended!";

			sendUserMessage(title, phaseOwner2, contnet, -1, -1);

			sendUserMessage(title, db.getUsernameOfSupervisor(), contnet, -1, -1);

			sendUserMessage(title, db.getUsernameOfManager(), contnet, -1, -1);

			notifyEmployeeTreatmentRequestsUpdated(p17.getEmpNumber());

			sendMessageToClient(client, command);

			break;

		case freezePhase:
			Phase p16 = (Phase) srMsg.getAttachedData()[0];

			db.insertFreeze(p16.getPhaseID(), p16.getStatus(), DateUtil.now(), DateUtil.NA);

			db.updatePhaseStatus(p16.getPhaseID(), PhaseStatus.Frozed);

			String phaseOwner = db.getUsernameOfEmployee(p16.getPhaseID());

			sendUserMessage("The request [" + p16.getRequestID() + "] has been suspended", phaseOwner,
					"The requests [" + p16.getRequestID() + "] has been suspended and no changes can be made to it!",
					-1, -1);

			sendUserMessage("The request [" + p16.getRequestID() + "] has been suspended", db.getUsernameOfSupervisor(),
					"The requests [" + p16.getRequestID() + "] has been suspended and no changes can be made to it!",
					-1, -1);

			sendUserMessage("The request [" + p16.getRequestID() + "] has been suspended", db.getUsernameOfManager(),
					"The requests [" + p16.getRequestID() + "] has been suspended and no changes can be made to it!",
					-1, -1);

			notifyEmployeeTreatmentRequestsUpdated(p16.getEmpNumber());

			sendMessageToClient(client, command);

			break;

		case requestOwnerConfirm:
			long reqId12 = (long) srMsg.getAttachedData()[0];
			Phase p12 = db.getClosingPhase(reqId12);

			PhaseStatus phaseStat12 = PhaseStatus.valueOfAdvanced(p12.getStatus());
			if (phaseStat12 == PhaseStatus.Waiting_For_Owner_Confirmation) {
				db.updatePhaseStatus(p12.getPhaseID(), PhaseStatus.Closed);
				db.updatePhaseTimeOfCompletion(p12.getPhaseID(), DateUtil.now());

				String reqOwner = db.getRequestOwnerUsername(p12.getRequestID());

				// Notify the supervisor and the owner about ending the request successfully.
				sendUserMessage("The request [" + p12.getRequestID() + "] has ended", db.getUsernameOfSupervisor(),
						"The requests [" + p12.getRequestID() + "] has ended successfully and comfirmed by the user.",
						-1, -1);

				sendUserMessage("The request [" + p12.getRequestID() + "] has ended", reqOwner,
						"The requests [" + p12.getRequestID() + "] has ended successfully.", -1, -1);

				db.setCompletionTimeOfRequestToNow(p12.getRequestID());

			} else {
				db.updatePhaseStatus(p12.getPhaseID(), PhaseStatus.Waiting_For_Supervisor_Confirmation);
			}

			sendMessageToClient(client, command);

			break;

		case requestOwnerDecline:
			long reqId11 = (long) srMsg.getAttachedData()[0];
			Phase p11 = db.getClosingPhase(reqId11);

			PhaseStatus phaseStat11 = PhaseStatus.valueOfAdvanced(p11.getStatus());
			if (phaseStat11 == PhaseStatus.Waiting_For_Owner_Confirmation) {
				db.updatePhaseStatus(p11.getPhaseID(), PhaseStatus.Closed);
				db.updatePhaseTimeOfCompletion(p11.getPhaseID(), DateUtil.now());

				String reqOwner = db.getRequestOwnerUsername(p11.getRequestID());

				// Notify the supervisor and the owner about ending the request successfully.
				sendUserMessage("The request [" + p11.getRequestID() + "] has ended", db.getUsernameOfSupervisor(),
						"The requests [" + p11.getRequestID() + "] has ended without success!", -1, -1);

				sendUserMessage("The request [" + p11.getRequestID() + "] has ended", reqOwner,
						"The requests [" + p11.getRequestID() + "] has ended without success!", -1, -1);

				db.setCompletionTimeOfRequestToNow(p11.getRequestID());
			} else {
				db.updatePhaseStatus(p11.getPhaseID(), PhaseStatus.Waiting_For_Supervisor_Confirmation);
			}

			sendMessageToClient(client, command);

			break;

		case declinedRequestEndedBySupervisor:

			Phase ph10 = (Phase) srMsg.getAttachedData()[0];

			PhaseStatus phaseStat10 = PhaseStatus.valueOfAdvanced(ph10.getStatus());
			if (phaseStat10 == PhaseStatus.Waiting_For_Supervisor_Confirmation) {
				db.updatePhaseStatus(ph10.getPhaseID(), PhaseStatus.Closed);
				db.updatePhaseTimeOfCompletion(ph10.getPhaseID(), DateUtil.now());

				String reqOwner = db.getRequestOwnerUsername(ph10.getRequestID());

				// Notify the supervisor and the owner about ending the request successfully.
				sendUserMessage("The request [" + ph10.getRequestID() + "] has ended", db.getUsernameOfSupervisor(),
						"The requests [" + ph10.getRequestID() + "] has ended without success!", -1, -1);

				sendUserMessage("The request [" + ph10.getRequestID() + "] has ended", reqOwner,
						"The requests [" + ph10.getRequestID() + "] has ended without success!", -1, -1);
				db.setCompletionTimeOfRequestToNow(ph10.getRequestID());

			} else {
				db.updatePhaseStatus(ph10.getPhaseID(), PhaseStatus.Waiting_For_Owner_Confirmation);
			}

			sendMessageToClient(client, command);

			break;

		case confirmRequestEndedBySupervisor:

			Phase ph9 = (Phase) srMsg.getAttachedData()[0];

			PhaseStatus phaseStat9 = PhaseStatus.valueOfAdvanced(ph9.getStatus());
			if (phaseStat9 == PhaseStatus.Waiting_For_Supervisor_Confirmation) {
				db.updatePhaseStatus(ph9.getPhaseID(), PhaseStatus.Closed);
				db.updatePhaseTimeOfCompletion(ph9.getPhaseID(), DateUtil.now());

				String reqOwner = db.getRequestOwnerUsername(ph9.getRequestID());

				// Notify the supervisor and the owner about ending the request successfully.
				sendUserMessage("The request [" + ph9.getRequestID() + "] has ended", db.getUsernameOfSupervisor(),
						"The requests [" + ph9.getRequestID() + "] has ended successfully and comfirmed by the user.",
						-1, -1);

				sendUserMessage("The request [" + ph9.getRequestID() + "] has ended", reqOwner,
						"The requests [" + ph9.getRequestID() + "] has ended successfully.", -1, -1);

				db.setCompletionTimeOfRequestToNow(ph9.getRequestID());

			} else {
				db.updatePhaseStatus(ph9.getPhaseID(), PhaseStatus.Waiting_For_Owner_Confirmation);
			}

			sendMessageToClient(client, command);

			break;

		case isMyRequestWaitingForMyConfirmation:

			boolean response = db.isRequestStatusEql(PhaseType.Closing,
					PhaseStatus.Waiting_For_Owner_And_Supervisor_Confirmation);

			if (response == false) {
				response = db.isRequestStatusEql(PhaseType.Closing, PhaseStatus.Waiting_For_Owner_Confirmation);
			}

			sendMessageToClient(client, command, response);

			break;

		case getRequestFiles:

			long reqId54648 = (long) srMsg.getAttachedData()[0];

			ArrayList<File> files24 = db.getFiles(reqId54648);

			sendMessageToClient(client, command, files24);

			break;
		case getExecutionReportForExaminationAndComsNames:

			long reqId0909 = (long) srMsg.getAttachedData()[0];

			ExecutionReport exeRep1 = db.getLatestExecutionReport(reqId0909);

			ArrayList<Object> res3123 = db.getRegularCommitteeMemebersNamesAndNumbers();

			String mem1un = (String) res3123.get(0);
			long mem1empNum = (long) res3123.get(1);
			String mem2un = (String) res3123.get(2);
			long mem2empNum = (long) res3123.get(3);

			sendMessageToClient(client, command, exeRep1, mem1un, mem1empNum, mem2un, mem2empNum);

			break;

		case assignExaminerForRequest:

			Phase ph4 = (Phase) srMsg.getAttachedData()[0];

			db.updateByObject(ph4);

			String username24 = db.getUsernameByEmpNumber(ph4.getEmpNumber());

			// Notify the supervisor about the start of the examination
			sendUserMessage("Examine Request", username24,
					"You have been assigned to examine the request[" + ph4.getRequestID() + "]", ph4.getRequestID(),
					ph4.getPhaseID());

			notifyEmployeeTreatmentRequestsUpdated(ph4.getEmpNumber());
			sendMessageToClient(client, command);

			break;

		case rejectExamination:
			Phase ph3 = (Phase) srMsg.getAttachedData()[0];

			db.updatePhaseStatus(ph3.getPhaseID(), PhaseStatus.Closed);
			db.updatePhaseTimeOfCompletion(ph3.getPhaseID(), DateUtil.now());

			// Notify the supervisor about the start of the examination
			sendUserMessage("The request [" + ph3.getRequestID() + "] execution has been rejected",
					db.getUsernameOfSupervisor(),
					"The requests [" + ph3.getRequestID() + "] execcution has been rejected by an examiner", -1, -1);
			initClosingPhase(ph3);

			timeExceptionValidation(ph3.getPhaseID());

			sendMessageToClient(client, command);

			break;

		case confirmExamination:

			Phase ph2 = (Phase) srMsg.getAttachedData()[0];

			db.updatePhaseStatus(ph2.getPhaseID(), PhaseStatus.Closed);
			db.updatePhaseTimeOfCompletion(ph2.getPhaseID(), DateUtil.now());

			// Notify the supervisor about the start of the examination
			sendUserMessage("The request [" + ph2.getRequestID() + "] execution has been confirmed",
					db.getUsernameOfSupervisor(),
					"The requests [" + ph2.getRequestID() + "] execcution has been confirmed by an examiner", -1, -1);
			initClosingPhase(ph2);

			timeExceptionValidation(ph2.getPhaseID());

			sendMessageToClient(client, command);

			break;

		case insertExecutionReport:

			ExecutionReport exeRep = (ExecutionReport) srMsg.getAttachedData()[0];
			long phaseId64 = (long) srMsg.getAttachedData()[1];

			exeRep.setReportID(db.getNewMaxID(ExecutionReport.getEmptyInstance()));

			db.insertObject(exeRep);
			db.updatePhaseStatus(phaseId64, PhaseStatus.Closed);
			db.updatePhaseTimeOfCompletion(phaseId64, DateUtil.now());
			timeExceptionValidation(phaseId64);

			long requestID533 = db.getRequestIdByPhaseId(phaseId64);
			// init examination phase

			long exaPhaseId = db.getNewMaxID(Phase.getEmptyInstance());
			Phase examinationPhase = new Phase(exaPhaseId, requestID533, PhaseType.Examination.name(),
					PhaseStatus.Active.name(), db.getComHeadEmpNum(), DateUtil.daysFromNow(7), DateUtil.daysFromNow(7),
					DateUtil.NA, DateUtil.now(), false);

			db.insertObject(examinationPhase);

			// send message to com head
			sendUserMessage("A request is waiting for examination", db.getUsernameOfComHead(),
					"The requests [" + requestID533 + "] is waiting for an examination. you have 7 days to examine it!",
					requestID533, exaPhaseId);

			// Notify the com member for a new treatment requests update
			notifyEmployeeTreatmentRequestsUpdated(db.getComHeadEmpNum());

			// Notify the supervisor about the start of the examination
			sendUserMessage("The request [" + requestID533 + "] is in examination", db.getUsernameOfSupervisor(),
					"The requests [" + requestID533 + "] has finished execution and is now in examination phase",
					requestID533, exaPhaseId);

			sendMessageToClient(client, command);

			break;

		case getLatestEvalReport:

			long requestId6663 = (long) srMsg.getAttachedData()[0];

			EvaluationReport evalR3 = db.getLatestEvaluationReport(requestId6663);

			sendMessageToClient(client, command, evalR3);

			break;

		case requestMoreDateForDecision:

			Phase p3 = (Phase) srMsg.getAttachedData()[0];

			String reason = (String) srMsg.getAttachedData()[1];

			// update phase status to Waiting_For_More_Data of decision phase
			db.updatePhaseStatus(p3.getPhaseID(), PhaseStatus.Closed);
			timeExceptionValidation(p3.getPhaseID());

			// send message to the committee members that more data has been requested
			ArrayList<String> comNames4 = db.getComsUsernames();

			for (String comName : comNames4) {
				sendUserMessage("More data requested", comName,
						"More data has been request for the the requests [" + p3.getRequestID() + "].");
			}

			long latestEvalEmpNum = db.getLatestEvaluatorEmpNumber(p3.getRequestID());
			String latestEvalUsername = db.getUsernameByEmpNumber(latestEvalEmpNum);

			// Send a message to the previous evaluator of the requested data
			sendUserMessage("More data requested", latestEvalUsername,
					"More data has been request for the the requests [" + p3.getRequestID() + "]." + "\nReason: "
							+ reason);

			// send a message to the supervisor of the requested data
			sendUserMessage("More data requested", db.getUsernameOfSupervisor(),
					"More data has been request for the the requests [" + p3.getRequestID() + "]." + "\nReason: "
							+ reason);

			// init evaluation phase
			initEvaluationPhase(p3.getRequestID());

			sendMessageToClient(client, command);

			break;

		case acceptDecisionPhase:

			Phase p2 = (Phase) srMsg.getAttachedData()[0];

			// update phase status to closed of decision phase
			db.updatePhaseStatus(p2.getPhaseID(), PhaseStatus.Closed);
			db.updatePhaseTimeOfCompletion(p2.getPhaseID(), DateUtil.now());
			timeExceptionValidation(p2.getPhaseID());

			// send message to the committee members that his request was accepted
			ArrayList<String> comNames3 = db.getComsUsernames();

			for (String comName : comNames3) {
				sendUserMessage("Request Accepted", comName,
						"The requests [" + p2.getRequestID() + "] has been declined!");
			}

			// init execution phase
			initExecutionPhase(p2);

			sendMessageToClient(client, command);

			break;

		case declineDecisionPhase:

			Phase p1 = (Phase) srMsg.getAttachedData()[0];

			// update phase status to closed of decision phase
			db.updatePhaseStatus(p1.getPhaseID(), PhaseStatus.Rejected);
			db.updatePhaseTimeOfCompletion(p1.getPhaseID(), DateUtil.now());
			timeExceptionValidation(p1.getPhaseID());

			// send message to the committee members that his request was declined
			ArrayList<String> comNames = db.getComsUsernames();

			for (String comName : comNames) {
				sendUserMessage("Request Declined", comName,
						"The requests [" + p1.getRequestID() + "] has been declined!");
			}

			// init closing phase
			initClosingPhaseRejecton(p1);

			sendMessageToClient(client, command);

			break;

		case insertEvaluationReport:

			EvaluationReport evaluationReport = (EvaluationReport) srMsg.getAttachedData()[0];
			long phaseId63 = (long) srMsg.getAttachedData()[1];

			evaluationReport.setReportID(db.getNewMaxID(EvaluationReport.getEmptyInstance()));
			db.insertObject(evaluationReport);

			db.updatePhaseStatus(phaseId63, PhaseStatus.Closed);
			db.updatePhaseTimeOfCompletion(phaseId63, DateUtil.now());
			timeExceptionValidation(phaseId63);

			long requestID53 = db.getRequestIdByPhaseId(phaseId63);
			// init decision phase
			long decPhaseId = db.getNewMaxID(Phase.getEmptyInstance());
			Phase decisionPhase = new Phase(decPhaseId, requestID53, PhaseType.Decision.name(),
					PhaseStatus.Active.name(), db.getComHeadEmpNum(), DateUtil.daysFromNow(7), DateUtil.daysFromNow(7),
					DateUtil.NA, DateUtil.now(), false);

			db.insertObject(decisionPhase);

			db.setRequestEstimateTimeOfExecution(requestID53, evaluationReport.getEstimatedExecutionTime());

			ArrayList<String> comNames2 = db.getComsUsernames();

			// send message to all com
			for (String comName : comNames2) {
				sendUserMessage("A request is waiting for decision", comName,
						"The requests [" + requestID53 + "] is waiting for a decision. you have 7 days to decide!",
						requestID53, decPhaseId);
			}

			ArrayList<Long> comEmpNums = db.getComsEmpNums();

			for (Long empNum : comEmpNums) {
				// Notify the com member for a new treatment requests update
				notifyEmployeeTreatmentRequestsUpdated(empNum);
			}

			sendMessageToClient(client, command);

			break;

		case updatePhaseEstimatedTime:

			Phase updatePhaseEst = (Phase) srMsg.getAttachedData()[0];

			db.updateByObject(updatePhaseEst);

			sendUserMessage("A phase is pending on deadline confirmation", db.getUsernameOfSupervisor(),
					"The phase [" + updatePhaseEst.getPhaseID() + "] is waiting for a confirmation for the deadline!",
					updatePhaseEst.getRequestID(), updatePhaseEst.getPhaseID());

			sendMessageToClient(client, command);

			break;

		case rejectPhaseDeadline:

			Phase rPhase = (Phase) srMsg.getAttachedData()[0];
			db.updateByObject(rPhase);

			sendUserMessage("Deadline Time Rejected", db.getUsernameByEmpNumber(rPhase.getEmpNumber()),
					"The deadline that you choosed for phase [" + rPhase.getPhaseID() + "] has been rejected!",
					rPhase.getRequestID(), rPhase.getPhaseID());

			sendMessageToClient(client, command);

			break;

		case confirmPhaseDeadline:

			Phase cPhase = (Phase) srMsg.getAttachedData()[0];
			db.updateByObject(cPhase);

			sendUserMessage("Deadline Time accepted", db.getUsernameByEmpNumber(cPhase.getEmpNumber()),
					"The deadline that you choosed for phase [" + cPhase.getPhaseID() + "] has been accepted!",
					cPhase.getRequestID(), cPhase.getPhaseID());

			// Notify the requester for a new treatment requests update
			notifyEmployeeTreatmentRequestsUpdated(cPhase.getEmpNumber());

			sendMessageToClient(client, command);

			break;

		case insertTimeExtension:

			PhaseTimeExtensionRequest pter = (PhaseTimeExtensionRequest) srMsg.getAttachedData()[0];

			// insert time extension and update phase status to wait for time extension
			// confirmation
			boolean result33 = db.insertTimeExtension(pter);
			long phId = pter.getPhaseID();
			long reqId = db.getRequestIdByPhaseId(phId);

			// insert request to database
			PhaseStatus status = PhaseStatus.Active_And_Waiting_For_Time_Extension;
			result33 &= db.updatePhaseStatus(phId, status);

			// notify the supervisor about the time extension
			String evalName = db.getFullNameByPhaseId(phId);
			sendUserMessage(
					"Confirm time extension", db.getUsernameOfSupervisor(), "The evaluator " + evalName + " of request "
							+ reqId + " has requested a time extension\n" + "Reason: " + pter.getDescription(),
					reqId, phId);

			if (result33 == false) {
				System.err.println("Error, command insertTimeExtension has failed!");
			}

			// Send back the results
			sendMessageToClient(client, command, result33, pter.getRequestedTimeInDays(),
					pter.getRequestedTimeInHours());

			break;

		case setEvaluationPhaseToWaitingToSetTime:

			Phase ph2424 = (Phase) srMsg.getAttachedData()[0];
			ph2424.setStatus(PhaseStatus.Waiting_To_Set_Time_Required_For_Phase.nameNo_());
			db.updateByObject(ph2424);

			String username42424 = db.getUsernameByEmpNumber(ph2424.getEmpNumber());

			sendUserMessage("Set required time to evaluate a request", username42424,
					"You have been assigned to evaluate the request[" + ph2424.getRequestID()
							+ "].\nPlease choose the required time to evaluate the request",
					ph2424.getRequestID(), ph2424.getPhaseID());

			notifyEmployeeTreatmentRequestsUpdated(ph2424.getEmpNumber());

			sendMessageToClient(client, command);

			break;

		case updatePhaseOwner:

			Phase ph = (Phase) srMsg.getAttachedData()[0];
			long empNumber = (long) srMsg.getAttachedData()[1];

			ph.setEmpNumber(empNumber);

			db.updateByObject(ph);

			sendMessageToClient(client, command);

			break;

		case getEmployeesListSimple:

			ArrayList<Employee> emps = db.getEmployees();
			sendMessageToClient(client, command, emps);

			break;

		case rejectPhaseTimeExtensionSupervisor:

			Phase phaseRTE2 = (Phase) srMsg.getAttachedData()[0];
			phaseRTE2.setStatus(PhaseStatus.Active.name());
			db.updateByObject(phaseRTE2);

			// delete the time extension from the database
			db.deleteObject(phaseRTE2.getPhaseTimeExtensionRequest());

			// send a message of confirmation to the time extension requester
			String subject1 = "Time extension rejected";
			String toUsername1 = db.getEmployeeByEmpNumber(phaseRTE2.getEmpNumber()).getUserName();
			String content1 = "Your time extension for the request [id:" + phaseRTE2.getRequestID()
					+ "] has been rejected!";
			sendUserMessage(subject1, toUsername1, content1, SYSTEM_EMPLOYEE_NUMBER, SYSTEM_EMPLOYEE_NUMBER);

			// Notify the requester for a new treatment requests update
			notifyEmployeeTreatmentRequestsUpdated(phaseRTE2.getEmpNumber());

			// send a confirmation message
			sendMessageToClient(client, command, true, phaseRTE2.getRequestID());

			break;

		case acceptPhaseTimeExtensionSupervisor:

			Phase phaseRTE = (Phase) srMsg.getAttachedData()[0];

			int addedDays = phaseRTE.getPhaseTimeExtensionRequest().getRequestedTimeInDays();
			int addedHours = phaseRTE.getPhaseTimeExtensionRequest().getRequestedTimeInDays();

			// add the time to the request
			Timestamp newDeadline = DateUtil.add(phaseRTE.getDeadline(), addedDays, addedHours);

			phaseRTE.setDeadline(newDeadline);
			phaseRTE.setHasBeenTimeExtended(true);
			phaseRTE.setStatus(PhaseStatus.Active.name());

			// update the request time
			db.updateByObject(phaseRTE);

//			// delete the time extension from the database
//			db.deleteObject(phaseRTE.getPhaseTimeExtensionRequest());

			// send a message of confirmation to the time extension requester
			String subject = "Time extension accepted";
			String toUsername = db.getEmployeeByEmpNumber(phaseRTE.getEmpNumber()).getUserName();
			String content = "Your time extension for the request [id:" + phaseRTE.getRequestID()
					+ "] has been accepted!";

			sendUserMessage(subject, toUsername, content, SYSTEM_EMPLOYEE_NUMBER, SYSTEM_EMPLOYEE_NUMBER);
			sendUserMessage(subject, db.getUsernameOfManager(), content, SYSTEM_EMPLOYEE_NUMBER,
					SYSTEM_EMPLOYEE_NUMBER);

			sendUserMessage(subject, db.getUsernameOfManager(),
					"Time extension has been added for the request [id:" + phaseRTE.getRequestID() + "]", -1, -1);

			// Notify the requester for a new treatment requests update
			notifyEmployeeTreatmentRequestsUpdated(phaseRTE.getEmpNumber());

			// send a confirmation message
			sendMessageToClient(client, command, true, phaseRTE.getRequestID());

			break;

		case getChangeRequestFromMessagePage:

			long requestID = (long) srMsg.getAttachedData()[0];

			System.out.println(requestID);

			ChangeRequest cr2222 = db.getChangeRequestById(requestID);

			sendMessageToClient(client, command, cr2222);

			break;

		case checkIfPhaseIsWaiting:

			long phaseID = (long) srMsg.getAttachedData()[0];

			boolean isWaiting = db.isPhaseStatus(phaseID, PhaseStatus.Waiting);

			sendMessageToClient(client, command, isWaiting);

			break;

		case GetMyIssuedRequests:

			String forUsername = (String) srMsg.getAttachedData()[0];

			int startingRow = (int) srMsg.getAttachedData()[1];
			int size = (int) srMsg.getAttachedData()[2];

			ArrayList<ChangeRequest> crs = db.getChangeRequests(forUsername, startingRow, size);

			sendMessageToClient(client, command, crs);

			break;

		case GetMyIssuedRequestsCount:

			String countOfMyRequestsCondition = (String) srMsg.getAttachedData()[0];
			int countOfMyIssuedRequests = db.getCountOf(ChangeRequest.getEmptyInstance(), countOfMyRequestsCondition);
			sendMessageToClient(client, command, countOfMyIssuedRequests);

			break;

		case getPermissionsData:

			String username23 = (String) srMsg.getAttachedData()[0];
			int countOfPhasesForUsername = db.getCountOfPhaseForEmpByUsername(username23);
			boolean isManager = db.isUserManager(username23);
			boolean hasAtleastOnePhaseToManage = countOfPhasesForUsername > 0;

			long empNum798 = db.getEmpNumByUsername(username23);
			boolean isSupervisor = false;
			boolean isComMember = false;
			boolean isComHead = false;

			if (empNum798 != -1) {
				isSupervisor = db.isEmployeeIsSupervisor(empNum798);
				isComMember = db.isEmployeeComMember(empNum798);

				if (isComMember) {
					isComHead = db.isEmployeeComHead(empNum798);
				}
			}

			sendMessageToClient(client, command, isManager, hasAtleastOnePhaseToManage, empNum798, isSupervisor,
					isComMember, isComHead);

			break;

		case getFullNameByUsername:

			String username45 = (String) srMsg.getAttachedData()[0];
			String fullName = db.getFullNameByUsername(username45);
			sendMessageToClient(client, command, fullName);

			break;
		case getEmployeeByEmployeeNumber:
			long empId = (long) srMsg.getAttachedData()[0];
			Employee emp = db.getEmployeeByEmpNumber(empId);
			sendMessageToClient(client, command, emp);
			break;

		case getSystemUserByRequest:

			long requestId2 = (long) srMsg.getAttachedData()[0];
			SystemUser sysUser = db.getSystemUserByRequestID(requestId2);
			sendMessageToClient(client, command, sysUser);

			break;

		case getPhasesOfRequestWithTimeExtensionsIfPossible:

			long requestIDforPhase = (long) srMsg.getAttachedData()[0];

			ArrayList<Phase> requestedPhases = db.getPhasesOfRequest(requestIDforPhase);

			sendMessageToClient(client, command, requestedPhases);

		case getCountOfPhasesTypes:

			long empNumberForPhases = (long) srMsg.getAttachedData()[0];

			int cntSupervision = db.isEmployeeIsSupervisor(empNumberForPhases) ? 1 : 0;
			int cntEvaluation = db.getCountOfPhasesByType(empNumberForPhases, PhaseType.Evaluation);

			int cntDecision = 0;
			if (db.isEmployeeComMember(empNumberForPhases))
				cntDecision = db.getCountOfPhasesByType(PhaseType.Decision);

			int cntExecution = db.getCountOfPhasesByType(empNumberForPhases, PhaseType.Execution);
			int cntExamination = db.getCountOfPhasesByType(empNumberForPhases, PhaseType.Examination);

			sendMessageToClient(client, command, cntSupervision, cntEvaluation, cntDecision, cntExecution,
					cntExamination);

			break;

		case updateMessage:

			Message msgToUpdate = (Message) srMsg.getAttachedData()[0];
			db.updateMessage(msgToUpdate);

			break;

		case Update:

			SqlObject updateObj = (SqlObject) srMsg.getAttachedData()[0];
			db.updateByObject(updateObj);

			break;
		case deleteObjects:
			String MESSAGES_DELETED_LIST_OF_MESSAGES_RESPONSE = "messagesDeletedListOfMessagesResponse";

			ArrayList<SqlObject> objs = (ArrayList<SqlObject>) srMsg.getAttachedData()[0];

			int res = 1;
			for (SqlObject sqlObject : objs) {
				res *= db.deleteObject(sqlObject);
			}

			MsgReturnType retType = res == 1 ? MsgReturnType.Success : MsgReturnType.Failure;
			sendMessageToClient(client, command, MESSAGES_DELETED_LIST_OF_MESSAGES_RESPONSE, retType);

			break;
		case getMessagesPrimary:

			// TODO for now this is for all messages, not just primary
			String userName = (String) srMsg.getAttachedData()[0];
			int startingRowMessages = (int) srMsg.getAttachedData()[1];
			int sizeMessages = (int) srMsg.getAttachedData()[2];

			ArrayList<Message> msgs = db.getMessages(userName, startingRowMessages, sizeMessages);
			// TODO add failure and success cases

			sendMessageToClient(client, command, msgs);

			break;
		case countOfObjects:

			String cooCondition = (String) srMsg.getAttachedData()[0];
			SqlObject sqlObject = (SqlObject) srMsg.getAttachedData()[1];
			int countOfObject = db.getCountOf(sqlObject, cooCondition);
			sendMessageToClient(client, command, countOfObject);

			break;
		case insertFile:

			File fileToInsert = (File) srMsg.getAttachedData()[0];
			db.insertFile(fileToInsert);

			break;

		case getFile:

			int fileID = (int) srMsg.getAttachedData()[0];

			// TODO: this has to run in a different thread since it might get the server
			// stuck, other clients wont be able to receive messages
			// Make multiple threads or a thread queue?
			File downloadedFile = db.getFile(fileID);

			sendMessageToClient(client, command, downloadedFile);

			break;

		case debug_simulateBigCalculations:
			ArrayList<String> stra = (ArrayList<String>) srMsg.getAttachedData()[0];

			for (int i = 0; i < 500000; i++) {
				System.out.println(stra.toString());
			}

			break;

		case insertRequest:

			ChangeRequest changeRequest = (ChangeRequest) srMsg.getAttachedData()[0];
			// Set a new max id
			changeRequest.setRequestID(db.getNewMaxID(changeRequest));

			result = 1;
			result = db.insertObject(changeRequest); // TODO: make it return a boolean

			// if the request was issued
			if (result == 1) {
				initEvaluationPhase(changeRequest.getRequestID());
			}

			sendBooleanResultMessage(client, command, result);

			break;

		case insertRequestWithFiles:

			ChangeRequest changeRequestWithFiles = (ChangeRequest) srMsg.getAttachedData()[0];
			// Set a new max id
			changeRequestWithFiles.setRequestID(db.getNewMaxID(changeRequestWithFiles));

			ArrayList<File> files = (ArrayList<File>) srMsg.getAttachedData()[1];

			result = 1;
			result *= db.insertObject(changeRequestWithFiles); // TODO: make it return a boolean
			for (File file : files) {
				// Set the request id for the file
				file.setRequestID(changeRequestWithFiles.getRequestID());
				result *= db.insertFile(file);
			}

			// if the request was issued
			if (result == 1) {
				initEvaluationPhase(changeRequestWithFiles.getRequestID());
			}

			sendBooleanResultMessage(client, command, result);

			break;

		case GetMyRequests:

			// TODO: fix in my requests list
			PhaseType phaseType = (PhaseType) srMsg.getAttachedData()[0];

			switch (phaseType) {
			case Supervision:

				ArrayList<ChangeRequest> requestsWithCurrentPhase = db.getChangeRequestWithCurrentPhase();

				sendMessageToClient(client, command, phaseType, requestsWithCurrentPhase);
				break;
			case Decision:

				ArrayList<ChangeRequest> crDecision = db.getChangeRequestPhaseForCom();

				sendMessageToClient(client, command, phaseType, crDecision);

				break;
			case Evaluation:
			case Examination:
			case Execution:

				long empNum = (long) srMsg.getAttachedData()[1];

				ArrayList<ChangeRequest> crSupervision = db.getChangeRequestPhaseByEmployee(empNum, phaseType);

				sendMessageToClient(client, command, phaseType, crSupervision);

				break;

			default:
				System.err.println("Error, the RequestsType " + phaseType.toString() + " is not defined!");
				break;
			}

			break;
		default:
			System.err.println("Error, undefine command [" + srMsg.getCommand() + "]");
			break;
		}

		for (ObjectClientFunc f : objectRecievedFromClientsEvents) {
			if (f != null)
				f.call(msg, client);
		}

	}

	private void initExecutionPhase(Phase p1) {
		long nextPhaseId = db.getNewMaxID(Phase.getEmptyInstance());
		Phase phase = new Phase(nextPhaseId, p1.getRequestID(), PhaseType.Execution.name(),
				PhaseStatus.Waiting_To_Set_Executer.nameNo_(), DEFAULT_EXECUTER_EMP_NUMBER, DateUtil.NA, DateUtil.NA,
				DateUtil.NA, DateUtil.now(), false);

		db.insertObject(phase);

		String subject = "Assign an executer";
		String toUsername = db.getUsernameOfSupervisor();
		String content = "Please confirm or assign an executer to the request";
		sendUserMessage(subject, toUsername, content, p1.getRequestID(), nextPhaseId);

	}

	private void initClosingPhaseRejecton(Phase p1) {
		long nextPhaseId = db.getNewMaxID(Phase.getEmptyInstance());
		Phase phase = new Phase(nextPhaseId, p1.getRequestID(), PhaseType.Closing.name(),
				PhaseStatus.Waiting_For_Owner_And_Supervisor_Confirmation.nameNo_(), db.getSupervisorEmpNum(),
				DateUtil.NA, DateUtil.NA, DateUtil.NA, DateUtil.now(), false);

		db.insertObject(phase);

		// Send a message to the supervisor
		String subject = "Closing Rejected Requst";
		String supervisorUsername = db.getUsernameOfSupervisor();
		String content = "Request Rejected, Please confirm the closing of the request [" + p1.getRequestID() + "].";
		sendUserMessage(subject, supervisorUsername, content, p1.getRequestID(), nextPhaseId);

		// send message to the owner
		String contentOwner = "Request Rejected, Please confirm the closing of your request [" + p1.getRequestID()
				+ "].";

		String ownerUsername = db.getRequestOwnerUsername(p1.getRequestID());
		sendUserMessage(subject, ownerUsername, contentOwner, p1.getRequestID(), nextPhaseId);

	}

	private void initClosingPhase(Phase p1) {

		long nextPhaseId = db.getNewMaxID(Phase.getEmptyInstance());
		Phase phase = new Phase(nextPhaseId, p1.getRequestID(), PhaseType.Closing.name(),
				PhaseStatus.Waiting_For_Owner_And_Supervisor_Confirmation.nameNo_(), db.getSupervisorEmpNum(),
				DateUtil.NA, DateUtil.NA, DateUtil.NA, DateUtil.now(), false);

		db.insertObject(phase);

		// Send a message to the supervisor
		String subject = "Closing Requst";
		String supervisorUsername = db.getUsernameOfSupervisor();
		String content = "Please confirm the closing of the request [" + p1.getRequestID() + "].";
		sendUserMessage(subject, supervisorUsername, content, p1.getRequestID(), nextPhaseId);

		// send message to the owner
		String contentOwner = "Please confirm the closing of your request [" + p1.getRequestID() + "].";

		String ownerUsername = db.getRequestOwnerUsername(p1.getRequestID());
		sendUserMessage(subject, ownerUsername, contentOwner, p1.getRequestID(), nextPhaseId);

	}

	private void initEvaluationPhase(long requestId) {

		long nextPhaseId = db.getNewMaxID(Phase.getEmptyInstance());
		long empNumber = DEFAULT_EVALUATOR_EMP_NUMBER;
		Phase phase = new Phase(nextPhaseId, requestId, PhaseType.Evaluation.name(),
				PhaseStatus.Waiting_To_Set_Evaluator.nameNo_(), empNumber, DateUtil.NA, DateUtil.NA, DateUtil.NA,
				DateUtil.now(), false);
		db.insertObject(phase);

		// Send a message to the supervisor
		sendNewRequestIssuedMessageToSupervisor(requestId, nextPhaseId);

	}

	private void sendNewRequestIssuedMessageToSupervisor(long reqId, long phaseId) {

		String subject = "Assign an evaluator";
		String toUsername = db.getUsernameOfSupervisor();
		String content = "Please confirm or assign an evalutor to the request";
		sendUserMessage(subject, toUsername, content, reqId, phaseId);
	}

	private void sendUserMessage(String subject, String toUsername, String content, long reqId, long phaseId) {

		String from = "System"; // this should not be a normal employee, this is made for the server
		Message msg = new Message(SYSTEM_EMPLOYEE_NUMBER, subject, from, toUsername, content, false, DateUtil.now(),
				false, false, false, reqId, phaseId);

		System.out.println("Send this message: ");
		System.out.println(msg);
		db.insertMessage(msg);

		notifyUserNewMessages(toUsername);
	}

	private void sendUserMessage(String subject, String toUsername, String content) {
		sendUserMessage(subject, toUsername, content, SYSTEM_EMPLOYEE_NUMBER, SYSTEM_EMPLOYEE_NUMBER);
	}

	private void notifyUserNewMessages(String toUsername) {
		sendMessageToAllClients(Command.receivedNewMessage, toUsername);
	}

	private void notifyEmployeeTreatmentRequestsUpdated(long toEmpNum) {
		sendMessageToAllClients(Command.receivedNewOrUpdateRequests, toEmpNum);
	}

	private void sendMessageToClient(ConnectionToClient client, Command cmd, Object... objs) {
		try {
			client.sendToClient(new SRMessage(cmd, objs));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMessageToAllClients(Command cmd, Object... objs) {
		sendToAllClients(new SRMessage(cmd, objs));
	}

	private void sendBooleanResultMessage(ConnectionToClient client, Command cmd, int result) {

		if (result == 1) {
			sendResultMessageToClient(client, Command.insertRequestWithFiles, MsgReturnType.Success);
		} else {
			sendResultMessageToClient(client, Command.insertRequestWithFiles, MsgReturnType.Failure);
		}
	}

	private void sendResultMessageToClient(ConnectionToClient client, Command cmd, MsgReturnType returnType) {
		sendResultMessageToClient(client, cmd, returnType, null);
	}

	private void sendResultMessageToClient(ConnectionToClient client, Command cmd, MsgReturnType returnType,
			Object... objs) {
		try {
			SRMessage msg = new SRMessage(cmd, objs);
			msg.setReturnType(returnType);
			client.sendToClient(msg);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void clientConnected(ConnectionToClient client) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				for (ClientFunc f : clientConnectedEvents) {
					if (f != null)
						f.call(client);
				}

			}
		});

	}

	@Override
	protected synchronized void clientException(ConnectionToClient client, Throwable exception) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				for (ClientThrowableFunc f : clientExceptionEvents) {
					if (f != null)
						f.call(client, exception);
				}

			}
		});
	}

	@Override
	protected synchronized void clientDisconnected(ConnectionToClient client) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				for (ClientFunc f : clientDisconnectedEvents) {
					if (f != null)
						f.call(client);
				}

			}
		});
	}

	@Override
	protected void listeningException(Throwable exception) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				for (ThrowableFunc f : serverExceptionEvents) {
					if (f != null)
						f.call(exception);
				}

			}
		});
	}

	@Override
	protected void serverClosed() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				for (VoidFunc f : serverClosedEvents) {
					if (f != null)
						f.call();
				}

			}
		});
	}

	@Override
	protected void serverStarted() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				for (VoidFunc f : serverStartedEvents) {
					if (f != null)
						f.call();
				}

			}
		});
	}

	@Override
	protected void serverStopped() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				for (VoidFunc f : serverStoppedEvents) {
					if (f != null)
						f.call();
				}

			}
		});
	}

	@Override
	public void sendToAllClients(Object msg) {
		// TODO Auto-generated method stub
		super.sendToAllClients(msg);
	}

	public static void addObjectRecievedFromClientsEvent(ObjectClientFunc ocf) {
		objectRecievedFromClientsEvents.add(ocf);
	}

	public static void addClientConnectedEvent(ClientFunc cf) {
		clientConnectedEvents.add(cf);
	}

	public static void addClientExceptionEvent(ClientThrowableFunc ctf) {
		clientExceptionEvents.add(ctf);
	}

	public static void addClientDisconnectedEvent(ClientFunc cf) {
		clientDisconnectedEvents.add(cf);
	}

	public static void addServerExceptionEvent(ThrowableFunc tf) {
		serverExceptionEvents.add(tf);
	}

	public static void addServerClosedEvent(VoidFunc tf) {
		serverClosedEvents.add(tf);
	}

	public static void addServerStartedEvent(VoidFunc tf) {
		serverStartedEvents.add(tf);
	}

	public static void addServerStoppedEvent(VoidFunc tf) {
		serverStoppedEvents.add(tf);
	}

	public String getHostAddress() {
		return inetAddress.getHostAddress();
	}

	public String getHostName() {
		return inetAddress.getHostName();
	}

	public static void onShutDown() {
		ArrayList<SystemUser> users = db.getAllUsers();
		for (SystemUser s : users) {
			db.logOut(s.getUserName());
		}
	}

	
	
	

}
