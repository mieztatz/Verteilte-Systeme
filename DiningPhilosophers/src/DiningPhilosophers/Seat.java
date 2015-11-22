package DiningPhilosophers;

public class Seat {
	
	private final int number;
	private final Fork forkRight;
	
	/** Jeder Platz hat einen Teller */
	private boolean plate;
	
	public Seat(final int number) {
		this.number = number;
		this.forkRight = new Fork();
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public Fork getForkRight() {
		return forkRight;
	}
	
	/** Wenn die Gabel des Platzes in Gebrauch ist, kann der Platz nicht mehr genutzt werden.
	 * @return true, wenn die Gabel in Gebrauch ist - ansonsten false
	 */
	public boolean isSeatInUse() {
		return forkRight.isUsed();
	}

}
