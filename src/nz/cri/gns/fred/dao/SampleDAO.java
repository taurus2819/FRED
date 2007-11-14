package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.RelationType;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.RelationshipType;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SampleMeta;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SedimentaryFeatureType;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.StratigraphicUnit;

public interface SampleDAO {
	public void delete(Object object) throws StorageAccessException;
	public <T> T saveOrUpdate(T object) throws StorageAccessException;
	public <T> T get(Integer id, Class<T> clazz);
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

	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;

	public List<Paleontology> getPaleontologies(Sample sample) throws StorageAccessException;

	public List<Adoption> getAdoptions(Sample sample) throws StorageAccessException;
}
