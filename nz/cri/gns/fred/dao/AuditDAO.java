package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.DataOrigin;
import nz.cri.gns.fred.model.LogTable;

/**
 *
 */
public interface AuditDAO {

	public Audit getAudit(int auditId) throws StorageAccessException;
	
	public void delete(Audit audit) throws StorageAccessException;	    
    
    public Audit createNewAudit();

	public AuditEdit createNewAuditEdit() throws StorageAccessException;

	public void delete(AuditEdit edit) throws StorageAccessException;
	
    public DataOrigin getDataOrigin(Integer id) throws StorageAccessException;
	
	public ConfidentialGroup createNewConfidentialGroup() throws StorageAccessException;
	
	public ConfidentialGroup getConfidentialGroup(Integer id) throws StorageAccessException;
	
	public void delete(ConfidentialGroup group) throws StorageAccessException;
	
	public LogTable createNewLog();
	
	public <T> T saveOrUpdate(T object) throws StorageAccessException;
	
	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;

}
