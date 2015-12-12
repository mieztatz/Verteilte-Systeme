package DiningPhilosophersDistribute;

import java.rmi.RemoteException;
import java.util.Random;

public class Table implements ITable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6908684959619272507L;


	private final int numberOfSeats;
	private final int number;
	private final IConnectionHelper connectionhelper;
	

	

	private static final int DEFAULT_NUMBER_OF_SEATS = 10;
	private final ISeat[] seats;
	
	/** Schüssel mit Spaghetti */
	private boolean bowl;
	
	public Table(final int numberOfSeats, final int number, final IConnectionHelper connectionHelper) {
		this.numberOfSeats = numberOfSeats;
		this.number = number;
		this.connectionhelper = connectionHelper;
		if (numberOfSeats >= 0) {
			seats = new ISeat[numberOfSeats];
		} else {
			seats = new ISeat[DEFAULT_NUMBER_OF_SEATS];
		}
		initSeats();
		bowl = true;
	}
	
	public int getNumber() {
		return number;
	}
	
	private void initSeats() {
		for (int i = 0; i < seats.length; i++) {
			seats[i] = new Seat(i, this);
		}
	}
	
	public int getNumberOfSeats() {
		return numberOfSeats;
	}
	
	public ISeat[] getSeats() {
		return this.seats;
	}
	
	/** 
	 * Gibt den linken Nachbarn des �bergebenen Platzes zur�ck.
	 * Falls der �bergebene Platz nicht am Tisch ist, wird NULL zur�ckgegeben.
	 * Tische sind miteinander verlinkt. Die linke Gable ist immer die Gabel
	 * des linken Tischnachbarn. Das hiesst, der Platz im Array an Stelle 0
	 * verlinkt auf das letzte Element des anderen Tisches, falls es einen gibt.
	 * @param other - der eigene Platz
	 * @return seat - der links benachbarte Platz
	 */
	public ISeat getLeftNeighbourFormWholeTable(final ISeat other) throws RemoteException {
		ISeat seat = null;
		if(other.getNumber() < seats.length && other.getNumber() >= 0) {
			if(other.getNumber() == 0) { // hier muss zum anderen Tisch verlinkt werden
				//pr�fen, ob es mehr als einen Tisch gibt und dieser Tisch enthalten ist
				if (connectionhelper.getNumberOfTables() > 1 && connectionhelper.containsTable(this)) {
					if (this.getNumber() == 0) {
						seat = connectionhelper.getTable(connectionhelper.getNumberOfTables() - 1).getLastSeatOfTable();
					} else {
						seat = connectionhelper.getTable(this.getNumber() -1).getLastSeatOfTable();
					}
				}
			} else {
				seat = seats[other.getNumber()- 1];
			}
		}
		return seat;
	}
	
	/**
	 * Gibt den linken Nachbarn des Tisches zur�ck.
	 * Nur lokal.
	 */
	public ISeat getLeftNeighbour(final ISeat other) throws RemoteException {
		return seats[(other.getNumber() + 1) % seats.length];
	}
	
	/**
	 * Gibt den rechten Nachbarn des übergebenen Platzes zurück.
	 * Falls der übergeben Platz nicht am Tisch ist, wird NUll zurückgegeben.
	 * @param other - der eigene Platz
	 * @return seat - der rechts benachbarte Platz
	 */
	public ISeat getRightNeighbour(final ISeat other) {
		ISeat seat = null;
		try {
			if(other.getNumber() < seats.length && other.getNumber() >= 0) {
				if(other.getNumber() == seats.length-1) {
					seat = seats[0];
				} else {
					seat = seats[other.getNumber() + 1];
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return seat;
	}
	
	public ISeat getFirstSeat() {
		return seats[0];
	}
	
	public ISeat getAnySeat() throws RemoteException {
		Random r = new Random();
		int tmp = r.nextInt(numberOfSeats);
		return seats[tmp];
	}
	
	public ISeat getLastSeatOfTable() {
		return seats[seats.length -1];
	}

}
