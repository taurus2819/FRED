package nz.cri.gns.fred.hibernate.dao;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

/**
 * @author iainm
 */
public interface HibernateProvider {

	/**
	 * Returns the current session for this thread if one exists
	 * or creates one if none does
	 */
	public Session currentSession() throws HibernateException;

	/**
	 * Closes the current session for this thread
	 */
    public void closeSession() throws HibernateException;
 
}
