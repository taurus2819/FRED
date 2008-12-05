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
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
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

public class RecordUtil extends ModelUtil implements FREDConstants, AuditedUtil {

	private FredDAO fredDAO;
	private FeatureUtil featureUtil;

	public RecordUtil(DAOFactory factory) {
		super(factory);
		this.fredDAO = factory.getFredDAO();
		featureUtil = new FeatureUtil(factory);
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
	
	public void deleteRecords(String[] featIDs, UserFolder folder, UserAccount user) {
		boolean errFlag = false;
		for (int i = 0; i < featIDs.length; i++) {
			try {
				deleteRecord(getRecord(Integer.parseInt(featIDs[i])), folder, user);
			} catch (Exception e) {
				errFlag = true;
			}
		}
		if (errFlag)
			throw new IllegalStateException("An error has occured. Not all localities have been removed/deleted");
	}
	
	public void deleteRecord(int recordId, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		Record record = fredDAO.get(recordId, nz.cri.gns.fred.hibernate.Record.class);
		deleteRecord(record, folder, user);
	}
	
	public void deleteRecord(Record record, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!isAllowedDeleteRecord(user, record, folder))
			throw new InsufficientPrivelegesException();
		
		//Get the sample
		Sample sample = record.getSample();
		//Remove it from the sample
		sample.getRecords().remove(record);
		//And delete it
		fredDAO.delete(record);
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
		fredDAO.saveOrUpdate(audit);
		
		if (PALEONTOLOGICAL.equals(getRecordType(record))) {
			audit = record.getPalListAudit();
			audit.setStatus(APPROVED);
			if (audit.getConfidentialFlag()) {
				GregorianCalendar cal = new GregorianCalendar();
				if (audit.getConfidPeriod().doubleValue() == 0.5)
					cal.add(Calendar.MONTH, 6);
				else
					cal.add(Calendar.YEAR, audit.getConfidPeriod().intValue());
				audit.setConfidLapseDate(cal.getTime());
			}
			fredDAO.saveOrUpdate(audit);
		}
	}
	
	public void submitRecords(String[] recIDs, UserFolder folder, UserAccount user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException, DataInputException {
		for (int i = 0; i < recIDs.length; i++) {
			submitRecord(getRecord(Integer.parseInt(recIDs[i])), folder, user);
		}
	}

	/**
     * Creates a new record entry of the given type and returns it.
     * @param sample the sample to which the record belongs
     * @param recordType one of FREDConstants.PALEONTOLOGICAL or FREDConstants.ADOPTION
     * @return a new <code>Record</code>
	 * @throws StorageAccessException 
	 */
    public Record createRecord(Sample sample, String recordType, int folderId, UserAccount user) throws StorageAccessException {
        Record record = fredDAO.createNewRecord();
        record.setSample(sample);
        
        Audit audit = fredDAO.createNewAudit();
		audit.setFolder(fredDAO.get(folderId, nz.cri.gns.fred.hibernate.Folder.class));
		audit.setStatus(FREDConstants.WORKING);
		audit.setCreatedDate(new Date());
		audit.setCreatedById(new Integer(user.getId()));
        record.setAudit(audit);

        if (recordType.equals(PALEONTOLOGICAL)) {
        	audit = fredDAO.createNewAudit();
    		audit.setStatus(FREDConstants.WORKING);
    		audit.setCreatedDate(new Date());
    		audit.setCreatedById(new Integer(user.getId()));
        	record.setPalListAudit(audit);
            Paleontology pal = fredDAO.createNewPaleontology();
        	record.setPaleontology(pal);
        	pal.setRecord(record);
        } else if (recordType.equals(ADOPTION)) {
            Adoption adoption = fredDAO.createNewAdoption();
        	record.setAdoption(adoption);
            adoption.setRecord(record);
        } else 
            throw new IllegalArgumentException("Invalid record type specified: " + recordType);
        
        return record;
    }


    public boolean isAllowedEditRecord(UserAccount user, Record record, UserFolder userFolder) throws StorageAccessException {
		Audit audit = record.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return featureUtil.hasMasterfileRights(user, record.getSample().getFeature(), UserFolder.FOLDER_EDIT_RIGHT, fredDAO) ||
				FREDUtil.checkEditSecurityClass(user);

		return userFolder.isAllowedEditLocalities();
    }
    
	public boolean isAllowedEditRecordConfid(UserAccount user, Record record, UserFolder userFolder) {
		Audit audit = record.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return audit.getCreatedBy().getUserId().toString().equals(user.getId());

		return userFolder.isAllowedEditLocalities();		
	}

	public boolean isAllowedDeleteRecord(UserAccount user, Record record, UserFolder userFolder) throws StorageAccessException {
		Audit audit = record.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return featureUtil.hasMasterfileRights(user, record.getSample().getFeature(), UserFolder.FOLDER_EDIT_RIGHT, fredDAO);

		return userFolder.isAllowedDeleteLocalities();
	}

    public boolean isAllowedSubmitRecord(UserAccount user, Record record, UserFolder userFolder) throws StorageAccessException {
    	//check if pal record has non-approved taxa
        if (record.getPaleontology() != null && !RecordUtil.isTaxaApproved(record))
        	return false;
        
		Audit audit = record.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return false;	

		return userFolder.isAllowedSubmitLocalities();
    }


    public boolean hasMasterfileEditRights(UserAccount user, Record record) throws StorageAccessException {
        return featureUtil.hasMasterfileRights(user, record.getSample().getFeature(), UserFolder.FOLDER_EDIT_RIGHT, fredDAO);
    }


    public Record getRecord(int recordId) throws StorageAccessException {
       return fredDAO.get(recordId, nz.cri.gns.fred.hibernate.Record.class);
    }

	public boolean isRecordConfidential(Record record) {
		if (record.getAudit().getConfidentialFlag().booleanValue())
			return true;
		return new SampleUtil(factory).isSampleConfidential(record.getSample());
	}
    
	public boolean isPalListConfidential(Paleontology palRecord) {
		if (palRecord.getRecord().getPalListAudit().getConfidentialFlag().booleanValue())
			return true;
		return isRecordConfidential(palRecord.getRecord());
	}
	
	public String getRecordConfidAccessListDescription(Record record) {
		String recDes = new AuditUtil(factory).getConfidAccessListDescription(record.getAudit());
		if (recDes != null)
			return recDes;
		return new SampleUtil(factory).getSampleConfidAccessListDescription(record.getSample());
	}
	
	public String getPalListConfidAccessListDescription(Paleontology palRecord) {
		String palDes = new AuditUtil(factory).getConfidAccessListDescription(palRecord.getRecord().getPalListAudit());
		if (palDes != null)
			return palDes;
		return getRecordConfidAccessListDescription(palRecord.getRecord());
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
			if (folder == null || !folder.isAllowedReadLocalities())
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


    public Audit saveOrUpdate(Audit audit) throws StorageAccessException {
        return fredDAO.saveOrUpdate(audit);
    }


    public void saveOrUpdate(Record record) throws StorageAccessException {
        fredDAO.saveOrUpdate(record);
    }

    public void delete(Record record) throws StorageAccessException {
    	Audit audit = record.getAudit();
        fredDAO.delete(record);
		//try and delete audit record (if can't then probably also used by feature) so just ignore error
		try {
			fredDAO.delete(audit);
		} catch (Exception e) {}         
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
		List<TaxonomicGroup> groups = new Vector<TaxonomicGroup>(set);
		Collections.sort(groups);
		return groups;
	}

	/**
	 * Returns all the list entries with the given group for the given pal entry
	 * @throws StorageAccessException 
	 */
	public List<PaleontologyListEntry> getListEntries(Paleontology pal, TaxonomicGroup group) throws StorageAccessException {
		List<PaleontologyListEntry> entries = fredDAO.getListEntries(pal, group);
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
	

	public List<Lab> getLabs() throws StorageAccessException {
		return fredDAO.getList("SELECT DISTINCT l FROM LabSection AS ls INNER JOIN ls.lab AS l", Lab.class);
	}
	
    public LabSection getLabSection(int id) throws StorageAccessException {
        return fredDAO.get(id, nz.cri.gns.fred.hibernate.LabSection.class);
    }

    public Lab findLab(String labName) throws StorageAccessException {
    	return fredDAO.findLab(labName);
    }
    
	public void saveOrUpdate(RecordDetails details) throws StorageAccessException {
		fredDAO.saveOrUpdate(details);
	}
	
	public List<Record> getListFromQueryBuilder(String query) throws StorageAccessException {
		return fredDAO.getList(query, Record.class);
	}
	
	public List<Paleontology> getPaleontologyRecords(Feature feature) throws StorageAccessException {
		return fredDAO.getList("FROM Paleontology AS p WHERE p.record.sample.feature = ?", Paleontology.class, feature);
	}

	public List<Paleontology> getPaleontologyRecords(Sample sample) throws StorageAccessException {
		return fredDAO.getList("FROM Paleontology AS p WHERE p.record.sample = ?", Paleontology.class, sample);
	}
	
	public List<Adoption> getAdoptionRecords(Feature feature) throws StorageAccessException {
		return fredDAO.getList("FROM Adoption AS a WHERE a.record.sample.feature = ?", Adoption.class, feature);
	}
	
	public List<Adoption> getAdoptionRecords(Sample sample) throws StorageAccessException {
		return fredDAO.getList("FROM Adoption AS a WHERE a.record.sample = ?", Adoption.class, sample);
	}
	
}