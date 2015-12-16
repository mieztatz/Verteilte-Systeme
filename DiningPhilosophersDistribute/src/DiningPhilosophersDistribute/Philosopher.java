package DiningPhilosophersDistribute;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

/**
 * Hochschule f�r angewandte Wissenschaften M�nchen
 * Verteilte Softwaresysteme - Praktikum
 * WS 2015/16
 * Aufgabe 4 Verteilte Programmierung
 * @author Diana irmscher - diana.irmscher@hm.edu
 */

public class Philosopher implements Runnable, Serializable {
	
	/** Wenn der Philosoph isst, sitzt er tempor�r an einem Platz.
	 *  Dieser Platz hat eine rechte Gabel.*/
//	private ISeat seat;
	
//	private int[] seat = null;
	
	private int seatNumber;
	private int tableNumber;
	
	/** Der Name eines Philosophen **/
	private final String name;
	
	/** Jeder Philosoph kennt den Tisch, an dem er sitzt
	 * und kann somit jederzeit seinen linken und rechten Sitznachbarn ansprechen. **/
	private final ITable table;
	
	private final IConnectionHelper connectionHelper;
	
	private ITable stubAnotherTable;
	
	private final boolean isVeryHungry;
	
	/** Anzahl der Essvorg�nge, welcher ein Philospoh bereits hatte. */
	private int process;
	
	/** Diese Variable gibt an, ob der Philosoph Hunger hat oder nicht. **/
	private boolean isHungry;
	
	boolean hasMeditate;
	
	public Philosopher(final String name, final ITable table, final IConnectionHelper connectionHelper, final boolean isVeryHungry) {
		this.name = name;
		this.table = table;
		this.connectionHelper = connectionHelper;
		this.stubAnotherTable = null;
		this.isVeryHungry = isVeryHungry;
		
		this.seatNumber = -1;
		this.tableNumber = -1;
		
		this.process = 0;
		this.isHungry = true;
		this.hasMeditate = true;
	}
	
	/** Getter f�r den Stub des Tisches */
	public ITable getTable() {
		return this.table;
	}
	
	public ITable getStubAnotherTable() {
		return this.stubAnotherTable;
	}
	
	public void setStubAnotherTable(final ITable stub) {
		this.stubAnotherTable = stub;
	}
	
	/** Getter und Setter der Klassenvariablen **/
	public boolean isHungry() {
		return isHungry;
	}

	public void setHungry(boolean isHungry) {
		this.isHungry = isHungry;
	}
	
	public String getPhilosopherName() {
		return this.name;
	}
	
	public int getSeatNumber() {
		return seatNumber;
	}
	
	public void setSeatNumber(final int number) {
		this.seatNumber = number;
	}
	
	public int getTableNumber() {
		return tableNumber;
	}
	
	public void setTabelNumber(final int number) {
		this.tableNumber = number;
	}
	
//	public int[] getSeat() {
//		return seat;
//		return new int[]{this.seatNumber, this.tableNumber};
//	}
//	
//	public void setSeat(int... args) {
//		seat = new int[]{args[0], args[1]};
//	}

	public int getProcess() {
		return process;
	}

	public void setProcess(final int process) {
		this.process = process;
	}
	
	/** LOGIK **/

	/**
	 * Meditieren und essen.
	 */
	@Override
	public void run() {
		System.out.println(this.getPhilosopherName() + " ist aktiv.");
		
		while(true) {
			/** *********** MEDITIEREN *********** **/
			if (!this.isHungry()) {
				meditate();
			}

			if (tryToEatLocal() || tryToEatRemote() || queueLocalAndTryToEat()) {
				afterSuccessfulDinner();
			}
		}
	}

	private void meditate() {
		Random r = new Random();
		double tmp = r.nextInt(100);
		if (this.isVeryHungry) {
			try {
				System.out.println(this.getPhilosopherName() + "ist sehr hungrig und meditiert nur " + 10/100 + " Sekunden.");
				Thread.sleep(10);
				hasMeditate = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			try {
				System.out.println(this.getPhilosopherName() + " meditiert f�r " + tmp/1000 + " Sekunden.");
				Thread.sleep((long)tmp);
				hasMeditate = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.setHungry(true);
	}

	private boolean tryToEatLocal() {
		try {
			return this.getTable().tryToEat(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private boolean tryToEatRemote() {
		//einen anderen Tisch versuchen
		String nameAnotherTable = "";
		try {
			nameAnotherTable = connectionHelper.getAnotherTable(this.getTable().getName());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (nameAnotherTable != null) {
			try {
				Registry registry = LocateRegistry.getRegistry();
				this.setStubAnotherTable((ITable) registry.lookup(nameAnotherTable));
				if (this.getStubAnotherTable() != null) {
					System.out.println(this.getPhilosopherName() + " hat sich einen neuen Tisch gesucht mit der Nummer " + getStubAnotherTable().getNumber());
					return this.getStubAnotherTable().tryToEat(this);
				} else {
					System.out.println("Verbindung zum neuen Tisch hat nicht funktioniert.");
				}
			} catch (Exception e) {
				System.err.println("Client exception: " + e.toString());
				e.printStackTrace();
			}
		} else {
			System.out.println("Es gibt keinen anderen Tisch, " + this.getPhilosopherName() + " muss es an seinem Tisch weiter versuchen.");
			return tryToEatLocal();
		}
		return false;
	}

	private boolean queueLocalAndTryToEat() {
		// an einen Sitzplatz anstellen, am besten lokal
		 System.out.println(this.getPhilosopherName() + " konnte nicht essen und muss auf den Platz warten.");
		 try {
			System.out.println(this.getPhilosopherName() + " wartet, bis er essen kann."); 
			return this.getTable().queueUpAndTryToEat(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private void afterSuccessfulDinner() {
		this.setProcess(this.getProcess() + 1);
		this.setHungry(false);
		hasMeditate = false;
	}
	
}