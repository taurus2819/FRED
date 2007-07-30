package nz.cri.gns.fred.dao;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.Country;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FeatureMeta;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.RegistrationArea;

public interface FeatureDAO {

	public <T> T saveOrUpdate(T object) throws StorageAccessException;
	
	/**
	 * @return a new, blank, audit table entry
	 */
	public Audit createNewAudit();

	public Feature cloneFeature(Feature feature);

	/**
	 * @return
	 */
	public FeatureMeta createNewFeatureMeta();

	/**
	 * @param feature
	 * @throws StorageAccessException
	 */
	public void delete(Feature feature) throws StorageAccessException;

	public void delete(Audit audit) throws StorageAccessException;
	
	/**
	 * @param featureId
	 * @return
	 * @throws StorageAccessException
	 */
	public Feature getFeature(int featureId) throws StorageAccessException;

	/**
	 * @param mapSheet
	 * @return
	 * @throws StorageAccessException
	 */
	public int getNextAvailableSerialNumber(String mapSheet) throws StorageAccessException;

	/**
	 * Returns a new, empty feature
	 */
	public Feature createNewFeature() throws StorageAccessException;

	/**
	 * Returns the registration area with the given id
	 */
	public RegistrationArea getRegistrationArea(int regAreaId) throws StorageAccessException;
	
	/**
	 * Returns all the features that have samples attached to the given audit.  The returned
	 * collection will be equivalent to that produced by:
	 * <pre>
	 * 		for (Sample sample : audit.getSamples()) {
	 * 			collection.add(sample.getFeature());
	 * 		}
	 * </pre>
	 * but should provide performance enhancements available by bypassing the Sample instantiation
	 * where possible.
	 * @param audit
	 * @return
	 * @throws StorageAccessException 
	 */
	public Collection<? extends Feature> getFeaturesBySample(Audit audit) throws StorageAccessException;
	
	/**
	 * Returns all the features that have records attached to the given audit.  The returned
	 * collection will be equivalent to that produced by:
	 * <pre>
	 * 		for (Record record : audit.getRecords()) {
	 * 			collection.add(record.getSample().getFeature());
	 * 		}
	 * </pre>
	 * but should provide performance enhancements available by bypassing the Sample and 
	 * Record instantiation where possible.
	 * @param audit
	 * @return
	 * @throws StorageAccessException 
	 */
	public Collection<? extends Feature> getFeaturesByRecord(Audit audit) throws StorageAccessException;

	/**
	 * Returns a new unintialised audit edit
	 */
	public AuditEdit createNewAuditEdit() throws StorageAccessException;

	public void delete(AuditEdit edit) throws StorageAccessException;
	
	/**
	 * Returns the frNumber entry with the given numbr
	 */
	public FrNumber getFrNumber(String frNum) throws StorageAccessException;

	/**
	 * Returns the frNumber entry with the given numbr
	 */
	public FrNumber getYardFrNumber(String frNum) throws StorageAccessException;
	
	/**
	 * Returns the first feature with the given name, or null if none exists.
	 */
	public Feature getFeatureWithName(String ident) throws StorageAccessException;

    /**
     * Returns features in the given masterfile folder, with an approval date between 
     * startDate and endDate
     * @param folder
     * @param startDate
     * @param endDate
     * @param status
     * @return
     */
	public List<Feature> getFeaturesInMasterfile(Folder masterfileFolder, Date startDate, Date endDate, String status) throws StorageAccessException;

    /**
     * Returns features in the given masterfile folder, with an approval date between 
     * startDate and endDate
     * @param folder
     * @param status
     * @return
     */
	public List<Feature> getFeaturesInMasterfile(Folder masterfileFolder, String status) throws StorageAccessException;
	
	/**
	 * Returns a list of fr numbers with a given map sheet
	 * @throws StorageAccessException 
	 * 
	 */
	public List<FrNumber> getFrNumbers(String mapSheet) throws StorageAccessException;
	
	/**
	 * Returns a list of fr numbers between the given start and end (inclusive)
	 * @throws StorageAccessException 
	 * 
	 */
	public List<FrNumber> getFrNumbers(String mapSheet, int start, int end) throws StorageAccessException;
	
	public void delete(FrNumber frNumber) throws StorageAccessException;
	
	public int getTotalFeatureCount() throws StorageAccessException;
	
	public Date getLastFeatureApprovalDate() throws StorageAccessException;
	
	public Country getCountry(String countryCode) throws StorageAccessException;
	
	public List<String> getFrMapSheets() throws StorageAccessException;
	
	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;

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
}
