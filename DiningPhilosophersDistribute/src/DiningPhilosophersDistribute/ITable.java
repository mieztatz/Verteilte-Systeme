package DiningPhilosophersDistribute;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITable extends Remote, Serializable {
	
	ISeat getSeat(int number) throws RemoteException;
	
	ISeat getLeftNeighbour(final int seat) throws RemoteException;
	
	IFork getLeftNeighbourForkOfSeat(final int seat) throws RemoteException;
	
	ISeat getAnySeat() throws RemoteException;
	
	int getNumber() throws RemoteException;
	
	String getName() throws RemoteException;
	
	ISeat getLastSeatOfTable() throws RemoteException;
	
	int getNumberOfSeats() throws RemoteException;
	
	ISeat[] getSeats() throws RemoteException;
	
	IFork getForkOfSeat(final int number) throws RemoteException;
	
	
	

}
