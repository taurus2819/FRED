package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.FolderTypeDAO;
import nz.cri.gns.fred.dao.StorageAccessException;

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
	
	public List getPersonalFolders(int userId) throws StorageAccessException {
		Vector folders = new Vector();
		folders.addAll(folderDAO.getPersonalFolders(userId));
		folders.addAll(folderDAO.getAccessibleFolders(userId, typeDAO.getFolderType("Personal")));
		
		Collections.sort(folders);
		return folders;
	}
	
	public List getAdminFolders(int userId) throws StorageAccessException {
		List folders = folderDAO.getAccessibleFolders(userId, typeDAO.getFolderType("Admin"));
		Collections.sort(folders);
		
		return folders;
	}
}
