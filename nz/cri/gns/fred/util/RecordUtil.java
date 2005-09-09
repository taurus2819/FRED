package nz.cri.gns.fred.util;

import java.util.Date;
import java.util.Iterator;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.RecordDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.hibernate.PalList;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RecordDetails;
import nz.cri.gns.fred.model.Sample;
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
		for (Iterator it = record.getPaleontology().getPalLists().iterator(); it.hasNext(); ) {
			PalList list = (PalList)it.next();
			if (!list.getTaxonomicLookup().getStatus().equals(FREDConstants.APPROVED)) 
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
				person = p.getGivenName() + " " + p.getFamilyName();
			}
		}
		
		String date = "";
		if (details.getDate() != null) {
			date = FREDUtil.formatDateForOutput(details.getDate(), details.getDateRounding());
		}
		
		return ((person.length() + date.length() > 0) ? person + ((person.length() > 0 && date.length() > 0) ? ", " : "") + date : "Unnamed Record");
	}
	
	public void deleteRecord(int recordId, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		Record record = recordDAO.getRecord(recordId);
		
		if (!isAllowedDeleteRecord(record, folder, user))
			throw new InsufficientPrivelegesException();
		
		recordDAO.delete(record);
		
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
            record.setPaleontology(recordDAO.createNewPaleontology());
        } else if (recordType.equals(ADOPTION)) {
            record.setAdoption(recordDAO.createNewAdoption());
        } else 
            throw new IllegalArgumentException("Invalid record type specified: " + recordType);
        
        return record;
    }


    public boolean isAllowedEditRecord(User user, Record record, UserFolder userFolder) throws StorageAccessException {
       return new SampleUtil(factory).isAllowedEditSample(user, record.getSample(), userFolder);
    }


    public boolean isAllowedSubmitRecord(User user, Record record, UserFolder userFolder) throws StorageAccessException {
        return new SampleUtil(factory).isAllowedSubmitSample(user, record.getSample(), userFolder);
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
}
