package DiningPhilosophersDistribute;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IConnectionHelper extends Remote, Serializable {
	
	boolean addTable(final String table) throws RemoteException;
	
	String getAnotherTable(final String table) throws RemoteException;

}
