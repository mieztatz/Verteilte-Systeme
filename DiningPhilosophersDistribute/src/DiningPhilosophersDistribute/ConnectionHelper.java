package DiningPhilosophersDistribute;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConnectionHelper implements IConnectionHelper{
	
	private static final long serialVersionUID = -1498480465650920580L;
	
	private final List<String> listOfTables = new ArrayList<>();
	
	public ConnectionHelper() {
		
	}
	
	public boolean addTable(final String table) {
		boolean wasSuccessful = false;
		wasSuccessful = this.listOfTables.add(table);
		if (wasSuccessful) {
			System.err.println("Es wurde ein neuer Tisch mit der Nummer" + table.substring(6) + " angemeldet.");
		} else {
			System.err.println("Der Tisch konnte nicht hinzugef�gt werden.");
		}
		
		if (listOfTables.size() > 1) {
			int privious = listOfTables.indexOf(table) - 1;
			this.connectTableHasNewNighbour(privious);
		}
		
		return wasSuccessful;
		
	}
	
	/**
	 * Gibt den Namen eines anderen Tisches zur�ck,
	 * wenn es mehr als einen Tisch gibt.
	 * @param table
	 * @return
	 */
	public String getAnotherTable(final String table) {
		if (listOfTables.size() == 1) {
			return null;
		} else if (listOfTables.size() == 2) {
			return listOfTables.get((listOfTables.indexOf(table) + 1) % listOfTables.size());
		} else {
			Random r = new Random();
			int tmp = r.nextInt(listOfTables.size());
			while (tmp == listOfTables.indexOf(table)) {
				tmp = r.nextInt(listOfTables.size());
			}
			return listOfTables.get(tmp - 1);
		}
	}
	
	public void connectTableHasNewNighbour(final int position) {
		String privious = listOfTables.get(position);
		
		try {
			Registry registry = LocateRegistry.getRegistry();
			ITable table = (ITable) registry.lookup(privious);
			//tabel sagen, dass er einen neuen nachbar hat
			// setter f�r nachbarn aufrufen
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
