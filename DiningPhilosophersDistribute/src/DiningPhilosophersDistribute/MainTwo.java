package DiningPhilosophersDistribute;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

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
		
		IConnectionHelper stubConnectionHelper = (IConnectionHelper) registry.lookup("ConnectionHelper");
		
		String tableTwo = "Table-2";
		final Table table = new Table(3, 2, tableTwo, stubConnectionHelper);
		ITable stubTable = (ITable) UnicastRemoteObject.exportObject(table, 0);
		registry.bind(tableTwo, stubTable);
		
		
		stubConnectionHelper.addTable(tableTwo);
		
		ErrorResistantPhilosopher[] philosophers = new ErrorResistantPhilosopher[]{new ErrorResistantPhilosopher("Fabian", stubTable, stubConnectionHelper, false),
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
	public static class ErrorResistantPhilosopher implements Runnable {

		private String name;
		private ITable table;
		private IConnectionHelper connectionHelper;
		private boolean isVeryHungry;
		private Philosopher philosoph;

		public ErrorResistantPhilosopher(final String name, final ITable table, final IConnectionHelper connectionHelper, final boolean isVeryHungry) {
			this.name = name;
			this.table = table;
			this.connectionHelper = connectionHelper;
			this.isVeryHungry = isVeryHungry;
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					philosoph = new Philosopher(name, table, connectionHelper, isVeryHungry);
					philosoph.run();
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Recovering from error.....");
				}
			}
		}

		public Philosopher getPhilosoph() {
			return philosoph;
		}
	}

}
