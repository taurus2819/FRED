package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.Iterator;
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
	    if ((folder.getAudits() != null && folder.getAudits().size() > 0)
	    		|| (folder.getFeatures() != null && folder.getFeatures().size() > 0))
	    	throw new IllegalStateException("Cannot delete folder as it is not empty");
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
	/** TODO need to query code field being VARCHAR with Iain. Should change to NUMBER and then can get rid of TO_NUMBER() in code below
	 * 
	 */
	public List<FolderRight> getRightTypesForDisplay(UserFolder folder) throws StorageAccessException {
		return (folder.getFolder().getFolderType().getName().equals("Personal")) 
			? folderDAO.getFolderRightList("code NOT IN ('1', '64')", "TO_NUMBER(code)") 
			: folderDAO.getFolderRightList("code <> '1'", "TO_NUMBER(code)");
	}
	/**
	 * Returns a list of UserFolder objects describing each user that has some access to
	 * the given folder.  The UserFolders are ordered alphabetically
	 * @param folder
	 * @return
	 * @throws StorageAccessException 
	 */
	public List<FolderAccessor> getNonOwningUsers(UserFolder userFolder) throws StorageAccessException {
		
		Folder folder = userFolder.getFolder();
		List<FolderAccessor> accessors = new Vector<FolderAccessor>(folder.getFolderUsers().size());
		
		for (FolderUser user : folder.getFolderUsers()) try {
			accessors.add(new FolderAccessor(user, FREDUtil.getUserName(user.getUserId().intValue())));
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
		Collections.sort(accessors);
		return accessors;
	}
	
	public void addUserToFolder(UserFolder folder, int userId, int permissions) throws StorageAccessException {
		FolderUser folderUser = folderDAO.createNewFolderUser();
		folderUser.setUserId(new Integer(userId));
		folderUser.setUserRights(new Integer(permissions));
		folderUser.setFolder(folder.getFolder());
		folder.getFolder().getFolderUsers().add(folderUser);
		folderDAO.save(folderUser);
	}

	public void removeUserFromFolder(UserFolder folder, int userId) throws StorageAccessException {
		Integer userAsInteger = new Integer(userId);
		for (Iterator<FolderUser> it = folder.getFolder().getFolderUsers().iterator(); it.hasNext(); ) {
			FolderUser user = it.next();
			if (user.getUserId().equals(userAsInteger)) {
				folderDAO.delete(user);
				it.remove();
				return;
			}
		}
	}

	public void toggleUserFolderRights(UserFolder folder, int userId, int newRight) throws StorageAccessException {
		Integer userAsInteger = new Integer(userId);
		for (FolderUser user : folder.getFolder().getFolderUsers()) {
			if (user.getUserId().equals(userAsInteger)) {
				user.setUserRights(new Integer(newRight ^ user.getUserRights().intValue()));
				folderDAO.update(user);
				return;
			}
		}
	}
}
