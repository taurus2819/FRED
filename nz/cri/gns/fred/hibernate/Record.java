package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class Record implements Serializable {

    /** identifier field */
    private Integer recordId;

    /** nullable persistent field */
    private String workingComments;

    /** nullable persistent field */
    private nz.cri.gns.fred.hibernate.Paleontology paleontology;

    /** nullable persistent field */
    private nz.cri.gns.fred.hibernate.Adoption adoption;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Sample sample;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Folder folder;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.AuditTable auditTable;

    /** persistent field */
    private Set recordMetas;

    /** full constructor */
    public Record(String workingComments, nz.cri.gns.fred.hibernate.Paleontology paleontology, nz.cri.gns.fred.hibernate.Adoption adoption, nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.Folder folder, nz.cri.gns.fred.hibernate.AuditTable auditTable, Set recordMetas) {
        this.workingComments = workingComments;
        this.paleontology = paleontology;
        this.adoption = adoption;
        this.sample = sample;
        this.folder = folder;
        this.auditTable = auditTable;
        this.recordMetas = recordMetas;
    }

    /** default constructor */
    public Record() {
    }

    /** minimal constructor */
    public Record(nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.Folder folder, nz.cri.gns.fred.hibernate.AuditTable auditTable, Set recordMetas) {
        this.sample = sample;
        this.folder = folder;
        this.auditTable = auditTable;
        this.recordMetas = recordMetas;
    }

    public Integer getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getWorkingComments() {
        return this.workingComments;
    }

    public void setWorkingComments(String workingComments) {
        this.workingComments = workingComments;
    }

    public nz.cri.gns.fred.hibernate.Paleontology getPaleontology() {
        return this.paleontology;
    }

    public void setPaleontology(nz.cri.gns.fred.hibernate.Paleontology paleontology) {
        this.paleontology = paleontology;
    }

    public nz.cri.gns.fred.hibernate.Adoption getAdoption() {
        return this.adoption;
    }

    public void setAdoption(nz.cri.gns.fred.hibernate.Adoption adoption) {
        this.adoption = adoption;
    }

    public nz.cri.gns.fred.hibernate.Sample getSample() {
        return this.sample;
    }

    public void setSample(nz.cri.gns.fred.hibernate.Sample sample) {
        this.sample = sample;
    }

    public nz.cri.gns.fred.hibernate.Folder getFolder() {
        return this.folder;
    }

    public void setFolder(nz.cri.gns.fred.hibernate.Folder folder) {
        this.folder = folder;
    }

    public nz.cri.gns.fred.hibernate.AuditTable getAuditTable() {
        return this.auditTable;
    }

    public void setAuditTable(nz.cri.gns.fred.hibernate.AuditTable auditTable) {
        this.auditTable = auditTable;
    }

    public Set getRecordMetas() {
        return this.recordMetas;
    }

    public void setRecordMetas(Set recordMetas) {
        this.recordMetas = recordMetas;
    }

}
