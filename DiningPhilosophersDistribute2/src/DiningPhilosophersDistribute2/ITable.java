package DiningPhilosophersDistribute2;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITable extends Remote, Serializable {
	
	ISeat getLeftNeighbour(final ISeat other) throws RemoteException;
	
	ISeat getAnySeat() throws RemoteException;
	

}
