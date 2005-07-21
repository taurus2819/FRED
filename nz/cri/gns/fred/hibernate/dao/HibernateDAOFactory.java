package nz.cri.gns.fred.hibernate.dao;

import java.util.List;

import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.type.IntegerType;
import net.sf.hibernate.type.StringType;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.FolderTypeDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.dao.TaxonomicGroupDAO;
import nz.cri.gns.fred.hibernate.FolderUser;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FolderType;
import nz.cri.gns.fred.model.UserFolder;

/**
 * @author iainm
 */
public class HibernateDAOFactory implements DAOFactory, FolderDAO, FolderTypeDAO, TaxonomicGroupDAO {

	private HibernateProvider provider;

	public HibernateDAOFactory(HibernateProvider provider) {
		this.provider = provider;
	}
	
	public FolderDAO getFolderDAO() {
		return this;
	}

	public Folder createNewFolder() {
		return new nz.cri.gns.fred.hibernate.Folder();
	}

	public List getPersonalFolders(int ownerId) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			List list = session.find("from Folder as folder where folder.ownerId = ?", new Integer(ownerId), new IntegerType());
			for (int i=0; i<list.size(); i++) {
				Folder folder = (Folder)list.get(i);
				list.set(i, UserFolder.getOwnedUserFolder(folder));
			}
			return list;
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}

	public List getAccessibleFolders(int userId, FolderType type) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("FROM FolderUser as fu INNER JOIN fu.folder AS f WHERE f.folderType = :type AND fu.comp_id.userId = :user");
			query.setEntity("type", type);
			query.setInteger("user", userId);
			List list = query.list();
			for (int i=0; i<list.size(); i++) {
				Object[] parts = (Object[])list.get(i);
				Folder folder = (Folder)parts[1];
				FolderUser rights = (FolderUser)parts[0];
				list.set(i, UserFolder.getAccessibleUserFolder(folder, rights.getUserRights().intValue()));
			}
			return list;
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}

	public Folder save(Folder folder) throws StorageAccessException {
	    try {
	        Session session = provider.currentSession();
	        session.save(folder);
	        return folder;
	    } catch (Exception e) {
	        throw new StorageAccessException(e);
	    }
	}

    public void delete(Folder folder) throws StorageAccessException {
	    try {
	        Session session = provider.currentSession();
	        session.delete(folder);
	    } catch (Exception e) {
	        throw new StorageAccessException(e);
	    }
    }
    
    /**
     * Returns the folder with the given id
     */
    public Folder getFolder(int folderId) throws StorageAccessException {
        try {
            Session session = provider.currentSession();
			List list = session.find("FROM Folder WHERE folderId = ?", new Integer(folderId), new IntegerType());
			if (list.size() == 0)
			    return null;
			return (Folder)list.get(0);
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
    }

	public List getWaitingMasterfileFeatures(Folder folder) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("FROM Feature AS f WHERE f.masterFile = :folder AND f.auditTable.status = :wait");
			query.setEntity("folder", folder);
			query.setString("wait", "waiting");
			return query.list();
        } catch (Exception e) {
            throw new StorageAccessException(e);
		}
	}

	public FolderTypeDAO getFolderTypeDAO() {
		return this;
	}

	public FolderType getFolderType(String label) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			return (FolderType)session.find("FROM FolderType WHERE name = ?", label, new StringType()).get(0);
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}

	public TaxonomicGroupDAO getTaxonomicGroupDAO() {
		return this;
	}

	public List getPanelsIsMemberOf(int userId) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			return session.find("SELECT g FROM TaxaPanel tp INNER JOIN tp.taxonomicGroup g WHERE tp.comp_id.panelistId = ?", new Integer(userId), new IntegerType());
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}

	public void closeSession() throws StorageAccessException {
		try {
			provider.closeSession();
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}

}
