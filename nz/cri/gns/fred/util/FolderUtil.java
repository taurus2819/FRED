package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.FolderTypeDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.UserFolder;

import nz.cri.gns.auth.UserAccount;

/**
 * @author iainm
 */
public class FolderUtil extends ModelUtil {

	private FolderDAO folderDAO;
	private FolderTypeDAO typeDAO;
	private DAOFactory factory;
	
	public FolderUtil(DAOFactory dao) {
		super(dao);
		this.folderDAO = dao.getFolderDAO();
		this.typeDAO = dao.getFolderTypeDAO();
	}
	
	/**
	 *@return a <code>List</code> of <code>UserFolder</code>s
	 */
	public List getPersonalFolders(UserAccount user) throws StorageAccessException {
		Vector folders = new Vector();
		folders.addAll(folderDAO.getPersonalFolders(Integer.parseInt(user.getId())));
		folders.addAll(folderDAO.getAccessibleFolders(Integer.parseInt(user.getId()), typeDAO.getFolderType("Personal")));
		
		Collections.sort(folders);
		return folders;
	}
	
	/**
	 *@return a <code>List</code> of <code>UserFolder</code>s
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
	
	public int getMasterfileFolderFeatureCount(Folder folder) throws StorageAccessException {
		return folderDAO.getWaitingMasterfileFeatureCount(folder);
	}
	
	public UserFolder getUserFolder(int folderId, UserAccount user) throws NumberFormatException, StorageAccessException {
		return folderDAO.getUserFolder(folderId, Integer.parseInt(user.getId()));
	}
}
