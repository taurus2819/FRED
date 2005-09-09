package nz.cri.gns.fred.dao;

import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Record;

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
    public void update(Audit audit) throws StorageAccessException;

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

}
