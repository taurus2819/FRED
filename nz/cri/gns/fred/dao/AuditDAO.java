package nz.cri.gns.fred.dao;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;

/**
 *
 */
public interface AuditDAO {

	/**
	 * @param audit
	 * @throws StorageAccessException
	 */
    public Audit update(Audit audit) throws StorageAccessException;

	public void delete(Audit audit) throws StorageAccessException;	    
    
    /**
     * Creates a new empty Audit
     */
    public Audit createNewAudit();

    public Audit save(Audit audit) throws StorageAccessException;

	/**
	 * Returns a new unintialised audit edit
	 */
	public AuditEdit createNewAuditEdit() throws StorageAccessException;

	public void save(AuditEdit edit) throws StorageAccessException;

	public void delete(AuditEdit edit) throws StorageAccessException;
    
}
