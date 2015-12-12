package DiningPhilosophersDistribute2;

public class Seat implements ISeat {
	
	private static final long serialVersionUID = -8600807833617312109L;

	private final int number;
	private final IFork forkRight;
	
	public Seat(final int number) {
		this.number = number;
		this.forkRight = new Fork();
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public IFork getForkRight() {
		return forkRight;
	}

}
