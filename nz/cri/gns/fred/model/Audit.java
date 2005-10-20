package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

/**
 *
 */
public interface Audit {
    public Integer getAuditId();
    public void setAuditId(Integer auditId);
    public String getStatus();
    public void setStatus(String status);
    public Integer getCreatedById();
    public void setCreatedById(Integer createdById);
    public Date getCreatedDate();
    public void setCreatedDate(Date createdDate);
    public Integer getSubmittedById();
    public void setSubmittedById(Integer submittedById);
    public Date getSubmittedDate();
    public void setSubmittedDate(Date submittedDate);
    public Integer getApprovedById();
    public void setApprovedById(Integer approvedById);
    public Date getApprovedDate();
    public void setApprovedDate(Date approvedDate);
    public String getWorkingComments();
    public void setWorkingComments(String workingComments);
    public String getCuratorComments();
    public void setCuratorComments(String curatorComments);
    public String getSendMessage();
    public void setSendMessage(String sendMessage);
    public Integer getSecurityClassId();
    public void setSecurityClassId(Integer securityClassId);
    public Folder getFolder();
    public void setFolder(Folder folder);
    public Set<Sample> getSamples();
    public void setSamples(Set<Sample> samples);
    public Set<Record> getRecords();
    public void setRecords(Set<Record> records);
    public Set<Feature> getFeatures();
    public void setFeatures(Set<Feature> features);
    public Set<AuditEdit> getAuditEdits();
    public void setAuditEdits(Set<AuditEdit> auditEdits);
}
