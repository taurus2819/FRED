package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.TaxonomicGroup;

public interface RecordDAO {

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

	public Lab findLab(String labName) throws StorageAccessException;
	
	/**
	 * Returns all taxa in the given list of the given group
	 * @throws StorageAccessException 
	 */
	public List<PaleontologyListEntry> getListEntries(Paleontology pal, TaxonomicGroup group) throws StorageAccessException;

	public void delete(Object object) throws StorageAccessException;
	public <T> T saveOrUpdate(T object) throws StorageAccessException;
	public <T> T get(Integer id, Class<T> clazz);
	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;

}
