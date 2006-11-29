package nz.cri.gns.fred.util;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.RecordDAO;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RecordDetails;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.Taxon;
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
	
	public static RecordDetails getRecordDetails(Record record) {
		return (record.getAdoption() == null) ? ((record.getPaleontology() == null) ? null : record.getPaleontology()) : record.getAdoption();
	}
	
	public static String getRecordName(Record record) {
		return getRecordName(getRecordDetails(record));
	}
	
	public static String getRecordName(RecordDetails details) {
		if (details == null)
			return "Unnamed Record";
		
		String personStr = "";
		if (!FREDUtil.isEmpty(details.getPersons())) {
			List<Person> persons = new Vector<Person>();
			for (Person person : details.getPersons())
				persons.add(person);
			Collections.sort(persons);
			for (Person person : persons)
				personStr = person.getName();
		}

		String date = "";
		if (details.getDate() != null)
			date = FREDUtil.formatDateForOutput(details.getDate(), details.getDateRounding());
		
		return ((personStr.length() + date.length() > 0) ? personStr + ((personStr.length() > 0 && date.length() > 0) ? ", " : "") + date : "Unnamed Record");
	}
	
	public static String getLabNumberDescription(Paleontology pal) {
		LabSection ls = pal.getLabSection();
		StringBuffer desc = new StringBuffer();
		if (ls != null) {
			if (ls.getLab() != null && ls.getLab().getName() != null)
				desc.append(ls.getLab().getName()).append(" ");
			if (ls.getCode() != null)
				desc.append(ls.getCode()).append(" ");
		}
		if (pal.getLabNumber() != null)
			desc.append(pal.getLabNumber());
		return desc.toString();			
	}
	
	public void deleteRecord(int recordId, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		Record record = recordDAO.getRecord(recordId);
				
		if (!isAllowedDeleteRecord(record, folder, user))
			throw new InsufficientPrivelegesException();
		
		//Get the sample
		Sample sample = record.getSample();
		//Remove it from the sample
		sample.getRecords().remove(record);
		//And delete it
		recordDAO.delete(record);
	}

	public void submitRecord(int recordId, UserFolder folder, UserAccount user) throws DataInputException, InsufficientPrivelegesException, StorageAccessException {
		submitRecord(getRecord(recordId), folder, user);
	}
	
	public void submitRecord(Record record, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!folder.isAllowedSubmitLocalities())
			throw new InsufficientPrivelegesException();
		
		Audit audit = record.getAudit();
		audit.setStatus(APPROVED);		//Records don't need approval
		audit.setSubmittedById(new Integer(user.getId()));
		audit.setSubmittedDate(new Date());
		audit.setWorkingComments(null);
		audit.setFolder(null);
		if (audit.getConfidentialFlag()) {
			GregorianCalendar cal = new GregorianCalendar();
			if (audit.getConfidPeriod().doubleValue() == 0.5)
				cal.add(Calendar.MONTH, 6);
			else
				cal.add(Calendar.YEAR, audit.getConfidPeriod().intValue());
			audit.setConfidLapseDate(cal.getTime());
		}
		recordDAO.update(audit);
		
		if (PALEONTOLOGICAL.equals(getRecordType(record))) {
			audit = record.getPalListAudit();
			audit.setStatus(APPROVED);
			audit.setSubmittedById(new Integer(user.getId()));
			audit.setSubmittedDate(new Date());
			if (audit.getConfidentialFlag()) {
				GregorianCalendar cal = new GregorianCalendar();
				if (audit.getConfidPeriod().doubleValue() == 0.5)
					cal.add(Calendar.MONTH, 6);
				else
					cal.add(Calendar.YEAR, audit.getConfidPeriod().intValue());
				audit.setConfidLapseDate(cal.getTime());
			}
			recordDAO.update(audit);
		}
		
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
    public Record createRecord(Sample sample, String recordType, int folderId, UserAccount user) throws StorageAccessException {
        Record record = recordDAO.createNewRecord();
        record.setSample(sample);
        
        Audit audit = recordDAO.createNewAudit();
		audit.setFolder(folderDAO.getFolder(folderId));
		audit.setStatus(FREDConstants.WORKING);
		audit.setCreatedDate(new Date());
		audit.setCreatedById(new Integer(user.getId()));
        record.setAudit(audit);

        if (recordType.equals(PALEONTOLOGICAL)) {
        	audit = recordDAO.createNewAudit();
    		audit.setStatus(FREDConstants.WORKING);
    		audit.setCreatedDate(new Date());
    		audit.setCreatedById(new Integer(user.getId()));
        	record.setPalListAudit(audit);
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
		Audit audit = record.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return FeatureUtil.hasMasterfileRights(user, record.getSample().getFeature(), UserFolder.FOLDER_EDIT_RIGHT, folderDAO) ||
				FREDUtil.checkEditSecurityClass(user);

		return userFolder.isAllowedEditLocalities();
    }

	public boolean isAllowedDeleteRecord(User user, Record record, UserFolder userFolder) throws StorageAccessException {
		Audit audit = record.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return false;

		return userFolder.isAllowedDeleteLocalities();
	}

    public boolean isAllowedSubmitRecord(User user, Record record, UserFolder userFolder) throws StorageAccessException {
    	//check if pal record has non-approved taxa
        if (record.getPaleontology() != null && !RecordUtil.isTaxaApproved(record))
        	return false;
        
		Audit audit = record.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return false;	

		return userFolder.isAllowedSubmitLocalities();
    }


    public boolean hasMasterfileEditRights(User user, Record record) throws StorageAccessException {
        return FeatureUtil.hasMasterfileRights(user, record.getSample().getFeature(), UserFolder.FOLDER_EDIT_RIGHT, folderDAO);
    }


    public Record getRecord(int recordId) throws StorageAccessException {
       return recordDAO.getRecord(recordId);
    }

    /**
     * @deprecated use isAllowedReadRecord
     */
    public boolean isAllowedViewRecord(User user, Record fromRecord) throws StorageAccessException {
    	return isAllowedReadRecord(user, fromRecord);
    }

    public boolean isAllowedReadRecord(UserAccount user, Record record) throws StorageAccessException {
		if (user == null)
			return false;
		
		//first check record
		if (record.getAudit().getStatus().equals(FREDConstants.APPROVED)) {
			if(!new AuditUtil(factory).isAllowedReadApproved(record.getAudit(), user))
				return false;			
		} else {
			UserFolder folder = new FolderUtil(factory).getUserFolder(record.getAudit().getFolder().getFolderId().intValue(), user);
			if (folder == null && !folder.isAllowedReadLocalities())
				return false;
		}
		
		//then check allowed to read sample (which checks feature)
		Sample sample = record.getSample();
		return new SampleUtil(factory).isAllowedReadSample(user, sample);
    }
    
    public boolean isAllowedReadPalList(UserAccount user, Paleontology palRecord) throws StorageAccessException {
    	if (user ==  null)
    		return false;
    	
    	if (palRecord.getRecord().getAudit().getStatus().equals(FREDConstants.APPROVED))
    		return new AuditUtil(factory).isAllowedReadApproved(palRecord.getRecord().getPalListAudit(), user) && isAllowedReadRecord(user, palRecord.getRecord());
    	
    	return isAllowedReadRecord(user, palRecord.getRecord());
    }
    
    public static String getRecordType(Record record) {
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
    	Audit audit = record.getAudit();
        recordDAO.delete(record);
		//try and delete audit record (if can't then probably also used by feature) so just ignore error
		try {
			recordDAO.delete(audit);
		} catch (Exception e) {}         
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
		List<PaleontologyListEntry> entries = recordDAO.getListEntries(pal, group);
		Collections.sort(entries);
		return entries;
	}

	/**
	 * Returns all the taxon for a given pal record with the given status
	 */
	public List<Taxon> getTaxon(Paleontology pal, String status) {
		Vector<Taxon> set = new Vector<Taxon>();
		if (pal.getListEntries() != null) {
			for (PaleontologyListEntry entry : pal.getListEntries()) {
				Taxon taxon = entry.getTaxon();
				if (taxon != null && taxon.getStatus().equals(status))
					set.add(taxon);
			}
		}
		return set;
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
