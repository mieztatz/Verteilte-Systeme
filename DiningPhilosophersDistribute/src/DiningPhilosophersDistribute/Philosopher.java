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
	public void runOne() {
		System.out.println(this.name + " ist aktiv.");
		
		while(true) {
			//immer der gleiche Prozess, die Philosophen sollen essen und meditieren.
			
			/** *********** MEDITIEREN *********** **/
			if (!this.isHungry()) {
				Random r = new Random();
				double tmp = r.nextInt(10000);
				System.out.println(this.getPhilosopherName() + " meditiert für " + tmp/1000 + " Sekunden.");
				if (this.isVeryHungry) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep((long)tmp);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				this.setHungry(true);
			}
			/** *********** Essen *********** **/
			if (this.getSeat() == null) {
				try {
					this.setSeat(this.table.getAnySeat());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				System.out.println(this.getPhilosopherName() + " betrachtet Sitznummer " + seat.getNumber());
			} catch (RemoteException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			//Gabeln lokal zwischenspeichern
			IFork forkRight = null;
			IFork forkLeft = null;
			try {
				forkRight = this.getSeat().getForkRight();
				forkLeft = this.table.getLeftNeighbour(this.getSeat()).getForkRight();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// Anfrage, ob rechte Gabel frei ist
			try {
				if (!forkRight.isUsed()) {
					System.out.println(this.getPhilosopherName() + " nimmt die rechte Gabel in die Hand.");
					//rechte Gabel ist frei, dann erst mal hinsetzen
					this.reserveFork(forkRight);
					try {
						if (!forkLeft.isUsed()) {
							eat(forkRight, forkLeft);
						} else {
							System.out.println(this.getPhilosopherName() + " versucht, die linke Gabel zu nehmen.");
							boolean wasSuccessful = false;
							for (int i = 0; i < 4 && !wasSuccessful; i++) {
								System.out.println(this.getPhilosopherName() + " versucht zum " + i + ". Mal, die Gabel zu nehmen.");
								try {
									if (!forkLeft.isUsed()) {
										this.reserveFork(forkLeft);
										System.out.println(this.getPhilosopherName() + " hat die linke Gabel beim " + i + ". Versuch bekommen.");
										wasSuccessful = true;
									} else {
										try {
											System.out.println(this.getPhilosopherName() + " hat die linke Gabel nicht bekommen und muss warten.");
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if (wasSuccessful) {
								eat(forkRight, forkLeft);
							} else {
								//nach vier Versuchen, die Gabel zu nehmen, wird nun wieder alles zurückgelgt
								//und in die Queue eingereiht
								this.unblockFork(forkRight);
								try {
									forkRight.addPhilosopher(this);
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								System.out.println(this.getPhilosopherName() + " wartet, bis er essen kann.");
							}
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch blockS
						e.printStackTrace();
					}
				} else {
					try {
						forkRight.addPhilosopher(this);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(this.getPhilosopherName() + "wartet, bis er essen kann.");
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		System.out.println(this.getPhilosopherName() + " ist aktiv.");
		
		while(true) {
			/** *********** MEDITIEREN *********** **/
			if (!this.isHungry()) {
				Random r = new Random();
				double tmp = r.nextInt(10000);
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
				boolean wasSuccessful = false;
				for (int i = 0; i < this.table.getNumberOfSeats() && !wasSuccessful; i ++) {
					wasSuccessful = this.tryToGetForks(forkRight, forkLeft);
					if (!wasSuccessful) {
						System.out.println(this.getPhilosopherName() + " konnte nicht essen und versucht es am Nachbarplatz.");
						this.setSeat(this.table.getLeftNeighbour(this.getSeat()));
						System.out.println(this.getPhilosopherName() + " betrachtet Sitznummer " + seat.getNumber());
						forkRight = this.getSeat().getForkRight();
						forkLeft = this.table.getLeftNeighbour(this.getSeat()).getForkRight();
					}
				}
				if (!wasSuccessful) {
					System.out.println(this.getPhilosopherName() + " konnte nicht an seinem Tisch essen und versucht es am Nachbartisch.");
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private boolean tryToGetForks(IFork forkRight, IFork forkLeft) {
		boolean hasEaten = false;
		try {
			//nimmt sich die rechte Gabel, wenn sie frei ist
			if (!forkRight.isUsed()) {
				this.reserveFork(forkRight);
				System.out.println(this.getPhilosopherName() + " nimmt die rechte Gabel in die Hand.");
				//versuchen, linke Gabel zu bekommen
				if (!forkLeft.isUsed()) {
					this.reserveFork(forkLeft);
					System.out.println(this.getPhilosopherName() + " nimmt die linke Gabel in die Hand.");
					this.eat(forkRight, forkLeft);
					hasEaten = true;
					//in der Methode eat() werden nach dem Essvorgang die Gabeln wieder freigegeben
				} else {
					//versuchen, die linke Gabel zu nehmen
					System.out.println(this.getPhilosopherName() + " versucht, die linke Gabel zu nehmen.");
					for (int j = 0; j < 4 && !hasEaten; j++) {
						if (!forkLeft.isUsed()) {
							this.reserveFork(forkLeft);
							this.eat(forkRight, forkLeft);
							hasEaten = true;
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
		this.reserveFork(forkLeft);
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
		ITable tableRight = forkRight.getSeat().getTable();
		ITable tableLeft = forkLeft.getSeat().getTable();
		String output = "";
		
		if(tableRight.equals(this.table) && tableLeft.equals(this.table)) {
			output += "LOKAL: ";
		} else {
			output += "REMOTE: ";
		}
		
		if (tableRight.equals(tableLeft)) {
			output += this.getPhilosopherName() + " isst an Tisch " + tableRight.getNumber();
		}else {
			output += this.getPhilosopherName() + " isst an Tisch " + tableRight.getNumber() + " mit der rechten Gabel.";
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