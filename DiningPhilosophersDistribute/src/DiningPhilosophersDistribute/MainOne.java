package DiningPhilosophersDistribute;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Hochschule f�r angewandte Wissenschaften M�nchen
 * Verteilte Softwaresysteme - Praktikum
 * WS 2015/16
 * Aufgabe 4 Verteilte Programmierung
 * @author Diana irmscher - diana.irmscher@hm.edu
 */


public class MainOne {
	
	public static void main(String args[]) {
		//jeder Tisch hat drei Sitzpl�tze mit einer rechten Gabel
		try {
			ConnectionHelper connectionHelper = new ConnectionHelper();
			ITable table = new Table(3, 1, connectionHelper);
			
			IConnectionHelper stub = (IConnectionHelper) UnicastRemoteObject.exportObject(connectionHelper, 0);

			Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			
			registry.bind("ConnectionHelper", stub);
			
			String tableName= "Table-1";
			
			ITable tableStub = (ITable) UnicastRemoteObject.exportObject(table, 0);
			registry.bind(tableName, tableStub);
			
			System.err.println("Server ready: Bereit zur Anmeldung.");
			
			//stub.setTable(table);
			
			Philosopher[] philosophers = new Philosopher[]{ new Philosopher("Lukas", tableStub, stub, false),
															new Philosopher("Robert", tableStub, stub, false),
															new Philosopher("Bauer0", tableStub, stub, false),
															new Philosopher("Linda", tableStub, stub, false),
															new Philosopher("Tom", tableStub, stub, false),
															new Philosopher("Ersin", tableStub, stub, false),
															new Philosopher("Alu", tableStub, stub, false),
															new Philosopher("Markus", tableStub, stub, false),
															new Philosopher("Moritz", tableStub, stub, false),
															new Philosopher("Chris Pohl", tableStub, stub, false),
															new Philosopher("Friedrich", tableStub, stub, false),
															new Philosopher("Ludwig", tableStub, stub, false),
															new Philosopher("Hans", tableStub, stub, false),
															new Philosopher("Martin", tableStub, stub, false)
					};
			
			for (int i = 0; i < philosophers.length; i++) {
				philosophers[i].start();
			}
			
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
		
		
	}

}
