package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.MetaCat;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;

public class PalList implements Serializable, nz.cri.gns.fred.model.PaleontologyListEntry {

    private static final long serialVersionUID = 20050818L;

    private Integer palListId;
    private String comments;
    private Integer specimenCount;
    private String specimenCoords;
    private String taxonomicName;
    private TaxonomicGroup taxonomicGroup;
    private Paleontology paleontology;
    private Taxon taxon;
    private Set<MetaCat> metaCats;
    
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

    public TaxonomicGroup getTaxonomicGroup() {
        return this.taxonomicGroup;
    }

    public void setTaxonomicGroup(TaxonomicGroup taxonomicGroup) {
        this.taxonomicGroup = taxonomicGroup;
    }

    public Paleontology getPaleontology() {
        return this.paleontology;
    }

    public void setPaleontology(Paleontology paleontology) {
        this.paleontology = paleontology;
    }

    public Taxon getTaxon() {
        return this.taxon;
    }

    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public Set<MetaCat> getMetaCats() {
        return this.metaCats;
    }

    public void setMetaCats(Set<MetaCat> metaCats) {
        this.metaCats = metaCats;
    }
    
	public int compareTo(nz.cri.gns.fred.model.PaleontologyListEntry arg0) {
		if (taxonomicName == null)
			return 1;
		if (arg0.getTaxonomicName() == null)
			return -1;
		return taxonomicName.compareTo(arg0.getTaxonomicName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(palListId);
	}

	public String getDisplayName() {
		return taxonomicName;
	}

	/*public boolean equals(Object o) {
		return o instanceof PalList && ((PalList)o).palListId.equals(palListId);
	}
	
	public int hashCode() {
		return 765 * palListId;
	}*/
}
