package ServerLogic.UtilityInterfaces;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * This interface is used to pass behaviors and registers to methods.
 * @author Bshara
 * */
public interface IPreparedStatement {
	/**
	 * 
	 * Only add the changes, the function will automatically execute update.
	 * 
	 * */
	public void executeChanges(PreparedStatement ps);

}
