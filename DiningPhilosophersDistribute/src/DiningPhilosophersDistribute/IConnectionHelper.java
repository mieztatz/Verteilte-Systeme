package DiningPhilosophersDistribute;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IConnectionHelper extends Remote, Serializable {
	
	
	ITable getTable(final int number) throws RemoteException;
	
	void setTable(final Table table) throws RemoteException;
	
	int getNumberOfTables() throws RemoteException;
	
	boolean containsTable(final ITable table) throws RemoteException;
	
	ITable getAnotherTabel(final int tableNumber) throws RemoteException;
	
	ISeat getAnySeatFormAllTables() throws RemoteException;

}
