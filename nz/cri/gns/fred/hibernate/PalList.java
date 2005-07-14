package nz.cri.gns.fred.hibernate;

import java.io.Serializable;


/** @author Hibernate CodeGenerator */
public class PalList implements Serializable {

    /** identifier field */
    private Integer palListId;

    /** nullable persistent field */
    private String comments;

    /** nullable persistent field */
    private Integer specimenCount;

    /** nullable persistent field */
    private String specimenCoords;

    /** nullable persistent field */
    private String taxonomicName;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.TaxonomicGroup taxonomicGroup;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Paleontology paleontology;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.TaxonomicLookup taxonomicLookup;

    /** full constructor */
    public PalList(String comments, Integer specimenCount, String specimenCoords, String taxonomicName, nz.cri.gns.fred.hibernate.TaxonomicGroup taxonomicGroup, nz.cri.gns.fred.hibernate.Paleontology paleontology, nz.cri.gns.fred.hibernate.TaxonomicLookup taxonomicLookup) {
        this.comments = comments;
        this.specimenCount = specimenCount;
        this.specimenCoords = specimenCoords;
        this.taxonomicName = taxonomicName;
        this.taxonomicGroup = taxonomicGroup;
        this.paleontology = paleontology;
        this.taxonomicLookup = taxonomicLookup;
    }

    /** default constructor */
    public PalList() {
    }

    /** minimal constructor */
    public PalList(nz.cri.gns.fred.hibernate.TaxonomicGroup taxonomicGroup, nz.cri.gns.fred.hibernate.Paleontology paleontology, nz.cri.gns.fred.hibernate.TaxonomicLookup taxonomicLookup) {
        this.taxonomicGroup = taxonomicGroup;
        this.paleontology = paleontology;
        this.taxonomicLookup = taxonomicLookup;
    }

    public Integer getPalListId() {
        return this.palListId;
    }

    public void setPalListId(Integer palListId) {
        this.palListId = palListId;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getSpecimenCount() {
        return this.specimenCount;
    }

    public void setSpecimenCount(Integer specimenCount) {
        this.specimenCount = specimenCount;
    }

    public String getSpecimenCoords() {
        return this.specimenCoords;
    }

    public void setSpecimenCoords(String specimenCoords) {
        this.specimenCoords = specimenCoords;
    }

    public String getTaxonomicName() {
        return this.taxonomicName;
    }

    public void setTaxonomicName(String taxonomicName) {
        this.taxonomicName = taxonomicName;
    }

    public nz.cri.gns.fred.hibernate.TaxonomicGroup getTaxonomicGroup() {
        return this.taxonomicGroup;
    }

    public void setTaxonomicGroup(nz.cri.gns.fred.hibernate.TaxonomicGroup taxonomicGroup) {
        this.taxonomicGroup = taxonomicGroup;
    }

    public nz.cri.gns.fred.hibernate.Paleontology getPaleontology() {
        return this.paleontology;
    }

    public void setPaleontology(nz.cri.gns.fred.hibernate.Paleontology paleontology) {
        this.paleontology = paleontology;
    }

    public nz.cri.gns.fred.hibernate.TaxonomicLookup getTaxonomicLookup() {
        return this.taxonomicLookup;
    }

    public void setTaxonomicLookup(nz.cri.gns.fred.hibernate.TaxonomicLookup taxonomicLookup) {
        this.taxonomicLookup = taxonomicLookup;
    }

}
