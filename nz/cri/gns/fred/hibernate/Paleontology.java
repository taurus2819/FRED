package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.hibernate.dao.AssignedKeyed;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;


/** @author Hibernate CodeGenerator */
public class Paleontology implements Serializable, nz.cri.gns.fred.model.Paleontology, AssignedKeyed {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer recordId;

    /** nullable persistent field */
    private Integer palId;

    /** nullable persistent field */
    private Date identificationDate;

    /** nullable persistent field */
    private String dateRounding;

    /** nullable persistent field */
    private String stageComments;

    /** nullable persistent field */
    private String labNumber;

    /** nullable persistent field */
    private String collectionComments;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.Record record;

    /** persistent field */
    private nz.cri.gns.fred.model.LabSection labSection;

    /** persistent field */
    private nz.cri.gns.fred.model.Stage stage;

    /** persistent field */
    private Set<PaleontologyListEntry> palLists;

    /** persistent field */
    private Set<Person> identifiers;

    /** full constructor */
    public Paleontology(Integer recordId, Integer palId, Date identificationDate, String dateRounding, String stageComments, String labNumber, String collectionComments, nz.cri.gns.fred.hibernate.Record record, nz.cri.gns.fred.hibernate.LabSection labSection, nz.cri.gns.fred.hibernate.Stage stage, Set<PaleontologyListEntry> palLists, Set<Person> identifiers) {
        this.recordId = recordId;
        this.palId = palId;
        this.identificationDate = identificationDate;
        this.dateRounding = dateRounding;
        this.stageComments = stageComments;
        this.labNumber = labNumber;
        this.collectionComments = collectionComments;
        this.record = record;
        this.labSection = labSection;
        this.stage = stage;
        this.palLists = palLists;
        this.identifiers = identifiers;
    }

    /** default constructor */
    public Paleontology() {
    }

    /** minimal constructor */
    public Paleontology(Integer recordId, nz.cri.gns.fred.hibernate.LabSection labSection, nz.cri.gns.fred.hibernate.Stage stage, Set<PaleontologyListEntry> palLists, Set<Person> identifiers) {
        this.recordId = recordId;
        this.labSection = labSection;
        this.stage = stage;
        this.palLists = palLists;
        this.identifiers = identifiers;
    }

    public Integer getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getPalId() {
        return this.palId;
    }

    public void setPalId(Integer palId) {
        this.palId = palId;
    }

    public Date getIdentificationDate() {
        return this.identificationDate;
    }

    public void setIdentificationDate(Date identificationDate) {
        this.identificationDate = identificationDate;
    }

    public String getDateRounding() {
        return this.dateRounding;
    }

    public void setDateRounding(String dateRounding) {
        this.dateRounding = dateRounding;
    }

    public String getStageComments() {
        return this.stageComments;
    }

    public void setStageComments(String stageComments) {
        this.stageComments = stageComments;
    }

    public String getLabNumber() {
        return this.labNumber;
    }

    public void setLabNumber(String labNumber) {
        this.labNumber = labNumber;
    }

    public String getCollectionComments() {
        return this.collectionComments;
    }

    public void setCollectionComments(String collectionComments) {
        this.collectionComments = collectionComments;
    }

    public nz.cri.gns.fred.model.Record getRecord() {
        return this.record;
    }

    public void setRecord(nz.cri.gns.fred.model.Record record) {
        this.record = record;
    }

    public nz.cri.gns.fred.model.LabSection getLabSection() {
        return this.labSection;
    }

    public void setLabSection(nz.cri.gns.fred.model.LabSection labSection) {
        this.labSection = labSection;
    }

    public nz.cri.gns.fred.model.Stage getStage() {
        return this.stage;
    }

    public void setStage(nz.cri.gns.fred.model.Stage stage) {
        this.stage = stage;
    }

    public Set<PaleontologyListEntry> getListEntries() {
        return this.palLists;
    }

    public void setListEntries(Set<PaleontologyListEntry> palLists) {
        this.palLists = palLists;
    }

    public Set<Person> getIdentifiers() {
        return this.identifiers;
    }

    public void setIdentifiers(Set<Person> identifiers) {
        this.identifiers = identifiers;
    }

	public Date getDate() {
		return getIdentificationDate();
	}

	public Set getPersons() {
		return getIdentifiers();
	}

	public void updateKey() {
		this.recordId = this.record.getRecordId();
	}

}
