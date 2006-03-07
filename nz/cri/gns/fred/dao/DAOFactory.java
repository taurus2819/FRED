package nz.cri.gns.fred.dao;

import nz.cri.gns.dataaccess.StorageAccessException;

/**
 * @author iainm
 */
public interface DAOFactory {

	public FolderDAO getFolderDAO();
	public FolderTypeDAO getFolderTypeDAO();
	public TaxonomicGroupDAO getTaxonomicGroupDAO();
	public FeatureDAO getFeatureDAO();
	public SampleDAO getSampleDAO();
	public RecordDAO getRecordDAO();
	public PersonDAO getPersonDAO();
    public TaxonomicDAO getTaxonomicDAO();
    public AuditDAO getAuditDAO();
    public BacklogStatusDAO getBacklogStatusDAO();
    public StratLexDAO getStratLexDAO();
	
	/**
	 * Closes the current session for this thread
	 * @throws StorageAccessException
	 */
	public void closeSession() throws StorageAccessException;
	
}
