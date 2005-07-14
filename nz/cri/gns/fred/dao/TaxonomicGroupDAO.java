package nz.cri.gns.fred.dao;

import java.util.List;

/**
 * @author iainm
 */
public interface TaxonomicGroupDAO {

	/**
	 * Returns a list of Taxonomic groups for which the given
	 * user is a member of the panel
	 * @throws StorageAccessException
	 */
	public List getPanelsIsMemberOf(int userId) throws StorageAccessException;

}
