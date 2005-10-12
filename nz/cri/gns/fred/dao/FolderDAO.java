package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FolderRight;
import nz.cri.gns.fred.model.FolderType;
import nz.cri.gns.fred.model.FolderUser;
import nz.cri.gns.fred.model.UserFolder;

/**
 * @author iainm
 */
public interface FolderDAO {

	/**
	 * Creates a new, unsaved folder
	 */
	public Folder createNewFolder();
	
	/**
	 * Returns a list of all <code>UserFolder</code>s representing the 
	 * personal folders owned by to the person
	 * @throws StorageAccessException
	 */
	public List<UserFolder> getPersonalFolders(int ownerId) throws StorageAccessException;
	
	/**
	 * Returns a list of <code>UserFolder</code>s of the given type to which the user has access 
	 * but does not (necessarily?) own.
	 * @throws StorageAccessException
	 */
	public List<UserFolder> getAccessibleFolders(int userId, FolderType type) throws StorageAccessException;

    /**
     * Saves the given new folder to persistent storage
     * @param folder
     */
    public Folder save(Folder folder) throws StorageAccessException;

    /**
     * Removes the current folder from persistent storage
     * @param folder
     */
    public void delete(Folder folder) throws StorageAccessException;
    
    /**
     * Returns the folder with the given id
     */
    public Folder getFolder(int folderId) throws StorageAccessException;

	/**
	 * Returns a count of any features that are in the masterfile folder given, with a 'waiting' status
	 * @throws StorageAccessException
	 */
	public int getWaitingMasterfileFeatureCount(Folder folder) throws StorageAccessException;

	/**
	 * @return the folder with the given id, initialised for the given user
	 * or null if the user has no rights to the given folder
	 * @throws StorageAccessException
	 */
	public UserFolder getUserFolder(int folderId, int userId) throws StorageAccessException;

	/**
	 * @param folder
	 * @return
	 * @throws StorageAccessException
	 */
	public List<Audit> getWorkingAuditsFor(Folder folder) throws StorageAccessException;

	/**
	 * Returns the folder rights which satisfy the given join, in the given order.  A query might look like
	 * WHERE &lt;join&gt; ORDER BY &lt;order&gt;
	 * @param join
	 * @param order
	 * @return
	 * @throws StorageAccessException
	 */
	public List<FolderRight> getFolderRightList(String join, String order) throws StorageAccessException;

	/**
	 * Creates a new, unsaved folder user
	 */
	public FolderUser createNewFolderUser();
	
}
