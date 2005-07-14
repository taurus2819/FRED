package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class Paleontology implements Serializable {

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
    private nz.cri.gns.fred.hibernate.Record record;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.LabSection labSection;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Stage stage;

    /** persistent field */
    private Set palLists;

    /** persistent field */
    private Set identifiers;

    /** full constructor */
    public Paleontology(Integer recordId, Integer palId, Date identificationDate, String dateRounding, String stageComments, String labNumber, String collectionComments, nz.cri.gns.fred.hibernate.Record record, nz.cri.gns.fred.hibernate.LabSection labSection, nz.cri.gns.fred.hibernate.Stage stage, Set palLists, Set identifiers) {
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
    public Paleontology(Integer recordId, nz.cri.gns.fred.hibernate.LabSection labSection, nz.cri.gns.fred.hibernate.Stage stage, Set palLists, Set identifiers) {
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

    public nz.cri.gns.fred.hibernate.Record getRecord() {
        return this.record;
    }

    public void setRecord(nz.cri.gns.fred.hibernate.Record record) {
        this.record = record;
    }

    public nz.cri.gns.fred.hibernate.LabSection getLabSection() {
        return this.labSection;
    }

    public void setLabSection(nz.cri.gns.fred.hibernate.LabSection labSection) {
        this.labSection = labSection;
    }

    public nz.cri.gns.fred.hibernate.Stage getStage() {
        return this.stage;
    }

    public void setStage(nz.cri.gns.fred.hibernate.Stage stage) {
        this.stage = stage;
    }

    public Set getPalLists() {
        return this.palLists;
    }

    public void setPalLists(Set palLists) {
        this.palLists = palLists;
    }

    public Set getIdentifiers() {
        return this.identifiers;
    }

    public void setIdentifiers(Set identifiers) {
        this.identifiers = identifiers;
    }

}
