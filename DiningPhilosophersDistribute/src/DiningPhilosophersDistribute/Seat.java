package DiningPhilosophersDistribute;

public class Seat implements ISeat {
	
	private static final long serialVersionUID = -8600807833617312109L;

	private final int number;
	private final IFork forkRight;
	private final ITable table;
	
	public Seat(final int number, final ITable table) {
		this.number = number;
		this.table = table;
		this.forkRight = new Fork(this);
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public ITable getTable() {
		return this.table;
	}
	
	public IFork getForkRight() {
		return this.forkRight;
	}

}
