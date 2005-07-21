package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.fred.model.TaxonomicGroup;

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

	/**
	 *@return a count of provisional taxa within the given group
	 * @throws StorageAccessException
	 */
	public int getProvisionalCount(TaxonomicGroup group) throws StorageAccessException;

}
