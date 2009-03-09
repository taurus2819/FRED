package nz.cri.gns.fred.hibernate.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import nz.cri.gns.dataaccess.HibernateProvider;
import nz.cri.gns.dataaccess.HibernateUtils;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FolderUser;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.FrUser;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.LogTable;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.RelationshipType;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SedimentaryFeatureType;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.model.StratigraphicUnit;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;

public class HibernateDAOFactory implements DAOFactory, FredDAO {

	private HibernateProvider provider;

	public HibernateDAOFactory(HibernateProvider provider) {
		this.provider = provider;
	}
	
	public FredDAO getFredDAO() {
		return this;
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

	public <T> T getFirst(String query, Class<T> clazz, String parameter) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, query, parameter, clazz);	
	}
	
	public <T> T getFirst(String query, Class<T> clazz, int parameter) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, query, parameter, clazz);	
	}
	
	public void evict(Object object) throws StorageAccessException {
		HibernateUtils.evict(provider, object);
	}
	
	//create methods
	
	public Folder createNewFolder() {
		return new nz.cri.gns.fred.hibernate.Folder();
	}

	public Audit createNewAudit() {
		Audit audit = new nz.cri.gns.fred.hibernate.AuditTable();
		audit.setConfidentialFlag(false);
		return audit;
	}


	public Feature createNewFeature() {
		return new nz.cri.gns.fred.hibernate.Feature();
	}
	
	public AuditEdit createNewAuditEdit() throws StorageAccessException {
		return new nz.cri.gns.fred.hibernate.AuditEdit();
	}
	




	/**
	 * @deprectaed use getTaxaCount
	 */
	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
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

	//FeatureDAO methods

	

	






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
	
	public FrNumber createFRNumber() {
		return new nz.cri.gns.fred.hibernate.FrNumber();
	}
	
	@SuppressWarnings("unchecked")
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
		Sample sample = new nz.cri.gns.fred.hibernate.Sample();
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

	public FossilGroup getFossilGroup(String name) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM FossilGroup AS fg WHERE fg.name = ?", name, FossilGroup.class);
	}

	public SentTo createNewSentTo() {
		return new nz.cri.gns.fred.hibernate.SentTo();
	}

	@SuppressWarnings("unchecked")
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
    
	@SuppressWarnings("unchecked")
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

	public Person createNewPerson() {
		return new nz.cri.gns.fred.hibernate.Person();
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
	
    public PaleontologyListEntry createNewPaleontologyListEntry() {
        return new nz.cri.gns.fred.hibernate.PalList();
    }

    public Taxon createNewTaxon() {
        return new nz.cri.gns.fred.hibernate.TaxonomicLookup();
    }
	
	public FolderUser createNewFolderUser() {
		return new nz.cri.gns.fred.hibernate.FolderUser();
	}
	
	public List<StratigraphicUnit> getMatchingUnitNames(String start, Match matchType, int maxResults) throws StorageAccessException {
		return HibernateUtils.list(provider, "FROM StratigraphicUnit unit WHERE lower(unit.name) LIKE ? ORDER BY unit.name", maxResults, StratigraphicUnit.class, matchType.getQueryRepresentation(start.toLowerCase()));
	}

	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException {
		return getList(query, null, clazz, parameters);
	}
	
	public <T extends Comparable<? super T>> List<T> getList(String query, Integer maxResults, Class<T> clazz, Object ... parameters) throws StorageAccessException {
		List<T> items = HibernateUtils.list(provider, query, maxResults, clazz, parameters);
		Collections.sort(items);
		return items;
	}
	
	public <T extends Comparable<? super T>> List<T> getList(Class<T> clazz, List<Criterion> criteria) throws StorageAccessException {
		return getList(clazz, criteria, null);
	}
	
	public <T extends Comparable<? super T>> List<T> getList(Class<T> clazz, List<Criterion> criteria, Integer matches) throws StorageAccessException {
		Criteria crit = provider.currentSession().createCriteria(clazz);
		for (Criterion criterion : criteria)
			crit.add(criterion);
		if (matches != null)
			crit.setMaxResults(matches);
		try {
			@SuppressWarnings("unchecked")
			List<T> l = crit.list();
			Collections.sort(l);
			return l;
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}

	public FrUser createNewFrUser() {
		return new nz.cri.gns.fred.hibernate.FrUser();
	}
	
	@SuppressWarnings("unchecked")
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

	public Lab findLab(String labName) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM Lab As l WHERE l.name = ?", labName, Lab.class);
	}

	public StratigraphicUnit findStratigraphicUnit(String name) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM StratigraphicUnit AS s WHERE s.name = ?", name, StratigraphicUnit.class);
	}
	
	public RelationshipType findRelationshipType(String name) throws StorageAccessException {
		return HibernateUtils.getFirst(provider, "FROM RelationshipType AS r WHERE r.name = ?", name, RelationshipType.class);
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