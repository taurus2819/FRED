package nz.cri.gns.fred.hibernate.util.hibernate6;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.hibernate.Session;

//import net.sf.hibernate.Criteria;
//import net.sf.hibernate.HibernateException;
//import net.sf.hibernate.Query;
//import net.sf.hibernate.Session;
//import net.sf.hibernate.expression.Criterion;
//import net.sf.hibernate.expression.Expression;
//import net.sf.hibernate.expression.MatchMode;
//import net.sf.hibernate.expression.Order;
//import nz.cri.gns.dataaccess.HibernateProvider;
//import nz.cri.gns.dataaccess.HibernateUtils;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.hibernate.TaxonomicLookup;
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
import nz.cri.gns.fred.model.LabSection;
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

public class HibernateDAOFactory
        implements DAOFactory, FredDAO {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.hibernate.util.HibernateDAOFactory");

    private final HibernateProvider provider;

    public HibernateDAOFactory(HibernateProvider provider) {
        this.provider = provider;
    }

    @Override
    public FredDAO getFredDAO() {
        return this;
    }

    @Override
    public <T> T saveOrUpdate(T object) throws StorageAccessException {
        return HibernateUtils.saveOrUpdate(provider, object);
    }

    @Override
    public <T> T save(T object) throws StorageAccessException {
        return HibernateUtils.save(provider, object);
    }

    @Override
    public void delete(Object object) throws StorageAccessException {
        HibernateUtils.delete(provider, object);
    }

    @Override
    public <T> T get(Integer id, Class<T> clazz) {
        try {
            return HibernateUtils.get(provider, clazz, id);
        } catch (StorageAccessException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    @Override
    public <T> T getFirst(String query, Class<T> clazz, String parameter) throws StorageAccessException {
        return HibernateUtils.getFirst(provider, query, parameter, clazz);
    }

    @Override
    public <T> T getFirst(String query, Class<T> clazz, int parameter) throws StorageAccessException {
        return HibernateUtils.getFirst(provider, query, parameter, clazz);
    }

    @Override
    public void evict(Object object) throws StorageAccessException {
        HibernateUtils.evict(provider, object);
    }

    //create methods
    @Override
    public Age createNewAge() {
        return new nz.cri.gns.fred.hibernate.Age();
    }

    @Override
    public Folder createNewFolder() {
        return new nz.cri.gns.fred.hibernate.Folder();
    }

    @Override
    public Audit createNewAudit() {
        Audit audit = new nz.cri.gns.fred.hibernate.AuditTable();
        audit.setConfidentialFlag(false);
        return audit;
    }

    @Override
    public Feature createNewFeature() {
        return new nz.cri.gns.fred.hibernate.Feature();
    }

    @Override
    public AuditEdit createNewAuditEdit() throws StorageAccessException {
        return new nz.cri.gns.fred.hibernate.AuditEdit();
    }

    /**
     * @param group
     * @deprecated use getTaxaCount
     */
    @SuppressWarnings("unchecked")
    @Override
    @Deprecated
    /*
    public int getProvisionalCount(TaxonomicGroup group) throws StorageAccessException {
        try {
            Session session = provider.currentSession();
            Query query = session.createQuery("SELECT count(taxon) FROM TaxonomicLookup AS taxon WHERE taxon.taxonomicGroup = :group AND taxon.status = :prov AND taxon.taxonomicName IS NOT NULL");
            query.setEntity("group", group);
            query.setString("prov", FREDConstants.PROVISIONAL);
            List<Integer> list = query.list();
            return list.get(0);
        } catch (HibernateException | StorageAccessException e) {
            throw new StorageAccessException(e);
        }
    }*/

   public int getProvisionalCount(TaxonomicGroup group) {
      return 0; //temp

   }


    /**
     *
     * @param group
     * @param status
     * @return
     * @throws StorageAccessException
     */
    @SuppressWarnings("unchecked")
    @Override
    public int getTaxaCount(TaxonomicGroup group, String status) throws StorageAccessException {
    try {
        Session session = provider.currentSession();

        String hql =
            "select count(taxon) " +
            "from TaxonomicLookup taxon " +
            "where taxon.taxonomicGroup = :group " +
            "  and taxon.status = :status " +
            "  and taxon.taxonomicName is not null";

        Long count = session.createQuery(hql, Long.class)
                            .setParameter("group", group)
                            .setParameter("status", status)
                            .getSingleResult();

        // this is for safety to guard against overflow if you the count is huge
        return Math.toIntExact(count);

    } catch (HibernateException e) {
        throw new StorageAccessException(e);
    }
}

    /**
     *
     * @param group
     * @param status
     * @return
     * @throws StorageAccessException
     */
    @SuppressWarnings("unchecked")
    @Override
    
    public List<Taxon> getTaxa(TaxonomicGroup group, String status) throws StorageAccessException {
    try {
        Session session = provider.currentSession();

        String hql =
            "select taxon " +
            "from TaxonomicLookup taxon " +
            "where taxon.taxonomicGroup = :group " +
            "  and taxon.status = :status " +
            "  and taxon.taxonomicName is not null";

        List<Taxon> results =
            session.createQuery(hql, Taxon.class)
                   .setParameter("group", group)
                   .setParameter("status", status)
                   .getResultList();

        // Return as interface type
        return (List) results;

    } catch (HibernateException e) {
        throw new StorageAccessException(e);
    }
}



    @Override
    public TaxonomicGroup findTaxonomicGroup(String groupName) throws StorageAccessException {
        return HibernateUtils.getFirst(provider, "FROM TaxonomicGroup As g WHERE g.name = ?1", groupName, TaxonomicGroup.class);
    }

    //FeatureDAO methods
    //SampleDAO methods
    @Override
    public Relationship cloneRelationship(Relationship relationship) {
        return (Relationship) ((nz.cri.gns.fred.hibernate.Relationship) relationship).clone();
    }

    @Override
    public SentTo cloneSentTo(SentTo sentTo) {
        return (SentTo) ((nz.cri.gns.fred.hibernate.SentTo) sentTo).clone();
    }

    @Override
    public SedimentaryFeature cloneSedimentaryFeature(SedimentaryFeature sedFeature) {
        SedimentaryFeature sedF = new nz.cri.gns.fred.hibernate.SedimentaryFeature();
        sedF.setAbundant(sedFeature.getAbundant());
        sedF.setSedimentaryFeatureType(sedFeature.getSedimentaryFeatureType());
        return sedF;
    }

    @Override
    public FrNumber createFRNumber() {
        return new nz.cri.gns.fred.hibernate.FrNumber();
    }

    /**
     *
     * @param audit
     * @return
     * @throws StorageAccessException
     */
    @SuppressWarnings("unchecked")
    @Override
    
    /*public AuditEdit getMostRecentEdit(Audit audit) throws StorageAccessException {
        try {
            Session session = provider.currentSession();
            Query query = session.createQuery("FROM AuditEdit as edit WHERE edit.editedDate = (SELECT max(editedDate) FROM auditEdit WHERE audit = edit.audit) AND edit.audit = :audit");
            query.setEntity("audit", audit);
            List list = query.list();
            if (list.isEmpty()) {
                return null;
            }
            return (AuditEdit) list.get(0);
        } catch (HibernateException | StorageAccessException e) {
            throw new StorageAccessException(e);
        }
    }*/

    public AuditEdit getMostRecentEdit(Audit audit) throws StorageAccessException {
        return null; //temp
    }

    @Override
    public Sample createNewSample() {
        return new nz.cri.gns.fred.hibernate.Sample();
    }

    @Override
    public SedimentaryFeature createNewSedimentaryFeature() {
        return new nz.cri.gns.fred.hibernate.SedimentaryFeature();
    }

    @Override
    public FossilGroup getFossilGroup(String name) throws StorageAccessException {
        return HibernateUtils.getFirst(provider, "FROM FossilGroup AS fg WHERE fg.name = ?1", name, FossilGroup.class);
    }

    @Override
    public SentTo createNewSentTo() {
        return new nz.cri.gns.fred.hibernate.SentTo();
    }

    /**
     *
     * @param lowerAge
     * @param lowerUncertain
     * @param upperAge
     * @param upperUncertain
     * @return
     * @throws StorageAccessException
     */
    @SuppressWarnings("unchecked")
    @Override
    /*
    public Stage findStage(Age lowerAge, boolean lowerUncertain, Age upperAge, boolean upperUncertain) throws StorageAccessException {
        try {
            StringBuilder query = new StringBuilder("FROM Stage AS s WHERE ");
            HashMap<String, Age> ageData = new HashMap<>(2);
            Vector<String> strData = new Vector<>(2);
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
            if (list.isEmpty()) {
                return null;
            }
            return (Stage) list.get(0);
        } catch (HibernateException | StorageAccessException e) {
            throw new StorageAccessException(e);
        }
    }*/

    public Stage findStage(Age lowerAge, boolean lowerUncertain, Age upperAge, boolean upperUncertain) throws StorageAccessException {
        return null; //temp
    }

    @Override
    public Stage createNewStage() {
        return new nz.cri.gns.fred.hibernate.Stage();
    }

    @Override
    public Relationship createNewRelationship() {
        return new nz.cri.gns.fred.hibernate.Relationship();
    }

    @Override
    public SedimentaryFeatureType getSedimentaryFeatureTypeWithName(String sedFeature) throws StorageAccessException {
        return HibernateUtils.getFirst(provider, "FROM SedimentaryFeatureType AS t WHERE t.name = ?1", sedFeature, SedimentaryFeatureType.class);
    }

    @Override
    public void attach(Object object) throws StorageAccessException {
        try {
            provider.currentSession().refresh(object);
        } catch (HibernateException e) {
            throw new StorageAccessException(e);
        }
    }

    @Override
    public Record createNewRecord() {
        return new nz.cri.gns.fred.hibernate.Record();
    }

    @Override
    public Paleontology createNewPaleontology() {
        return new nz.cri.gns.fred.hibernate.Paleontology();
    }

    @Override
    public Adoption createNewAdoption() {
        return new nz.cri.gns.fred.hibernate.Adoption();
    }

    @Override
    public Folder getMasterfileFolder(Record record) throws StorageAccessException {
        return HibernateUtils.getFirst(provider, "SELECT f FROM Record AS r INNER JOIN r.sample AS s INNER JOIN s.feature AS feat INNER JOIN feat.masterFile AS f WHERE r.recordId = ?1", record.getRecordId(), Folder.class);
    }

    /**
     *
     * @param pal
     * @param group
     * @return
     * @throws StorageAccessException
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<PaleontologyListEntry> getListEntries(Paleontology pal, TaxonomicGroup group) throws StorageAccessException {
        try {
        Session session = provider.currentSession();

        String hql = "select ple from PalList ple where ple.taxonomicGroup = :grp and ple.paleontology = :pal";

        return session.createQuery(hql, PaleontologyListEntry.class)
                      .setParameter("grp", group)
                      .setParameter("pal", pal)
                      .getResultList();

    } catch (HibernateException e) {
        throw new StorageAccessException(e);
    }
}
    

//    public List<PaleontologyListEntry> getListEntries(Paleontology pal, TaxonomicGroup group)  {
//        return null; //temp
//    }

    @Override
    public Person createNewPerson() {
        return new nz.cri.gns.fred.hibernate.Person();
    }

    @Override
    public List<Taxon> getMatchingTaxa(String str, TaxonomicGroup group, Match matchType, int maxMatches) throws StorageAccessException {
        return getMatchingTaxa(str, group, matchType, maxMatches, true);
    }

    @Override
    public List<Taxon> getMatchingBadTaxa(String str, TaxonomicGroup group, Match matchType, int maxMatches) throws StorageAccessException {
        return getMatchingTaxa(str, group, matchType, maxMatches, false);
    }

    /* OLD HIBERNATE CODE - hibernate 2
    public List<Taxon> getMatchingTaxa(String str, TaxonomicGroup group, Match matchType, int maxMatches, boolean good) throws StorageAccessException {
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
            case EXACT:
                crit.add(Expression.like("taxonomicName", str, MatchMode.EXACT));
                break;
        }
        if (group != null) {
            crit.add(Expression.eq("taxonomicGroup", group));
        }
        if(good){
            crit.add(Expression.in("status", new String[]{"approved", "provisional"}));
        } else{
            crit.add(Expression.in("status", new String[]{"rejected", "obsolete"}));
        }
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
    }*/
        
    public List<Taxon> getMatchingTaxa(String str, TaxonomicGroup group, Match matchType,
                                   int maxMatches, boolean good) throws StorageAccessException {
    try {
        if (str == null || str.trim().isEmpty()) {
            return List.of();
        }

        str = str.trim();

        Session session = provider.currentSession();

        String pattern;
        switch (matchType) {
            case ANYWHERE:
                pattern = "%" + str + "%";
                break;
            case BEGINNING:
                pattern = str + "%";
                break;
            case END:
                pattern = "%" + str;
                break;
            case EXACT:
                pattern = str;
                break;
            default:
                pattern = str;
                break;
        }

        List<String> statuses = good
                ? List.of("approved", "provisional")
                : List.of("rejected", "obsolete");

        StringBuilder hql = new StringBuilder(
            "from TaxonomicLookup taxon " +
            "where taxon.taxonomicName like :pattern " +
             "and taxon.status in (:statuses) ");

        if (group != null) {
            hql.append(" and taxon.taxonomicGroup = :group ");
        }

        hql.append("order by taxon.taxonomicGroup.groupId asc, taxon.taxonomicName asc");

        var query = session.createQuery(hql.toString(), TaxonomicLookup.class)
                .setParameter("pattern", pattern)
                .setParameterList("statuses", statuses)
                .setMaxResults(maxMatches);

        if (group != null) {
            query.setParameter("group", group);
        }

        @SuppressWarnings("unchecked")
        List<Taxon> result = (List<Taxon>) (List<?>) query.getResultList();

        return result;

    } catch (HibernateException e) {
        throw new StorageAccessException(e);
    }
}
    
    /**
     *
     * @param taxonomicGroup
     * @param hql
     * @param taxonomicCleanName
     * @return
     * @throws StorageAccessException
     */
    @Override
    public TaxonomicLookup getTaxonomicLookup(TaxonomicGroup taxonomicGroup, String name, String author) throws StorageAccessException{
        try {
        String cleanName = name == null ? null : name.trim();

        StringBuilder hql = new StringBuilder(
            "from TaxonomicLookup t " +
            "where t.taxonomicName = :name " );

        if (taxonomicGroup != null) {
            hql.append(" and t.taxonomicGroup = :group");
        }

        var query = provider.currentSession()
                .createQuery(hql.toString(), TaxonomicLookup.class)
                .setParameter("name", cleanName)
                .setMaxResults(1);

        if (taxonomicGroup != null) {
            query.setParameter("group", taxonomicGroup);
        }

        TaxonomicLookup taxon = query.uniqueResult();   

        if (taxon != null && author != null && (taxon.getAuthor() == null || taxon.getAuthor().isEmpty())) {
            taxon.setAuthor(author.isEmpty() ? "" : author);
        }

        return taxon;
    } catch (HibernateException e) {
        throw new StorageAccessException(e);
    }
    }
 
    @Override
    //OLD HIBERNATE CODE - hibernate 2
    /*public List<TaxonomicGroup> getMatchingTaxonomicGroups(String str, Match matchType, int maxMatches) throws StorageAccessException {
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
    }*/

    public List<TaxonomicGroup> getMatchingTaxonomicGroups(String str, Match matchType, int maxMatches)
        throws StorageAccessException {
    try {
        Session session = provider.currentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<TaxonomicGroup> cq = cb.createQuery(TaxonomicGroup.class);
        Root<nz.cri.gns.fred.hibernate.TaxonomicGroup> root = cq.from(nz.cri.gns.fred.hibernate.TaxonomicGroup.class);

        String pattern;
        switch (matchType) {
            case ANYWHERE:
                pattern = "%" + str + "%";
                break;
            case BEGINNING:
                pattern = str + "%";
                break;
            case END:
                pattern = "%" + str;
                break;
            default:
                pattern = str;
                break;
        }

        cq.select(root)
          .where(cb.like(cb.lower(root.get("name")), pattern.toLowerCase()))
          .orderBy(cb.asc(root.get("groupId")));

        return session.createQuery(cq)
                      .setMaxResults(maxMatches)
                      .getResultList();

    } catch (HibernateException e) {
        throw new StorageAccessException(e);
    }
}

    @Override
    public PaleontologyListEntry createNewPaleontologyListEntry() {
        return new nz.cri.gns.fred.hibernate.PalList();
    }

    @Override
    public Taxon createNewTaxon() {
        return new nz.cri.gns.fred.hibernate.TaxonomicLookup();
    }

    @Override
    public FolderUser createNewFolderUser() {
        return new nz.cri.gns.fred.hibernate.FolderUser();
    }

    @Override
    public List<StratigraphicUnit> getMatchingUnitNames(String start, Match matchType, int maxResults) throws StorageAccessException {
        return HibernateUtils.list(provider, "FROM StratigraphicUnit unit WHERE lower(unit.name) LIKE ? ORDER BY unit.name", maxResults, StratigraphicUnit.class, matchType.getQueryRepresentation(start.toLowerCase()));
    }

    @Override
    public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object... parameters) throws StorageAccessException {
        System.out.println("HibernateDAOFactory (line# 538): at HibernateDAOFactory.getList(): " + query);
        
        return getList(query, null, clazz, parameters);
    }

    /**
     *
     * @param <T>
     * @param query
     * @param entity
     * @param clazz
     * @param maxResults
     * @return
     * @throws StorageAccessException
     * @throws HibernateException
     */
    @Override
    /*public <T extends Comparable<? super T>> List<T> getListFromSQL(String query, String entity, Class<T> clazz, Integer maxResults) throws StorageAccessException, HibernateException {
        Session session = provider.currentSession();
        Query sqlQuery = session.createSQLQuery(query, entity, clazz);

        if (maxResults != null) {
            sqlQuery.setMaxResults(maxResults);
        }
        @SuppressWarnings("unchecked")
        List<T> list = (List<T>) sqlQuery.list();
        return list;
    }*/

    public <T extends Comparable<? super T>> List<T> getListFromSQL(String query, String entity, Class<T> clazz, Integer maxResults)  {
        return null; //temp
    }

    /**
     *
     * @param <T>
     * @param query
     * @param maxResults
     * @param clazz
     * @param parameters
     * @return
     * @throws StorageAccessException
     */
    @Override
    public <T extends Comparable<? super T>> List<T> getList(String query, Integer maxResults, Class<T> clazz, Object... parameters) throws StorageAccessException {
        System.out.println("at HibernateDAOFactory.getList2(): " + query);
        
        List<T> items = HibernateUtils.list(provider, query, maxResults, clazz, parameters);
        Collections.sort(items);
        return items;
    }

/*
    @Override
    public <T extends Comparable<? super T>> List<T> getList(Class<T> clazz, List<Criterion> criteria) throws StorageAccessException {
        return getList(clazz, criteria, null);
    }
    */

/*
    @Override
    public <T extends Comparable<? super T>> List<T> getList(Class<T> clazz, List<Criterion> criteria, Integer matches) throws StorageAccessException {
        Criteria crit = provider.currentSession().createCriteria(clazz);
        for (Criterion criterion : criteria) {
            crit.add(criterion);
        }
        if (matches != null) {
            crit.setMaxResults(matches);
        }
        try {
            @SuppressWarnings("unchecked")
            List<T> l = crit.list();
            Collections.sort(l);
            return l;
        } catch (HibernateException e) {
            throw new StorageAccessException(e);
        }
    }
    */


    @Override
    public <T extends Comparable<? super T>> List<T> getUnsortedList(String query, Class<T> clazz, Object... parameters) throws StorageAccessException {
        
        //System.out.println("at HibernateDAOFactory.getUnsortedList(): " + query);
        
        return getUnsortedList(query, null, clazz, parameters);
    }

    public <T extends Comparable<? super T>> List<T> getUnsortedList(String query, Integer maxResults, Class<T> clazz, Object... parameters) throws StorageAccessException {
        List<T> items = HibernateUtils.list(provider, query, maxResults, clazz, parameters);
        return items;
    }

    @Override
    public FrUser createNewFrUser() {
        return new nz.cri.gns.fred.hibernate.FrUser();
    }

    /**
     *
     * @return
     * @throws StorageAccessException
     */
    @SuppressWarnings("unchecked")
    @Override
    /*public int getMaxAgeId() throws StorageAccessException {
        try {
            Session session = provider.currentSession();
            Query query = session.createQuery("SELECT MAX(a.ageId) FROM Age AS a");
            List<Integer> list = query.list();
            return list.get(0);
        } catch (HibernateException | StorageAccessException e) {
            throw new StorageAccessException(e);
        }
    }*/

    public int getMaxAgeId() {
        return 0; //temp
    }

    @Override
    public ConfidentialGroup createNewConfidentialGroup() throws StorageAccessException {
        return new nz.cri.gns.fred.hibernate.ConfidentialGroup();
    }

    @Override
    public Lab findLab(String labName) throws StorageAccessException {
        return HibernateUtils.getFirst(provider, "FROM Lab As l WHERE l.name = ?1", labName, Lab.class);
    }

    @Override
    public StratigraphicUnit findStratigraphicUnit(String name) throws StorageAccessException {
        return HibernateUtils.getFirst(provider, "FROM StratigraphicUnit AS s WHERE s.name = ?1", name, StratigraphicUnit.class);
    }

    @Override
    public RelationshipType findRelationshipType(String name) throws StorageAccessException {
        return HibernateUtils.getFirst(provider, "FROM RelationshipType AS r WHERE r.name = ?1", name, RelationshipType.class);
    }

    @Override
    public List<Paleontology> getPaleontologies(Sample sample) throws StorageAccessException {
        return HibernateUtils.list(provider, "FROM Paleontology AS p WHERE p.record.sample = ?1", Paleontology.class, sample);
    }

    @Override
    public List<Adoption> getAdoptions(Sample sample) throws StorageAccessException {
        return HibernateUtils.list(provider, "FROM Adoption AS a WHERE a.record.sample = ?1", Adoption.class, sample);
    }

    @Override
    public LogTable createNewLog() {
        return new nz.cri.gns.fred.hibernate.LogTable();
    }

    private Session retrieveSession() {
        try {
            return provider.currentSession();
        } catch (StorageAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    /*public LabSection getLabSection(String labName, String labCode) {
        try {
            Query query = retrieveSession().createQuery(
                    "FROM LabSection AS section WHERE section.lab.name = :name and section.code = :code");
            query.setString("name", labName);
            query.setString("code", labCode);
            return (LabSection) query.uniqueResult();
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }*/


    public LabSection getLabSection(String labName, String labCode) {
        return null; //temp
    }
}
