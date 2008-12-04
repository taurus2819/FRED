package nz.cri.gns.fred.hibernate.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.MatchMode;
import net.sf.hibernate.expression.Order;
import net.sf.hibernate.type.IntegerType;
import net.sf.hibernate.type.StringType;
import nz.cri.gns.dataaccess.HibernateProvider;
import nz.cri.gns.dataaccess.HibernateUtils;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.dao.AuditDAO;
import nz.cri.gns.fred.dao.BacklogStatusDAO;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FeatureDAO;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.FolderTypeDAO;
import nz.cri.gns.fred.dao.PersonDAO;
import nz.cri.gns.fred.dao.RecordDAO;
import nz.cri.gns.fred.dao.SampleDAO;
import nz.cri.gns.fred.dao.SiteDAO;
import nz.cri.gns.fred.dao.StageDAO;
import nz.cri.gns.fred.dao.StratLexDAO;
import nz.cri.gns.fred.dao.TaxonomicDAO;
import nz.cri.gns.fred.dao.UserDAO;
import nz.cri.gns.fred.hibernate.AuditTable;
import nz.cri.gns.fred.hibernate.FolderUser;
import nz.cri.gns.fred.hibernate.PalList;
import nz.cri.gns.fred.hibernate.TaxonomicLookup;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.BacklogStatus;
import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.Country;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FeatureMeta;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FolderRight;
import nz.cri.gns.fred.model.FolderType;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.FrUser;
import nz.cri.gns.fred.model.FrUserView;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.LogTable;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RelationType;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.RelationshipType;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SampleMeta;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SedimentaryFeatureType;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.model.StratigraphicUnit;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.UserFolder;

/**
 * @author iainm
 */
public class HibernateDAOFactory implements DAOFactory, TaxonomicDAO, PersonDAO, RecordDAO, SampleDAO, FolderDAO, FolderTypeDAO, FeatureDAO, AuditDAO, BacklogStatusDAO, StratLexDAO, UserDAO, StageDAO, SiteDAO {

	private HibernateProvider provider;

	public HibernateDAOFactory(HibernateProvider provider) {
		this.provider = provider;
	}
	
	public <T> T saveOrUpdate(T object) throws StorageAccessException {
		return HibernateUtils.saveOrUpdate(provider, object);
	}
	
	public <T> T save(T object) throws StorageAccessException {
		return HibernateUtils.save(provider, object);
	}
	
	public void delete(Object object) throws StorageAccessException {
		HibernateUtils.delete(provider, object);
	}
	
	public <T> T get(Integer id, Class<T> clazz) {
		try {
			return HibernateUtils.get(provider, clazz, id);
		} catch (StorageAccessException e) {
			e.printStackTrace();
		}
		return null;
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

	public List<Folder> getFolders(FolderType type) throws HibernateException, StorageAccessException {
		Session session = provider.currentSession();
		Query query = session.createQuery("FROM Folder as f WHERE f.folderType = :type");
			query.setEntity("type", type);
			List list = query.list();
			return list;		
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
			e.printStackTrace();
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

	/**
	 * @deprectaed use getTaxaCount
	 */
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

	public int getTaxaCount(TaxonomicGroup group, String status) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("SELECT count(taxon) FROM TaxonomicLookup AS taxon WHERE taxon.taxonomicGroup = :group AND taxon.status = :prov AND taxon.taxonomicName IS NOT NULL");
			query.setEntity("group", group);
			query.setString("prov", status);
			List list = query.list();
			return ((Integer)list.get(0)).intValue();
        } catch (Exception e) {
            throw new StorageAccessException(e);
		}
	}	
	
	public List<Taxon> getTaxa(TaxonomicGroup group, String status) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("SELECT taxon FROM TaxonomicLookup AS taxon WHERE taxon.taxonomicGroup = :group AND taxon.status = :prov AND taxon.taxonomicName IS NOT NULL");
			query.setEntity("group", group);
			query.setString("prov", status);
			return query.list();
        } catch (Exception e) {
            throw new StorageAccessException(e);
		}		
	}
	
	public TaxonomicGroup findTaxonomicGroup(String groupName) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM TaxonomicGroup As g WHERE g.name = ?", groupName, TaxonomicGroup.class);
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
		Audit audit = new AuditTable();
		audit.setConfidentialFlag(false);
		return audit;
	}

	public Feature cloneFeature(Feature feature) {
		return (Feature)((nz.cri.gns.fred.hibernate.Feature)feature).clone();
	}

	public FeatureMeta createNewFeatureMeta() {
		return new nz.cri.gns.fred.hibernate.FeatureMeta(false);
	}

	public int getNextAvailableSerialNumber(String mapSheet) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
			List list = session.find("SELECT max(fr.serialNumber) FROM FrNumber AS fr WHERE fr.serialNumber < 6000 AND fr.obsolete IS NULL AND fr.mapSheet = ?", mapSheet, new StringType());
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
	
	public FrNumber getFrNumber(String frNum) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM FrNumber AS f WHERE f.frNumber = ? AND f.obsolete IS NULL", frNum, FrNumber.class);
	}

	public FrNumber getYardFrNumber(String frNum) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM FrNumber AS f WHERE f.frNumber = ? AND f.obsolete IS NOT NULL", frNum, FrNumber.class);
	}
	
	public Feature getFeatureWithName(String name) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM Feature AS f WHERE f.featureName = ?", name, Feature.class);
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

	public List<FrNumber> getFrNumbers(String mapSheet) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
            Query query = session.createQuery("FROM FrNumber as num WHERE num.mapSheet = :map AND num.obsolete IS NULL");
            query.setString("map", mapSheet);
            List<FrNumber> frNums = query.list();
            Collections.sort(frNums);
            return frNums;
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}
	
	public List<FrNumber> getFrNumbers(String mapSheet, int start, int end) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
            Query query = session.createQuery("FROM FrNumber as num WHERE num.mapSheet = :map AND num.serialNumber BETWEEN :start AND :end AND num.obsolete IS NULL");
            query.setString("map", mapSheet);
            query.setInteger("start", start);
            query.setInteger("end", end);
            List<FrNumber> frNums = query.list();
            Collections.sort(frNums);
            return frNums;
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

	public Sample createNewSample(Feature feature) {
		nz.cri.gns.fred.hibernate.Sample sample = new nz.cri.gns.fred.hibernate.Sample();
		sample.setFeature(feature);
		if (feature.getSamples() != null)
			feature.getSamples().add(sample);
		else {
			Set<Sample> samples = new HashSet<Sample>();
			samples.add(sample);
			feature.setSamples(samples);
		}
		return sample;
	}

	public SedimentaryFeature createNewSedimentaryFeature() {
		return new nz.cri.gns.fred.hibernate.SedimentaryFeature();
	}

	public RelationType getRelationType(String relationTypeName) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM RelationType AS rt WHERE rt.name = ?", relationTypeName, RelationType.class);
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
		return HibernateUtils.getFirst(provider, "FROM FossilGroup AS fg WHERE fg.name = ?", name, FossilGroup.class);
	}

	public SentTo createNewSentTo() {
		return new nz.cri.gns.fred.hibernate.SentTo();
	}

	public Stage findStage(Age lowerAge, boolean lowerUncertain, Age upperAge, boolean upperUncertain) throws StorageAccessException {
		try {
			StringBuffer query = new StringBuffer("FROM Stage AS s WHERE ");
			HashMap<String, Age> ageData = new  HashMap<String, Age>(2);
			Vector<String> strData = new Vector<String>(2);
			if (lowerAge == null) {
				query.append("s.lowerAge IS NULL ");
			} else {
				query.append("s.lowerAge = :lower ");
				ageData.put("lower", lowerAge);
			}
			if (lowerUncertain) {
				query.append("AND s.stageLowerMod = :lmod ");
				strData.add("lmod");
			} else {
				query.append("AND s.stageLowerMod IS NULL ");
			}
			if (upperAge == null) {
				query.append("AND s.upperAge IS NULL ");
			} else {
				query.append("AND s.upperAge = :upper ");
				ageData.put("upper", upperAge);
			}
			if (upperUncertain) {
				query.append("AND s.stageUpperMod = :umod");
				strData.add("umod");
			} else {
				query.append("AND s.stageUpperMod IS NULL");
			}
			
            Session session = provider.currentSession();
            Query hquery = session.createQuery(query.toString());
            for (String str : strData) {
            	hquery.setString(str, "?");
            }
            for (String str : ageData.keySet()) {
            	hquery.setEntity(str, ageData.get(str));
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

	public Relationship createNewRelationship() {
		return new nz.cri.gns.fred.hibernate.Relationship();
	}

	public SedimentaryFeatureType getSedimentaryFeatureTypeWithName(String sedFeature) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM SedimentaryFeatureType AS t WHERE t.name = ?", sedFeature, SedimentaryFeatureType.class);
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
        return HibernateUtils.getFirst(provider, "SELECT f FROM Record AS r INNER JOIN r.sample AS s INNER JOIN s.feature AS feat INNER JOIN feat.masterFile AS f WHERE r.recordId = ?", record.getRecordId(), Folder.class);
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
    
    public PersonDAO getPersonDAO() {
		return this;
	}

	public Person createNewPerson() {
		return new nz.cri.gns.fred.hibernate.Person();
	}

	public Person getPerson(String name) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM Person As p WHERE p.name = ?", name, Person.class);
	}

	public List<Person> getMatchingPersons(String str, Match matchType, int maxMatches) throws StorageAccessException {
		Criteria crit = provider.currentSession().createCriteria(nz.cri.gns.fred.hibernate.Person.class);
		switch (matchType) {
			case ANYWHERE:
				crit.add(Expression.ilike("name", str, MatchMode.ANYWHERE));
				break;
			case BEGINNING:
				crit.add(Expression.ilike("name", str, MatchMode.START));
				break;
			case END:
				crit.add(Expression.ilike("name", str, MatchMode.END));
				break;
		}
		crit.setMaxResults(maxMatches);
		crit.addOrder(Order.asc("name"));
		try {
			@SuppressWarnings("unchecked")
			List<Person> pp = crit.list();
			return pp;
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
				
	}

	public List<Taxon> getMatchingTaxa(String str, TaxonomicGroup group, Match matchType, int maxMatches) throws StorageAccessException {
		Criteria crit = provider.currentSession().createCriteria(nz.cri.gns.fred.hibernate.TaxonomicLookup.class);
		switch (matchType) {
			case ANYWHERE:
				crit.add(Expression.like("taxonomicName", str, MatchMode.ANYWHERE));
				break;
			case BEGINNING:
				crit.add(Expression.like("taxonomicName", str, MatchMode.START));
				break;
			case END:
				crit.add(Expression.like("taxonomicName", str, MatchMode.END));
				break;
		} if (group != null)
			crit.add(Expression.eq("taxonomicGroup", group));
		crit.add(Expression.in("status", new String[] {"approved", "provisional"}));
		crit.setMaxResults(maxMatches);
		crit.addOrder(Order.asc("taxonomicGroup.groupId"));
		crit.addOrder(Order.asc("taxonomicName"));
		try {
			@SuppressWarnings("unchecked")
			List<Taxon> pp = crit.list();
			return pp;
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}
	
	public List<TaxonomicGroup> getMatchingTaxonomicGroups(String str, Match matchType, int maxMatches) throws StorageAccessException {
		Criteria crit = provider.currentSession().createCriteria(nz.cri.gns.fred.hibernate.TaxonomicGroup.class);
		switch (matchType) {
			case ANYWHERE:
				crit.add(Expression.ilike("name", str, MatchMode.ANYWHERE));
				break;
			case BEGINNING:
				crit.add(Expression.ilike("name", str, MatchMode.START));
				break;
			case END:
				crit.add(Expression.ilike("name", str, MatchMode.END));
				break;
		}
		crit.setMaxResults(maxMatches);
		crit.addOrder(Order.asc("groupId"));
		try {
			@SuppressWarnings("unchecked")
			List<TaxonomicGroup> pp = crit.list();
			return pp;
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}
	
	public List<Age> getMatchingAges(String str, Match matchType, int maxMatches) throws StorageAccessException {
		Criteria crit = provider.currentSession().createCriteria(nz.cri.gns.fred.hibernate.Age.class);
		switch (matchType) {
			case ANYWHERE:
				crit.add(Expression.or
						(Expression.ilike("name", str, MatchMode.ANYWHERE)
						, Expression.ilike("code", str, MatchMode.ANYWHERE)));
				break;
			case BEGINNING:
				crit.add(Expression.or
						(Expression.ilike("name", str, MatchMode.START)
						, Expression.ilike("code", str, MatchMode.START)));
				break;
			case END:
				crit.add(Expression.or
						(Expression.ilike("name", str, MatchMode.END)
						, Expression.ilike("code", str, MatchMode.END)));
				break;
		}
		crit.add(Expression.eq("obsoleteFlag", false));
		crit.setMaxResults(maxMatches);
		crit.addOrder(Order.asc("baseAge"));
		crit.addOrder(Order.asc("topAge"));
		try {
			@SuppressWarnings("unchecked")
			List<Age> pp = crit.list();
			return pp;
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}
	
    public TaxonomicDAO getTaxonomicDAO() {
        return this;
    }

    public PaleontologyListEntry createNewPaleontologyListEntry() {
        return new PalList();
    }

    public Taxon createNewTaxon() {
        return new TaxonomicLookup();
    }
	
	public nz.cri.gns.fred.model.FolderUser createNewFolderUser() {
		return new FolderUser(false);
	}

	public AuditDAO getAuditDAO() {
		return this;
	}

	public BacklogStatusDAO getBacklogStatusDAO() {
		return this;
	}
	
	public BacklogStatus getBacklogStatus(String mapNumber) throws StorageAccessException {
		BacklogStatus bs;
		try {
			 bs = HibernateUtils.getFirst(provider, "FROM BacklogStatus AS bs WHERE bs.mapNumber = ?", mapNumber, BacklogStatus.class);
		} catch (Exception e) {
			e.printStackTrace();
			bs = null;
		}
		return bs;
	}

	public List<BacklogStatus> getBacklogStatusInMasterfile(int masterfileId) throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("FROM BacklogStatus AS bs WHERE bs.masterfileId = :mfId");
            query.setInteger("mfId", masterfileId);
            return query.list();
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}
	
	public int getSumLocalityCount() throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("SELECT sum(bs.localityCount) FROM BacklogStatus AS bs");
    		List list = query.list();
    		return ((Integer)list.get(0)).intValue();
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}
	
	public int getSumLocalityCount(int masterfileId) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("SELECT sum(bs.localityCount) FROM BacklogStatus AS bs WHERE bs.masterfileId = :mfId");
			query.setInteger("mfId", masterfileId);
			List list = query.list();
			if (list.get(0) != null)
				return ((Integer)list.get(0)).intValue();
			return 0;
        } catch (Exception e) {
            throw new StorageAccessException(e);
		}		
	}

	public int getSumProcessingCount() throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("SELECT sum(bs.processingCount) FROM BacklogStatus AS bs");
    		List list = query.list();
    		return ((Integer)list.get(0)).intValue();
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}
	
	public int getSumProcessingCount(int masterfileId) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("SELECT sum(bs.processingCount) FROM BacklogStatus AS bs WHERE bs.masterfileId = :mfId");
			query.setInteger("mfId", masterfileId);
			List list = query.list();
			if (list.get(0) != null)
				return ((Integer)list.get(0)).intValue();
			return 0;
        } catch (Exception e) {
            throw new StorageAccessException(e);
		}		
	}
	
	public int getSumCompletedCount() throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("SELECT sum(bs.completedCount) FROM BacklogStatus AS bs");
    		List list = query.list();
    		return ((Integer)list.get(0)).intValue();
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}
	
	public int getSumCompletedCount(int masterfileId) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("SELECT sum(bs.completedCount) FROM BacklogStatus AS bs WHERE bs.masterfileId = :mfId");
			query.setInteger("mfId", masterfileId);
			List list = query.list();
			if (list.get(0) != null)
				return ((Integer)list.get(0)).intValue();
			return 0;
        } catch (Exception e) {
            throw new StorageAccessException(e);
		}		
	}

	public int getSumNewCount() throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("SELECT sum(bs.newCount) FROM BacklogStatus AS bs");
    		List list = query.list();
    		return ((Integer)list.get(0)).intValue();
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}
	
	public int getSumNewCount(int masterfileId) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			Query query = session.createQuery("SELECT sum(bs.newCount) FROM BacklogStatus AS bs WHERE bs.masterfileId = :mfId");
			query.setInteger("mfId", masterfileId);
			List list = query.list();
			if (list.get(0) != null)
				return ((Integer)list.get(0)).intValue();
			return 0;
        } catch (Exception e) {
            throw new StorageAccessException(e);
		}		
	}
	
	public StratLexDAO getStratLexDAO() {
		return this;
	}

	public List<StratigraphicUnit> getMatchingUnitNames(String start, Match matchType, int maxResults) throws StorageAccessException {
		return HibernateUtils.list(provider, "FROM StratigraphicUnit unit WHERE lower(unit.name) LIKE ? ORDER BY unit.name", maxResults, StratigraphicUnit.class, matchType.getQueryRepresentation(start.toLowerCase()));
	}

	public int getTotalFeatureCount() throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("SELECT count(*) FROM Feature AS f JOIN f.audit AS a WHERE a.status='approved'");
    		List list = query.list();
    		return ((Integer)list.get(0)).intValue();
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
	}
	
	public Date getLastFeatureApprovalDate() throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("SELECT MAX(a.approvedDate) FROM Feature AS f JOIN f.audit AS a");
    		List list = query.list();
    		return (Date)list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new StorageAccessException(e);
		}		
	}
	
	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException {
		List<T> items = HibernateUtils.list(provider, query, clazz, parameters);
		Collections.sort(items);
		return items;
	}
	
	public <T extends Comparable<? super T>> List<T> getList(Class<T> clazz, List<Criterion> criteria) throws StorageAccessException {
		Criteria crit = provider.currentSession().createCriteria(clazz);
		for (Criterion criterion : criteria)
			crit.add(criterion);
		try {
			@SuppressWarnings("unchecked")
			List<T> l = crit.list();
			Collections.sort(l);
			return l;
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}
	
	public <T> T getFirst(String query, Class<T> clazz, String parameter) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, query, parameter, clazz);	
	}
	
	public UserDAO getUserDAO() {
		return this;
	}

	public FrUserView getFrUserView(String userName) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM FrUserView As f WHERE f.userName = ?", userName, FrUserView.class);	
	}

	public FrUser createNewFrUser() {
		return new nz.cri.gns.fred.hibernate.FrUser();
	}
	
	public StageDAO getStageDAO() {
		return this;
	}
	
	public int getMaxAgeId() throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("SELECT MAX(a.ageId) FROM Age AS a");
    		List list = query.list();
    		return ((Integer)list.get(0)).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new StorageAccessException(e);
		}
	}

	public ConfidentialGroup createNewConfidentialGroup() throws StorageAccessException {
		return new nz.cri.gns.fred.hibernate.ConfidentialGroup();
	}
	
	public SiteDAO getSiteDAO() {
		return this;
	}

	public Lab findLab(String labName) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM Lab As l WHERE l.name = ?", labName, Lab.class);
	}

	public StratigraphicUnit findStratigraphicUnit(String name) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM StratigraphicUnit AS s WHERE s.name = ?", name, StratigraphicUnit.class);
	}
	
	public RelationshipType findRelationshipType(String name) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM RelationshipType AS r WHERE r.name = ?", name, RelationshipType.class);
	}
	
	public Country getCountry(String countryCode) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM Country AS c WHERE c.countryCode = ?", countryCode, Country.class);
	}
	
	public List<String> getFrMapSheets() throws StorageAccessException {
		try {
            Session session = provider.currentSession();
            Query query = session.createQuery("SELECT DISTINCT fr.mapSheet FROM FrNumber AS fr");
    		List<String> list = query.list();
    		Collections.sort(list);
    		return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new StorageAccessException(e);
		}		
	}

	public Iterator<Feature> getAllFeatures() throws StorageAccessException {
		return HibernateUtils.iterate(provider, "FROM Feature");
	}

	public void evict(Feature feature) throws StorageAccessException {
		HibernateUtils.evict(provider, feature);
	}

	public Iterator<Feature> getFeatures(String hqlQuery) throws StorageAccessException {
		return HibernateUtils.iterate(provider, hqlQuery);
	}

	public void evictComplete(Feature feature) throws StorageAccessException {
		for (Sample sample : feature.getSamples()) {
			for (Record record : sample.getRecords()) {
				if (record.getAdoption() != null)
					HibernateUtils.evict(provider, record.getAdoption());
				if (record.getPaleontology() != null) {
					for (PaleontologyListEntry entry : record.getPaleontology().getListEntries())
						HibernateUtils.evict(provider, entry);
					HibernateUtils.evict(provider, record.getPaleontology());
				}
				HibernateUtils.evict(provider, record);
			}
			HibernateUtils.evict(provider, sample);
		}
		HibernateUtils.evict(provider, feature);
	}

	public List<Paleontology> getPaleontologies(Sample sample) throws StorageAccessException {
		return HibernateUtils.list(provider, "FROM Paleontology AS p WHERE p.record.sample = ?", Paleontology.class, sample);
	}

	public List<Adoption> getAdoptions(Sample sample) throws StorageAccessException {
		return HibernateUtils.list(provider, "FROM Adoption AS a WHERE a.record.sample = ?", Adoption.class, sample);
	}

	public LogTable createNewLog() {
		return new nz.cri.gns.fred.hibernate.LogTable();
	}
	
}
