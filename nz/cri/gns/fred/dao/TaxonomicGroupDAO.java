package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.fred.model.TaxaPanel;
import nz.cri.gns.fred.model.TaxonomicGroup;

/**
 * @author iainm
 */
public interface TaxonomicGroupDAO {

	public TaxaPanel createNewTaxaPanel();
	
	public TaxaPanel save(TaxaPanel panel) throws StorageAccessException;
	
	public TaxonomicGroup getTaxonomicGroup(int groupId) throws StorageAccessException;
	
	public TaxonomicGroup save(TaxonomicGroup group) throws StorageAccessException;
	
	/**
	 * Returns a list of Taxonomic groups for which the given
	 * user is a member of the panel
	 * @throws StorageAccessException
	 */
	public List<TaxonomicGroup> getPanelsIsMemberOf(int userId) throws StorageAccessException;
	
	/**
	 *@return a count of provisional taxa within the given group
	 * @throws StorageAccessException
	 */
	public int getProvisionalCount(TaxonomicGroup group) throws StorageAccessException;

	/**
	 * Returns the group with the given name
	 * @throws StorageAccessException 
	 */
	public TaxonomicGroup findTaxonomicGroup(String groupName) throws StorageAccessException;

	public List<Integer> getPanelsIsMemberOf(TaxonomicGroup group) throws StorageAccessException;
	
}
