package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.PalListMeta;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Taxon;

/** @author Hibernate CodeGenerator */
public class PalList implements Serializable, PaleontologyListEntry {

    private static final long serialVersionUID = 20050818L;

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
    private nz.cri.gns.fred.model.TaxonomicGroup taxonomicGroup;

    /** persistent field */
    private nz.cri.gns.fred.model.Paleontology paleontology;

    /** persistent field */
    private Taxon taxon;

    /** persistent field */
    private Set<PalListMeta> palListMetas;
    
    /** full constructor */
    public PalList(String comments, Integer specimenCount, String specimenCoords, String taxonomicName, nz.cri.gns.fred.model.TaxonomicGroup taxonomicGroup, nz.cri.gns.fred.model.Paleontology paleontology, nz.cri.gns.fred.model.Taxon taxonomicLookup, Set<PalListMeta> palListMetas) {
        this.comments = comments;
        this.specimenCount = specimenCount;
        this.specimenCoords = specimenCoords;
        this.taxonomicName = taxonomicName;
        this.taxonomicGroup = taxonomicGroup;
        this.paleontology = paleontology;
        this.taxon = taxonomicLookup;
        this.palListMetas = palListMetas;
    }

    /** default constructor */
    public PalList() {
    }

    /** minimal constructor */
    public PalList(nz.cri.gns.fred.model.TaxonomicGroup taxonomicGroup, nz.cri.gns.fred.model.Paleontology paleontology, nz.cri.gns.fred.model.Taxon taxonomicLookup, Set<PalListMeta> palListMetas) {
        this.taxonomicGroup = taxonomicGroup;
        this.paleontology = paleontology;
        this.taxon = taxonomicLookup;
        this.palListMetas = palListMetas;
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

    public nz.cri.gns.fred.model.TaxonomicGroup getTaxonomicGroup() {
        return this.taxonomicGroup;
    }

    public void setTaxonomicGroup(nz.cri.gns.fred.model.TaxonomicGroup taxonomicGroup) {
        this.taxonomicGroup = taxonomicGroup;
    }

    public nz.cri.gns.fred.model.Paleontology getPaleontology() {
        return this.paleontology;
    }

    public void setPaleontology(nz.cri.gns.fred.model.Paleontology paleontology) {
        this.paleontology = paleontology;
    }

    public Taxon getTaxon() {
        return this.taxon;
    }

    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public Set<PalListMeta> getPalListMetas() {
        return this.palListMetas;
    }

    public void setPalListMetas(Set<PalListMeta> palListMetas) {
        this.palListMetas = palListMetas;
    }
    
	public String getUniqueIdentifier() {
		return String.valueOf(getPalListId());
	}

	public String getDisplayName() {
		return getTaxonomicName();
	}

	public int compareTo(nz.cri.gns.fred.model.PaleontologyListEntry arg0) {
		return getDisplayName().compareTo(arg0.getDisplayName());
	}
	
}
