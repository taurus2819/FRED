package nz.cri.gns.fred.dao;

import java.util.List;

import net.sf.hibernate.HibernateException;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FolderRight;
import nz.cri.gns.fred.model.FolderType;
import nz.cri.gns.fred.model.FolderUser;
import nz.cri.gns.fred.model.UserFolder;

public interface FolderDAO {
	public void delete(Object object) throws StorageAccessException;
	public <T> T saveOrUpdate(T object) throws StorageAccessException;
	public <T> T get(Integer id, Class<T> clazz);
	/**
	 * Creates a new, unsaved folder
	 */
	public Folder createNewFolder();
	
	/**
	 * Returns a list of all <code>UserFolder</code>s of the given type representing the 
	 * folders owned by the person
	 * @throws StorageAccessException
	 */
	public List<UserFolder> getOwnedFolders(int ownerId, FolderType type) throws StorageAccessException;
	
	/**
	 * Returns a list of <code>UserFolder</code>s of the given type to which the user has access 
	 * but does not (necessarily?) own.
	 * @throws StorageAccessException
	 */
	public List<UserFolder> getAccessibleFolders(int userId, FolderType type) throws StorageAccessException;

    /**
     * Returns all folders of the given type
     */
	public List<Folder> getFolders(FolderType type) throws HibernateException, StorageAccessException;

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
	 * Returns all audits with the given folder as their working folder
	 * @param folder
	 * @return
	 * @throws StorageAccessException
	 */
	public List<Audit> getAuditsFor(Folder folder) throws StorageAccessException;

	/**
	 * Returns all audits of the given status with the given folder as their working folder
	 * @param folder
	 * @return
	 * @throws StorageAccessException
	 */
	public List<Audit> getAuditsFor(Folder folder, String status) throws StorageAccessException;

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
