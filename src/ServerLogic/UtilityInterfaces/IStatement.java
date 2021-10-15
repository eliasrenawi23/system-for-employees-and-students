package ServerLogic.UtilityInterfaces;
import java.sql.ResultSet;
/**
 * This interface is used to pass behaviors and registers to methods.
 * @author Bshara
 * */
public interface IStatement {
	public void executeQuery(ResultSet rs);
}
