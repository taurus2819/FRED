package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import nz.cri.gns.dataaccess.StorageAccessException;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.DataOrigin;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.UserView;

public class AuditTable implements Serializable, Audit {

    private static final long serialVersionUID = 20050818L;
    private Integer auditId;
    private String status;
    private Integer createdById;
    private Date createdDate;
    private Integer submittedById;
    private Date submittedDate;
    private Integer approvedById;
    private Date approvedDate;
    private String workingComments;
    private String curatorComments;
    private String sendMessage;
    private Boolean confidentialFlag;
    private Double confidPeriod;
    private Date confidLapseDate;
    private Boolean confidEmailFlag;
    private String confidLapseEmail;
    private Folder folder;
    private DataOrigin dataOrigin;
    private UserView createdBy;
    private UserView submittedBy;
    private UserView approvedBy;
    private Set<Sample> samples;
    private Set<Record> records;
    private Set<Record> recordByPalListAuditIds;
    private Set<Feature> features;
    private Set<AuditEdit> auditEdits;
    private Set<ConfidentialGroup> confidGroups;

    @Override
    public void processAuditString(
            String auditString,
            nz.cri.gns.fred.dao.DAOFactory factory,
            nz.cri.gns.auth.UserAccount user)
            throws nz.cri.gns.dataaccess.StorageAccessException {
        // Confidential for 1 year accessible to AU; CU; GNS; with a lapse email to milan@es.co.nz
        // 1:1 Year;Auckland University;Canterbury University;GNS Science(milan@es.co.nz)
        // Open sample
        // 0
        // Confidential for 6 months
        // 1:6 Months
        auditString = (auditString == null || auditString.equals("") ? "0" : auditString.toLowerCase());
        String confidFlag = auditString.substring(0, 1);
        if (confidFlag.equals("0")) {
            setConfidentialFlag(false);
            setConfidPeriod(0.0D);
            setConfidEmailFlag(false);
            setConfidLapseEmail("");
            setConfidGroups(new java.util.HashSet());
        } else {
            if (auditString.matches("1:[6125] [monthyears]*(;[a-z]* [a-z]*)*(\\([^@]*@[^)]*\\))?")) {
                String[] confidParts = auditString.split("[:;()]");

                // Process the period
                String periodString = confidParts[1];
                String[] periodParts = periodString.split(" ");
                // default period is 6 months
                double actualPeriod = 0.5D;
                if (periodParts[1].matches("year[s]*")) // process period as years
                {
                    actualPeriod = Double.parseDouble(periodParts[0]);
                } else if (periodParts[1].matches("month[s]*")) {
                    actualPeriod = Double.parseDouble(periodParts[0]) / 12.0D;
                }

                boolean altEmailExists = true;
                int altEmailPosn = confidParts.length - 1;
                String altEmail = confidParts[confidParts.length - 1].matches("[^@]*@.*") ? confidParts[confidParts.length - 1] : "";
                if (altEmail.equals("")) {
                    altEmailExists = false;
                    altEmailPosn++;
                }

                // Find all the groups
                java.util.List<String> confidOrgs = java.util.Arrays.asList(confidParts).subList(2, altEmailPosn);
                java.util.Set<ConfidentialGroup> groupsToAdd = new java.util.HashSet<ConfidentialGroup>();
                java.util.List<ConfidentialGroup> possibleGroups = (new nz.cri.gns.fred.util.AuditUtil(factory)).getConfidentialGroups(user);
                for (String desiredGroup : confidOrgs) {
                    for (ConfidentialGroup group : possibleGroups) {
                        if (group.getName().toLowerCase().equals(desiredGroup)) {
                            groupsToAdd.add(group);
                        }
                    }
                }

                // Got all the stuff we need, now set it all
                setConfidentialFlag(true);
                setConfidPeriod(actualPeriod);
                setConfidEmailFlag(altEmailExists);
                setConfidLapseEmail(altEmail);
                this.setConfidGroups(groupsToAdd);
            } else {
                // failed to parse the value so default to 6 months with no email or groups
                setConfidentialFlag(true);
                setConfidPeriod(0.5D);
                this.setConfidEmailFlag(false);
                this.setConfidLapseEmail("");
                setConfidGroups(new java.util.HashSet<ConfidentialGroup>());
            }
        }
    }

    @Override
    public String createAuditString(
            nz.cri.gns.fred.dao.DAOFactory factory,
            nz.cri.gns.auth.UserAccount user)
            throws nz.cri.gns.dataaccess.StorageAccessException {
        // Confidential for 1 year accessible to AU; CU; GNS; with a lapse email to milan@es.co.nz
        // 1:1 Year;Auckland University;Canterbury University;GNS Science(milan@es.co.nz)
        // Open sample
        // 0
        // Confidential for 6 months
        // 1:6 Months

        java.lang.StringBuilder auditString = new java.lang.StringBuilder();

        if (getConfidentialFlag()) {
            auditString.append("1:");
            Double actualPeriod = getConfidPeriod();
            int conversionMultiplier = 1;
            String datePeriod = "Year";
            if (actualPeriod < 1.0) {
                conversionMultiplier = 12;
                datePeriod = "Month";
            }
            int approxPeriod = (new Double(Math.floor(actualPeriod * conversionMultiplier))).intValue();
            auditString.append(approxPeriod + " " + datePeriod + (approxPeriod == 1 ? "" : "s"));

            Set<ConfidentialGroup> groups = this.getConfidGroups();
            for (ConfidentialGroup group : groups) 
            {
                auditString.append(";"+group.getName());
            }
            
            String lapseEmail = this.getConfidLapseEmail();
            if (lapseEmail!=null&&!lapseEmail.equals(""))
            {
                auditString.append("("+lapseEmail+")");
            }

        } else {
            auditString.append(0);
        }

        return auditString.toString();
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

    public Boolean getConfidEmailFlag() {
        return confidEmailFlag;
    }

    public void setConfidEmailFlag(Boolean confidEmailFlag) {
        this.confidEmailFlag = confidEmailFlag;
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

    public int compareTo(Audit arg0) {
        try {
            return submittedDate.compareTo(arg0.getSubmittedDate());
        } catch (Exception e) {
            return auditId.compareTo(arg0.getAuditId());
        }
    }

    /*public boolean equals(Object o) {
    return o instanceof AuditTable && ((AuditTable)o).auditId.equals(auditId);
    }
    
    public int hashCode() {
    return 272 * auditId;
    }*/
}
