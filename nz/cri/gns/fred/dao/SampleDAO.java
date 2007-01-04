package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.BedThickness;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.Carbonate;
import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.model.DrillType;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.GrainSize;
import nz.cri.gns.fred.model.Hardness;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.RelationType;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.RelationshipType;
import nz.cri.gns.fred.model.RockColour;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SampleMeta;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SedimentaryFeatureType;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.StratigraphicUnit;
import nz.cri.gns.fred.model.Weathering;

/**
 *
 */
public interface SampleDAO {

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
	 * @return
	 */
	public SampleMeta createNewSampleMeta();

	/**
	 * @param newSample
	 * @throws StorageAccessException
	 */
	public Sample save(Sample newSample) throws StorageAccessException;

	/**
	 * @param sampleId
	 * @return
	 * @throws StorageAccessException
	 */
	public Sample getSample(int sampleId) throws StorageAccessException;

	/**
	 * @param sample
	 * @throws StorageAccessException
	 */
	public void delete(Sample sample) throws StorageAccessException;

	/**
	 * @param audit
	 * @throws StorageAccessException
	 */
	public Audit update(Audit audit) throws StorageAccessException;

	/**
	 * @param audit
	 * @throws StorageAccessException
	 */
	public void update(Sample sample) throws StorageAccessException;

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
	 * Creates a new empty Audit object
	 * @return
	 */
	public Audit createNewAudit();

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

	public Audit save(Audit audit) throws StorageAccessException;

	public void delete(Audit audit) throws StorageAccessException;	
	
	/**
	 * Return the fossil group with the given name or null if one doesn't exist
	 * @throws StorageAccessException 
	 */
	public FossilGroup getFossilGroup(String name) throws StorageAccessException;

	public Lab findLab(String labName) throws StorageAccessException;

	/**
	 * Creates a new, uninitialised Relationship object
	 */
	public Relationship createNewRelationship();

	/**
	 * Creates a new, uninitialised SentTo object
	 */
	public SentTo createNewSentTo();

	/**
	 * Saves the given sentTo to persistent storage
	 */
	public void save(SentTo sentTo) throws StorageAccessException;
	
	/**
	 * Creates a new, uninitialised SedimentaryFeature object
	 */
	public SedimentaryFeature createNewSedimentaryFeature();

	/**
	 * Saves the given relationship to persistent storage
	 * @throws StorageAccessException 
	 */
	public void save(Relationship rel) throws StorageAccessException;

	/**
	 * Returns the GrainSize with the given id
	 * @throws StorageAccessException 
	 */
	public GrainSize getGrainSize(Integer id) throws StorageAccessException;

	public Hardness getHardness(Integer id) throws StorageAccessException;

	public Weathering getWeathering(Integer id) throws StorageAccessException;

	public Bedding getBedding(Integer id) throws StorageAccessException;

	public BedThickness getBeddingThickness(Integer id) throws StorageAccessException;

	public RockColour getRockColour(Integer id) throws StorageAccessException;

	public ColourModifier getColourModifier(Integer id) throws StorageAccessException;

	public Carbonate getCarbonate(Integer id) throws StorageAccessException;

	public SedimentaryFeatureType getSedimentaryFeatureTypeWithName(String sedFeature) throws StorageAccessException;

	public DrillType getDrillType(int drillTypeId) throws StorageAccessException;
	
	public StratigraphicUnit findStratigraphicUnit(String name) throws StorageAccessException;
	
	public RelationshipType findRelationshipType(String name) throws StorageAccessException;

    public void saveOrUpdate(Audit audit) throws StorageAccessException;

    public void saveOrUpdate(Sample sample) throws StorageAccessException;

    public void attach(Object o) throws StorageAccessException;

	public void delete(SentTo sentTo) throws StorageAccessException;
	
	public void delete(Relationship rel) throws StorageAccessException;
	
	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;

}
