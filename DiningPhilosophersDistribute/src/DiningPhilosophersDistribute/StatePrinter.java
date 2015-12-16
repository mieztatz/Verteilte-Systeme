package DiningPhilosophersDistribute;

import DiningPhilosophersDistribute.MainOne.ErrorResistantPhilosopher;

public class StatePrinter implements Runnable {

	private Iterable<ErrorResistantPhilosopher> philosophs;

	StatePrinter(Iterable<ErrorResistantPhilosopher> philosophs) {
		this.philosophs = philosophs;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				printCurrentState();
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void printCurrentState() {
		StringBuilder b = new StringBuilder();
		b.append("=START====================================\n");
		for (ErrorResistantPhilosopher resistantPhilosoph : philosophs) {
			Philosopher philosoph = resistantPhilosoph.getPhilosoph();
			b.append(philosoph.getPhilosopherName() + ": " + philosoph.getProcess() + "\n");
		}
		b.append("=END====================================\n");
		System.out.println(b.toString());
	}
	
}
