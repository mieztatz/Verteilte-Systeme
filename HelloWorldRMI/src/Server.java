import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Hello {

	@Override
	public String sayHello() {
		System.out.println("ICH BIN IM SERVER");
		return "Hello World";
	}
	
	public static void main(String args[]) {
		
		try {
			Server server = new Server();
			Hello hello = (Hello) UnicastRemoteObject.exportObject(server, 0);
			
			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			
			registry.bind("Hello", hello);
			
			System.err.println("Server ready");
			
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
		
	}

}
