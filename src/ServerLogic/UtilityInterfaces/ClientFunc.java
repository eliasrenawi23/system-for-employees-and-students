package ServerLogic.UtilityInterfaces;

import ocsf.server.ConnectionToClient;
/**
 * This interface is used to pass behaviors and registers to methods.
 * @author Bshara
 * */
public interface ClientFunc {
	public void call(ConnectionToClient client);
}
