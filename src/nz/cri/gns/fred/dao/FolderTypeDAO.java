package nz.cri.gns.fred.dao;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.FolderType;

/**
 * @author iainm
 */
public interface FolderTypeDAO {

	public FolderType getFolderType(String label) throws StorageAccessException;
	
}
