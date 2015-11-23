package DiningPhilosophers;

/**
 * Hochschule für angewandte Wissenschaften München
 * Verteilte Softwaresysteme - Praktikum
 * WS 2015/16
 * Aufgabe 3.3 Parallele Programmierung - Programm "Speisende Philosophen"
 * @author Diana irmscher - diana.irmscher@hm.edu
 */

public class Main {
	
	public static void main(String[] args) throws InterruptedException {
		
		/** Ein Tisch mit 5 Plätzen */
		final Table table = new Table(5);
		
		/** 5 Philosophen, die essen möchten */
		final Philosopher[] philosophers = new Philosopher[]{new Philosopher("Longinos", table, false),
		                                                     new Philosopher("Origenes", table, false),
		                                                     new Philosopher("Plotin", table, false),
		                                                     new Philosopher("Porphyrios", table, false),
		                                                     new Philosopher("HeYan", table, true)};
		
		for(Philosopher p : philosophers) {
			p.start();
		}
	}
}
