package Protocol;

import java.io.Serializable;


/**
 * This class contains all the needed information to communicate between server-client,
 * the message contains a command, a return type (success or failure) and an array of objects that can be attached to the message.
 * 
 * @author Bshara
 * */
public class SRMessage implements Serializable {

	private Command command;
	private MsgReturnType returnType; 
	private Object[] data;


	public SRMessage(Command command, Object[] data) {
		this.command = command;
		this.data = data;
	}

	
	public SRMessage(Command command, MsgReturnType returnType, Object[] data) {
		super();
		this.command = command;
		this.returnType = returnType;
		this.data = data;
	}


	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public Object[] getAttachedData() {
		return data;
	}

	public void setAttachedData(Object[] attachedData) {
		this.data = attachedData;
	}


	public MsgReturnType getReturnType() {
		return returnType;
	}


	public void setReturnType(MsgReturnType returnType) {
		this.returnType = returnType;
	}


	@Override
	public String toString() {
		return "SRMessage [command=" + command.toString() + ", data=" + data.toString() + "]";
	}
	
	

}
