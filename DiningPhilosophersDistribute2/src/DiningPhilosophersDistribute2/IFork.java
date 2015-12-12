package DiningPhilosophersDistribute2;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IFork extends Remote, Serializable {
	boolean isUsed() throws RemoteException;
	boolean addPhilosopher(final Philosopher philosopher) throws RemoteException;
	boolean getAndSetUsed(boolean b) throws RemoteException;
	

}
