package DiningPhilosophersDistribute;

import DiningPhilosophersDistribute.MainOne.ErrorResistantPhilosopher;

/**
 * Hochschule für angewandte Wissenschaften München
 * Verteilte Softwaresysteme - Praktikum
 * WS 2015/16
 * Aufgabe 4 Verteilte Programmierung
 * @author Diana irmscher - diana.irmscher@hm.edu
 */

public class TabelMaster implements Runnable {
	
	private final ITable table;
	
	private Iterable<ErrorResistantPhilosopher> philosophs;
	
	public TabelMaster(final ITable table, final Iterable<ErrorResistantPhilosopher> philosophs) {
		this.table = table;
		this.philosophs = philosophs;
	}
	
	@Override
	public void run() {
		
		while (true) {
			try {
				watchPhilosophers();
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void watchPhilosophers() {
		int difference = 0;
		Philosopher low = null;
		Philosopher high = null;
		while(true) {
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for(ErrorResistantPhilosopher resistantPhilosoph : philosophs) {
				if(resistantPhilosoph.getPhilosoph().getProcess() < low.getProcess()) {
					low = resistantPhilosoph.getPhilosoph();
				}
				if(resistantPhilosoph.getPhilosoph().getProcess() > high.getProcess()) {
					high = resistantPhilosoph.getPhilosoph();
				}
			}
			difference = Math.abs(high.getProcess() - low.getProcess());
			if (difference >= 10) {
				System.out.println("Der Philosoph " + high.getPhilosopherName() + "hat zu oft gegessen und wird vorübergehend gesperrt.");
				high.dontEatForAWhile();
			}
		}
	}

}
