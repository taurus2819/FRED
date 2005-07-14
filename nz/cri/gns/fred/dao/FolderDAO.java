package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FolderType;

/**
 * @author iainm
 */
public interface FolderDAO {

	/**
	 * Creates a new, unsaved folder
	 */
	public Folder createNewFolder();
	
	/**
	 * Returns a list of all personal folders owned by to the person
	 * @throws StorageAccessException
	 */
	public List getPersonalFolders(int ownerId) throws StorageAccessException;
	
	/**
	 * Returns a list of folders of the given type to which the user has access 
	 * but does not (necessarily?) own.
	 * @throws StorageAccessException
	 */
	public List getAccessibleFolders(int userId, FolderType type) throws StorageAccessException;
	
}
