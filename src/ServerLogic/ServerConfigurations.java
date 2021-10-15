package ServerLogic;


/**
 * This is a simple class that registers to the Server class events to prints simple notifications in the console
 * the events are server stopped, server exception and server disconnected.
 * */
public class ServerConfigurations {

	public static void InjectEvents() {
		
		
		Server.addClientConnectedEvent(client -> {
			System.out.println("Client " + client.getInetAddress() + " connected");

		});
		
		
		Server.addClientDisconnectedEvent(client -> {
			System.out.println("Client " + client.getInetAddress() + " disconnected");

		});
		
		Server.addClientExceptionEvent((client, exception) -> {
			System.out.println("Client " + client.getInetAddress() + " exception with " + exception.toString());

		});
		
		
		Server.addServerStoppedEvent(() -> {
			System.out.println("Server stopped");
		});
		

	}
}
