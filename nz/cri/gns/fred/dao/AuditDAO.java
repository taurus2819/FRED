package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.DataOrigin;

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

	public AuditEdit save(AuditEdit edit) throws StorageAccessException;

	public void delete(AuditEdit edit) throws StorageAccessException;
    
	public DataOrigin getDataOrigin(Integer id) throws StorageAccessException;
	
	public ConfidentialGroup createNewConfidentialGroup() throws StorageAccessException;
	
	public ConfidentialGroup getConfidentialGroup(Integer id) throws StorageAccessException;
	
	public ConfidentialGroup save(ConfidentialGroup group) throws StorageAccessException;
	
	public void delete(ConfidentialGroup group) throws StorageAccessException;
	
	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;

}
