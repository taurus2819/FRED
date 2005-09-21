package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.RecordMeta;
import nz.cri.gns.fred.model.Person;

/** @author Hibernate CodeGenerator */
public class Record implements Serializable, nz.cri.gns.fred.model.Record {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer recordId;

    /** nullable persistent field */
    private String workingComments;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.Paleontology paleontology;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.Adoption adoption;

    /** persistent field */
    private nz.cri.gns.fred.model.Sample sample;

    /** persistent field */
    private nz.cri.gns.fred.model.Folder folder;

    /** persistent field */
    private nz.cri.gns.fred.model.Audit auditTable;

    /** persistent field */
    private Set<RecordMeta> recordMetas;
    
    /** persistent field */
    private Set<Person> persons;
    
    /** full constructor */
    public Record(String workingComments, nz.cri.gns.fred.hibernate.Paleontology paleontology, nz.cri.gns.fred.hibernate.Adoption adoption, nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.Folder folder, nz.cri.gns.fred.hibernate.AuditTable auditTable, Set<RecordMeta> recordMetas) {
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
    public Record(nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.Folder folder, nz.cri.gns.fred.hibernate.AuditTable auditTable, Set<RecordMeta> recordMetas) {
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

    public nz.cri.gns.fred.model.Paleontology getPaleontology() {
        return this.paleontology;
    }

    public void setPaleontology(nz.cri.gns.fred.model.Paleontology paleontology) {
        this.paleontology = paleontology;
    }

    public nz.cri.gns.fred.model.Adoption getAdoption() {
        return this.adoption;
    }

    public void setAdoption(nz.cri.gns.fred.model.Adoption adoption) {
        this.adoption = adoption;
    }

    public nz.cri.gns.fred.model.Sample getSample() {
        return this.sample;
    }

    public void setSample(nz.cri.gns.fred.model.Sample sample) {
        this.sample = sample;
    }

    public nz.cri.gns.fred.model.Folder getFolder() {
        return this.folder;
    }

    public void setFolder(nz.cri.gns.fred.model.Folder folder) {
        this.folder = folder;
    }

    public nz.cri.gns.fred.model.Audit getAudit() {
        return this.auditTable;
    }

    public void setAudit(nz.cri.gns.fred.model.Audit auditTable) {
        this.auditTable = auditTable;
    }

    public Set<RecordMeta> getRecordMetas() {
        return this.recordMetas;
    }

    public void setRecordMetas(Set<RecordMeta> recordMetas) {
        this.recordMetas = recordMetas;
    }

    public Set<Person> getPersons() {
        return persons;
    }
    
    public void setPersons(Set<Person> persons) {
        this.persons = persons;
    }
}
