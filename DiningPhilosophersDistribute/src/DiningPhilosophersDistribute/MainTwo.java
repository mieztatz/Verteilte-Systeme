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
		
		Philosopher[] philosophers = new Philosopher[]{new Philosopher("Fabian", table, stub, false),
													   new Philosopher("Luca", table, stub, false),
													   new Philosopher("Benni", table, stub, false),
													   new Philosopher("Max", table, stub, false),
													   new Philosopher("Fabio", table, stub, false),
													   new Philosopher("Sabine", table, stub, false),
													   new Philosopher("Matti", table, stub, false),
													   new Philosopher("Nico", table, stub, false),
													   new Philosopher("Marius", table, stub, false),
													   new Philosopher("Anselm", table, stub, false)};
		
		for (int i = 0; i < philosophers.length; i++) {
			philosophers[i].start();
		}
		
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
}

}
