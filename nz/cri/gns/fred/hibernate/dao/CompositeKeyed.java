package nz.cri.gns.fred.hibernate.dao;

/**
 *
 */
public interface CompositeKeyed {

	public boolean isUnsaved();

	/**
	 * Confirm that the key is up to date per the contents of the object
	 */
	public void updateKey();

	public void setKey(CompositeKey arg1);
	
}
