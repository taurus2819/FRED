package nz.cri.gns.fred.dao;

import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;

public interface TaxonomicDAO {

    /**
     * Creates a new, unsaved paleontological list entry
     * @return
     */
    public PaleontologyListEntry createNewPaleontologyListEntry();

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
	public void save(Taxon taxon) throws StorageAccessException;

    /**
     * Finds the taxon with the given parameters
     * @param taxonomicGroup
     * @param name
     * @return the taxon matching the given parameters or null if none exists
     * @throws StorageAccessException 
     */
	public Taxon getTaxon(TaxonomicGroup taxonomicGroup, String name) throws StorageAccessException;

}
