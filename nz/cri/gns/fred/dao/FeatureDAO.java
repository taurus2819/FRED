package nz.cri.gns.fred.dao;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FeatureMeta;

/**
 *
 */
public interface FeatureDAO {

	/**
	 * @return a new, blank, audit table entry
	 */
	public Audit createNewAudit();

	public Audit save(Audit audit) throws StorageAccessException;
	
	public Feature cloneFeature(Feature feature);

	/**
	 * @return
	 */
	public FeatureMeta createFeatureMeta();

	/**
	 * @param newFeature
	 * @throws StorageAccessException
	 */
	public Feature save(Feature newFeature) throws StorageAccessException;

	/**
	 * @param feature
	 * @throws StorageAccessException
	 */
	public void delete(Feature feature) throws StorageAccessException;

	/**
	 * @param feature
	 * @throws StorageAccessException
	 */
	public void update(Feature feature) throws StorageAccessException;

	/**
	 * @param audit
	 * @throws StorageAccessException
	 */
	public void update(Audit audit) throws StorageAccessException;

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
	
}
