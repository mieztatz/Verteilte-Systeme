package DiningPhilosophers;

import java.util.ArrayList;
import java.util.List;

public class Fork {
	
	private boolean isUsed;
	private List<Philosopher> queue = new ArrayList<>();
	
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
	
	/**
	 * Gibt die Anzahl der wartenden Philosophen zurück.
	 * @return int - die Anzahl der Elemente in der Queue.
	 */
	public int getNumberOfWaitingPhilosophers() {
		return this.queue.size();
	}
	
	public boolean addPhilosopher(final Philosopher philosopher) {
		boolean isAdded = false;
//		synchronized (this) {
			isAdded = this.queue.add(philosopher);
			System.out.println(philosopher.getPhilosopherName() + " hat sich in die Warteschlange eingereiht");
			try {
				System.out.println(philosopher.getPhilosopherName() + 
						" wartet, bis er als nächstes dran ist, an Position " + this.queue.indexOf(philosopher));
				synchronized(philosopher) {
					philosopher.wait();
				}
				System.out.println(philosopher.getPhilosopherName() + " ruft: \"Juhu, ich darf wieder essen!\"");
				// der jetzt schon hungrig?
			} catch (InterruptedException e) {
				e.printStackTrace();
//			}
		}
		return isAdded;
	}
	
	public boolean isUsed() {
		return this.isUsed;
	}
	
	public void setUsed(final boolean isUsed) {
		this.isUsed = isUsed;
		if (!isUsed) {
			activateNextPhilosopher();
		}
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
            synchronized(nextPhilosopher) {
            	nextPhilosopher.notify();
    			System.out.println("Der Status des Philosophen " 
    		              + nextPhilosopher.getPhilosopherName() + " ist jetzt: " + nextPhilosopher.getState() + ".");
    			//den Philosophen aus der Warteschlange entfernen
    			this.queue.remove(nextPhilosopher);
    			//und zum essen schicken
				try {
					nextPhilosopher.eat();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
		}
	}
}
