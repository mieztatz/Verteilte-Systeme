package DiningPhilosophers;

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
	
	private final String name;
	
	private boolean isHungry;
	
	public boolean isHungry() {
		return isHungry;
	}
	
	private Table table;

	public void setHungry(boolean isHungry) {
		this.isHungry = isHungry;
	}

	public Philosopher(final String name, final Table table) {
		this.name = name;
		this.process = 0;
		this.isHungry = true;
		this.table = table;
	}
	
	public void run() {
		System.out.println(this.name + " ist aktiv.");
		while(this.isHungry) {
			try {
				this.eat();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	public synchronized void eat() throws InterruptedException {
//		Seat seat = table.getFirstSeat();
		Seat seat = table.getAnySeat();
		System.out.println(this.getPhilosopherName() + " betrachtet Sitznummer " + seat.getNumber());
		
		for (int i = seat.getNumber(); i < table.getNumberOfSeats() && this.isHungry(); i = (i + 1 ) % table.getNumberOfSeats()) {
			if (!seat.isSeatInUse()) {
				if (!table.getLeftNeighbour(seat).getRightFork().isUsed()) {
					this.setForkRight(seat.getRightFork());
					this.setForkLeft(table.getLeftNeighbour(seat).getRightFork());
					System.out.println(this.getPhilosopherName() + " isst auf Platz " + seat.getNumber() + ".");
					this.wait(5000);
					this.setProcess(this.getProcess() + 1);
					this.setHungry(false);
					//Sitz wieder freigeben
					this.setForkRight(null);
					this.setForkLeft(null);
					seat.getRightFork().setUsed(false);
					table.getLeftNeighbour(seat).getRightFork().setUsed(false);
				}
			} else {
				seat = table.getLeftNeighbour(seat);
			}
				
		}
	}

	
	

}
