package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.FolderTypeDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FolderRight;

import nz.cri.gns.auth.UserAccount

;
/**
 * @author iainm
 */
public class FolderUtil {

	private FolderDAO folderDAO;
	private FolderTypeDAO typeDAO;
	
	public FolderUtil(DAOFactory dao) {
		this.folderDAO = dao.getFolderDAO();
		this.typeDAO = dao.getFolderTypeDAO();
	}
	
	public List getPersonalFolders(UserAccount user) throws StorageAccessException {
		Vector folders = new Vector();
		folders.addAll(folderDAO.getPersonalFolders(Integer.parseInt(user.getId())));
		folders.addAll(folderDAO.getAccessibleFolders(Integer.parseInt(user.getId()), typeDAO.getFolderType("Personal")));
		
		Collections.sort(folders);
		return folders;
	}
	
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
	
	public boolean getUserHasAdminRights(Folder folder, UserAccount user) {
	    int userId = Integer.parseInt(user.getId());
	    if (folder.getOwnerId().intValue() == userId)
	        return true;
	    else {
	    	//Commented out by Ben so will compile
	        //FolderRight right = folderDAO.getFolderRight(folder, userId);
	    	return false;
	    }
	}
}
