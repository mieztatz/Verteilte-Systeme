package DiningPhilosophersDistribute2;

import java.rmi.RemoteException;
import java.util.Random;

/**
 * Hochschule für angewandte Wissenschaften München
 * Verteilte Softwaresysteme - Praktikum
 * WS 2015/16
 * Aufgabe 3.3 Parallele Programmierung - Programm "Speisende Philosophen"
 * @author Diana irmscher - diana.irmscher@hm.edu
 */

public class Philosopher extends Thread {
	
	/** Anzahl der Essvprgäng, welcher ein Philospoh bereits hatte.
	 *  Nach drei Essvorgängen wird der Integer wieder auf Null gesetzt.
	 *  Der Philospoh isst dann für eine längere Zeit nicht.
	 */
	private int process;
	
	/** Wenn der Philosoph isst, sitzt er temporär an einem Platz.
	 *  Dieser Platz hat eine rechte Gabel.
	 */
	private ISeat seat;
	
	/** Der Name eines Philosophen **/
	private final String name;
	
	/** Diese Variable gibt an, ob der Philosoph Hunger hat oder nicht. **/
	private boolean isHungry;
	
	private final boolean isVeryHungry;
	
	/** Jeder Philosoph kennt den Tisch, an dem er sitzt
	 * und kann somit jederzeit seinen linken und rechten Sitznachbarn ansprechen. **/
	private final ITable table;
	
	public Philosopher(final String name, final ITable table, final boolean isVeryHungry) {
		this.name = name;
		this.process = 0;
		this.isHungry = true;
		this.table = table;
		this.isVeryHungry = isVeryHungry;
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
	
	@Override
	public void run() {
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
						// TODO Auto-generated catch block
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

	private void eat(IFork forkRight, IFork forkLeft) {
		//dann kann der Philosoph essen
		this.reserveFork(forkLeft);
		System.out.println(this.getPhilosopherName() + " isst.");
		try {
			Thread.sleep(5000);
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

	/**
	 * Setzt den übergeben Wert.
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
	 * Setzt den übergeben Wert.
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