package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.BacklogStatus;

/**
 *
 */
public interface BacklogStatusDAO {

	/**
	 * @param mapNumber
	 * @return
	 * @throws StorageAccessException
	 */
	public BacklogStatus getBacklogStatus(String mapNumber) throws StorageAccessException;

    /**
     * Returns backlog status records in the given masterfile folder
     * @param masterfileId
     * @return
     */
	public List<BacklogStatus> getBacklogStatusInMasterfile(int masterfileId) throws StorageAccessException;
	
	public int getSumLocalityCount() throws StorageAccessException;
	
	public int getSumLocalityCount(int masterfileId) throws StorageAccessException;

	public int getSumProcessingCount() throws StorageAccessException;
	
	public int getSumProcessingCount(int masterfileId) throws StorageAccessException;
	
	public int getSumCompletedCount() throws StorageAccessException;
	
	public int getSumCompletedCount(int masterfileId) throws StorageAccessException;
	
	public int getSumNewCount() throws StorageAccessException;
	
	public int getSumNewCount(int masterfileId) throws StorageAccessException;

}
