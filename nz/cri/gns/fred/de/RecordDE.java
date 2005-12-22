package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RecordMeta;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.util.RecordUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.Template;
import nz.cri.gns.jsp.IconnedLink;

public abstract class RecordDE extends DETemplate implements DataEntryForm {

	protected User user;
	protected Record record;
	
    protected RecordUtil recordUtil;
    protected ContentProvider provider;
    protected UserFolder workingFolder;
    protected boolean isAllowedSubmit;
    protected DAOFactory factory;

    protected RecordDE(User user, Sample sample, int folderID, String recordType, DAOFactory factory, ContentProvider content)   throws StorageAccessException, InsufficientPrivelegesException {
        initialise((recordUtil = new RecordUtil(factory)).createRecord(sample, recordType, folderID), folderID, user, factory, content);
    }
    
    /*this.user = user;
		
        if (!(recordType.equals(Record.ADOPTION_RECORD) || recordType.equals(Record.PALEONTOLOGY_RECORD)))
			throw new DataInputException("Record Type", "Invalid value");
		this.recordType = recordType;
		this.folder = new Folder(folderID, user, state);		
	}*/

    public RecordDE(Record record, int folderId, User user, DAOFactory factory, ContentProvider provider) throws InsufficientPrivelegesException, StorageAccessException {
        recordUtil = new RecordUtil(factory);
        initialise(record, folderId, user, factory, provider);
        if (record.getAudit().getStatus().equals(FREDConstants.APPROVED)) {
            
        }
    }

	private void initialise(Record record, int folderId, User user, DAOFactory factory, ContentProvider provider) throws StorageAccessException, InsufficientPrivelegesException {
        this.record = record;
        this.user = user;
        this.factory = factory;
        this.provider = provider;
        
        FolderUtil folderUtil = new FolderUtil(factory);
        
        //check status for editing
        if (!recordUtil.isAllowedEditRecord(user, record, folderUtil.getUserFolder(folderId, user)))
            throw new InsufficientPrivelegesException("Insufficient rights to edit record");
        if (record.getAudit().getFolder() != null)
            workingFolder = folderUtil.getUserFolder(record.getAudit().getFolder().getFolderId().intValue(), user);
        else if (record.getAudit().getStatus().equals(FREDConstants.APPROVED)) {
            if (recordUtil.hasMasterfileEditRights(user, record)) {
                workingFolder = folderUtil.getUserFolder(record.getSample().getFeature().getMasterFile().getFolderId(), user);
            } else {
                throw new InsufficientPrivelegesException("You do not have access to edit this record");
            }                
        }
        isAllowedSubmit = recordUtil.isAllowedSubmitRecord(user, record, workingFolder);
   }

	public void copyFrom(int recordId) throws InsufficientPrivelegesException, StorageAccessException {
        Record fromRecord = recordUtil.getRecord(recordId);
        if (!recordUtil.isAllowedReadRecord(user, fromRecord))
            throw new InsufficientPrivelegesException("You do not have access to that record");
        
        getFromDatabase(fromRecord);
    }

	protected void getFromDatabase(Record fromRecord) {
        
        if (fromRecord.getAdoption() == null ^ record.getAdoption() == null) {
            throw new IllegalArgumentException("From record was of a different type");
        }
        if (fromRecord.getAdoption() != null) {
            Adoption fromAdoption = fromRecord.getAdoption();
            Adoption adoption = record.getAdoption();
            adoption.setAdoptionDate(fromAdoption.getAdoptionDate());
            adoption.setComments(fromAdoption.getComments());
            adoption.setDateRounding(fromAdoption.getDateRounding());
            adoption.setStage(fromAdoption.getStage());
            //Adoptors
            Set<Person> adoptors = adoption.getAdopters(); 
            if (adoptors == null) {
                adoptors = new HashSet<Person>();
                adoption.setAdopters(adoptors);
            } else {
                adoptors.clear();
            }
            if (fromAdoption.getAdopters() != null)
                adoptors.addAll(fromAdoption.getAdopters());
        } else {
            Paleontology fromPal = fromRecord.getPaleontology();
            Paleontology pal = record.getPaleontology();
            
            pal.setCollectionComments(fromPal.getCollectionComments());
            pal.setDateRounding(fromPal.getDateRounding());
            pal.setIdentificationDate(fromPal.getIdentificationDate());
            pal.setLabNumber(fromPal.getLabNumber());
            pal.setLabSection(fromPal.getLabSection());
            pal.setStage(fromPal.getStage());
            pal.setStageComments(fromPal.getStageComments());
            
            //Identifiers
            Set<Person> identifiers = pal.getIdentifiers(); 
            if (identifiers == null) {
                identifiers = new HashSet<Person>();
                pal.setIdentifiers(identifiers);
            } else {
                identifiers.clear();
            }
            if (fromPal.getIdentifiers() != null)
                identifiers.addAll(fromPal.getIdentifiers());
            
            //Pal lists
            Set<PaleontologyListEntry> lists = pal.getListEntries();
            if (lists == null) {
                lists = new HashSet<PaleontologyListEntry>();
                pal.setListEntries(lists);
            } else
                lists.clear();
            if (fromPal.getListEntries() != null)
                lists.addAll(fromPal.getListEntries());
        }

        record.setWorkingComments(fromRecord.getWorkingComments());
        //TODO not copying metas....for now ???
	}

	public List<IconnedLink> getNavigation() {
        List<IconnedLink> links = new Vector<IconnedLink>(4);
        try {
            String args = "?FoldID=" + workingFolder.getFolderId() 
                + ((record.getRecordId() == null) ? "" : ("&RecID=" + record.getRecordId()))
                + "&RecType=" + URLEncoder.encode(recordUtil.getRecordType(record), "ISO-8859-1");
            
            links.add(new IconnedLink("load_record.jsp" + args, "images/load.gif", "Copy From"));
        } catch (UnsupportedEncodingException e) {
            //Aint' gonna happen
        }
        links.add(new IconnedLink("javascript:document.form1.SaveType.value='Save';document.form1.submit();", "images/save.gif", "Save"));
        if (isAllowedSubmit)
            links.add(new IconnedLink("javascript:document.form1.SaveType.value='Submit';document.form1.submit();", "images/submit.gif", "Submit"));
        
        return links;
	}

	public void makeDataEntryHTML(PrintWriter out, DAOFactory factory) throws SQLException, IOException {
        reinitialise(factory);
		Template template = provider.getContent("record.de.form");
        prepareTemplate(template, provider);
        if (record.getRecordId() != null) 
            template.addSub("recordId", record.getRecordId().toString());
        template.addSub("featureName", FeatureUtil.getFeatureName(record.getSample().getFeature()));
		if (!record.getSample().getFeature().getFeatureType().equals(FREDConstants.OUTCROP))
			template.addSub("isDrillVert", "yes");
        template.addSub("drillholeDepth", SampleUtil.getDrillHoleDepthDescription(record.getSample()));
        template.addSub("recordType", recordUtil.getRecordType(record));
        if (workingFolder != null)
            template.addSub("folderId", workingFolder.getFolderId().toString());
        template.addSub("workingComments", record.getWorkingComments());
        
        template.loadUntil(out, "{@recordMeta}");
        
        Set<RecordMeta> images = record.getRecordMetas();
        if (images != null) try {
            for (RecordMeta meta : images) {
                out.println(FREDUtil.getMetaTitle(meta) + "<br />");
            }
        } catch (Exception e) {
        }

        template.loadAll(out);
	}

	protected void makeEndBitHTML(PrintWriter out) throws IOException {
        Template template = provider.getContent("record.de.end");
        if (isAllowedSubmit) 
            template.addSub("isAllowedSubmit", "yes");

        template.loadAll(out);
    }

	public int save() throws InsufficientPrivelegesException, StorageAccessException {
		if (!workingFolder.isAllowedCreateLocalities()) 
            throw new InsufficientPrivelegesException("You do not have the ability to save a record in this folder");
        
        if (record.getRecordId() == null) {
            //Save
            Audit audit = record.getAudit();
            audit.setStatus(FREDConstants.WORKING);
            audit.setCreatedById(user.getPersonId());
            audit.setCreatedDate(new Date());
            recordUtil.save(audit);
            recordUtil.save(record);
        } else {
            recordUtil.update(record);
        }

        return record.getRecordId();
    }


	public int submit() throws SQLException, IOException, InsufficientPrivelegesException, DataInputException, StorageAccessException {
		if (!workingFolder.isAllowedSubmitLocalities())
			throw new InsufficientPrivelegesException("You do not have permission to submit a record from this folder");
		checkMandatoryFields();
		int recordID = save();
        FREDUtil.submit(record, user, recordUtil, false);
		//change status
		return recordID;
	}
	
	protected void checkMandatoryFields() throws DataInputException {
	}
	
	public void delete() throws IOException, SQLException, InsufficientPrivelegesException, StorageAccessException {
		if (record != null) {
            recordUtil.delete(record);
		}
	}
	
	public int getWorkingFolderID() {
		return workingFolder.getFolderId();
	}

	public void updateFromRequest(HttpServletRequest request, DAOFactory factory) throws DataInputException {
        record.setWorkingComments(request.getParameter("WorkComm"));
    }
	
    protected void reinitialise(DAOFactory factory) {
        recordUtil = new RecordUtil(factory);
        if (record.getRecordId() != null) try {
        	record = recordUtil.getRecord(record.getRecordId().intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
