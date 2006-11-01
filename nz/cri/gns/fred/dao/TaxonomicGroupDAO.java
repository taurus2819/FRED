package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;

/**
 * @author iainm
 */
public interface TaxonomicGroupDAO {

	public TaxonomicGroup getTaxonomicGroup(int groupId) throws StorageAccessException;
	
	public TaxonomicGroup save(TaxonomicGroup group) throws StorageAccessException;
	
	public Taxon save(Taxon taxon) throws StorageAccessException;
	
	/*
	public List<TaxonomicGroup> getTaxonomicGroupsIsPanelistOf(int userId) throws StorageAccessException;
	*/
	
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

	/*
	public List<Integer> getPanelistsOfTaxonomicGroup(TaxonomicGroup group) throws StorageAccessException;
	*/
	
}
