package DiningPhilosophersDistribute;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.text.TabExpander;

/**
 * Hochschule für angewandte Wissenschaften München
 * Verteilte Softwaresysteme - Praktikum
 * WS 2015/16
 * Aufgabe 4 Verteilte Programmierung
 * @author Diana irmscher - diana.irmscher@hm.edu
 */

public class MainTwo {
	
	public static void main(String args[]) {
		try {
		Registry registry = LocateRegistry.getRegistry();
		
		IConnectionHelper stubConnectionHelper = (IConnectionHelper) registry.lookup("ConnectionHelper");
		
		String tableTwo = "Table-2";
		final Table table = new Table(3, 2, tableTwo, stubConnectionHelper);
		ITable stubTable = (ITable) UnicastRemoteObject.exportObject(table, 0);
		registry.bind(tableTwo, stubTable);
		
		
		stubConnectionHelper.addTable(tableTwo);
		
		Philosopher[] philosophers = new Philosopher[]{new Philosopher("Fabian", stubTable, stubConnectionHelper, false),
//													   new Philosopher("Luca", stubTable, stubConnectionHelper, false),
//													   new Philosopher("Benni", stubTable, stubConnectionHelper, false),
//													   new Philosopher("Max", stubTable, stubConnectionHelper, false),
//													   new Philosopher("Fabio", stubTable, stubConnectionHelper, false),
//													   new Philosopher("Sabine", stubTable, stubConnectionHelper, false),
//													   new Philosopher("Matti", stubTable, stubConnectionHelper, false),
//													   new Philosopher("Nico", stubTable, stubConnectionHelper, false),
//													   new Philosopher("Marius", stubTable, stubConnectionHelper, false),
//													   new Philosopher("Anselm", stubTable, stubConnectionHelper, false)
				};
		
		for (int i = 0; i < philosophers.length; i++) {
			new Thread(philosophers[i]).start();
		}
		
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
}

}
