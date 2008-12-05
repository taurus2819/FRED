package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FolderRight;
import nz.cri.gns.fred.model.FolderType;
import nz.cri.gns.fred.model.FolderUser;
import nz.cri.gns.fred.model.FrUserView;
import nz.cri.gns.fred.model.UserFolder;

public class FolderUtil extends ModelUtil {

	private FredDAO fredDAO;
	
	private UserUtil userUtil;
	
	public FolderUtil(DAOFactory dao) {
		super(dao);
		this.fredDAO = dao.getFredDAO();
		this.userUtil = new UserUtil(factory);
	}
		
	public Folder getFolder(int folderId) throws StorageAccessException {
		return fredDAO.get(folderId, nz.cri.gns.fred.hibernate.Folder.class);
	}
	
	/**
	 * Returns a list of <code>UserFolder</code>s representing
	 * personal folders belonging to the given user,
	 * or to which the given user has access but is not the owner
	 */
	public List<UserFolder> getPersonalFolders(UserAccount user) throws StorageAccessException {
		Vector<UserFolder> folders = new Vector<UserFolder>();
		folders.addAll(getOwnedFolders(Integer.parseInt(user.getId()), fredDAO.getFolderType(Folder.FOLDER_TYPE_PERSONAL)));
		folders.addAll(getAccessibleFolders(Integer.parseInt(user.getId()), fredDAO.getFolderType(Folder.FOLDER_TYPE_PERSONAL)));
		Collections.sort(folders);
		return folders;
	}
	
	/**
	 * Returns a list of <code>UserFolder</code>s representing
	 * admin folders to which the given user has access
	 */
	public List<UserFolder> getAdminFolders(UserAccount user) throws StorageAccessException {
		List<UserFolder> folders = getAccessibleFolders(Integer.parseInt(user.getId()), fredDAO.getFolderType(Folder.FOLDER_TYPE_ADMIN));
		Collections.sort(folders);
		return folders;
	}

	public List<Folder> getAdminFolders() throws HibernateException, StorageAccessException {
		List<Folder> folders = getFolders(fredDAO.getFolderType(Folder.FOLDER_TYPE_ADMIN));
		Collections.sort(folders);
		return folders;
	}
	
	/**
	 * Returns a list of <code>UserFolder</code>s representing
	 * backlog admin folders to which the given user has access
	 */
	public List<UserFolder> getBacklogAdminFolders(UserAccount user) throws StorageAccessException {
		List<UserFolder> folders = getAccessibleFolders(Integer.parseInt(user.getId()), fredDAO.getFolderType(Folder.FOLDER_TYPE_BACKLOG_ADMIN));
		Collections.sort(folders);
		return folders;
	}
	
	/**
	 * Returns a list of <code>UserFolder</code>s representing
	 * backlog folders belonging to the given user,
	 * or to which the given user has access but is not the owner
	 */
	public List<UserFolder> getBacklogFolders(UserAccount user) throws StorageAccessException {
		Vector<UserFolder> folders = new Vector<UserFolder>();
		folders.addAll(getOwnedFolders(Integer.parseInt(user.getId()), fredDAO.getFolderType(Folder.FOLDER_TYPE_BACKLOG)));
		folders.addAll(getAccessibleFolders(Integer.parseInt(user.getId()), fredDAO.getFolderType(Folder.FOLDER_TYPE_BACKLOG)));
		Collections.sort(folders);
		return folders;
	}	
	
	public List<UserFolder> getOwnedFolders(Integer ownerId, FolderType type) throws StorageAccessException {
		List<UserFolder> userFolders = new Vector<UserFolder>();
		FrUserView owner = userUtil.getFrUserView(ownerId);
		List<Folder> folders = fredDAO.getList("FROM Folder as f where f.owner = ? and f.folderType = ?", Folder.class, owner, type);
		for (Folder folder : folders)
			userFolders.add(UserFolder.getOwnedUserFolder(folder));
		return userFolders;
	}
	
	public List<UserFolder> getAccessibleFolders(int userId, FolderType type) throws StorageAccessException {
		List<UserFolder> userFolders = new Vector<UserFolder>();
		FrUserView user = userUtil.getFrUserView(userId);
		List<FolderUser> fus = fredDAO.getList("FROM FolderUser as f where f.user = ? and f.folder.folderType = ?", FolderUser.class, user, type);
		for (FolderUser fu : fus)
			userFolders.add(UserFolder.getAccessibleUserFolder(fu.getFolder(), fu.getUserRights()));
		return userFolders;
	}

	public List<Folder> getFolders(FolderType type) throws HibernateException, StorageAccessException {
		return fredDAO.getList("FROM Folder as f WHERE f.folderType = ?", Folder.class, type);		
	}
	
	public List<UserFolder> getPersonalPlusBacklogFolders(UserAccount user) throws StorageAccessException {
		List<UserFolder> folders = getPersonalFolders(user);
		folders.addAll(getBacklogFolders(user));
		Collections.sort(folders);
		return folders;
	}
	
	public Folder addFolder(String name, UserAccount user)  throws StorageAccessException {
	    Folder folder = fredDAO.createNewFolder();
	    folder.setName(name);
	    folder.setOwner(userUtil.getFrUserView(user.getId()));
	    folder.setFolderType(fredDAO.getFolderType(Folder.FOLDER_TYPE_PERSONAL));
	    fredDAO.saveOrUpdate(folder);
	    return folder;
	}
	
	public Folder addBacklogFolder(String name, UserAccount user)  throws StorageAccessException {
	    Folder folder = fredDAO.createNewFolder();
	    folder.setName(name);
	    folder.setOwner(userUtil.getFrUserView(user.getId()));
	    folder.setFolderType(fredDAO.getFolderType(Folder.FOLDER_TYPE_BACKLOG));
	    fredDAO.saveOrUpdate(folder);
	    return folder;
	}
	
	public void deleteFolder(int folderId, UserAccount user)  throws StorageAccessException {
		UserFolder userFolder = getUserFolder(folderId, user);
		if (!userFolder.isAllowedAdmin())
			throw new IllegalStateException("Cannot delete folder as no admin rights");
	    Folder folder = fredDAO.get(folderId, nz.cri.gns.fred.hibernate.Folder.class);
	    if (!FolderUtil.isFolderEmpty(folder))
	    	throw new IllegalStateException("Cannot delete folder as it is not empty");
	    fredDAO.delete(folder);
	}
	
	public boolean getUserHasAdminRights(UserFolder folder) {
	    return (folder.getRights() & Folder.FOLDER_ADMIN_RIGHT) > 0;
	}
	public int getMasterfileFolderFeatureCount(Folder folder) throws StorageAccessException {
		return fredDAO.getWaitingMasterfileFeatureCount(folder);
	}
	
	public UserFolder getUserFolder(int folderId, UserAccount user) throws StorageAccessException {
		return fredDAO.getUserFolder(folderId, Integer.parseInt(user.getId()));
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
		return (folder.getFolder().getFolderType().getName().equals(Folder.FOLDER_TYPE_PERSONAL)
			|| folder.getFolder().getFolderType().getName().equals(Folder.FOLDER_TYPE_BACKLOG)) 
			? fredDAO.getFolderRightList("code NOT IN ('1', '64')", "TO_NUMBER(code)") 
			: fredDAO.getFolderRightList("code NOT IN ('1', '4', '8', '16')", "TO_NUMBER(code)");
	}
	
	public void addUserToFolder(UserFolder folder, Integer userId, Integer permissions) throws StorageAccessException {
		UserUtil userUtil = new UserUtil(factory);
		FolderUser folderUser = fredDAO.createNewFolderUser();
		folderUser.setUser(userUtil.getFrUserView(userId));
		folderUser.setUserRights(new Integer(permissions));
		folderUser.setFolder(folder.getFolder());
		folder.getFolder().getFolderUsers().add(folderUser);
		fredDAO.saveOrUpdate(folderUser);
	}

	public void removeUserFromFolder(UserFolder folder, Integer userId) throws StorageAccessException {
		UserUtil userUtil = new UserUtil(factory);
		FolderUser fu = getFolderUser(folder, userUtil.getFrUserView(userId));
		fredDAO.delete(fu);
	}

	public void toggleUserFolderRights(UserFolder folder, int userId, int newRight) throws StorageAccessException {
		UserUtil userUtil = new UserUtil(factory);
		FolderUser fu = getFolderUser(folder, userUtil.getFrUserView(userId));
		fu.setUserRights(new Integer(newRight ^ fu.getUserRights().intValue()));
		fredDAO.saveOrUpdate(fu);
	}
	
	public FolderUser getFolderUser(UserFolder folder, FrUserView user) throws StorageAccessException {
		List<FolderUser> fus = fredDAO.getList("FROM FolderUser AS f WHERE f.folder = ? AND f.user = ?", FolderUser.class, folder.getFolder(), user);
		if (fus.size() > 0)
			return fus.get(0);
		return null;
	}
	
	public static boolean isFolderEmpty(Folder folder) {
	    return (FREDUtil.isEmpty(folder.getAudits()) && FREDUtil.isEmpty(folder.getFeatures()));
	}
	
}