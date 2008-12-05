package nz.cri.gns.fred.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.expression.Criterion;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.Country;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FolderRight;
import nz.cri.gns.fred.model.FolderType;
import nz.cri.gns.fred.model.FolderUser;
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
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SedimentaryFeatureType;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.model.StratigraphicUnit;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;

public interface FredDAO {
    public Audit createNewAudit();
	public AuditEdit createNewAuditEdit() throws StorageAccessException;
    public ConfidentialGroup createNewConfidentialGroup() throws StorageAccessException;
	public LogTable createNewLog();
	public <T> T get(Integer id, Class<T> clazz);
	public <T> T getFirst(String query, Class<T> clazz, int parameter) throws StorageAccessException;
	public <T> T getFirst(String query, Class<T> clazz, String parameter) throws StorageAccessException;
	public void delete(Object object) throws StorageAccessException;
	public <T> T saveOrUpdate(T object) throws StorageAccessException;
	public <T> T save(T object) throws StorageAccessException;
	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;
	public <T extends Comparable<? super T>> List<T> getList(Class<T> clazz, List<Criterion> criteria) throws StorageAccessException;
	
	//create
	public Feature createNewFeature() throws StorageAccessException;
	
	//to be rationalised



	
	public int getTotalFeatureCount() throws StorageAccessException;
	
	public Date getLastFeatureApprovalDate() throws StorageAccessException;
	
	public Country getCountry(String countryCode) throws StorageAccessException;
	
	public List<String> getFrMapSheets() throws StorageAccessException;
	
	
	public Iterator<Feature> getAllFeatures() throws StorageAccessException;

	/**
	 * Cleans out resources related to this feature
	 * @param feature
	 */
	public void evict(Feature feature) throws StorageAccessException;

	public Iterator<Feature> getFeatures(String hqlQuery) throws StorageAccessException;

	/**
	 * Cleans out the feature and also all its children objects
	 * @param feature
	 * @throws StorageAccessException
	 */
	public void evictComplete(Feature feature) throws StorageAccessException;
	

	/**
	 * Creates a new, unsaved folder
	 */
	public Folder createNewFolder();
	


	/**
	 * Returns a count of any features that are in the masterfile folder given, with a 'waiting' status
	 * @throws StorageAccessException
	 */
	public int getWaitingMasterfileFeatureCount(Folder folder) throws StorageAccessException;

	/**
	 * Returns all audits with the given folder as their working folder
	 * @param folder
	 * @return
	 * @throws StorageAccessException
	 */
	public List<Audit> getAuditsFor(Folder folder) throws StorageAccessException;

	/**
	 * Returns all audits of the given status with the given folder as their working folder
	 * @param folder
	 * @return
	 * @throws StorageAccessException
	 */
	public List<Audit> getAuditsFor(Folder folder, String status) throws StorageAccessException;

	/**
	 * Returns the folder rights which satisfy the given join, in the given order.  A query might look like
	 * WHERE &lt;join&gt; ORDER BY &lt;order&gt;
	 * @param join
	 * @param order
	 * @return
	 * @throws StorageAccessException
	 */
	public List<FolderRight> getFolderRightList(String join, String order) throws StorageAccessException;

	/**
	 * Creates a new, unsaved folder user
	 */
	public FolderUser createNewFolderUser();
	
	public FolderType getFolderType(String label) throws StorageAccessException;
	
	public Person createNewPerson();

	/**
	 * If one exists in the database a person with the full name given. 
	 * @throws StorageAccessException 
	 */
	public Person getPerson(String name) throws StorageAccessException;

	public List<Person> getMatchingPersons(String str, Match matchType, int maxMatches) throws StorageAccessException;

	   /**
     * Creates a new empty Record
     */
    public Record createNewRecord();



   /**
    * Creates a new empty Paleontology
    */
    public Paleontology createNewPaleontology();

   /**
    * Creates a new empty Adoption
    */
    public Adoption createNewAdoption();

    /**
     * Returns the masterfile folder of this record.  Provided to bypass having to instantiate
     * Sample and Feature objects 
     * @throws StorageAccessException 
     */
    public Folder getMasterfileFolder(Record record) throws StorageAccessException;

	public Lab findLab(String labName) throws StorageAccessException;
	
	/**
	 * Returns all taxa in the given list of the given group
	 * @throws StorageAccessException 
	 */
	public List<PaleontologyListEntry> getListEntries(Paleontology pal, TaxonomicGroup group) throws StorageAccessException;


	/**
	 * @param relationship
	 * @return
	 */
	public Relationship cloneRelationship(Relationship relationship);

	/**
	 * @param sentTo
	 * @return
	 */
	public SentTo cloneSentTo(SentTo sentTo);

	/**
	 * @param sedFeature
	 * @return
	 */
	public SedimentaryFeature cloneSedimentaryFeature(SedimentaryFeature sedFeature);


	/**
	 * @return a new FrNumber
	 */
	public FrNumber createFRNumber();

	/**
	 * @param audit
	 * @return
	 * @throws StorageAccessException
	 */
	public AuditEdit getMostRecentEdit(Audit audit) throws StorageAccessException;


	/**
	 * Creates a new empty sample object
	 * @return
	 */
	public Sample createNewSample(Feature feature) throws StorageAccessException;

	/**
	 * Retrieves the so-named relation type
	 * @throws StorageAccessException 
	 */
	public RelationType getRelationType(String relationTypeName) throws StorageAccessException;

	/**
	 * Retrieves the so-named relationship type relevant to the given relation type
	 * @throws StorageAccessException 
	 */
	public RelationshipType getRelationshipType(RelationType relationType, String relationshipTypeName) throws StorageAccessException;

	/**
	 * Returns all relationships of the given sample with the given type.
	 * @throws StorageAccessException 
	 */
	public List<? extends Relationship> getRelationships(Sample sample, RelationshipType relationshipType) throws StorageAccessException;

	/**
	 * Return the fossil group with the given name or null if one doesn't exist
	 * @throws StorageAccessException 
	 */
	public FossilGroup getFossilGroup(String name) throws StorageAccessException;


	/**
	 * Creates a new, uninitialised Relationship object
	 */
	public Relationship createNewRelationship();

	/**
	 * Creates a new, uninitialised SentTo object
	 */
	public SentTo createNewSentTo();

	/**
	 * Creates a new, uninitialised SedimentaryFeature object
	 */
	public SedimentaryFeature createNewSedimentaryFeature();

	/**
	 * Returns the GrainSize with the given id
	 * @throws StorageAccessException 
	 */

	public SedimentaryFeatureType getSedimentaryFeatureTypeWithName(String sedFeature) throws StorageAccessException;

	public StratigraphicUnit findStratigraphicUnit(String name) throws StorageAccessException;
	
	public RelationshipType findRelationshipType(String name) throws StorageAccessException;

    public void attach(Object o) throws StorageAccessException;


	public List<Paleontology> getPaleontologies(Sample sample) throws StorageAccessException;

	public List<Adoption> getAdoptions(Sample sample) throws StorageAccessException;
	

	public int getMaxAgeId() throws StorageAccessException;
	
	/**
	 * Locates, if one exists, a Stage entry in persistent storage that uses the given 
	 * stages (by id) and has uncertainty as specified. 
	 *@return a Stage object or null if no such object exists
	 */
	public Stage findStage(Age startStage, boolean startUncertain, Age stopStage, boolean stopUncertain) throws StorageAccessException;

	/**
	 * Creates a new, uninitialised Stage object
	 */
	public Stage createNewStage();
	
	public List<Age> getMatchingAges(String str, Match matchType, int maxMatches) throws StorageAccessException;	
	
	/**
	 * Return a list of units whose names start with the given string, case
	 * insensitively
	 */
	public List<StratigraphicUnit> getMatchingUnitNames(String start, Match matchType, int maxResults) throws StorageAccessException;


	
    /**
     * Creates a new, unsaved paleontological list entry
     * @return
     */
    public PaleontologyListEntry createNewPaleontologyListEntry();

    /**
     * Creates a new, unsaved taxon
     * @return
     */
    public Taxon createNewTaxon();

	/**
	 *@return a count of provisional taxa within the given group
	 * @throws StorageAccessException
	 * @deprectaed use getTaxaCount
	 */
	public int getProvisionalCount(TaxonomicGroup group) throws StorageAccessException;

	/**
	 *@return a count of taxa within the given group with the given status
	 * @throws StorageAccessException
	 */
	public int getTaxaCount(TaxonomicGroup group, String status) throws StorageAccessException;

	/**
	 *@return a list of taxa within the given group with the given status
	 * @throws StorageAccessException
	 */
	public List<Taxon> getTaxa(TaxonomicGroup group, String status) throws StorageAccessException;
	
	/**
	 * Returns the group with the given name
	 * @throws StorageAccessException 
	 */
	public TaxonomicGroup findTaxonomicGroup(String groupName) throws StorageAccessException;

	public List<Taxon> getMatchingTaxa(String str, TaxonomicGroup group, Match matchType, int maxMatches) throws StorageAccessException;
	public List<TaxonomicGroup> getMatchingTaxonomicGroups(String str, Match matchType, int maxMatches) throws StorageAccessException;	
	/*
	public List<Integer> getPanelistsOfTaxonomicGroup(TaxonomicGroup group) throws StorageAccessException;
	*/

	public FrUserView getFrUserView(String userName) throws StorageAccessException;
	public FrUser createNewFrUser();
	
}