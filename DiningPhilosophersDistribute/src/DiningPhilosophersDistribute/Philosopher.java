package DiningPhilosophersDistribute;

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

public class Philosopher extends Thread {
	
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
				Random r = new Random();
				double tmp = r.nextInt(100);
				if (this.isVeryHungry) {
					try {
						Thread.sleep(10);
						System.out.println(this.getPhilosopherName() + "ist sehr hungrig und meditiert nur " + 10/100 + " Sekunden.");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					try {
						System.out.println(this.getPhilosopherName() + " meditiert f�r " + tmp/1000 + " Sekunden.");
						Thread.sleep((long)tmp);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				this.setHungry(true);
			}
			/** *********** Essen *********** **/
			boolean wasSuccessful = false;
			boolean hasMeditate = false;
			//ersten Sitzplatz zuweisen lassen
			if (this.getSeatNumber() == -1 && this.getTableNumber() == -1) {
				try {
					this.setSeatNumber(this.getTable().getAnySeat().getNumber());
					this.setTabelNumber(this.getTable().getNumber());
					System.out.println(this.getPhilosopherName() + " hat Tisch " + this.getTable());
					System.out.println(this.getPhilosopherName() + " betrachtet Sitznummer " + this.getSeatNumber() + " an Tisch " + this.getTableNumber());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Das ist ganz schlecht, ich merke mir nur noch die Zahlen, wo ich bin
			// das Objekt hole ich mir immer remote
//			IFork forkRight = null;
//			IFork forkLeft = null;
//			try {
//				forkRight = this.getTable().getForkOfSeat(this.getSeat()[0]);
//				forkLeft = this.getTable().getLeftNeighbourForkOfSeat(this.getSeat()[0]);
//			} catch (RemoteException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			//jetzt �ber die einzelnen Pl�tze des lokalen Tisches iterieren
			try {
				for (int i = 0; i < this.getTable().getNumberOfSeats() - 1 && !wasSuccessful; i ++) {
					wasSuccessful = this.tryToGetForks();
					if (!wasSuccessful) {
						System.out.println(this.getPhilosopherName() + " konnte nicht essen und versucht es am Nachbarplatz.");
						//this.unblockFork(forkRight);
						this.setSeatNumber(this.table.getLeftNeighbour(this.getSeatNumber()).getNumber());
						this.setTabelNumber(this.getTable().getNumber());
						System.out.println(this.getPhilosopherName() + " betrachtet Sitznummer " + this.getSeatNumber());
//						forkRight = this.getSeat().getForkRight();
//						forkLeft = this.table.getLeftNeighbour(this.getSeat()).getForkRight();
					}
				}
				if (!wasSuccessful) {
					//this.unblockFork(forkRight);
					System.out.println(this.getPhilosopherName() + " konnte nicht an seinem Tisch essen und versucht es am Nachbartisch.");
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//jetzt am ganzen Tisch versuchen, wenn noch nicht gegessen
			if (!wasSuccessful) {
				try {
					String nameAnotherTable = connectionHelper.getAnotherTable(this.getTable().getName());
					//jetzt aus der Registry den anderen Tisch holen
					Registry registry = LocateRegistry.getRegistry();
					try {
						this.setStubAnotherTable((ITable) registry.lookup(nameAnotherTable));
					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(this.getPhilosopherName() + " hat sich einen neuen Tisch gesucht mit der Nummer " + getStubAnotherTable().getNumber());
					
					//ISeat tmp = connectionHelper.getAnySeatFormAllTables();
					
					//Achtung, der Tisch kann null sein, wenn nur ein Tisch angemeldet ist
					if (this.getStubAnotherTable() != null) {
						this.setSeatNumber(this.getStubAnotherTable().getAnySeat().getNumber());
						this.setTabelNumber(this.getStubAnotherTable().getNumber());
						System.out.println(this.getPhilosopherName() + " betrachtet Sitznummer " + this.getSeatNumber() + " an Tisch " + this.getTableNumber());
					} else {
						this.setSeatNumber(this.getTable().getAnySeat().getNumber());
						this.setTabelNumber(this.getTable().getNumber());
						System.out.println("Es gibt keinen anderen Tisch, " + this.getPhilosopherName() + "muss es an seinem Tisch weiter versuchen.");
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// dem Philosophen wurde jetzt irgendein Platz zugewiesen
				// wenn er an diesem Platz nicht essen kann, stellt er sich in die Queue an
//				try {
//					forkRight = this.getSeat().getForkRight();
//					System.out.println(this.getPhilosopherName() + " hat die rechte Gabel von " + forkRight.getSeat().toString());
//					forkLeft = this.table.getLeftNeighbour(this.getSeat()).getForkRight();
//					forkLeft = this.connectionHelper.getLeftNeighbourFormAllTables(this.getSeat()).getForkRight();
//					System.out.println(this.getPhilosopherName() + " hat die linke Gabel von " + forkLeft.getSeat().toString());
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
				wasSuccessful = this.tryToGetForks();
				if (!wasSuccessful) {
					System.out.println(this.getPhilosopherName() + " konnte nicht essen und muss auf den Platz warten.");
					// jetzt den Philosoph in die Queue einreihen
					if (this.getStubAnotherTable() != null) {
						try {
							this.getStubAnotherTable().getSeat(this.getSeatNumber()).getForkRight().addPhilosopher(this);
							System.out.println(this.getPhilosopherName() + " wartet, bis er essen kann.");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						try {
							this.getTable().getSeat(this.getSeatNumber()).getForkRight().addPhilosopher(this);
							System.out.println(this.getPhilosopherName() + " wartet, bis er essen kann.");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
//					this.unblockFork(forkRight);
//					try {
//						System.err.println(this.getPhilosopherName() + " wartet, bis er essen kann.\n");
//						forkRight.addPhilosopher(this);
//					} catch (RemoteException e) {
//						e.printStackTrace();
//					}
				}
			}
		}
	}
	
//	private boolean tryToGetForks() {
//		boolean hasEaten = false;
//		boolean hasGotForkRight = false;
//		boolean hasGotForkLeft = false;
//		
//		try {
//			//nimmt sich die rechte Gabel, wenn sie frei ist
//			synchronized (this.getTable().getForkOfSeat(this.getSeatNumber())) {
//				System.out.println(this.getTable().getSeat(this.getSeatNumber()));
//				System.out.println(this.getTable().getForkOfSeat(this.getSeatNumber()));
//				if (!this.getTable().getForkOfSeat(this.getSeatNumber()).isUsed()) {
//					hasGotForkRight = this.reserveForkRight();
//					System.out.println(this.getPhilosopherName() + " hat die rechte Gabel erfolgreich bekommen.");
//				}
//			}
//			//wenn die rechte Gabel bekommen, dann auch die linke versuchen
//			if (hasGotForkRight) {
//				synchronized (this.getTable().getLeftNeighbourForkOfSeat(this.getSeatNumber())) {
//					if (!this.getTable().getLeftNeighbourForkOfSeat(this.getSeatNumber()).isUsed()) {
//						hasGotForkLeft = this.reserveForkLeft();
//						System.out.println(this.getPhilosopherName() + " hat die linke Gabel erfolgreich bekommen.");
//					}
//				}
//				
//				if (hasGotForkLeft) {
//					this.eat();
//					hasEaten = true;
//					//in der Methode eat() werden nach dem Essvorgang die Gabeln wieder freigegeben
//				} else {
//					//versuchen, die linke Gabel zu nehmen
//					System.out.println(this.getPhilosopherName() + " versucht, die linke Gabel zu nehmen.");
//					for (int j = 0; j < 4 && !hasEaten; j++) {
//						synchronized (this.getTable().getLeftNeighbourForkOfSeat(this.getSeatNumber())) {
//							if (!this.getTable().getLeftNeighbourForkOfSeat(this.getSeatNumber()).isUsed()) {
//								hasGotForkLeft = this.reserveForkLeft();
//								System.out.println(this.getPhilosopherName() + " hat die linke Gabel erfolgreich bekommen.");
//							}	
//						}
//						if (hasGotForkLeft) {
//							this.eat();
//							hasEaten = true;
//						}
//						 else {
//							try {
//								System.out.println(this.getPhilosopherName() + " hat die linke Gabel nicht bekommen.");
//								Thread.sleep(1000);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//					if (!hasEaten) {
//						System.out.println(this.getPhilosopherName() + " hat die linke Gabel VIERMAL nicht bekommen.");
//						//wenn die linke Gabel nicht da, dann muss ich die rechte wieder freigeben
//						this.unblockForkRight();
//					}
//				}
//			} else {
//				System.out.println(this.getPhilosopherName() + " hat die rechte Gabel nicht bekommen.");
//			}
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return hasEaten;
//	}
	
//	/** In dieser Methode isst der Philosoph.
//	 * Achtung: synchronized-Bl�cke in reserveFork()
//	 * und in unblockFork.
//	 * @param forkRight
//	 * @param forkLeft
//	 */
//	private void eat() {
//		//dann kann der Philosoph essen
//		//this.reserveFork(forkLeft);
//		try {
//			whichTabel(this.getTable().getForkOfSeat(this.getSeatNumber()), this.getTable().getLeftNeighbourForkOfSeat(this.getSeatNumber()));
//		} catch (RemoteException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		try {
//			Thread.sleep(200);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		this.setProcess(this.getProcess() + 1);
//		this.setHungry(false);
//		this.unblockForkRight();
//		this.unblockForkLeft();
//		//auf unzul�ssigen Wert setzen
//		this.setSeatNumber(-1);
//		this.setTabelNumber(-1);
//		System.out.println(this.getPhilosopherName() + " ist fertig und geht meditieren.");
//		//jetzt kann der Philosoph wieder meditieren gehen
//	}
	
//	private void whichTabel(final IFork forkRight, final IFork forkLeft) throws RemoteException {
//		int tableRight = forkRight.getSeat().getTable().getNumber();
//		int tableLeft = forkLeft.getSeat().getTable().getNumber();
//		String output = "";
//		
//		
//		if(tableRight == this.table.getNumber() && tableLeft == this.table.getNumber()) {
//			output += "LOKAL: ";
//		} else {
//			output += "REMOTE: ";
//		}
//		
//		if (tableRight == tableLeft) {
//			output += this.getPhilosopherName() + " isst an Sitzplatz " + forkRight.getSeat().getNumber() + " an Tisch " + tableRight;
//		}else {
//			output += this.getPhilosopherName() + " isst an Tisch " + tableRight + " mit der rechten Gabel.";
//		}
//		System.out.println(output);
//	}

//	/**
//	 * Setzt den �bergeben Wert.
//	 * Liefert true, wenn der Wert getoggelt wurde, ansonsten false.
//	 * @param forkRight
//	 * @return
//	 */
//	private boolean unblockForkRight() {
//		boolean result = false;
//		boolean previously;
//		try {
//			previously = this.getTable().getForkOfSeat(this.getSeatNumber()).getAndSetUsed(false, this);
//			result = !(previously && this.getTable().getForkOfSeat(this.getSeatNumber()).isUsed());
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return result;
//	}
	
//	private boolean unblockForkLeft() {
//		boolean result = false;
//		boolean previously;
//		try {
//			previously = this.getTable().getLeftNeighbourForkOfSeat(this.getSeatNumber()).getAndSetUsed(false, this);
//			result = !(previously && this.getTable().getLeftNeighbourForkOfSeat(this.getSeatNumber()).isUsed());
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return result;
//	}

//	/**
//	 * Setzt den �bergeben Wert.
//	 * Liefert true, wenn der Wert getoggelt wurde, ansonsten false.
//	 * @param forkRight
//	 * @return
//	 */
//	private boolean reserveForkRight() {
//		boolean previously;
//		boolean result = false;
//		try {
//			previously = this.getTable().getForkOfSeat(this.getSeatNumber()).getAndSetUsed(true, this);
//			result = !(previously && this.getTable().getForkOfSeat(this.getSeatNumber()).isUsed());
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return result;
//	}
	
//	private boolean reserveForkLeft() {
//		boolean previously;
//		boolean result = false;
//		try {
//			previously = this.getTable().getLeftNeighbourForkOfSeat(this.getSeatNumber()).getAndSetUsed(true, this);
//			result = !(previously && this.getTable().getLeftNeighbourForkOfSeat(this.getSeatNumber()).isUsed());
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return result;
//	}

}