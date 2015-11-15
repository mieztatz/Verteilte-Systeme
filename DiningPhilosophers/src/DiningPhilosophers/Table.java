package DiningPhilosophers;

import java.util.Random;

public class Table {
	
	private final int numberOfSeats;
	

	private static final int DEFAULT_NUMBER_OF_SEATS = 10;
	private final Seat[] seats;
	
	/** Schüssel mit Spaghetti */
	private boolean bowl;
	
	public Table(final int numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
		if (numberOfSeats >= 0) {
			seats = new Seat[numberOfSeats];
		} else {
			seats = new Seat[DEFAULT_NUMBER_OF_SEATS];
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
	public Seat getLeftNeighbour(final Seat other) {
		Seat seat = null;
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
	public Seat getRightNeighbour(final Seat other) {
		Seat seat = null;
		if(other.getNumber() < seats.length && other.getNumber() >= 0) {
			if(other.getNumber() == seats.length-1) {
				seat = seats[0];
			} else {
				seat = seats[other.getNumber() + 1];
			}
		}
		return seat;
	}
	
	public Seat getFirstSeat() {
		return seats[0];
	}
	
	public Seat getAnySeat() {
		Random r = new Random();
		int tmp = r.nextInt(4);
		return seats[tmp];
	}

}
