package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.FolderTypeDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FolderAccessor;
import nz.cri.gns.fred.model.FolderRight;
import nz.cri.gns.fred.model.FolderUser;
import nz.cri.gns.fred.model.UserFolder;

import nz.cri.gns.auth.UserAccount;

/**
 * @author iainm
 */
public class FolderUtil extends ModelUtil {

	private FolderDAO folderDAO;
	private FolderTypeDAO typeDAO;
	
	public FolderUtil(DAOFactory dao) {
		super(dao);
		this.folderDAO = dao.getFolderDAO();
		this.typeDAO = dao.getFolderTypeDAO();
	}
	
	/**
	 * Returns a list of <code>UserFolder</code>s representing
	 * personal folders belonging to the given user,
	 * or to which the given user has access but is not the owner
	 */
	public List<UserFolder> getPersonalFolders(UserAccount user) throws StorageAccessException {
		Vector<UserFolder> folders = new Vector<UserFolder>();
		folders.addAll(folderDAO.getPersonalFolders(Integer.parseInt(user.getId())));
		folders.addAll(folderDAO.getAccessibleFolders(Integer.parseInt(user.getId()), typeDAO.getFolderType("Personal")));
		
		Collections.sort(folders);
		return folders;
	}
	
	/**
	 * Returns a list of <code>UserFolder</code>s representing
	 * admin folders to which the given user has access
	 */
	public List getAdminFolders(UserAccount user) throws StorageAccessException {
		List folders = folderDAO.getAccessibleFolders(Integer.parseInt(user.getId()), typeDAO.getFolderType("Admin"));
		Collections.sort(folders);
		
		return folders;
	}
	
	public Folder addFolder(String name, UserAccount user)  throws StorageAccessException {
	    Folder folder = folderDAO.createNewFolder();
	    folder.setName(name);
	    folder.setOwnerId(new Integer(user.getId()));
	    folder.setFolderType(typeDAO.getFolderType("Personal"));
	    
	    folderDAO.save(folder);
	    return folder;
	}
	
	public void deleteFolder(int folderId, UserAccount user)  throws StorageAccessException {
	    Folder folder = folderDAO.getFolder(folderId);
	    folderDAO.delete(folder);
	}
	
	public boolean getUserHasAdminRights(UserFolder folder) {
	    return (folder.getRights() & Folder.FOLDER_ADMIN_RIGHT) > 0;
	}
	public int getMasterfileFolderFeatureCount(Folder folder) throws StorageAccessException {
		return folderDAO.getWaitingMasterfileFeatureCount(folder);
	}
	
	public UserFolder getUserFolder(int folderId, UserAccount user) throws StorageAccessException {
		return folderDAO.getUserFolder(folderId, Integer.parseInt(user.getId()));
}
	
	/**
	 * Returns a list of right types in the appropriate order and omitting any
	 * innappropriate rights for the given folder's type.
	 * @throws StorageAccessException 
	 */
	public List<FolderRight> getRightTypesForDisplay(UserFolder folder) throws StorageAccessException {
		return (folder.getFolder().getFolderType().getName().equals("Personal")) 
			? folderDAO.getFolderRightList("code NOT IN ('1', '64')", "code") 
			: folderDAO.getFolderRightList("code NOT IN ('32', '64')", "code DESC");
	}
	/**
	 * Returns a list of UserFolder objects describing each user that has some access to
	 * the given folder.  The UserFolders are ordered alphabetically
	 * @param folder
	 * @return
	 * @throws StorageAccessException 
	 */
	public List<FolderAccessor> getNonOwningUsers(UserFolder folder) throws StorageAccessException {
		List<FolderUser> users = folderDAO.getNonOwningUsers(folder.getFolder());
		List<FolderAccessor> accessors = new Vector<FolderAccessor>(users.size());
		//Ordering is going to be painful
		for (FolderUser user : users) try {
			accessors.add(new FolderAccessor(user, FREDUtil.getUserName(user.getUserId().intValue())));
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
		Collections.sort(accessors);
		return accessors;
	}
}
