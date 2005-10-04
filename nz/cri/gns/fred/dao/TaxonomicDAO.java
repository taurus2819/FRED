package nz.cri.gns.fred.dao;

import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;

public interface TaxonomicDAO {

    /**
     * Creates a new, unsaved paleontological list entry
     * @return
     */
    public PaleontologyListEntry createPaleontologyListEntry();

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
    public Taxon createTaxon();

    /**
     * Saves the taxon!
     * @param taxon
     * @throws StorageAccessException 
     */
	public void save(Taxon taxon) throws StorageAccessException;

}
