package nz.cri.gns.fred.hibernate.dao;

import java.util.Collection;
import java.util.List;

import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.type.IntegerType;
import net.sf.hibernate.type.StringType;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FeatureDAO;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.FolderTypeDAO;
import nz.cri.gns.fred.dao.RecordDAO;
import nz.cri.gns.fred.dao.SampleDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.dao.TaxonomicGroupDAO;
import nz.cri.gns.fred.hibernate.AuditTable;
import nz.cri.gns.fred.hibernate.FolderUser;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FeatureMeta;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FolderRight;
import nz.cri.gns.fred.model.FolderType;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RegistrationArea;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SampleMeta;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.UserFolder;

/**
 * @author iainm
 */
public class HibernateDAOFactory implements DAOFactory, RecordDAO, SampleDAO, FolderDAO, FolderTypeDAO, FeatureDAO, TaxonomicGroupDAO {

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

	public List<UserFolder> getPersonalFolders(int ownerId) throws StorageAccessException {
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

	public List<UserFolder> getAccessibleFolders(int userId, FolderType type) throws StorageAccessException {
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
	    return (Folder)save((Object)folder);
	}

    public void delete(Folder folder) throws StorageAccessException {
    	delete((Object)folder);
    }
    
    	
    
	private void delete(Object object) throws StorageAccessException {
		try {
	        Session session = provider.currentSession();
	        session.delete(object);
	    } catch (Exception e) {
	        throw new StorageAccessException(e);
	    }
    }
    
    /**
     * Returns the folder with the given id
     */
    public Folder getFolder(int folderId) throws StorageAccessException {
		return (Folder)getFirst("FROM Folder WHERE folderId = ?", folderId);
    }

	public int getWaitingMasterfileFeatureCount(Folder folder) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("SELECT count(f) FROM Feature AS f WHERE f.masterFile = :folder AND f.audit.status = :wait");
			query.setEntity("folder", folder);
			query.setString("wait", FREDConstants.WAITING);
			List list = query.list();
			return ((Integer)list.get(0)).intValue();
        } catch (Exception e) {
            throw new StorageAccessException(e);
		}
	}
	
	public UserFolder getUserFolder(int folderId, int userId) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			List list = session.find("FROM Folder AS f WHERE f.folderId = ?", new Integer(folderId), new IntegerType());
			if (list.size() == 0)
				return null;
			
			Folder folder = (Folder)list.get(0);
			if (folder.getOwnerId() != null && folder.getOwnerId().intValue() == userId) {
				return UserFolder.getOwnedUserFolder(folder);
			}
			
			Query query = session.createQuery("FROM FolderUser AS fu WHERE fu.folder = :folder AND fu.comp_id.userId = :user");
			query.setEntity("folder", folder);
			query.setInteger("user", userId);
			list = query.list();
			if (list.size() == 0)
				return null;
			return UserFolder.getAccessibleUserFolder(folder, ((FolderUser)list.get(0)).getUserRights().intValue());
        } catch (Exception e) {
            throw new StorageAccessException(e);
		}
	}

	public List getWorkingAuditsFor(Folder folder) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("FROM AuditTable as audit WHERE audit.folder = :folder AND audit.status = :working");
			query.setEntity("folder", folder);
			query.setString("working", FREDConstants.WORKING);
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

	public List<FolderRight> getFolderRightList(String join, String order) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			return session.find("FROM FolderRight WHERE " + join + " ORDER BY " + order);
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}
	

	public List<nz.cri.gns.fred.model.FolderUser> getNonOwningUsers(Folder folder) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("FROM FolderUser AS fu WHERE fu.folder = :foldr");
			query.setEntity("foldr", folder);
			return query.list();
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}


	public TaxonomicGroupDAO getTaxonomicGroupDAO() {
		return this;
	}

	//TaxonomicDAO methods
	public List getPanelsIsMemberOf(int userId) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			return session.find("SELECT g FROM TaxaPanel tp INNER JOIN tp.taxonomicGroup g WHERE tp.comp_id.panelistId = ?", new Integer(userId), new IntegerType());
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}


	public int getProvisionalCount(TaxonomicGroup group) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("SELECT count(taxon) FROM TaxonomicLookup AS taxon WHERE taxon.taxonomicGroup = :group AND taxon.status = :prov AND taxon.taxonomicName IS NOT NULL");
			query.setEntity("group", group);
			query.setString("prov", FREDConstants.PROVISIONAL);
			List list = query.list();
			return ((Integer)list.get(0)).intValue();
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

	public FeatureDAO getFeatureDAO() {
		return this;
	}

	//FeatureDAO methods
	public Audit createNewAudit() {
		return new AuditTable();
	}

	public Audit save(Audit audit) throws StorageAccessException {
	    return (Audit)save((Object)audit);
	}

	private Object save(Object object) throws StorageAccessException {
		try {
	        Session session = provider.currentSession();
	        session.save(object);
	        return object;
	    } catch (Exception e) {
	        throw new StorageAccessException(e);
	    }
	}
	
	private Object saveOrUpdate(Object object) throws StorageAccessException {
		try {
	        Session session = provider.currentSession();
	        session.saveOrUpdate(object);
	        return object;
	    } catch (Exception e) {
	        throw new StorageAccessException(e);
	    }
	}

	public Feature cloneFeature(Feature feature) {
		return (Feature)((nz.cri.gns.fred.hibernate.Feature)feature).clone();
	}

	public FeatureMeta createNewFeatureMeta() {
		return new nz.cri.gns.fred.hibernate.FeatureMeta(false);
	}

	public Feature save(Feature newFeature) throws StorageAccessException {
	    return (Feature)save((Object)newFeature);
	}

	public void delete(Feature feature) throws StorageAccessException {
		delete((Object)feature);
	}
	
	public void update(Feature feature) throws StorageAccessException {
		update((Object)feature);
	}

	private void update(Object object) throws StorageAccessException {
		try {
	        Session session = provider.currentSession();
	        session.update(object);
	    } catch (Exception e) {
	        throw new StorageAccessException(e);
	    }
	}

	public void update(Audit audit) throws StorageAccessException {
		update((Object)audit);
	}

	public Feature getFeature(int featureId) throws StorageAccessException {
		return (Feature)getFirst("FROM Feature as f WHERE f.featureId = ?", featureId);
	}

	public int getNextAvailableSerialNumber(String mapSheet) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
			List list = session.find("SELECT max(fr.serialNumber) FROM FrNumber AS fr WHERE AND fr.serialNumber < 6000 AND fr.mapSheet = ?", mapSheet, new StringType());
			if (list.size() == 0)
			    return 1;
			return ((Integer)list.get(0)).intValue() + 1;
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}

	public Feature createNewFeature() {
		return new nz.cri.gns.fred.hibernate.Feature();
	}

	public RegistrationArea getRegistrationArea(int regAreaId) throws StorageAccessException {
		return (RegistrationArea) getFirst("FROM registrationArea ra WHERE ra.regAreaId = ?", regAreaId);
	}
	
	private Object getFirst(String query, int id) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
			List list = session.find(query, new Integer(id), new IntegerType());
			if (list.size() == 0)
			    return null;
			return list.get(0);
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}

	public Collection<? extends Feature> getFeaturesBySample(Audit audit) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
			Query query = session.createQuery("SELECT ftre FROM Sample AS smple INNER JOIN smple.feature AS ftre WHERE smple.audit = :adt");
			query.setEntity("adt", audit);
			return query.list();
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}
	
	public Collection<? extends Feature> getFeaturesByRecord(Audit audit) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
			Query query = session.createQuery("SELECT ftre FROM Record AS rec INNER JOIN rec.sample AS smple INNER JOIN smple.feature AS ftre WHERE rec.audit = :adt");
			query.setEntity("adt", audit);
			return query.list();
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}
	
	public AuditEdit createNewAuditEdit() throws StorageAccessException {
		return new nz.cri.gns.fred.hibernate.AuditEdit();
	}

	public void save(AuditEdit edit) throws StorageAccessException {
		save((Object)edit);
	}

	public void saveOrUpdate(Feature feature) throws StorageAccessException {
		saveOrUpdate((Object)feature);
	}

	public SampleDAO getSampleDAO() {
		return this;
	}

	//SampleDAO methods
	public Sample cloneSample(Sample sample) {
		return (Sample)((nz.cri.gns.fred.hibernate.Sample)sample).clone();
	}

	public Relationship cloneRelationship(Relationship relationship) {
		return (Relationship)((nz.cri.gns.fred.hibernate.Relationship)relationship).clone();
	}

	public SentTo cloneSentTo(SentTo sentTo) {
		return (SentTo)((nz.cri.gns.fred.hibernate.SentTo)sentTo).clone();
	}

	public SedimentaryFeature cloneSedimentaryFeature(SedimentaryFeature sedFeature) {
		return (SedimentaryFeature)((nz.cri.gns.fred.hibernate.SedimentaryFeature)sedFeature).clone();
	}

	public SampleMeta createSampleMeta() {
		return new nz.cri.gns.fred.hibernate.SampleMeta(false);
	}

	public Sample save(Sample newSample) throws StorageAccessException {
	    return (Sample)save((Object)newSample);
	}

	public Sample getSample(int sampleId) throws StorageAccessException {
		return (Sample)getFirst("FROM Sample as s WHERE s.sampleId = ?", sampleId);
	}

	public void delete(Sample sample) throws StorageAccessException {
		delete((Object)sample);
	}
	
	public void update(Sample sample) throws StorageAccessException {
		update((Object)sample);
	}

	public FrNumber createFRNumber() {
		return new nz.cri.gns.fred.hibernate.FrNumber();
	}
	
	public AuditEdit getMostRecentEdit(Audit audit) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("FROM AuditEdit as edit WHERE edit.editedDate = (SELECT max(editedDate) FROM auditEdit WHERE audit = edit.audit) AND edit.audit = :audit");
            query.setEntity("audit", audit);
            List list = query.list();
			if (list.size() == 0)
			    return null;
			return (AuditEdit)list.get(0);
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}

	public RecordDAO getRecordDAO() {
		return this;
	}

	public Record getRecord(int recordId) throws StorageAccessException {
		return (Record)getFirst("FROM Record as r WHERE r.recordId = ?", recordId);
	}

	public void delete(Record record) throws StorageAccessException {
		delete((Object)record);
	}

}
