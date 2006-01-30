package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Folder;


/** @author Hibernate CodeGenerator */
public class AuditTable implements Serializable, Audit {

	private static final long serialVersionUID = 20050818L;
	
    /** identifier field */
    private Integer auditId;

    /** persistent field */
    private String status;

    /** nullable persistent field */
    private Integer createdById;

    /** nullable persistent field */
    private Date createdDate;

    /** nullable persistent field */
    private Integer submittedById;

    /** nullable persistent field */
    private Date submittedDate;

    /** nullable persistent field */
    private Integer approvedById;

    /** nullable persistent field */
    private Date approvedDate;

    /** nullable persistent field */
    private String workingComments;

    /** nullable persistent field */
    private String curatorComments;

    /** nullable persistent field */
    private String sendMessage;

    /** nullable persistent field */
    private Integer securityClassId;

    /** persistent field */
    private nz.cri.gns.fred.model.Folder folder;

    /** persistent field */
    private nz.cri.gns.fred.model.DataOrigin dataOrigin;

    /** persistent field */
    private Set<nz.cri.gns.fred.model.Sample> samples;

    /** persistent field */
    private Set<nz.cri.gns.fred.model.Record> records;

    /** persistent field */
    private Set<nz.cri.gns.fred.model.Feature> features;

    /** persistent field */
    private Set<nz.cri.gns.fred.model.AuditEdit> auditEdits;

    /** full constructor */
    public AuditTable(String status, Integer createdById, Date createdDate, Integer submittedById, Date submittedDate, Integer approvedById, Date approvedDate, String workingComments, String curatorComments, String sendMessage, Integer securityClassId, nz.cri.gns.fred.hibernate.Folder folder, nz.cri.gns.fred.hibernate.DataOrigin dataOrigin, Set<nz.cri.gns.fred.model.Sample> samples, Set<nz.cri.gns.fred.model.Record> records, Set<nz.cri.gns.fred.model.Feature> features, Set<nz.cri.gns.fred.model.AuditEdit> auditEdits) {
        this.status = status;
        this.createdById = createdById;
        this.createdDate = createdDate;
        this.submittedById = submittedById;
        this.submittedDate = submittedDate;
        this.approvedById = approvedById;
        this.approvedDate = approvedDate;
        this.workingComments = workingComments;
        this.curatorComments = curatorComments;
        this.sendMessage = sendMessage;
        this.securityClassId = securityClassId;
        this.folder = folder;
        this.dataOrigin = dataOrigin;
        this.samples = samples;
        this.records = records;
        this.features = features;
        this.auditEdits = auditEdits;
    }

    /** default constructor */
    public AuditTable() {
    }

    /** minimal constructor */
    public AuditTable(String status, nz.cri.gns.fred.hibernate.Folder folder, nz.cri.gns.fred.hibernate.DataOrigin dataOrigin, Set<nz.cri.gns.fred.model.Sample> samples, Set<nz.cri.gns.fred.model.Record> records, Set<nz.cri.gns.fred.model.Feature> features, Set<nz.cri.gns.fred.model.AuditEdit> auditEdits) {
        this.status = status;
        this.folder = folder;
        this.dataOrigin = dataOrigin;
        this.samples = samples;
        this.records = records;
        this.features = features;
        this.auditEdits = auditEdits;
    }

    public Integer getAuditId() {
        return this.auditId;
    }

    public void setAuditId(Integer auditId) {
        this.auditId = auditId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(Integer createdById) {
        this.createdById = createdById;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getSubmittedById() {
        return this.submittedById;
    }

    public void setSubmittedById(Integer submittedById) {
        this.submittedById = submittedById;
    }

    public Date getSubmittedDate() {
        return this.submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public Integer getApprovedById() {
        return this.approvedById;
    }

    public void setApprovedById(Integer approvedById) {
        this.approvedById = approvedById;
    }

    public Date getApprovedDate() {
        return this.approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getWorkingComments() {
        return this.workingComments;
    }

    public void setWorkingComments(String workingComments) {
        this.workingComments = workingComments;
    }

    public String getCuratorComments() {
        return this.curatorComments;
    }

    public void setCuratorComments(String curatorComments) {
        this.curatorComments = curatorComments;
    }

    public String getSendMessage() {
        return this.sendMessage;
    }

    public void setSendMessage(String sendMessage) {
        this.sendMessage = sendMessage;
    }

    public Integer getSecurityClassId() {
        return this.securityClassId;
    }

    public void setSecurityClassId(Integer securityClassId) {
        this.securityClassId = securityClassId;
    }

    public Folder getFolder() {
        return this.folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public nz.cri.gns.fred.model.DataOrigin getDataOrigin() {
        return this.dataOrigin;
    }

    public void setDataOrigin(nz.cri.gns.fred.model.DataOrigin dataOrigin) {
        this.dataOrigin = dataOrigin;
    }

    public Set<nz.cri.gns.fred.model.Sample> getSamples() {
        return this.samples;
    }

    public void setSamples(Set<nz.cri.gns.fred.model.Sample> samples) {
        this.samples = samples;
    }

    public Set<nz.cri.gns.fred.model.Record> getRecords() {
        return this.records;
    }

    public void setRecords(Set<nz.cri.gns.fred.model.Record> records) {
        this.records = records;
    }

    public Set<nz.cri.gns.fred.model.Feature> getFeatures() {
        return this.features;
    }

    public void setFeatures(Set<nz.cri.gns.fred.model.Feature> features) {
        this.features = features;
    }

    public Set<nz.cri.gns.fred.model.AuditEdit> getAuditEdits() {
        return this.auditEdits;
    }

    public void setAuditEdits(Set<nz.cri.gns.fred.model.AuditEdit> auditEdits) {
        this.auditEdits = auditEdits;
    }

}
