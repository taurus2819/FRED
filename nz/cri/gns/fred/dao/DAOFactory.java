package nz.cri.gns.fred.dao;

/**
 * @author iainm
 */
public interface DAOFactory {

	public FolderDAO getFolderDAO();
	public FolderTypeDAO getFolderTypeDAO();
	public TaxonomicGroupDAO getTaxonomicGroupDAO();
	/**
	 * Closes the current session for this thread
	 * @throws StorageAccessException
	 */
	public void closeSession() throws StorageAccessException;
	
}
