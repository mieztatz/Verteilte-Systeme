package DiningPhilosophersDistribute2;

import java.rmi.RemoteException;
import java.util.Random;

public class Table implements ITable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6908684959619272507L;


	private final int numberOfSeats;
	

	private static final int DEFAULT_NUMBER_OF_SEATS = 10;
	private final ISeat[] seats;
	
	/** Schüssel mit Spaghetti */
	private boolean bowl;
	
	public Table(final int numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
		if (numberOfSeats >= 0) {
			seats = new ISeat[numberOfSeats];
		} else {
			seats = new ISeat[DEFAULT_NUMBER_OF_SEATS];
		}
		initSeats();
		bowl = true;
	}
	
	private void initSeats() {
		for (int i = 0; i < seats.length; i++) {
			seats[i] = new Seat(i);
		}
	}
	
	public int getNumberOfSeats() {
		return numberOfSeats;
	}
	/** 
	 * Gibt den linken Nachbarn des übergebenen Platzes zurück.
	 * Falls der übergebene Platz nicht am Tisch ist, wird NULL zurückgegeben.
	 * @param other - der eigene Platz
	 * @return seat - der links benachbarte Platz
	 */
	public ISeat getLeftNeighbour(final ISeat other) throws RemoteException {
		ISeat seat = null;
		if(other.getNumber() < seats.length && other.getNumber() >= 0) {
			if(other.getNumber() == 0) {
				seat = seats[seats.length-1];
			} else {
				seat = seats[other.getNumber()- 1];
			}
		}
		return seat;
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
	
	@Override
	public String toString() {
		return "Der Tisch ist verf�gbar.";
	}

}
