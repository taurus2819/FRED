package nz.cri.gns.fred.dao;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Feature;

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
	
}
