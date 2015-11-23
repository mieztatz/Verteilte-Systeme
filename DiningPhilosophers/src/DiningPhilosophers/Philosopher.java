package DiningPhilosophers;

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
	private Seat seat;
	
	/** Der Name eines Philosophen **/
	private final String name;
	
	/** Diese Variable gibt an, ob der Philosoph Hunger hat oder nicht. **/
	private boolean isHungry;
	
	/** Jeder Philosoph kennt den Tisch, an dem er sitzt
	 * und kann somit jederzeit seinen linken und rechten Sitznachbarn ansprechen. **/
	private final Table table;
	
	public Philosopher(final String name, final Table table) {
		this.name = name;
		this.process = 0;
		this.isHungry = true;
		this.table = table;
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
	
	public Seat getSeat() {
		return this.seat;
	}
	
	public void setSeat(final Seat seat) {
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
				try {
					this.sleep((long)tmp);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.setHungry(true);
			}
			/** *********** Essen *********** **/
			if (this.getSeat() == null) {
				this.setSeat(this.table.getAnySeat());
			}
			System.out.println(this.getPhilosopherName() + " betrachtet Sitznummer " + seat.getNumber());
			//Gabeln lokal zwischenspeichern
			Fork forkRight = this.getSeat().getForkRight();
			Fork forkLeft = this.table.getLeftNeighbour(this.getSeat()).getForkRight();
			
			// Anfrage, ob rechte Gabel frei ist
			if (!forkRight.isUsed()) {
				System.out.println(this.getPhilosopherName() + " nimmt die rechte Gabel in die Hand.");
				//rechte Gabel ist frei, dann erst mal hinsetzen
				this.reserveFork(forkRight);
				if (!forkLeft.isUsed()) {
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
					//jetzt kann der Philosoph wieder meditiere gehen
				} else {
					System.out.println(this.getPhilosopherName() + " versucht, die linke Gabel zu nehmen.");
					boolean wasSuccessful = false;
					for (int i = 0; i < 4 && !wasSuccessful; i++) {
						System.out.println(this.getPhilosopherName() + " versucht zum " + i + ". Mal, die Gabel zu nehmen.");
						if (!forkLeft.isUsed()) {

								this.reserveFork(forkLeft);
								System.out.println(this.getPhilosopherName() + " hat die linke Gabel beim " + i + ". Versuch bekommen.");
								wasSuccessful = true;

						} else {
							try {
								System.out.println(this.getPhilosopherName() + " hat die linke Gabel nicht bekommen und muss warten.");
								this.wait(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					if (wasSuccessful) {
						//dann kann der Philosoph essen
						this.reserveFork(forkLeft);
						System.out.println(this.getPhilosopherName() + " isst.");
						try {
							this.wait(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						this.setProcess(this.getProcess() + 1);
						this.setHungry(false);
						this.unblockFork(forkRight);
						this.unblockFork(forkLeft);
						//jetzt kann der Philosoph wieder meditieren gehen
					} else {
						//nach vier Versuchen, die Gabel zu nehmen, wird nun wieder alles zurückgelgt
						//und in die Queue eingereiht
						this.unblockFork(forkRight);
						forkRight.addPhilosopher(this);
						System.out.println(this.getPhilosopherName() + " wartet, bis er essen kann.");
					}
				}
			} else {
				forkRight.addPhilosopher(this);
				System.out.println(this.getPhilosopherName() + "wartet, bis er essen kann.");
			}
		}
	}

	private void unblockFork(Fork fork) {
		fork.setUsed(false);
	}

	private void reserveFork(Fork forkRight) {
		forkRight.setUsed(true);
	}

}