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
		final Philosopher[] philosophers = new Philosopher[]{new Philosopher("Longinos", table),
		                                                     new Philosopher("Origenes", table),
		                                                     new Philosopher("Plotin", table),
		                                                     new Philosopher("Porphyrios", table),
		                                                     new Philosopher("HeYan", table)};
		
		for(Philosopher p : philosophers) {
			p.start();
		}
	}
}
