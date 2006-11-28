package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.DataOrigin;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.UserView;

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
    private Boolean confidentialFlag;
 
    /** nullable persistent field */
    private Double confidPeriod;
    
    /** nullable persistent field */
    private Date confidLapseDate;

    /** nullable persistent field */
    private String confidLapseEmail;
    
    /** persistent field */
    private Folder folder;

    /** persistent field */
    private DataOrigin dataOrigin;

    /** persistent field */
    private UserView createdBy;
    
    /** persistent field */
    private UserView submittedBy;
    
    /** persistent field */
    private UserView approvedBy;
    
    /** persistent field */
    private Set<Sample> samples;

    /** persistent field */
    private Set<Record> records;

    /** persistent field */
    private Set<Record> recordByPalListAuditIds;
    
    /** persistent field */
    private Set<Feature> features;

    /** persistent field */
    private Set<AuditEdit> auditEdits;
    
    /** persistent field */
    private Set<ConfidentialGroup> confidGroups;

    /** full constructor */
    public AuditTable(String status, Integer createdById, Date createdDate, Integer submittedById, Date submittedDate, Integer approvedById, Date approvedDate, String workingComments, String curatorComments, String sendMessage, Boolean confidentialFlag, Double confidPeriod, Date confidLapseDate, String confidLapseEmail, Folder folder, DataOrigin dataOrigin, UserView createdBy, UserView submittedBy, UserView approvedBy, Set<Sample> samples, Set<Record> records, Set<Record> recordByPalListAuditIds, Set<Feature> features, Set<AuditEdit> auditEdits, Set<ConfidentialGroup> confidGroups) {
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
        this.confidentialFlag = confidentialFlag;
        this.confidPeriod = confidPeriod;
        this.confidLapseDate = confidLapseDate;
        this.confidLapseEmail = confidLapseEmail;
        this.folder = folder;
        this.dataOrigin = dataOrigin;
        this.createdBy = createdBy;
        this.submittedBy = submittedBy;
        this.approvedBy = approvedBy;
        this.samples = samples;
        this.records = records;
        this.recordByPalListAuditIds = recordByPalListAuditIds;
        this.features = features;
        this.auditEdits = auditEdits;
        this.confidGroups = confidGroups;
    }

    /** default constructor */
    public AuditTable() {
    }

    /** minimal constructor */
    public AuditTable(String status, Folder folder, DataOrigin dataOrigin, Set<Sample> samples, Set<Record> records, Set<Feature> features, Set<AuditEdit> auditEdits) {
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

	public Boolean getConfidentialFlag() {
		return confidentialFlag;
	}
	
    public void setConfidentialFlag(Boolean confidentialFlag) {
		this.confidentialFlag = confidentialFlag;
	}

	public Double getConfidPeriod() {
		return confidPeriod;
	}
	
	public void setConfidPeriod(Double confidPeriod) {
		this.confidPeriod = confidPeriod;
	}

	public Date getConfidLapseDate() {
		return confidLapseDate;
	}

	public void setConfidLapseDate(Date confidLapseDate) {
		this.confidLapseDate = confidLapseDate;
	}
	
	public String getConfidLapseEmail() {
		return confidLapseEmail;
	}
	
	public void setConfidLapseEmail(String confidLapseEmail) {
		this.confidLapseEmail = confidLapseEmail;
	}

	public Folder getFolder() {
        return this.folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public DataOrigin getDataOrigin() {
        return this.dataOrigin;
    }

    public void setDataOrigin(DataOrigin dataOrigin) {
        this.dataOrigin = dataOrigin;
    }

    public void setCreatedBy(UserView createdBy) {
		this.createdBy = createdBy;
	}

	public UserView getCreatedBy() {
		return createdBy;
	}

	public void setSubmittedBy(UserView submittedBy) {
		this.submittedBy = submittedBy;
	}

	public UserView getSubmittedBy() {
		return submittedBy;
	}

	public void setApprovedBy(UserView approvedBy) {
		this.approvedBy = approvedBy;
	}

	public UserView getApprovedBy() {
		return approvedBy;
	}

	public Set<Sample> getSamples() {
        return this.samples;
    }

    public void setSamples(Set<Sample> samples) {
        this.samples = samples;
    }

    public Set<Record> getRecords() {
        return this.records;
    }

    public void setRecords(Set<Record> records) {
        this.records = records;
    }

	public Set<Record> getRecordByPalListAuditIds() {
		return recordByPalListAuditIds;
	}
	
    public void setRecordByPalListAuditIds(Set<Record> recordByPalListAuditIds) {
		this.recordByPalListAuditIds = recordByPalListAuditIds;
	}

	public Set<Feature> getFeatures() {
        return this.features;
    }

    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    public Set<AuditEdit> getAuditEdits() {
        return this.auditEdits;
    }

    public void setAuditEdits(Set<AuditEdit> auditEdits) {
        this.auditEdits = auditEdits;
    }

	public Set<ConfidentialGroup> getConfidGroups() {
		return confidGroups;
	}
	
	public void setConfidGroups(Set<ConfidentialGroup> confidGroups) {
		this.confidGroups = confidGroups;
	}

}
