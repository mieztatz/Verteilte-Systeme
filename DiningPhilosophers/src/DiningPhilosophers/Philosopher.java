package DiningPhilosophers;

import java.util.Random;

import com.sun.swing.internal.plaf.synth.resources.synth;

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
	
	/** Wenn ein Philosoph einen Platz bekommen hat, dann hat er automatisch auch eine rechte Gabel. */
	private Fork forkLeft;
	
	/** Nachdem ein Philosoph eine rechte Gabel hat, muss er versuchen, von seinem Nachbarn die Gabel zu bekommen. */
	private Fork forkRight;
	
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
	/**
	 * Die überschriebene Run-Methode gibt auf der Konsole aus, dass der
	 * Thread bzw. Philosoph aktiv ist.
	 * Gleich zu Beginn des Programms wird der Philosoph zum Meditieren geschickt.
	 */
	@Override
	public void run() {
		System.out.println(this.name + " ist aktiv.");
		try {
			this.goIntoMeditationRoom();
			//this.eat();
		} catch (InterruptedException e) {
			//Exceptionhandling hier besser lösen
			e.printStackTrace();
		}
	}
	
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

	public Fork getForkLeft() {
		return forkLeft;
	}

	public void setForkLeft(final Fork forkLeft) {
			this.forkLeft = forkLeft;
	}

	public Fork getForkRight() {
		return forkRight;
	}

	public void setForkRight(final Fork forkRight) {
		this.forkRight = forkRight;
	}
	
	public void reserveFork(final Fork fork) {
		fork.setUsed(true);
	}
	
	/**
	 * Wenn die Gabel wieder frei gelassen wird, kann ein
	 * anderer Philosoph essen. Dafür mit dieser über die Gabel aktiviert werden.
	 * Das kann nur der aktuelle Thread, da dieser über den Monitur verfügt.
	 * @param fork
	 */
	public void unblockFork(final Fork fork) {
		fork.setUsed(false);
	}
	
	/** Philosophen sitzen den ganzen Tag im Meditationsraum.
	 * Bekommt ein Philosoph Hunger, betritt er den Esssaal.
	 * @throws InterruptedException
	 */
	public void meditationRoom(final long millis) throws InterruptedException {
		Thread.sleep(millis);
	}
	
	/** Hier die Logik für die Platzvergabe einfügen
	 * @throws InterruptedException 
	 * 
	 */
	public void eat() throws InterruptedException {
		this.setSeat(this.table.getAnySeat());
		System.out.println(this.getPhilosopherName() + " betrachtet Sitznummer " + seat.getNumber());
		Fork forkRight = this.getSeat().getForkRight();
		Fork forkLeft = this.table.getLeftNeighbour(this.getSeat()).getForkRight();
//		boolean isWaiting = false;
		
		// alles wird über Gabeln abgefragt, ist die rechte Gabel frei, und die Gabel
		// linken Nachbarplatz auch, dann kann der Philosoph essen
		if (!forkRight.isUsed()) {
			System.out.println(this.getPhilosopherName() + " nimmt die rechte Gabel in die Hand.");
			//rechte Gabel ist frei, dann erst mal hinsetzen
			this.reserveFork(forkRight);
			if (!forkLeft.isUsed()) {
				//der Philosoph kann essen, da beide Gabeln frei sind
				philosopherCanEat(forkRight, forkLeft);
			} else {
				//der Philosoph muss warten, bis die linke Gabel frei ist
				System.out.println(this.getPhilosopherName() + " versucht, die linke Gabel zu nehmen.");
				if (tryToGetLeftFork(forkLeft)) {
					philosopherCanEat(forkRight, forkLeft);
				} else {
					//nach vier Versuchen, die Gabel zu nehmen, wird nun wieder alles zurückgelgt
					//und in die Queue eingereiht
					this.unblockFork(forkRight);
					forkRight.addPhilosopher(this);
					System.out.println(this.getPhilosopherName() + " wartet, bis er essen kann.");
				}
			}
		} else {
			this.unblockFork(forkRight);
			forkRight.addPhilosopher(this);
			System.out.println(this.getPhilosopherName() + "wartet, bis er essen kann.");
		}
	}
	
	public void goIntoMeditationRoom() throws InterruptedException {
		Random r = new Random();
		double tmp = r.nextInt(10000);
		System.out.println(this.getPhilosopherName() + " meditiert für " + tmp/1000 + " Sekunden.");
		this.sleep((long)tmp);
		this.setHungry(true);
		this.eat();
	}
	
	/**
	 * Rekursive Methode, um die linke Gabel zu bekommen.
	 *
	 * @param forkLeft
	 */
	public boolean tryToGetLeftFork(final Fork forkLeft) {
		boolean wasSuccessful = false;
		for (int i = 0; i < 4 && !wasSuccessful; i++) {
			System.out.println(this.getPhilosopherName() + " versucht zum " + i + ". Mal, die Gabel zu nehmen.");
			if (!forkLeft.isUsed()) {
				synchronized(this){
					this.reserveFork(forkLeft);
					System.out.println(this.getPhilosopherName() + " hat die linke Gabel beim " + i + ". Versuch bekommen.");
					wasSuccessful = true;
				}
			} else {
				try {
					System.out.println(this.getPhilosopherName() + " hat die linke Gabel nicht bekommen und muss warten.");
					this.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return wasSuccessful;
	}
	
	public void philosopherCanEat(final Fork forkRight, final Fork forkLeft) {
		//die rechte Gabel ist bereits reserviert
		this.reserveFork(forkLeft);
		
		this.setForkRight(forkRight);
		this.setForkLeft(forkLeft);
		
		//essen
		try {
			System.out.println(this.getPhilosopherName() + " isst.");
			this.wait(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		this.setProcess(this.getProcess() + 1);
		this.setHungry(false);
		
		this.setForkRight(null);
		this.setForkLeft(null);
		
		//Gabeln wieder freigeben
		this.unblockFork(forkRight);
		this.unblockFork(forkLeft);
		
		//meditieren gehen
		try {
			this.goIntoMeditationRoom();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	

}
