package DiningPhilosophersDistribute2;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
		ITable stub = (ITable) registry.lookup("Table");
		String response = stub.toString();
		System.out.println("response: " + response);
		
		Philosopher philosopher = new Philosopher("Fabian", stub, false);
		
		philosopher.start();
		
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
}

}
