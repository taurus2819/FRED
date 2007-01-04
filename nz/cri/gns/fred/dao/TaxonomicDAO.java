package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;

public interface TaxonomicDAO {

	public Taxon getTaxon(int taxonId) throws StorageAccessException;
	
    /**
     * Creates a new, unsaved paleontological list entry
     * @return
     */
    public PaleontologyListEntry createNewPaleontologyListEntry();
    
    public PaleontologyListEntry getPaleontologyListEntry(int palListId) throws StorageAccessException;

    /**
     * Finds the taxon with the given parameters
     * @param taxonomicGroup
     * @param name
     * @param author
     * @return the taxon matching the given parameters or null if none exists
     * @throws StorageAccessException 
     */
    public Taxon getTaxon(TaxonomicGroup taxonomicGroup, String name, String author) throws StorageAccessException;

    /**
     * Creates a new, unsaved taxon
     * @return
     */
    public Taxon createNewTaxon();

    /**
     * Saves the taxon!
     * @param taxon
     * @throws StorageAccessException 
     */
	public Taxon save(Taxon taxon) throws StorageAccessException;

	public void delete(Taxon taxon) throws StorageAccessException;
	
    /**
     * Finds the taxon with the given parameters
     * @param taxonomicGroup
     * @param name
     * @return the taxon matching the given parameters or null if none exists
     * @throws StorageAccessException 
     */
	public Taxon getTaxon(TaxonomicGroup taxonomicGroup, String name) throws StorageAccessException;

	public TaxonomicGroup getTaxonomicGroup(int groupId) throws StorageAccessException;
	
	public TaxonomicGroup save(TaxonomicGroup group) throws StorageAccessException;
	
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
	
	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;
	
}
