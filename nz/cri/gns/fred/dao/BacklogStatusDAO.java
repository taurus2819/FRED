package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.BacklogStatus;
import nz.cri.gns.fred.model.Folder;

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
	
}
