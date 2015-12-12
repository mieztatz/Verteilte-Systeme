package DiningPhilosophersDistribute;

/**
 * Hochschule f�r angewandte Wissenschaften M�nchen
 * Verteilte Softwaresysteme - Praktikum
 * WS 2015/16
 * Aufgabe 4 Verteilte Programmierung
 * @author Diana irmscher - diana.irmscher@hm.edu
 */

public class TabelMaster extends Thread {
	
	private final Table table;
	
	private final Philosopher[] philosophers;
	
	public TabelMaster(final Table table, final Philosopher[] philosophers) {
		this.table = table;
		this.philosophers = new Philosopher[philosophers.length];
		for (int i = 0; i < philosophers.length; i++) {
			this.philosophers[i] = philosophers[i];
		}
	}
	
	@Override
	public void run() {
		int difference = 0;
		Philosopher low = this.philosophers[0];
		Philosopher high = this.philosophers[1];
		while(true) {
			for(Philosopher p : this.philosophers) {
				if(p.getProcess() < low.getProcess()) {
					low = p;
				}
				if(p.getProcess() > high.getProcess()) {
					high = p;
				}
			}
			difference = Math.abs(high.getProcess() - low.getProcess());
			if (difference >= 10) {
				System.out.println("Der Philosoph " + high.getPhilosopherName() + "hat zu oft gegessen und wird vor�bergehend gesperrt.");
			}
		}
	}
	
	

}
