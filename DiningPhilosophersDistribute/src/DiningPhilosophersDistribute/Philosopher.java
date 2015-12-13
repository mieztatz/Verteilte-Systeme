package DiningPhilosophersDistribute;

import java.rmi.RemoteException;
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
	private ISeat seat;
	
	/** Der Name eines Philosophen **/
	private final String name;
	
	/** Jeder Philosoph kennt den Tisch, an dem er sitzt
	 * und kann somit jederzeit seinen linken und rechten Sitznachbarn ansprechen. **/
	private final ITable table;
	
	private final IConnectionHelper connectionHelper;
	
	private final boolean isVeryHungry;
	
	/** Anzahl der Essvorg�nge, welcher ein Philospoh bereits hatte. */
	private int process;
	
	/** Diese Variable gibt an, ob der Philosoph Hunger hat oder nicht. **/
	private boolean isHungry;
	
	public Philosopher(final String name, final ITable table, final IConnectionHelper connectionHelper, final boolean isVeryHungry) {
		this.name = name;
		this.table = table;
		this.connectionHelper = connectionHelper;
		this.isVeryHungry = isVeryHungry;
		
		this.process = 0;
		this.isHungry = true;
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
	
	public ISeat getSeat() {
		return this.seat;
	}
	
	public void setSeat(final ISeat seat) {
		this.seat = seat;
	}

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
				double tmp = r.nextInt(1000);
				if (this.isVeryHungry) {
					try {
						Thread.sleep(10);
						System.out.println(this.getPhilosopherName() + "ist sehr hungrig und meditiert nur " + 10/1000 + " Sekunden.");
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
			//erste Sitzplatz zuweisen lassen
			if (this.getSeat() == null) {
				try {
					this.setSeat(this.table.getAnySeat());
					System.out.println(this.getPhilosopherName() + " betrachtet Sitznummer " + seat.getNumber());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//Gabeln lokal merken
			IFork forkRight = null;
			IFork forkLeft = null;
			try {
				forkRight = this.getSeat().getForkRight();
				forkLeft = this.table.getLeftNeighbour(this.getSeat()).getForkRight();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//jetzt �ber die einzelnen Pl�tze des lokalen Tisches iterieren
			try {
				for (int i = 0; i < this.table.getNumberOfSeats() && !wasSuccessful; i ++) {
					wasSuccessful = this.tryToGetForks(forkRight, forkLeft);
					if (!wasSuccessful) {
						System.out.println(this.getPhilosopherName() + " konnte nicht essen und versucht es am Nachbarplatz.");
						this.unblockFork(forkRight);
						this.setSeat(this.table.getLeftNeighbour(this.getSeat()));
						System.out.println(this.getPhilosopherName() + " betrachtet Sitznummer " + seat.getNumber());
						forkRight = this.getSeat().getForkRight();
						forkLeft = this.table.getLeftNeighbour(this.getSeat()).getForkRight();
					}
				}
				if (!wasSuccessful) {
					this.unblockFork(forkRight);
					System.out.println(this.getPhilosopherName() + " konnte nicht an seinem Tisch essen und versucht es am Nachbartisch.");
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//jetzt am ganzen Tisch versuchen, wenn noch nicht gegessen
			if (!wasSuccessful) {
				try {
					this.setSeat(connectionHelper.getAnySeatFormAllTables());
					System.out.println(this.getPhilosopherName() + " betrachtet Sitznummer " + seat.getNumber() + " an Tisch " + seat.getTable().getNumber());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// dem Philosophen wurde jetzt irgendein Platz zugewiesen
				// wenn er an diesem Platz nicht essen kann, stellt er sich in die Queue an
				try {
					forkRight = this.getSeat().getForkRight();
					forkLeft = this.table.getLeftNeighbour(this.getSeat()).getForkRight();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				wasSuccessful = this.tryToGetForks(forkRight, forkLeft);
				if (!wasSuccessful) {
					System.out.println(this.getPhilosopherName() + " konnte nicht essen und muss auf den Platz warten.");
				    // jetzt den Philosoph in die Queue einreihen
					this.unblockFork(forkRight);
					try {
						forkRight.addPhilosopher(this);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					System.out.println(this.getPhilosopherName() + " wartet, bis er essen kann.");
				}
			}
		}
	}
	
	private boolean tryToGetForks(IFork forkRight, IFork forkLeft) {
		boolean hasEaten = false;
		boolean hasGotForkRight = false;
		boolean hasGotForkLeft = false;
		try {
			//nimmt sich die rechte Gabel, wenn sie frei ist
			if (!forkRight.isUsed()) {
				hasGotForkRight = this.reserveFork(forkRight);
				System.out.println(this.getPhilosopherName() + "----------------------------" + hasGotForkRight);
				if (hasGotForkRight) {
					System.out.println(this.getPhilosopherName() + " nimmt die rechte Gabel in die Hand.");
					//versuchen, linke Gabel zu bekommen
					if (!forkLeft.isUsed()) {
						hasGotForkLeft = this.reserveFork(forkLeft);
						if (hasGotForkLeft) {
							System.out.println(this.getPhilosopherName() + " nimmt die linke Gabel in die Hand.");
							this.eat(forkRight, forkLeft);
							hasEaten = true;
						}
						//in der Methode eat() werden nach dem Essvorgang die Gabeln wieder freigegeben
					} else {
						//versuchen, die linke Gabel zu nehmen
						System.out.println(this.getPhilosopherName() + " versucht, die linke Gabel zu nehmen.");
						for (int j = 0; j < 4 && !hasEaten; j++) {
							if (!forkLeft.isUsed()) {
								hasGotForkLeft = this.reserveFork(forkLeft);
								if (hasGotForkLeft) {
									System.out.println(this.getPhilosopherName() + " nimmt die linke Gabel in die Hand.");
									this.eat(forkRight, forkLeft);
									hasEaten = true;
								}
							} else {
								try {
									System.out.println(this.getPhilosopherName() + " hat die linke Gabel nicht bekommen.");
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			} else {
				System.out.println(this.getPhilosopherName() + " hat die rechte Gabel nicht bekommen.");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hasEaten;
	}
	
	/** In dieser Methode isst der Philosoph.
	 * Achtung: synchronized-Bl�cke in reserveFork()
	 * und in unblockFork.
	 * @param forkRight
	 * @param forkLeft
	 */
	private void eat(IFork forkRight, IFork forkLeft) {
		//dann kann der Philosoph essen
		//this.reserveFork(forkLeft);
		try {
			whichTabel(forkRight, forkLeft);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.setProcess(this.getProcess() + 1);
		this.setHungry(false);
		this.unblockFork(forkRight);
		this.unblockFork(forkLeft);
		this.setSeat(null);
		//jetzt kann der Philosoph wieder meditieren gehen
	}
	
	private void whichTabel(final IFork forkRight, final IFork forkLeft) throws RemoteException {
		int tableRight = forkRight.getSeat().getTable().getNumber();
		int tableLeft = forkLeft.getSeat().getTable().getNumber();
		String output = "";
		
		
		if(tableRight == this.table.getNumber() && tableLeft == this.table.getNumber()) {
			output += "LOKAL: ";
		} else {
			output += "REMOTE: ";
		}
		
		if (tableRight == tableLeft) {
			output += this.getPhilosopherName() + " isst an Sitzplatz " + forkRight.getSeat().getNumber() + " an Tisch " + tableRight;
		}else {
			output += this.getPhilosopherName() + " isst an Tisch " + tableRight + " mit der rechten Gabel.";
		}
		System.out.println(output);
	}

	/**
	 * Setzt den �bergeben Wert.
	 * Liefert true, wenn der Wert getoggelt wurde, ansonsten false.
	 * @param forkRight
	 * @return
	 */
	private boolean unblockFork(IFork forkRight) {
		boolean result = false;
		boolean previously;
		try {
			previously = forkRight.getAndSetUsed(false);
			result = !(previously && forkRight.isUsed());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Setzt den �bergeben Wert.
	 * Liefert true, wenn der Wert getoggelt wurde, ansonsten false.
	 * @param forkRight
	 * @return
	 */
	private boolean reserveFork(IFork forkRight) {
		boolean previously;
		boolean result = false;
		try {
			previously = forkRight.getAndSetUsed(true);
			result = !(previously && forkRight.isUsed());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}