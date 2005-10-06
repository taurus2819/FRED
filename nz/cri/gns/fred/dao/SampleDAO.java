package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.BedThickness;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.Carbonate;
import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.model.DrillType;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.GrainSize;
import nz.cri.gns.fred.model.Hardness;
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
import nz.cri.gns.fred.model.Weathering;

/**
 *
 */
public interface SampleDAO {

	/**
	 * @param sample
	 * @return
	 */
	public Sample cloneSample(Sample sample);

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
	public SampleMeta createSampleMeta();

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
	public Sample createNewSample() throws StorageAccessException;

	/**
	 * Creates a new empty sedimentary feature object
	 * @return
	 */
	public SedimentaryFeature createNewSedimentaryFeature() throws StorageAccessException;

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

	/**
	 * Return the fossil group with the given name or null if one doesn't exist
	 * @throws StorageAccessException 
	 */
	public FossilGroup getFossilGroup(String name) throws StorageAccessException;

	/**
	 * Creates a new empty sentTo object
	 * @return
	 */
	public SentTo createNewSentTo();

	/**
	 * Locates, if one exists, a Stage entry in persistent storage that uses the given 
	 * stages (by id) and has uncertainty as specified. 
	 *@return a Stage object or null if no such object exists
	 */
	public Stage findStage(String startStageId, boolean startUncertain, String stopStageId, boolean stopUncertain) throws StorageAccessException;

	/**
	 * Creates a new, uninitialised Stage object
	 */
	public Stage createNewStage();

	/**
	 * Saves the given stage to persistent storage
	 */
	public void save(Stage stage) throws StorageAccessException;

	/**
	 * Creates a new, uninitialised Relationship object
	 */
	public Relationship createNewRelationship();

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

    public void saveOrUpdate(Audit audit) throws StorageAccessException;

    public void saveOrUpdate(Sample sample) throws StorageAccessException;

    public void attach(Object o) throws StorageAccessException;
}
