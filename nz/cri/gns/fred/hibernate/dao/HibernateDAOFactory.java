package nz.cri.gns.fred.hibernate.dao;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.type.IntegerType;
import net.sf.hibernate.type.ManyToOneType;
import net.sf.hibernate.type.StringType;
import net.sf.hibernate.type.Type;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.fred.dao.AuditDAO;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FeatureDAO;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.FolderTypeDAO;
import nz.cri.gns.fred.dao.PersonDAO;
import nz.cri.gns.fred.dao.RecordDAO;
import nz.cri.gns.fred.dao.SampleDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.dao.TaxonomicDAO;
import nz.cri.gns.fred.dao.TaxonomicGroupDAO;
import nz.cri.gns.fred.hibernate.AuditTable;
import nz.cri.gns.fred.hibernate.FolderUser;
import nz.cri.gns.fred.hibernate.PalList;
import nz.cri.gns.fred.hibernate.TaxonomicLookup;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.BedThickness;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.Carbonate;
import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.model.DrillType;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FeatureMeta;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FolderRight;
import nz.cri.gns.fred.model.FolderType;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.GrainSize;
import nz.cri.gns.fred.model.Hardness;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RecordDetails;
import nz.cri.gns.fred.model.RegistrationArea;
import nz.cri.gns.fred.model.RelationType;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.RelationshipType;
import nz.cri.gns.fred.model.RockColour;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SampleMeta;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SedimentaryFeatureType;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.model.TaxaPanel;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.model.Weathering;

/**
 * @author iainm
 */
public class HibernateDAOFactory implements TaxonomicDAO, DAOFactory, PersonDAO, RecordDAO, SampleDAO, FolderDAO, FolderTypeDAO, FeatureDAO, TaxonomicGroupDAO, AuditDAO {

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

	public List<UserFolder> getOwnedFolders(int ownerId, FolderType type) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			//List list = session.find("from Folder as folder where folder.ownerId = ?", new Integer(ownerId), new IntegerType());
			Query query = session.createQuery("FROM Folder as folder where folder.ownerId = :owner and folder.folderType = :type");
			query.setInteger("owner", ownerId);
			query.setEntity("type", type);
			List list = query.list();
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
			Query query = session.createQuery("FROM FolderUser as fu INNER JOIN fu.folder_ AS f WHERE f.folderType = :type AND fu.comp_id.userId = :user");
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
		return (Folder)getFirst("FROM Folder AS f WHERE f.folderId = ?", folderId);
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
			
			Query query = session.createQuery("FROM FolderUser AS fu WHERE fu.folder_ = :folder AND fu.comp_id.userId = :user");
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

	public List<Audit> getAuditsFor(Folder folder, String status) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("FROM AuditTable as audit WHERE audit.folder = :folder AND audit.status = :status");
			query.setEntity("folder", folder);
			query.setString("status", status);
			return query.list();
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}

	public List<Audit> getAuditsFor(Folder folder) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("FROM AuditTable as audit WHERE audit.folder = :folder");
			query.setEntity("folder", folder);
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

	public TaxonomicGroupDAO getTaxonomicGroupDAO() {
		return this;
	}

	//TaxonomicDAO methods
	public TaxonomicGroup getTaxonomicGroup(int groupId) throws StorageAccessException {
		return (TaxonomicGroup)getFirst("FROM TaxonomicGroup as tg WHERE tg.groupId = ?", groupId);
	}
	
	public List getPanelsIsMemberOf(int userId) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			return session.find("SELECT g FROM TaxaPanel tp INNER JOIN tp.taxonomicGroup g WHERE tp.comp_id.panelistId = ?", new Integer(userId), new IntegerType());
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}
	

	public List<Integer> getPanelsIsMemberOf(TaxonomicGroup group) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			return session.find("SELECT tp.comp_id.panelistId FROM TaxaPanel tp WHERE tp.taxonomicGroup = ?", group, new ManyToOneType(nz.cri.gns.fred.hibernate.TaxonomicGroup.class));
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

	public TaxonomicGroup findTaxonomicGroup(String groupName) throws StorageAccessException {
		return (TaxonomicGroup)getFirst("FROM TaxonomicGroup As g WHERE g.name = ?", groupName);
	}
	
	public TaxaPanel createNewTaxaPanel() {
		return new nz.cri.gns.fred.hibernate.TaxaPanel(false);
	}
	
	public TaxaPanel save(TaxaPanel panel) throws StorageAccessException {
	    return (TaxaPanel)save((Object)panel);
	}

	public TaxonomicGroup save(TaxonomicGroup group) throws StorageAccessException {
	    return (TaxonomicGroup)save((Object)group);
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

	public Audit update(Audit audit) throws StorageAccessException {
		update((Object)audit);
        return audit;
	}

	public void delete(Audit audit) throws StorageAccessException {
		delete((Object)audit);
	}	
	
	public Feature getFeature(int featureId) throws StorageAccessException {
		return (Feature)getFirst("FROM Feature as f WHERE f.featureId = ?", featureId);
	}

	public int getNextAvailableSerialNumber(String mapSheet) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
			List list = session.find("SELECT max(fr.serialNumber) FROM FrNumber AS fr WHERE fr.serialNumber < 6000 AND fr.mapSheet = ?", mapSheet, new StringType());
			if (list.size() == 0 || list.get(0) == null)
			    return 1;
			return ((Integer)list.get(0)).intValue() + 1;
        } catch (Exception e) {
        	e.printStackTrace();
            throw new StorageAccessException(e);
        }
	}

	public Feature createNewFeature() {
		return new nz.cri.gns.fred.hibernate.Feature();
	}

	public RegistrationArea getRegistrationArea(int regAreaId) throws StorageAccessException {
		return get(nz.cri.gns.fred.hibernate.RegistrationArea.class, regAreaId);
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

	private Object getFirst(String query, String value) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
			List list = session.find(query, value, new StringType());
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

	public void delete(AuditEdit edit) throws StorageAccessException {
		delete((Object)edit);
	}
	
	public void saveOrUpdate(Feature feature) throws StorageAccessException {
		saveOrUpdate((Object)feature);
	}

	public FrNumber getFrNumber(String frNum) throws StorageAccessException {
		return (FrNumber)getFirst("FROM FrNumber AS f WHERE f.frNumber = ?", frNum);
	}

	public Feature getFeatureWithName(String name) throws StorageAccessException {
		return (Feature)getFirst("FROM Feature AS f WHERE f.featureName = ?", name);
	}

    public void saveOrUpdate(Audit audit) throws StorageAccessException {
        saveOrUpdate((Object)audit);
    }

	public List<Feature> getFeaturesInMasterfile(Folder masterfileFolder, Date startDate, Date endDate, String status) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("SELECT feat FROM Feature as feat INNER JOIN feat.audit AS audit WHERE feat.masterFile = :folder AND "
            		+ (status.equals(FREDConstants.WAITING) ? "audit.submittedDate" : "audit.approvedDate")
            		+ " BETWEEN :start AND :end AND audit.status = :status");
            query.setEntity("folder", masterfileFolder);
            query.setTimestamp("start", startDate);
            query.setTimestamp("end", endDate);
            query.setString("status", status);
            return query.list();
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}

	public List<Feature> getFeaturesInMasterfile(Folder masterfileFolder, String status) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("SELECT feat FROM Feature as feat INNER JOIN feat.audit AS audit WHERE feat.masterFile = :folder AND audit.status = :status");
            query.setEntity("folder", masterfileFolder);
            query.setString("status", status);
            return query.list();
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}

	public List<FrNumber> getFrNumbers(String mapSheet, int start, int end) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
            Query query = session.createQuery("FROM FrNumber as num WHERE num.mapSheet = :map AND num.serialNumber BETWEEN :start AND :end");
            query.setString("map", mapSheet);
            query.setInteger("start", start);
            query.setInteger("end", end);
            return query.list();
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}

	public SampleDAO getSampleDAO() {
		return this;
	}

	//SampleDAO methods
	public Relationship cloneRelationship(Relationship relationship) {
		return (Relationship)((nz.cri.gns.fred.hibernate.Relationship)relationship).clone();
	}

	public SentTo cloneSentTo(SentTo sentTo) {
		return (SentTo)((nz.cri.gns.fred.hibernate.SentTo)sentTo).clone();
	}

	public SedimentaryFeature cloneSedimentaryFeature(SedimentaryFeature sedFeature) {
		SedimentaryFeature sedF = new nz.cri.gns.fred.hibernate.SedimentaryFeature();
		sedF.setAbundant(sedFeature.getAbundant());
		sedF.setSedimentaryFeatureType(sedFeature.getSedimentaryFeatureType());
		return sedF;
	}

	public SampleMeta createNewSampleMeta() {
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

	public Sample createNewSample() {
		return new nz.cri.gns.fred.hibernate.Sample();
	}

	public SedimentaryFeature createNewSedimentaryFeature() {
		return new nz.cri.gns.fred.hibernate.SedimentaryFeature();
	}

	public RelationType getRelationType(String relationTypeName) throws StorageAccessException {
		return (RelationType)getFirst("FROM RelationType AS rt WHERE rt.name = ?", relationTypeName);
	}

	public RelationshipType getRelationshipType(RelationType relationType, String relationshipTypeName) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("FROM RelationshipType AS rel WHERE rel.relationType = :reltype AND rel.name = :name");
            query.setEntity("reltype", relationType);
            query.setString("name", relationshipTypeName);
            List list = query.list();
			if (list.size() == 0)
			    return null;
			return (RelationshipType)list.get(0);
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}

	public List<? extends Relationship> getRelationships(Sample sample, RelationshipType relationshipType) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("FROM Relationship AS rel WHERE rel.relationshipType = :reltype AND rel.sample = :samp");
            query.setEntity("reltype", relationshipType);
            query.setEntity("samp", sample);
            return query.list();
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}

	public FossilGroup getFossilGroup(String name) throws StorageAccessException {
		return (FossilGroup)getFirst("FROM FossilGroup AS fg WHERE fg.name = ?", name);
	}

	public SentTo createNewSentTo() {
		return new nz.cri.gns.fred.hibernate.SentTo();
	}

	public Stage findStage(String startStageId, boolean startUncertain, String stopStageId, boolean stopUncertain) throws StorageAccessException {
		try {
			StringBuffer query = new StringBuffer("FROM Stage AS s WHERE ");
			HashMap<String, Integer> intData = new  HashMap<String, Integer>(2);
			Vector<String> strData = new Vector<String>(2);
			if (startStageId == null) {
				query.append("s.stageLowerId IS NULL ");
			} else {
				query.append("s.stageLowerId = :lower ");
				intData.put("lower", new Integer(startStageId));
			}
			if (startUncertain) {
				query.append("s.stageLowerMod = :lmod ");
				strData.add("lmod");
			} else {
				query.append("s.stageLowerMod IS NULL ");
			}
			if (stopStageId == null) {
				query.append("s.stageUpperId IS NULL ");
			} else {
				query.append("s.stageUpperId = :upper ");
				intData.put("upper", new Integer(stopStageId));
			}
			if (stopUncertain) {
				query.append("s.stageUpperMod = :umod");
				strData.add("umod");
			} else {
				query.append("s.stageUpperMod IS NULL");
			}
			
            Session session = provider.currentSession();
            Query hquery = session.createQuery(query.toString());
            for (String str : strData) {
            	hquery.setString(str, "?");
            }
            for (String str : intData.keySet()) {
            	hquery.setInteger(str, intData.get(str));
            }
            List list = hquery.list();
            if (list.size() == 0)
            	return null;
            return (Stage)list.get(0);
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}

	public Stage createNewStage() {
		return new nz.cri.gns.fred.hibernate.Stage();
	}

	public void save(Stage stage) throws StorageAccessException {
		save((Object)stage);
	}

	public Relationship createNewRelationship() {
		return new nz.cri.gns.fred.hibernate.Relationship();
	}

	public void save(Relationship rel) throws StorageAccessException {
		save((Object)rel);
	}

	public GrainSize getGrainSize(Integer id) throws StorageAccessException {
		return get(nz.cri.gns.fred.hibernate.GrainSize.class, id);
	}

	private <T> T get(Class<T> clazz, Integer id) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			return (T)session.get(clazz, id);
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}

	public Hardness getHardness(Integer id) throws StorageAccessException {
		return get(nz.cri.gns.fred.hibernate.Hardness.class, id);
	}

	public Weathering getWeathering(Integer id) throws StorageAccessException {
		return get(nz.cri.gns.fred.hibernate.Weathering.class, id);
	}

	public Bedding getBedding(Integer id) throws StorageAccessException {
		return get(nz.cri.gns.fred.hibernate.Bedding.class, id);
	}

	public BedThickness getBeddingThickness(Integer id) throws StorageAccessException {
		return get(nz.cri.gns.fred.hibernate.BedThickness.class, id);
	}

	public RockColour getRockColour(Integer id) throws StorageAccessException {
		return get(nz.cri.gns.fred.hibernate.RockColour.class, id);
	}

	public ColourModifier getColourModifier(Integer id) throws StorageAccessException {
		return get(nz.cri.gns.fred.hibernate.ColourModifier.class, id);
	}

	public Carbonate getCarbonate(Integer id) throws StorageAccessException {
		return get(nz.cri.gns.fred.hibernate.Carbonate.class, id);
	}
	
	public SedimentaryFeatureType getSedimentaryFeatureTypeWithName(String sedFeature) throws StorageAccessException {
		return (SedimentaryFeatureType)getFirst("FROM SedimentaryFeatureType AS t WHERE t.name = ?", sedFeature);
	}

	public DrillType getDrillType(int drillTypeId) throws StorageAccessException {
		try {
			return (DrillType) provider.currentSession().get(nz.cri.gns.fred.hibernate.DrillType.class, drillTypeId);
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}
    
    public void saveOrUpdate(Sample sample) throws StorageAccessException {
        saveOrUpdate((Object)sample);
    }

    public void attach(Object object) throws StorageAccessException {
        try {
            provider.currentSession().refresh(object);
        } catch (HibernateException e) {
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

    public Record createNewRecord() {
        return new nz.cri.gns.fred.hibernate.Record();
    }

    public Paleontology createNewPaleontology() {
        return new nz.cri.gns.fred.hibernate.Paleontology();
    }

    public Adoption createNewAdoption() {
        return new nz.cri.gns.fred.hibernate.Adoption();
    }

    public Folder getMasterfileFolder(Record record) throws StorageAccessException {
        return (Folder)getFirst("SELECT f FROM Record AS r INNER JOIN r.sample AS s INNER JOIN s.feature AS feat INNER JOIN feat.masterFile AS f WHERE r.recordId = ?", record.getRecordId());
    }
    
    public void save(Record record) throws StorageAccessException {
        save((Object)record);
    }

    public void update(Record record) throws StorageAccessException {
        update((Object)record);
    }

	public List<Lab> getAllLabs() throws StorageAccessException {
		try {
            Session session = provider.currentSession();
			return session.find("SELECT DISTINCT l FROM LabSection AS ls INNER JOIN ls.lab AS l ORDER BY l.name");
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}

	public List<PaleontologyListEntry> getListEntries(Paleontology pal, TaxonomicGroup group) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
			Query query = session.createQuery("SELECT ple FROM PalList AS ple INNER JOIN ple.paleontology AS p WHERE ple.taxonomicGroup = :grp AND p = :pal");
			query.setEntity("grp", group);
			query.setEntity("pal", pal);
			return query.list();
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}
    
    public LabSection getLabSection(int id) throws StorageAccessException {
        try {
            return (LabSection)provider.currentSession().get(nz.cri.gns.fred.hibernate.LabSection.class, id);
        } catch (HibernateException e) {
            throw new StorageAccessException(e);
        }
    }
	
    public PersonDAO getPersonDAO() {
		return this;
	}

	public Person createNewPerson() {
		return new nz.cri.gns.fred.hibernate.Person();
	}

	public void save(Person person) throws StorageAccessException {
		save((Object)person);
	}

	public Person getPerson(String name) throws StorageAccessException {
		return (Person)getFirst("FROM Person As p WHERE p.name = ?", name);
	}

    public TaxonomicDAO getTaxonomicDAO() {
        return this;
    }

    public PaleontologyListEntry createNewPaleontologyListEntry() {
        return new PalList();
    }

    public Taxon getTaxon(TaxonomicGroup taxonomicGroup, String name, String author) throws StorageAccessException {
        try {
        	//We do have an author
            Query query = provider.currentSession().createQuery("FROM TaxonomicLookup AS t WHERE t.taxonomicGroup = :grp AND t.taxonomicName = :name AND t.author = :author");
            query.setEntity("grp", taxonomicGroup);
            query.setString("name", name);
            query.setString("author", author);
            List list = query.list();
            if (list == null || list.size() == 0) {
           		return null;

            }
            return (Taxon)list.get(0);
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
    }

    public Taxon getTaxon(TaxonomicGroup taxonomicGroup, String name) throws StorageAccessException {
        try {
            Query query = provider.currentSession().createQuery("FROM TaxonomicLookup AS t WHERE t.taxonomicGroup = :grp AND t.taxonomicName = :name");
            query.setEntity("grp", taxonomicGroup);
            query.setString("name", name);
            List list = query.list();
            if (list == null || list.size() == 0) {
	        	return null;
            }
            return (Taxon)list.get(0);
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
    }
    
    public Taxon createNewTaxon() {
        return new TaxonomicLookup();
    }

	public void save(Taxon taxon) throws StorageAccessException {
		save((Object)taxon);
	}

	public void save(RecordDetails details) throws StorageAccessException {
		save((Object)details);
	}

	public void update(RecordDetails details) throws StorageAccessException {
		update((Object)details);
	}

	public nz.cri.gns.fred.model.FolderUser createNewFolderUser() {
		return new FolderUser(false);
	}

	public void save(nz.cri.gns.fred.model.FolderUser folderUser) throws StorageAccessException {
		save((Object)folderUser);
	}

	public void delete(nz.cri.gns.fred.model.FolderUser user) throws StorageAccessException {
		delete((Object)user);
		//Need to flush straight away
		try {
	        Session session = provider.currentSession();
	        session.flush();
	    } catch (Exception e) {
	        throw new StorageAccessException(e);
	    }
	}

	public void update(nz.cri.gns.fred.model.FolderUser user) throws StorageAccessException {
		update((Object)user);
	}
	
	public AuditDAO getAuditDAO() {
		return this;
	}
}
