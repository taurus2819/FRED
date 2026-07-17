package nz.cri.gns.fred.hibernate.util.hibernate6;


import nz.cri.gns.dataaccess.StorageAccessException;
import org.hibernate.Session;



public interface HibernateProvider {

	/**
	 * Returns the current session for this thread if one exists
	 * or creates one if none does
	 */
	public Session currentSession() throws StorageAccessException;

	/**
	 * Closes the current session for this thread
	 */
    public void closeSession() throws StorageAccessException;
 
}