package DiningPhilosophersDistribute;

import java.rmi.RemoteException;
import java.util.Random;

public class Table implements ITable {
	
	private static final long serialVersionUID = -6908684959619272507L;
	
	private static final int DEFAULT_NUMBER_OF_SEATS = 10;
	
	private final Seat[] seats;
	private final int numberOfSeats;
	private final int number;
	private final String name;
	private final IConnectionHelper connectionhelper;
	private ITable neighbour;

	public Table(final int numberOfSeats, final int number, final String name, final IConnectionHelper connectionHelper) {
		this.numberOfSeats = numberOfSeats;
		this.number = number;
		this.name = name;
		this.connectionhelper = connectionHelper;
		if (numberOfSeats >= 0) {
			seats = new Seat[numberOfSeats];
		} else {
			seats = new Seat[DEFAULT_NUMBER_OF_SEATS];
		}
		initSeats();
		neighbour = this;
	}
	
	public int getNumber() {
		return number;
	}
	
	public String getName() {
		return this.name;
	}
	
	private void initSeats() {
		for (int i = 0; i < seats.length; i++) {
			seats[i] = new Seat(i, this);
		}
	}
	
	public ITable getNeighbourTable() {
		return this.neighbour;
	}
	
	public void setNeighbourTable(final ITable neighbour) {
		this.neighbour = neighbour;
	}
	
//	public ISeat getSeat(int number) {
//		return this.getSeats()[number];
//	}
	
//	public int getNumberOfSeats() {
//		return numberOfSeats;
//	}
	
	public Seat[] getSeats() {
		return this.seats;
	}
	
//	/** 
//	 * Gibt den linken Nachbarn des �bergebenen Platzes zur�ck.
//	 * Falls der �bergebene Platz nicht am Tisch ist, wird NULL zur�ckgegeben.
//	 * Tische sind miteinander verlinkt. Die linke Gable ist immer die Gabel
//	 * des linken Tischnachbarn. Das hiesst, der Platz im Array an Stelle 0
//	 * verlinkt auf das letzte Element des anderen Tisches, falls es einen gibt.
//	 * @param other - der eigene Platz
//	 * @return seat - der links benachbarte Platz
//	 */
//	public ISeat getLeftNeighbourFormWholeTable(final ISeat other) throws RemoteException {
//		ISeat seat = null;
//		if(other.getNumber() < seats.length && other.getNumber() >= 0) {
//			if(other.getNumber() == 0) { // hier muss zum anderen Tisch verlinkt werden
//				//pr�fen, ob es mehr als einen Tisch gibt und dieser Tisch enthalten ist
//				if (connectionhelper.getNumberOfTables() > 1 && connectionhelper.containsTable(this)) {
//					if (this.getNumber() == 0) {
//						seat = connectionhelper.getTable(connectionhelper.getNumberOfTables() - 1).getLastSeatOfTable();
//					} else {
//						seat = connectionhelper.getTable(this.getNumber() -1).getLastSeatOfTable();
//					}
//				}
//			} else {
//				seat = seats[other.getNumber()- 1];
//			}
//		}
//		return seat;
//	}
	
	/**
	 * Gibt den linken Nachbarn des Tisches zur�ck.
	 */
	public Seat getLeftNeighbour(final Seat seat) {
//		if (seat.getNumber() == seats.length - 1) {
//			return null;
//		}
		return seats[(seat.getNumber() + 1) % seats.length];
	}
	
//	public IFork getForkOfSeat(final int number) {
//		IFork fork = null;
//		try {
//			return this.getSeats()[number].getForkRight();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return fork;
//	}
	
//	public IFork getLeftNeighbourForkOfSeat(final int seat) {
//		IFork fork = null;
//		try {
//			return this.getLeftNeighbour(seat).getForkRight();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return fork;
//	}
	
//	/**
//	 * Gibt den rechten Nachbarn des �bergebenen Platzes zur�ck.
//	 * Falls der �bergeben Platz nicht am Tisch ist, wird NUll zur�ckgegeben.
//	 * @param other - der eigene Platz
//	 * @return seat - der rechts benachbarte Platz
//	 */
//	public ISeat getRightNeighbour(final ISeat other) {
//		ISeat seat = null;
//		try {
//			if(other.getNumber() < seats.length && other.getNumber() >= 0) {
//				if(other.getNumber() == seats.length-1) {
//					seat = seats[0];
//				} else {
//					seat = seats[other.getNumber() + 1];
//				}
//			}
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return seat;
//	}
	
//	public ISeat getFirstSeat() {
//		return seats[0];
//	}
	
	public Seat getAnySeat() {
		Random r = new Random();
		int tmp = r.nextInt(numberOfSeats);
		return seats[tmp];
	}
	
//	public ISeat getLastSeatOfTable() {
//		return seats[seats.length -1];
//	}

//	public boolean isForkInUse(final Fork fork) {
//		boolean isUsed = false;
//		try {
//			return this.getSeats()[fork.getSeat().getNumber()].getForkRight().isUsed();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return isUsed;
//	}
	
	@Override
	public boolean tryToEat(final Philosopher philosopher) throws RemoteException {
		Seat seat = this.getAnySeat();
		System.out.println(philosopher.getPhilosopherName() + " betrachtet Sitznummer " + seat.getNumber() + " an Tisch " + this.getNumber());
		return tryToEat(philosopher, seat);
	}

	/**
	 * Hier ist der Sitzplatz bereits bekannt.
	 * @param philosopher
	 * @param seat
	 * @return
	 */
	private boolean tryToEat(final Philosopher philosopher, Seat seat) {
		boolean wasSuccessful = false;
		//Gabeln merken
		Fork forkRight = seat.getForkRight();
		
		//wenn nicht der letzte Sitzplatz
		if (seat.getNumber() < seats.length-1) {
			Fork forkLeft = this.getLeftNeighbour(seat).getForkRight();
			
			for(int i = 0; i < this.seats.length - 1 && !wasSuccessful; i++) {
				wasSuccessful = tryToGetForks(forkRight, forkLeft, philosopher);
				if (!wasSuccessful) {
					System.out.println(philosopher.getPhilosopherName() + " konnte nicht essen und versucht es am Nachbarplatz.");
					seat = this.getLeftNeighbour(seat);
					forkRight = seat.getForkRight();
					forkLeft = this.getLeftNeighbour(seat).getForkRight();
					System.out.println(philosopher.getPhilosopherName() + " betrachtet Sitznummer " + seat.getNumber());
				
				}
			}
		} else {
			for(int i = 0; i < this.seats.length - 1 && !wasSuccessful; i++) {
				wasSuccessful = tryToGetForks(forkRight, philosopher);
				if (!wasSuccessful) {
					System.out.println(philosopher.getPhilosopherName() + " konnte nicht essen und versucht es am Nachbarplatz.");
					seat = this.getLeftNeighbour(seat);
					forkRight = seat.getForkRight();
					System.out.println(philosopher.getPhilosopherName() + " betrachtet Sitznummer " + seat.getNumber());
				}
			}
		}
		if (!wasSuccessful) {
			System.out.println(philosopher.getPhilosopherName() + " konnte nicht an seinem Tisch essen und versucht es am Nachbartisch.");
		}
		return wasSuccessful;
	}
	
	public boolean reserveFork(final Fork fork, final Philosopher philosopher) {
		boolean previously;
		boolean result = false;
		previously = fork.getAndSetUsed(true, philosopher);
		result = !(previously && fork.isUsed());
		return result;
	}
	
	public boolean unblockFork(final Fork fork, final Philosopher philosopher) {
		boolean result = false;
		boolean previously;
		previously = fork.getAndSetUsed(false, philosopher);
		result = !(previously && fork.isUsed());
		return result;
	}
	
	public boolean tryToGetForks(final Fork forkRight, final Philosopher philosopher) {
		boolean hasEaten = false;
		boolean hasGotForkRight = false;
		boolean hasGotForkLeft = false;
		
		if (!forkRight.isUsed()) {
			hasGotForkRight = this.reserveFork(forkRight, philosopher);
			System.out.println(philosopher.getPhilosopherName() + " hat die rechte Gabel erfolgreich bekommen.");
		}
		
		if (hasGotForkRight) {
			try {
				if (!this.getNeighbourTable().getSeats()[0].getForkRight().isUsed()) {
					hasGotForkLeft = this.getNeighbourTable().reserveRightFork(philosopher);
					System.out.println(philosopher.getPhilosopherName() + " hat die linke Gabel vom Nachbartisch erfolgreich bekommen.");
				}
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (hasGotForkLeft) {
				this.eat(forkRight, philosopher);
				hasEaten = true;
			} else {
				System.out.println(philosopher.getPhilosopherName() + " versucht, die linke Gabel vom Nachbartisch zu nehmen.");
				for (int j = 0; j < 4 && !hasEaten; j++) {
					try {
						if (!this.getNeighbourTable().getSeats()[0].getForkRight().isUsed()) {
							hasGotForkLeft = this.getNeighbourTable().reserveRightFork(philosopher);
							System.out.println(philosopher.getPhilosopherName() + " hat die linke Gabel vom Nachbartisch erfolgreich bekommen.");
						}
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (hasGotForkLeft) {
						this.eat(forkRight, philosopher);
						hasEaten = true;
					}
					 else {
						try {
							System.out.println(philosopher.getPhilosopherName() + " hat die linke Gabel vom Nachbartisch nicht bekommen.");
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				if (!hasEaten) {
					System.out.println(philosopher.getPhilosopherName() + " hat die linke Gabel vom Nachbartisch VIERMAL nicht bekommen.");
					//wenn die linke Gabel nicht da, dann muss ich die rechte wieder freigeben
					this.unblockFork(forkRight, philosopher);
				}
			}
		} else {
			System.out.println(philosopher.getPhilosopherName() + " hat die rechte Gabel nicht bekommen.");
		}
		return hasEaten;
	}
	
	public boolean tryToGetForks(final Fork forkRight, final Fork forkLeft, final Philosopher philosopher) {
		boolean hasEaten = false;
		boolean hasGotForkRight = false;
		boolean hasGotForkLeft = false;
		
		if (!forkRight.isUsed()) {
			hasGotForkRight = this.reserveFork(forkRight, philosopher);
			System.out.println(philosopher.getPhilosopherName() + " hat die rechte Gabel erfolgreich bekommen.");
		}
		
		if (hasGotForkRight) {
			if (!forkLeft.isUsed()) {
				hasGotForkLeft = this.reserveFork(forkLeft, philosopher);
				System.out.println(philosopher.getPhilosopherName() + " hat die linke Gabel erfolgreich bekommen.");
			}
			if (hasGotForkLeft) {
				this.eat(forkRight, forkLeft, philosopher);
				hasEaten = true;
			} else {
				System.out.println(philosopher.getPhilosopherName() + " versucht, die linke Gabel zu nehmen.");
				for (int j = 0; j < 4 && !hasEaten; j++) {
					if (!forkLeft.isUsed()) {
						hasGotForkLeft = this.reserveFork(forkLeft, philosopher);
						System.out.println(philosopher.getPhilosopherName() + " hat die linke Gabel erfolgreich bekommen.");
					}
					if (hasGotForkLeft) {
						this.eat(forkRight, forkLeft, philosopher);
						hasEaten = true;
					}
					 else {
						try {
							System.out.println(philosopher.getPhilosopherName() + " hat die linke Gabel nicht bekommen.");
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				if (!hasEaten) {
					System.out.println(philosopher.getPhilosopherName() + " hat die linke Gabel VIERMAL nicht bekommen.");
					//wenn die linke Gabel nicht da, dann muss ich die rechte wieder freigeben
					this.unblockFork(forkRight, philosopher);
				}
			}
		} else {
			System.out.println(philosopher.getPhilosopherName() + " hat die rechte Gabel nicht bekommen.");
		}
		return hasEaten;
	}
	
	public void eat(final Fork forkRight, final Fork forkLeft, final Philosopher philosopher) {
		try {
			System.err.println(philosopher.getPhilosopherName() + " isst an Platz " + forkRight.getSeat().getNumber() + " an Tisch " + this.getNumber());
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.unblockFork(forkRight, philosopher);
		this.unblockFork(forkLeft, philosopher);
		System.out.println(philosopher.getPhilosopherName() + " ist fertig und geht meditieren.");
	}
	
	public void eat(final Fork forkRight, final Philosopher philosopher) {
		try {
			System.err.println(philosopher.getPhilosopherName() + " isst an Platz " + forkRight.getSeat().getNumber() + " an Tisch " + this.getNumber()
					+ " und an Tisch " + this.getNeighbourTable().getNumber() + " mit der linken Gabel.");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.unblockFork(forkRight, philosopher);
		try {
			this.getNeighbourTable().unblockFork(this.getNeighbourTable().getSeats()[0].getForkRight(), philosopher);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(philosopher.getPhilosopherName() + " ist fertig und geht meditieren.");
	}
	
	public boolean queueUpAndTryToEat(final Philosopher philosopher) throws RemoteException {
		boolean wasAdded = false;
		//jetzt die Pl�tze analysieren und die Philosophen zuweisen
		int length = seats[0].getForkRight().getQueue().size();
		int position = 0;
		for (int i = 0; i < seats.length; i++) {
			if (length > seats[i].getForkRight().getQueue().size()) {
				length = seats[i].getForkRight().getQueue().size();
				position = i;
			}
		}
		wasAdded = seats[position].getForkRight().addPhilosopher(philosopher);
		
		return tryToEat(philosopher, seats[position]);
	}

	@Override
	public boolean reserveRightFork(Philosopher philosopher) {
		return this.reserveFork(this.getSeats()[0].getForkRight(), philosopher);
	}
}
