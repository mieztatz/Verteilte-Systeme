package DiningPhilosophersDistribute2;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Hochschule für angewandte Wissenschaften München
 * Verteilte Softwaresysteme - Praktikum
 * WS 2015/16
 * Aufgabe 4 Verteilte Programmierung
 * @author Diana irmscher - diana.irmscher@hm.edu
 */


public class MainOne {
	
	public static void main(String args[]) {
		//jeder Tisch hat drei Sitzplätze mit einer rechten Gabel
		try {
			final Table tab = new Table(3);
			ITable table = (ITable) UnicastRemoteObject.exportObject(tab, 0);
			
			Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			
			registry.bind("Table", table);
			
			System.err.println("Server ready: Der Tisch ist gedeckt.");
			
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
		
		
	}

}
