package DiningPhilosophersDistribute2;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISeat extends Remote, Serializable {
	
	IFork getForkRight() throws RemoteException;
	
	int getNumber() throws RemoteException;
	
	

}
