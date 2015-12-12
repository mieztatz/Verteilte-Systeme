package DiningPhilosophersDistribute;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.text.TabExpander;

/**
 * Hochschule f�r angewandte Wissenschaften M�nchen
 * Verteilte Softwaresysteme - Praktikum
 * WS 2015/16
 * Aufgabe 4 Verteilte Programmierung
 * @author Diana irmscher - diana.irmscher@hm.edu
 */

public class MainTwo {
	
	public static void main(String args[]) {
		try {
		Registry registry = LocateRegistry.getRegistry();
		
		IConnectionHelper stub = (IConnectionHelper) registry.lookup("ConnectionHelper");
		final Table table = new Table(3, 2, stub);
		
		//System.out.println("response: " + response);
		
		stub.setTable(table);
		
		Philosopher philosopher = new Philosopher("Fabian", table, stub, false);
		
		//System.out.println(stub.getNumberOfTables());
		philosopher.start();
		
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
}

}
