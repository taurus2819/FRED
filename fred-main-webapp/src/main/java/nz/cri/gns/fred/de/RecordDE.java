package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.domain.exception.InsufficientPrivelegesException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.util.AuditUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.util.RecordUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.Template;
import nz.cri.gns.jsp.IconnedLink;
import nz.cri.gns.xss.SanitizeHttpServletRequest;

public abstract class RecordDE extends DETemplate implements DataEntryForm {

    protected User user;
    protected Record record;
    private Record copyRecord;
    protected RecordUtil recordUtil;
    protected ContentProvider provider;
    protected UserFolder workingFolder;
    protected boolean isAllowedSave = false;
    protected boolean isAllowedSubmit = false;
    protected DAOFactory factory;
    
    private static final Logger LOG = Logger.getLogger("nz.cri.gns.fred.de.RecordDE");
    protected RecordDE(User user, Sample sample, int folderID, String recordType, DAOFactory factory, ContentProvider content) throws StorageAccessException, InsufficientPrivelegesException {
        initialise((recordUtil = new RecordUtil(factory)).createRecord(sample, recordType, folderID, user), folderID, user, factory, content);
    }

    public RecordDE(Record record, int folderId, User user, DAOFactory factory, ContentProvider provider) throws InsufficientPrivelegesException, StorageAccessException {
        recordUtil = new RecordUtil(factory);
        initialise(record, folderId, user, factory, provider);
    }

    private void initialise(Record record, int folderId, User user, DAOFactory factory, ContentProvider provider) throws StorageAccessException, InsufficientPrivelegesException {
        this.record = record;
        this.user = user;
        this.factory = factory;
        this.provider = provider;

        FolderUtil folderUtil = new FolderUtil(factory);

        //check status
        if (!recordUtil.isAllowedReadRecord(user, record)) {
            throw new InsufficientPrivelegesException("Insufficient rights to view record");
        }
        if (record.getAudit().getFolder() != null) {
            workingFolder = folderUtil.getUserFolder(record.getAudit().getFolder().getFolderId().intValue(), user);
        }

        try {
            isAllowedSave = recordUtil.isAllowedEditRecord(user, record, workingFolder);
            isAllowedSubmit = recordUtil.isAllowedSubmitRecord(user, record, workingFolder);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void copyFrom(int recordId) throws InsufficientPrivelegesException, StorageAccessException {
        Record fromRecord = recordUtil.getRecord(recordId);
        if (!recordUtil.isAllowedReadRecord(user, fromRecord)) {
            throw new InsufficientPrivelegesException("You do not have access to that record");
        }
        if (!RecordUtil.getRecordType(record).equals(RecordUtil.getRecordType(fromRecord))) {
            throw new IllegalArgumentException("Incompatible Record Types for copy operation");
        }
        this.copyRecord = fromRecord;
    }

    protected void getFromDatabase(Record fromRecord) {
        if (fromRecord.getAdoption() != null) {
            Adoption fromAdoption = fromRecord.getAdoption();
            Adoption adoption = record.getAdoption();
            adoption.setAdoptionDate(fromAdoption.getAdoptionDate());
            adoption.setComments(fromAdoption.getComments());
            adoption.setDateRounding(fromAdoption.getDateRounding());
            adoption.setStage(fromAdoption.getStage());
            //Adoptors
            Set<Person> adoptors = adoption.getAdoptors();
            if (adoptors == null) {
                adoptors = new HashSet<Person>();
                adoption.setAdoptors(adoptors);
            } else {
                adoptors.clear();
            }
            if (fromAdoption.getAdoptors() != null) {
                adoptors.addAll(fromAdoption.getAdoptors());
            }
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
            if (fromPal.getIdentifiers() != null) {
                identifiers.addAll(fromPal.getIdentifiers());
            }

            //Pal lists
            Set<PaleontologyListEntry> lists = pal.getListEntries();
            if (lists == null) {
                lists = new HashSet<PaleontologyListEntry>();
                pal.setListEntries(lists);
            } else {
                lists.clear();
            }
            if (fromPal.getListEntries() != null) {
                lists.addAll(fromPal.getListEntries());
            }
        }

        record.getAudit().setWorkingComments(fromRecord.getAudit().getWorkingComments());

        //TODO not copying metas....for now ???
    }

    public List<IconnedLink> getNavigation() {
        List<IconnedLink> links = new Vector<IconnedLink>(4);
        try {
            String args = ((workingFolder == null) ? "?q" : ("?FoldID=" + workingFolder.getFolderId()))
                    + ((record.getRecordId() == null) ? "" : ("&RecID=" + record.getRecordId()))
                    + "&RecType=" + URLEncoder.encode(RecordUtil.getRecordType(record), "ISO-8859-1");
            links.add(new IconnedLink("load_record.jsp" + args, "images/load.gif", "Copy From"));
        } catch (UnsupportedEncodingException e) {
            //Aint' gonna happen
        }
        links.add(new IconnedLink("javascript:submitForm('Save');", "images/save.gif", "Save"));
        if (isAllowedSubmit) {
            links.add(new IconnedLink("javascript:submitForm('Submit');", "images/submit.gif", "Submit"));
        }

        return links;
    }

    public void makeDataEntryHTML(PrintWriter out, DAOFactory factory) throws SQLException, IOException {
        reinitialise(factory);
        Template template = provider.getContent("record.de.form");
        prepareTemplate(template, provider);
        if (record.getRecordId() != null) {
            template.addSub("recordId", record.getRecordId().toString());
        }
        template.addSub("featureName", FeatureUtil.getFeatureIdentifyingName(record.getSample().getFeature()));
        if (!record.getSample().getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
            template.addSub("isDrillVert", "yes");
        }
        template.addSub("drillholeDepth", SampleUtil.getDrillHoleDepthDescription(record.getSample()));
        template.addSub("recordType", RecordUtil.getRecordType(record));
        if (workingFolder != null) {
            template.addSub("folderId", workingFolder.getFolderId().toString());
        }
        template.addSub("workingComments", record.getAudit().getWorkingComments());

        template.loadAll(out);
    }

    protected void makeEndBitHTML(PrintWriter out) throws IOException {
        Template template = provider.getContent("record.de.end");
        if (isAllowedSubmit) {
            template.addSub("isAllowedSubmit", "yes");
        }

        template.loadAll(out);
    }

    public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
        out.write("<tr><td>" + record.getRecordId() + "</td>\n");
        out.write("<td>" + FeatureUtil.getFeatureIdentifyingName(record.getSample().getFeature()) + ((!FREDConstants.OUTCROP.equals(record.getSample().getFeature().getFeatureType())) ? ": " + SampleUtil.getDrillHoleDepthDescription(record.getSample()) : "") + "</td>\n");
        out.write("<td>" + ((workingFolder != null) ? workingFolder.getFolderId() : "") + "</td>\n");
        out.write("<td>" + record.getAudit().getStatus() + "</td>\n");
        out.write("<td>" + DBUtils.nvl(record.getAudit().getCuratorComments()) + "</td>\n");
        out.write("<td>" + DBUtils.nvl(record.getAudit().getWorkingComments()) + "</td>\n");
    }

    public int save(int dataOriginId) throws InsufficientPrivelegesException, StorageAccessException {
        if (!isAllowedSave) {
            throw new InsufficientPrivelegesException("Insufficient rights to save this record");
        }

        if (record.getRecordId() == null) {
            //Save
            Audit audit = record.getAudit();
            audit.setStatus(FREDConstants.WORKING);
            audit.setCreatedById(user.getId().intValue());
            audit.setCreatedDate(new Date());
            audit.setDataOrigin((new AuditUtil(factory)).getDataOrigin(new Integer(dataOriginId)));
            recordUtil.saveOrUpdate(audit);
            recordUtil.saveOrUpdate(record);
        } else {
            recordUtil.saveOrUpdate(record);
        }

        return record.getRecordId();
    }

    public int submit(int dataOriginId) throws SQLException, IOException, InsufficientPrivelegesException, DataInputException, StorageAccessException {
        if (!isAllowedSubmit) {
            throw new InsufficientPrivelegesException("Insufficient rights to submit this record");
        }
        checkMandatoryFields();
        int recordID = save(dataOriginId);
        recordUtil.submitRecord(record, workingFolder, user);
        return recordID;
    }

    protected void checkMandatoryFields() throws DataInputException {
    }

    public int getWorkingFolderID() {
        return workingFolder.getFolderId();
    }

    public void updateFromRequest(HttpServletRequest request, DAOFactory factory, boolean addIfNew) throws DataInputException {
        SanitizeHttpServletRequest sanitizeHttpRequest = new SanitizeHttpServletRequest();
        record.getAudit().setWorkingComments(sanitizeHttpRequest.stripAllScripts(request.getParameter("WorkComm")));
    }

    protected void reinitialise(DAOFactory factory) {
        recordUtil = new RecordUtil(factory);
        if (record.getRecordId() != null) {
            try {
                record = recordUtil.getRecord(record.getRecordId().intValue());
                if (copyRecord != null) {
                    getFromDatabase(copyRecord);
                    copyRecord = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
