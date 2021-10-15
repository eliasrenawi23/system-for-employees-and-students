package ClientLogic;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import Protocol.Command;
import Protocol.SRMessage;
import Protocol.SRMessageFunc;
import Utility.StringFunc;
import Utility.VoidFunc;
import javafx.application.Platform;
import ocsf.client.AbstractClient;


/**
 * This class extends the AbstractClient class,  contains all the methods necessary to setup the client side of a client-server architecture.
 *  When a client is thus connected to the server, the two programs can then exchange  Object  instances.
 *  This class provides methods to register to the handleMessageFromServer as an observer, where is the handleMessageFromServer is the observable.
 *  Other events that can be registered to are: server exception events, server connection closed events and server connection established events.
 * 
 * */
public class Client extends AbstractClient {

	private static Client instance;
	private static ConcurrentHashMap<String, VoidFunc> serverExceptionEvents;
	private static ConcurrentHashMap<String, VoidFunc> serverConnectionClosedEvents;
	private static ConcurrentHashMap<String, VoidFunc> serverConnectionEstablishedEvents;

	private static ConcurrentHashMap<String, SRMessageFunc> messageRecievedFromServerEvents;
	private static ConcurrentHashMap<String, StringFunc> stringRecievedFromServerEvents;

	static {
		//instance = new Client("10.0.0.212", 5555);
		instance = new Client("localhost", 5555);

		serverExceptionEvents = new ConcurrentHashMap<String, VoidFunc>();
		serverConnectionClosedEvents = new ConcurrentHashMap<String, VoidFunc>();
		serverConnectionEstablishedEvents = new ConcurrentHashMap<String, VoidFunc>();
		messageRecievedFromServerEvents = new ConcurrentHashMap<String, SRMessageFunc>();
		stringRecievedFromServerEvents = new ConcurrentHashMap<String, StringFunc>();
	}

	public static Client getInstance() {

		return instance;
	}

	public static VoidFunc connExceptionFromClient;
	// Initialize the client
	public void initialize(String host, int port) {


		instance.setHost(host);
		instance.setPort(port);
		try {
		
			instance.openConnection();
		} catch (IOException e) {
			if (connExceptionFromClient != null) {
				connExceptionFromClient.call();
			}
		}
	}

	public Client(String host, int port) {
		super(host, port);
	}

	public void request(Command cmd, Object... objs) {
		System.out.println("Message sent: " + cmd.toString());

		try {
			SRMessage srMsg = new SRMessage(cmd, objs);
			instance.sendToServer(srMsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void requestWithListener(Command cmd, SRMessageFunc listener, String key,  Object... objs) {

		System.out.println("Message sent: " + cmd.toString());
		Client.addMessageRecievedFromServer(key, listener);
		try {
			
			SRMessage srMsg = new SRMessage(cmd, objs);
			instance.sendToServer(srMsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void request(Command cmd) {

		request(cmd, new Object[1]);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		// TODO: might be a slow way to change the UI, try using AnimationTimeline
		Platform.runLater(new Runnable() {

			
			@Override
			public void run() {
				SRMessage srMsg = (SRMessage) msg;
				
				System.out.println("Message Received from server: " + srMsg.getCommand().toString());

				
				for (SRMessageFunc f : messageRecievedFromServerEvents.values()) {
					if (f != null)
						f.call(srMsg);
				}

				if (msg instanceof String) {
					String str = (String) msg;
					for (StringFunc f : stringRecievedFromServerEvents.values()) {
						if (f != null)
							f.call(str);
					}
				}
			}
		});

	}

	@Override
	protected void connectionException(Exception exception) {
		// TODO Auto-generated method stub
		super.connectionException(exception);

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				for (VoidFunc voidFunc : serverExceptionEvents.values()) {
					if (voidFunc != null)
						voidFunc.call();
				}
			}
		});
	}

	@Override
	protected void connectionClosed() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				for (VoidFunc voidFunc : serverConnectionClosedEvents.values()) {
					if (voidFunc != null)
						voidFunc.call();
				}
			}
		});

	}

	@Override
	protected void connectionEstablished() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				for (VoidFunc voidFunc : serverConnectionEstablishedEvents.values()) {
					if (voidFunc != null)
						voidFunc.call();
				}
			}
		});
	}

	public static void addServerConnectionEstablishedEvent(String key, VoidFunc voidFunc) {
		synchronized (voidFunc) {
			serverConnectionEstablishedEvents.remove(key);
			serverConnectionEstablishedEvents.putIfAbsent(key, voidFunc);
		}
		
	}

	public static void addServerConnectionClosedEvent(String key, VoidFunc voidFunc) {
		serverConnectionClosedEvents.remove(key);
		serverConnectionClosedEvents.putIfAbsent(key, voidFunc);
	}

	public static void addServerExceptionEvent(String key, VoidFunc voidFunc) {
		serverExceptionEvents.remove(key);
		serverExceptionEvents.putIfAbsent(key, voidFunc);
	}

	public static void addStringRecievedFromServer(String key, StringFunc stringFunc) {
		stringRecievedFromServerEvents.remove(key);
		stringRecievedFromServerEvents.putIfAbsent(key, stringFunc);
	}

	public static void addMessageRecievedFromServer(String key, SRMessageFunc sRMessageFunc) {
		messageRecievedFromServerEvents.remove(key);
		messageRecievedFromServerEvents.putIfAbsent(key, sRMessageFunc);
	}
	
	


	public static void removeServerConnectionEstablishedEvent(String key) {
		serverConnectionEstablishedEvents.remove(key);
	}

	public static void removeServerConnectionClosedEvent(String key) {
		serverConnectionClosedEvents.remove(key);
	}

	public static void removeServerExceptionEvent(String key) {
		serverExceptionEvents.remove(key);
	}

	public static void removeStringRecievedFromServer(String key) {
		stringRecievedFromServerEvents.remove(key);
	}

	public static void removeMessageRecievedFromServer(String key) {
		messageRecievedFromServerEvents.remove(key);
	}
}
