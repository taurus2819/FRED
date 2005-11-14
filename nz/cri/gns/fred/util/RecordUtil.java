package nz.cri.gns.fred.util;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.RecordDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RecordDetails;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.UserFolder;

/**
 *
 */
public class RecordUtil extends ModelUtil implements FREDConstants, AuditedUtil {

	private RecordDAO recordDAO;
	private FolderDAO folderDAO;


	public RecordUtil(DAOFactory factory) {
		super(factory);
		this.recordDAO = factory.getRecordDAO();
		this.folderDAO = factory.getFolderDAO();
	}
	

	public static boolean isTaxaApproved(Record record) {
		if (record.getPaleontology().getListEntries() == null)
			return true;
		for (PaleontologyListEntry entry : record.getPaleontology().getListEntries()) {
			if (entry.getTaxon() != null && !entry.getTaxon().getStatus().equals(FREDConstants.APPROVED)) 
				return false;
		}
		return true;
	}
	
	public static String getRecordName(Record record) {
		RecordDetails details = (record.getAdoption() == null) ? ((record.getPaleontology() == null) ? null : (RecordDetails)record.getPaleontology()) : record.getAdoption();
		if (details == null)
			return "Unnamed Record";
		
		String person = "";
		if (details.getPersons() != null) {
			Iterator it = details.getPersons().iterator();
			if (it.hasNext()) {
				Person p = (Person)it.next();
				person = p.getName();
			}
		}
		
		String date = "";
		if (details.getDate() != null) {
			date = FREDUtil.formatDateForOutput(details.getDate(), details.getDateRounding());
		}
		
		return ((person.length() + date.length() > 0) ? person + ((person.length() > 0 && date.length() > 0) ? ", " : "") + date : "Unnamed Record");
	}
	
	public void deleteRecord(int recordId, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		System.out.println("Deleting record: " + recordId);
		
		Record record = recordDAO.getRecord(recordId);
		System.out.println(record.getRecordId() + ": " + record);
		
		if (!isAllowedDeleteRecord(record, folder, user))
			throw new InsufficientPrivelegesException();
		
		//Get the sample
		System.out.println("Getting sample");
		Sample sample = record.getSample();
		System.out.println(sample.getSampleId() + ": " + sample);
		//Remove it from the sample
		System.out.println("Removing record");
		Set records = sample.getRecords();
		System.out.println("Num records before remove: " + records.size());
		records.remove(record);
		System.out.println("Num records after remove: " + records.size());
		//And delete it
		System.out.println("Deleting record");
		recordDAO.delete(record);
		System.out.println("Returning");
		
	}

	public void submitRecord(int recordId, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!folder.isAllowedSubmitLocalities())
			throw new InsufficientPrivelegesException();
		
		Record record = recordDAO.getRecord(recordId);
		Audit audit = record.getAudit();
		audit.setStatus(APPROVED);		//Records don't need approval
		audit.setSubmittedById(new Integer(user.getId()));
		audit.setSubmittedDate(new Date());
		audit.setWorkingComments(null);
		audit.setFolder(null);
		recordDAO.update(audit);
	}

	
	/**
	 * @param record
	 * @param folder
	 * @param user
	 * @return
	 * @throws StorageAccessException
	 * @throws NumberFormatException
	 */
	private boolean isAllowedDeleteRecord(Record record, UserFolder folder, UserAccount user) throws StorageAccessException {
		// TODO I made this up cos it wasn't checked before...is it right?
		Audit audit = record.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return false;

		if (audit.getStatus().equals(WAITING))
			return FeatureUtil.hasMasterfileRights(user, record.getSample().getFeature(), UserFolder.FOLDER_DELETE_RIGHT, folderDAO);

		return folder.isAllowedDeleteLocalities();
	}

	/**
     * Creates a new record entry of the given type and returns it.
     * @param sample the sample to which the record belongs
     * @param recordType one of FREDConstants.PALEONTOLOGICAL or FREDConstants.ADOPTION
     * @return a new <code>Record</code>
	 * @throws StorageAccessException 
	 */
    public Record createRecord(Sample sample, String recordType, int folderId) throws StorageAccessException {
        Record record = recordDAO.createNewRecord();
        record.setSample(sample);
        
        Audit audit = recordDAO.createNewAudit();
        audit.setStatus(WORKING);
        audit.setFolder(sample.getAudit().getFolder());
        audit.setFolder(folderDAO.getFolder(folderId));
        record.setAudit(audit);

        if (recordType.equals(PALEONTOLOGICAL)) {
            Paleontology pal = recordDAO.createNewPaleontology();
        	record.setPaleontology(pal);
        	pal.setRecord(record);
        } else if (recordType.equals(ADOPTION)) {
            Adoption adoption = recordDAO.createNewAdoption();
        	record.setAdoption(adoption);
            adoption.setRecord(record);
        } else 
            throw new IllegalArgumentException("Invalid record type specified: " + recordType);
        
        return record;
    }


    public boolean isAllowedEditRecord(User user, Record record, UserFolder userFolder) throws StorageAccessException {
       return new SampleUtil(factory).isAllowedEditSample(user, record.getSample(), userFolder);
    }


    public boolean isAllowedSubmitRecord(User user, Record record, UserFolder userFolder) throws StorageAccessException {
        return new SampleUtil(factory).isAllowedSubmitSample(user, record.getSample(), userFolder) && (record.getPaleontology() == null || RecordUtil.isTaxaApproved(record));
    }


    public boolean hasMasterfileEditRights(User user, Record record) throws StorageAccessException {
        return FeatureUtil.hasMasterfileRights(user, record.getSample().getFeature(), UserFolder.FOLDER_EDIT_RIGHT, folderDAO);
    }


    public Record getRecord(int recordId) throws StorageAccessException {
       return recordDAO.getRecord(recordId);
    }


    public boolean isAllowedViewRecord(User user, Record fromRecord) throws StorageAccessException {
        //TODO this will be upgraded once security is in place 
        Folder folder = fromRecord.getAudit().getFolder();
        if (folder == null)
            folder = recordDAO.getMasterfileFolder(fromRecord);
        
        return new FolderUtil(factory).getUserFolder(folder.getFolderId(), user).isAllowedReadLocalities();
    }


    public String getRecordType(Record record) {
        return (record.getAdoption() != null) ? ADOPTION : PALEONTOLOGICAL;
    }


    public Audit save(Audit audit) throws StorageAccessException {
        return recordDAO.save(audit);
    }


    public void save(Record record) throws StorageAccessException {
        recordDAO.save(record);
    }


    public void update(Record record) throws StorageAccessException {
        recordDAO.update(record);
    }


    public Audit update(Audit audit) throws StorageAccessException {
        return recordDAO.update(audit);
    }


    public void delete(Record record) throws StorageAccessException {
        recordDAO.delete(record);
    }

    /**
     * Returns an (alphabetically ordered) list of all the labs that are relevant to FRED
     * @throws StorageAccessException 
     */
	public List<Lab> getAllLabs() throws StorageAccessException {
		return recordDAO.getAllLabs();
	}

	/**
	 * Returns all the groups that are appropriate for the given pal entry
	 */
	public List<TaxonomicGroup> getTaxonomicGroups(Paleontology pal) {
		if (pal.getListEntries() == null)
			return new Vector<TaxonomicGroup>();
		TreeSet<TaxonomicGroup> set = new TreeSet<TaxonomicGroup>();
		for (PaleontologyListEntry entry : pal.getListEntries())
			set.add(entry.getTaxonomicGroup());
		return new Vector<TaxonomicGroup>(set);
	}

	/**
	 * Returns all the list entries with the given group for the given pal entry
	 * @throws StorageAccessException 
	 */
	public List<PaleontologyListEntry> getListEntries(Paleontology pal, TaxonomicGroup group) throws StorageAccessException {
		return recordDAO.getListEntries(pal, group);
	}


    public LabSection getLabSection(int id) throws StorageAccessException {
        return recordDAO.getLabSection(id);
    }


	public void save(RecordDetails details) throws StorageAccessException {
		recordDAO.save(details);
	}
	
	public void update(RecordDetails details) throws StorageAccessException {
		recordDAO.update(details);
	}
}
