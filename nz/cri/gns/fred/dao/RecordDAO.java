package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.TaxonomicGroup;

/**
 *
 */
public interface RecordDAO {

	/**
	 * @param recordId
	 * @return
	 * @throws StorageAccessException
	 */
    public Record getRecord(int recordId) throws StorageAccessException;

	/**
	 * @param record
	 * @throws StorageAccessException
	 */
    public void delete(Record record) throws StorageAccessException;

	/**
	 * @param audit
	 * @throws StorageAccessException
	 */
    public Audit update(Audit audit) throws StorageAccessException;

    /**
     * Creates a new empty Record
     */
    public Record createNewRecord();

    /**
     * Creates a new empty Audit
     */
    public Audit createNewAudit();

   /**
    * Creates a new empty Paleontology
    */
    public Paleontology createNewPaleontology();

   /**
    * Creates a new empty Adoption
    */
    public Adoption createNewAdoption();

    /**
     * Returns the masterfile folder of this record.  Provided to bypass having to instantiate
     * Sample and Feature objects 
     * @throws StorageAccessException 
     */
    public Folder getMasterfileFolder(Record record) throws StorageAccessException;

    public Audit save(Audit audit) throws StorageAccessException;

    public void save(Record record) throws StorageAccessException;

    public void update(Record record) throws StorageAccessException;

    /**
     * Returns an (alphabetically ordered) list of all the labs that are relevant to FRED
     * @throws StorageAccessException 
     */
	public List<Lab> getAllLabs() throws StorageAccessException;

	/**
	 * Returns all taxa in the given list of the given group
	 * @throws StorageAccessException 
	 */
	public List<PaleontologyListEntry> getListEntries(Paleontology pal, TaxonomicGroup group) throws StorageAccessException;

}
