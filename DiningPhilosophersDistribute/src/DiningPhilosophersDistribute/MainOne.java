package DiningPhilosophersDistribute;

import java.rmi.RemoteException;
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
			Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			
			//ConnectionHelper in die Registry einbinden
			ConnectionHelper connectionHelper = new ConnectionHelper();
			IConnectionHelper stubConnectionHelper = (IConnectionHelper) UnicastRemoteObject.exportObject(connectionHelper, 0);
			registry.bind("ConnectionHelper", stubConnectionHelper);
			
			//Tisch-1 in die Registry einbinden
			String tableOne = "Table-1";
			ITable table = new Table(3, 1, tableOne, stubConnectionHelper);
			ITable stubTable = (ITable) UnicastRemoteObject.exportObject(table, 0);
			registry.bind(tableOne, stubTable);
			
			//Sitze in Registry einbinden
			ISeat seat1 = new Seat(0, stubTable);
			ISeat seat2 = new Seat(1, stubTable);
			ISeat seat3 = new Seat(2, stubTable);
	
			System.err.println("Server ready: Bereit zur Anmeldung.");
			
			//den Tisch im ConnectionHelper anmelden
			stubConnectionHelper.addTable(tableOne);
			
			
			Philosopher[] philosophers = new Philosopher[]{ new Philosopher("Lukas", stubTable, stubConnectionHelper, false),
															new Philosopher("Robert", stubTable, stubConnectionHelper, false),
//															new Philosopher("Bauer0", stubTable, stubConnectionHelper, false),
//															new Philosopher("Linda", stubTable, stubConnectionHelper, false),
//															new Philosopher("Tom", stubTable, stubConnectionHelper, false),
//															new Philosopher("Ersin", stubTable, stubConnectionHelper, false),
//															new Philosopher("Alu", stubTable, stubConnectionHelper, false),
//															new Philosopher("Markus", stubTable, stubConnectionHelper, false),
//															new Philosopher("Moritz", stubTable, stubConnectionHelper, false),
//															new Philosopher("Chris Pohl", stubTable, stubConnectionHelper, false),
//															new Philosopher("Friedrich", stubTable, stubConnectionHelper, false),
//															new Philosopher("Ludwig", stubTable, stubConnectionHelper, false),
//															new Philosopher("Hans", stubTable, stubConnectionHelper, false),
//															new Philosopher("Martin", stubTable, stubConnectionHelper, false)
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
