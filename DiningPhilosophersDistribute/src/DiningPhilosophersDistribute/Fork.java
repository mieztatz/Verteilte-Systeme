package DiningPhilosophersDistribute;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hochschule für angewandte Wissenschaften München
 * Verteilte Softwaresysteme - Praktikum
 * WS 2015/16
 * Aufgabe 3.3 Parallele Programmierung - Programm "Speisende Philosophen"
 * @author Diana irmscher - diana.irmscher@hm.edu
 */

public class Fork implements IFork {
	
	private final Seat seat;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -332972450635777961L;

	private AtomicBoolean isUsed = new AtomicBoolean(false);
	
	private List<Philosopher> queue = new ArrayList<>();
	
	public Fork(final Seat seat) {
		this.seat = seat;
	}
	
	public ISeat getSeat() {
		return this.seat;
	}
	
	//ist das auch �ber den Getter hier gelockt?
	public boolean isUsed() {
		return isUsed.get();
	}
	/**
	 * Atomically sets to the given value and returns the previous value.
	 * @param isUsed
	 * @return isUsed previously
	 */
	public boolean getAndSetUsed(final boolean isUsed, Philosopher philosopher) {
		boolean previous = this.isUsed.getAndSet(isUsed);

		if (isUsed) {
			try {
				System.out.println(philosopher.getPhilosopherName() + " hat die Gabel "+ this.getSeat().getNumber() +" bekommen, das meldet die Gabel.");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				System.out.println(philosopher.getPhilosopherName() + " hat die Gabel "+ this.getSeat().getNumber() +" zur�ckgegeben, das meldet die Gabel.");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (!(previous && this.isUsed()) && !this.isUsed()) {
			this.activateNextPhilosopher();
		}
		return previous;
	}
	
	/**
	 * Gibt die gesamte Queue mit allen wartenden Philosophen zurück.
	 * Jede Gable verfügt über eine Queue.
	 * In dieser Queue sind alle Philosophen vermerkt,
	 * die als nächstes mit dieser Gabel essen möchten.
	 * @return List<Philosophers> - Die Liste mit allen wartenden Philosophen.
	 */
	public List<Philosopher> getQueue() {
		if (this.queue != null) {
			return this.queue;
		}
		else
			System.out.println("Es gibt keine Queue.");
		return null;
	}
	
	/**
	 * Gibt den Philosophen zurück, der als nächster essen kann.
	 * 
	 * @return Philosopher - der Philosoph, welcher als nächster essen darf
	 */
	public Philosopher getNextPhilosopher() {
		Philosopher philosopher = null;
		if (!this.queue.isEmpty()) {
			philosopher = this.queue.get(0);
		}
		return philosopher;
	}
	
	public boolean addPhilosopher(final Philosopher philosopher) {
		boolean isAdded = false;
		synchronized (this.getQueue()) {
			isAdded = this.queue.add(philosopher);
		}
		System.out.println(philosopher.getPhilosopherName() + " hat sich in die Warteschlange eingereiht");
		try {
			System.out.println(philosopher.getPhilosopherName() + 
					" wartet, bis er als nächstes dran ist, an Position " + this.queue.indexOf(philosopher));
			synchronized (philosopher) {
				philosopher.wait();
			}
			System.out.println(philosopher.getPhilosopherName() + " ruft: \"Juhu, ich darf wieder essen!\"");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return isAdded;
	}
	
	/**
	 * Wenn die Gabel wieder freigeben ist, dann kann, wenn ein
	 * Philosoph auf die Gabel wartet, dieser jetzt auf die Gabel
	 * gestzt werden.
	 */
	public void activateNextPhilosopher() {
		if (!this.queue.isEmpty()) {
			Philosopher nextPhilosopher = this.queue.get(0);
			//Philosoph aufwecken
            System.out.println("Der Status des Philosophen " 
			              + nextPhilosopher.getPhilosopherName() + " ist: " + nextPhilosopher.getState() + ".");
            // kann nur aufgeweckt werden von letzten thread mit Monitor
            synchronized (nextPhilosopher) {
            	nextPhilosopher.notify();
			}
    		System.out.println("Der Status des Philosophen " + nextPhilosopher.getPhilosopherName() + " ist jetzt: " + nextPhilosopher.getState() + ".");
    		//den Philosophen aus der Warteschlange entfernen
    		synchronized (this.queue) {
    			this.queue.remove(nextPhilosopher);
			}
    		//jetzt wieder zum esenn gehen
		}
	}

}