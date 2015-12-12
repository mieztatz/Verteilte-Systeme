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
			final Table table = new Table(3, 1, connectionHelper);
			
			IConnectionHelper stub = (IConnectionHelper) UnicastRemoteObject.exportObject(connectionHelper, 0);

			Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			
			registry.bind("ConnectionHelper", stub);
			
			System.err.println("Server ready: Bereit zur Anmeldung.");
			
			stub.setTable(table);
			
			Philosopher[] philosophers = new Philosopher[]{ new Philosopher("Lukas", table, stub, false),
															//new Philosopher("Robert", table, stub, false),
															//new Philosopher("Bauer0", table, stub, false)
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
